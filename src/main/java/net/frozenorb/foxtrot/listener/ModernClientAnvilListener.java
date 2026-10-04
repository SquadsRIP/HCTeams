package net.frozenorb.foxtrot.listener;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.EnchantmentRules;
import net.minecraft.server.v1_7_R4.Container;
import net.minecraft.server.v1_7_R4.Enchantment;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.Items;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

// The server runs 1.7 anvil rules, but 1.8 clients (protocol 47+) predict the result and level cost with 1.8 rules:
// renaming costs 1 level instead of 7+, and enchant/repair costs differ. The server then refused clicks the client
// showed as affordable, so the inputs bounced back into the anvil. For those clients, recompute the result and cost
// with the 1.8 rules right before vanilla handles the click on the result slot.
// Every level cost (1.7 and 1.8 clients) is also scaled by anvil.cost-multiplier. Clients compute the cost on their own
// screen and the 1.7 server only sends it when the anvil opens, so the scaled cost is resent every tick.
public final class ModernClientAnvilListener implements Listener {
   private static final int MODERN_PROTOCOL = 47;
   private static final int MAX_NAME_LENGTH = 30;
   private static final int TOO_EXPENSIVE = 40;
   private Field costField;
   private Field materialCostField;
   private Field renameTextField;
   private boolean reflectionFailed;
   // 1.7 clients keep vanilla rules: the result item vanilla computed, mapped to the scaled cost written for it.
   private final Map<ItemStack, Integer> scaledVanillaResults = new WeakHashMap<>();

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getInventory() instanceof AnvilInventory && event.getRawSlot() == 2 && event.getWhoClicked() instanceof Player) {
         EntityPlayer handle = ((CraftPlayer)event.getWhoClicked()).getHandle();
         if (handle.playerConnection != null && !handle.abilities.canInstantlyBuild) {
            Container container = handle.activeContainer;
            if (this.resolveFields(container)) {
               try {
                  if (isModern(handle)) {
                     AnvilResult result = this.computeModern(container, event.getInventory());
                     this.costField.setInt(container, result.cost);
                     this.materialCostField.setInt(container, result.materialCost);
                     event.getInventory().setItem(2, result.item == null ? null : CraftItemStack.asCraftMirror(result.item));
                  } else {
                     this.scaleVanillaCost(container);
                  }
               } catch (IllegalAccessException ex) {
                  this.reflectionFailed = true;
               }
            }
         }
      }
   }

   // Runs every tick. Any inventory resend makes the client recompute the cost with its own rules, so keep
   // overwriting it with the cost the server will actually charge.
   public void syncCosts() {
      for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         Inventory top = player.getOpenInventory().getTopInventory();
         if (top instanceof AnvilInventory && top.getItem(0) != null) {
            EntityPlayer handle = ((CraftPlayer)player).getHandle();
            Container container = handle.activeContainer;
            if (handle.playerConnection != null && !handle.abilities.canInstantlyBuild && this.resolveFields(container)) {
               try {
                  if (isModern(handle)) {
                     AnvilResult result = this.computeModern(container, top);
                     if (result.item != null) {
                        handle.setContainerData(container, 0, result.cost);
                        // The client hides the result when its own (unscaled) cost reaches 40; show it again.
                        if (result.unscaledCost >= TOO_EXPENSIVE
                           && top.getItem(2) == null
                           && !new EnchantmentRules(Foxtrot.getInstance().getConfig())
                              .exceedsLimits(CraftItemStack.asCraftMirror(result.item), top.getItem(0), top.getItem(1))) {
                           handle.a(container, 2, result.item);
                        }
                     }
                  } else if (container.getSlot(2).getItem() != null) {
                     handle.setContainerData(container, 0, this.scaleVanillaCost(container));
                  }
               } catch (IllegalAccessException ex) {
                  this.reflectionFailed = true;
               }
            }
         }
      }
   }

   private static boolean isModern(EntityPlayer handle) {
      return handle.playerConnection.networkManager.getVersion() >= MODERN_PROTOCOL;
   }

   private AnvilResult computeModern(Container container, Inventory anvil) throws IllegalAccessException {
      ItemStack left = CraftItemStack.asNMSCopy(anvil.getItem(0));
      ItemStack right = CraftItemStack.asNMSCopy(anvil.getItem(1));
      return compute(left, right, (String)this.renameTextField.get(container), costMultiplier());
   }

   // Vanilla 1.7 recomputes the result (a new ItemStack) and the cost whenever an input or the name changes; scale
   // each new cost once.
   private int scaleVanillaCost(Container container) throws IllegalAccessException {
      ItemStack result = container.getSlot(2).getItem();
      if (result == null) {
         return this.costField.getInt(container);
      } else {
         Integer scaled = this.scaledVanillaResults.get(result);
         if (scaled == null) {
            scaled = scaleCost(this.costField.getInt(container), costMultiplier());
            this.scaledVanillaResults.put(result, scaled);
         }

         this.costField.setInt(container, scaled);
         return scaled;
      }
   }

   private static double costMultiplier() {
      return Math.max(0.0, Foxtrot.getInstance().getConfig().getDouble("anvil.cost-multiplier", 0.5));
   }

   static int scaleCost(int cost, double multiplier) {
      return cost <= 0 ? cost : Math.max(1, (int)Math.ceil(cost * multiplier));
   }

   private boolean resolveFields(Container container) {
      if (this.reflectionFailed || !container.getClass().getSimpleName().equals("ContainerAnvil")) {
         return false;
      } else if (this.costField != null) {
         return true;
      } else {
         try {
            Class<?> type = container.getClass();
            this.costField = type.getDeclaredField("a");
            this.materialCostField = type.getDeclaredField("m");
            this.renameTextField = type.getDeclaredField("n");
            this.costField.setAccessible(true);
            this.materialCostField.setAccessible(true);
            this.renameTextField.setAccessible(true);
            return true;
         } catch (ReflectiveOperationException ex) {
            this.reflectionFailed = true;
            this.costField = null;
            return false;
         }
      }
   }

   // Port of 1.8's ContainerRepair.updateRepairOutput.
   static AnvilResult compute(ItemStack left, ItemStack right, String renameText, double costMultiplier) {
      if (left == null) {
         return new AnvilResult(null, 0, 0, 0);
      } else {
         ItemStack output = left.cloneItemStack();
         Map<Integer, Integer> enchants = getEnchants(output);
         int priorWork = left.getRepairCost() + (right == null ? 0 : right.getRepairCost());
         int cost = 0;
         int renameCost = 0;
         int materialCost = 0;
         if (right != null) {
            boolean book = right.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.g(right).size() > 0;
            if (output.g() && output.getItem().a(left, right)) {
               int repair = Math.min(output.j(), output.l() / 4);
               if (repair <= 0) {
                  return new AnvilResult(null, 0, 0, 0);
               }

               int used;
               for (used = 0; repair > 0 && used < right.count; used++) {
                  output.setData(output.j() - repair);
                  cost++;
                  repair = Math.min(output.j(), output.l() / 4);
               }

               materialCost = used;
            } else {
               if (!book && (output.getItem() != right.getItem() || !output.g())) {
                  return new AnvilResult(null, 0, 0, 0);
               }

               if (output.g() && !book) {
                  int leftDurability = left.l() - left.j();
                  int rightDurability = right.l() - right.j();
                  int damage = output.l() - (leftDurability + rightDurability + output.l() * 12 / 100);
                  if (damage < 0) {
                     damage = 0;
                  }

                  if (damage < output.getData()) {
                     output.setData(damage);
                     cost += 2;
                  }
               }

               for (Entry<Integer, Integer> entry : getEnchants(right).entrySet()) {
                  int id = entry.getKey();
                  Enchantment enchantment = id >= 0 && id < Enchantment.byId.length ? Enchantment.byId[id] : null;
                  if (enchantment != null) {
                     int current = enchants.containsKey(id) ? enchants.get(id) : 0;
                     int level = entry.getValue();
                     level = current == level ? level + 1 : Math.max(level, current);
                     boolean applies = enchantment.canEnchant(left) || left.getItem() == Items.ENCHANTED_BOOK;

                     for (int other : enchants.keySet()) {
                        Enchantment otherEnchantment = other >= 0 && other < Enchantment.byId.length ? Enchantment.byId[other] : null;
                        if (other != id && otherEnchantment != null && !enchantment.a(otherEnchantment)) {
                           applies = false;
                           cost++;
                        }
                     }

                     if (applies) {
                        if (level > enchantment.getMaxLevel()) {
                           level = enchantment.getMaxLevel();
                        }

                        enchants.put(id, level);
                        int multiplier;
                        switch (enchantment.getRandomWeight()) {
                           case 1:
                              multiplier = 8;
                              break;
                           case 2:
                              multiplier = 4;
                              break;
                           case 5:
                              multiplier = 2;
                              break;
                           case 10:
                              multiplier = 1;
                              break;
                           default:
                              multiplier = 0;
                        }

                        if (book) {
                           multiplier = Math.max(1, multiplier / 2);
                        }

                        cost += multiplier * level;
                     }
                  }
               }
            }
         }

         if (renameText == null || renameText.trim().isEmpty()) {
            if (left.hasName()) {
               renameCost = 1;
               cost += renameCost;
               output.t();
            }
         } else if (!isUnchangedName(left, renameText)) {
            renameCost = 1;
            cost += renameCost;
            output.c(renameText);
         }

         int unscaled = priorWork + cost;
         int total = scaleCost(unscaled, costMultiplier);
         if (cost <= 0) {
            output = null;
         }

         if (renameCost == cost && renameCost > 0 && total >= TOO_EXPENSIVE) {
            total = TOO_EXPENSIVE - 1;
         }

         if (total >= TOO_EXPENSIVE) {
            output = null;
         }

         if (output != null) {
            int repairCost = output.getRepairCost();
            if (right != null && repairCost < right.getRepairCost()) {
               repairCost = right.getRepairCost();
            }

            output.setRepairCost(repairCost * 2 + 1);
            setEnchants(enchants, output);
         }

         return new AnvilResult(output, output == null ? 0 : total, materialCost, unscaled);
      }
   }

   // The 1.8 client sends the item's own name (colour codes included) when the player didn't edit it, or "" for an
   // unnamed item. The server strips the colour codes, and the client's text box cuts the name at 30 characters.
   private static boolean isUnchangedName(ItemStack left, String renameText) {
      if (!left.hasName()) {
         return false;
      } else {
         String name = left.getName();
         String shown = name.length() > MAX_NAME_LENGTH ? name.substring(0, MAX_NAME_LENGTH) : name;
         return renameText.equals(name) || renameText.equals(name.replace("§", "")) || renameText.equals(shown.replace("§", ""));
      }
   }

   private static NBTTagList getEnchantList(ItemStack item) {
      return item.getItem() == Items.ENCHANTED_BOOK ? Items.ENCHANTED_BOOK.g(item) : item.getEnchantments();
   }

   private static Map<Integer, Integer> getEnchants(ItemStack item) {
      Map<Integer, Integer> enchants = new LinkedHashMap<>();
      NBTTagList list = getEnchantList(item);
      if (list != null) {
         for (int i = 0; i < list.size(); i++) {
            NBTTagCompound entry = list.get(i);
            enchants.put((int)entry.getShort("id"), (int)entry.getShort("lvl"));
         }
      }

      return enchants;
   }

   private static void setEnchants(Map<Integer, Integer> enchants, ItemStack item) {
      if (item.getItem() == Items.ENCHANTED_BOOK) {
         NBTTagList list = Items.ENCHANTED_BOOK.g(item);

         for (Entry<Integer, Integer> entry : enchants.entrySet()) {
            boolean found = false;

            for (int i = 0; i < list.size(); i++) {
               NBTTagCompound existing = list.get(i);
               if (existing.getShort("id") == entry.getKey()) {
                  if (existing.getShort("lvl") < entry.getValue()) {
                     existing.setShort("lvl", (short)entry.getValue().intValue());
                  }

                  found = true;
                  break;
               }
            }

            if (!found) {
               list.add(enchantEntry(entry.getKey(), entry.getValue()));
            }
         }

         if (item.tag == null) {
            item.setTag(new NBTTagCompound());
         }

         item.tag.set("StoredEnchantments", list);
      } else {
         NBTTagList list = new NBTTagList();

         for (Entry<Integer, Integer> entry : enchants.entrySet()) {
            list.add(enchantEntry(entry.getKey(), entry.getValue()));
         }

         if (list.size() > 0) {
            if (item.tag == null) {
               item.setTag(new NBTTagCompound());
            }

            item.tag.set("ench", list);
         } else if (item.tag != null) {
            item.tag.remove("ench");
         }
      }
   }

   private static NBTTagCompound enchantEntry(int id, int level) {
      NBTTagCompound entry = new NBTTagCompound();
      entry.setShort("id", (short)id);
      entry.setShort("lvl", (short)level);
      return entry;
   }

   static final class AnvilResult {
      final ItemStack item;
      final int cost;
      final int materialCost;
      final int unscaledCost;

      AnvilResult(ItemStack item, int cost, int materialCost, int unscaledCost) {
         this.item = item;
         this.cost = cost;
         this.materialCost = materialCost;
         this.unscaledCost = unscaledCost;
      }
   }
}
