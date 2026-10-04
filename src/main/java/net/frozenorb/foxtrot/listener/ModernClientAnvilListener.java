package net.frozenorb.foxtrot.listener;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
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

// The server runs 1.7 anvil rules, but 1.8 clients (protocol 47+) predict the result and level cost with 1.8 rules:
// renaming costs 1 level instead of 7+, and enchant/repair costs differ. The server then refused clicks the client
// showed as affordable, so the inputs bounced back into the anvil. For those clients, recompute the result and cost
// with the 1.8 rules right before vanilla handles the click on the result slot.
public final class ModernClientAnvilListener implements Listener {
   private static final int MODERN_PROTOCOL = 47;
   private static final int MAX_NAME_LENGTH = 30;
   private Field costField;
   private Field materialCostField;
   private Field renameTextField;
   private boolean reflectionFailed;

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getInventory() instanceof AnvilInventory && event.getRawSlot() == 2 && event.getWhoClicked() instanceof Player) {
         EntityPlayer handle = ((CraftPlayer)event.getWhoClicked()).getHandle();
         if (handle.playerConnection != null
            && handle.playerConnection.networkManager.getVersion() >= MODERN_PROTOCOL
            && !handle.abilities.canInstantlyBuild) {
            Container container = handle.activeContainer;
            if (this.resolveFields(container)) {
               try {
                  ItemStack left = CraftItemStack.asNMSCopy(event.getInventory().getItem(0));
                  ItemStack right = CraftItemStack.asNMSCopy(event.getInventory().getItem(1));
                  AnvilResult result = compute(left, right, (String)this.renameTextField.get(container));
                  this.costField.setInt(container, result.cost);
                  this.materialCostField.setInt(container, result.materialCost);
                  event.getInventory().setItem(2, result.item == null ? null : CraftItemStack.asCraftMirror(result.item));
               } catch (IllegalAccessException ex) {
                  this.reflectionFailed = true;
               }
            }
         }
      }
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
   static AnvilResult compute(ItemStack left, ItemStack right, String renameText) {
      if (left == null) {
         return new AnvilResult(null, 0, 0);
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
                  return new AnvilResult(null, 0, 0);
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
                  return new AnvilResult(null, 0, 0);
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

         int total = priorWork + cost;
         if (cost <= 0) {
            output = null;
         }

         if (renameCost == cost && renameCost > 0 && total >= 40) {
            total = 39;
         }

         if (total >= 40) {
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

         return new AnvilResult(output, output == null ? 0 : total, materialCost);
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

      AnvilResult(ItemStack item, int cost, int materialCost) {
         this.item = item;
         this.cost = cost;
         this.materialCost = materialCost;
      }
   }
}
