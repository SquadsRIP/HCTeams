package net.frozenorb.foxtrot.gameplay.ability.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitSwapper extends Ability {
   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.YELLOW_FLOWER;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kit Swapper";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fRight Click to switch your armor"));
      toReturn.add(ChatColor.translate("&6❙ &fto another armor type that's in your inventory!"));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &d&lAbility Crate&f!"));
      return toReturn;
   }

   @Override
   public Boolean isAllowedAtLocation(Location location) {
      return Foxtrot.getInstance().getMapHandler().isKitMap()
         ? !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location)
         : !Foxtrot.getInstance().getServerHandler().isWarzone(location)
            && location.getWorld().getEnvironment() == Environment.NORMAL
            && !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   @Override
   public long getCooldown() {
      return 30000L;
   }

   @Override
   public Category getCategory() {
      return Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "Your armor has switched to another kit!";
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Player player = event.getPlayer();
         if (this.isSimilar(event.getItem())) {
            event.setCancelled(true);
            AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
            if (!abilityUseEvent.isCancelled()) {
               if (player.getItemInHand().getAmount() == 1) {
                  player.setItemInHand(null);
               } else {
                  player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
               }

               HashMap<Integer, ItemStack> items = new HashMap<>();
               Item.ArmorType current = Arrays.stream(Item.ArmorType.values())
                  .filter(
                     it -> Arrays.stream(player.getInventory().getArmorContents())
                        .filter(Objects::nonNull)
                        .anyMatch(armorx -> armorx.getType().name().startsWith(it.name()))
                  )
                  .findFirst()
                  .orElse(null);
               if (current == null) {
                  player.sendMessage(ChatColor.RED + "You are not wearing a full set.");
               } else {
                  for (Item.ArmorType type : Item.ArmorType.values()) {
                     if (type != current) {
                        Map<ItemStack, Integer> slots = new HashMap<>();
                        Map<Item.ArmorPart, ItemStack> armor = new HashMap<>();

                        for (Item.ArmorPart part : Item.ArmorPart.values()) {
                           armor.put(part, null);
                        }

                        int i = 0;

                        for (ItemStack content : player.getInventory().getContents()) {
                           if (content != null) {
                              for (Item.ArmorPart part : Item.ArmorPart.values()) {
                                 if (content.getType().name().startsWith(type.name())
                                    && content.getType().name().endsWith(part.name())
                                    && (
                                       !armor.containsKey(part)
                                          || armor.get(part) == null
                                          || content.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)
                                             >= armor.get(part).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)
                                    )) {
                                    armor.put(part, content);
                                    slots.put(content, i);
                                 }
                              }
                           }

                           i++;
                        }

                        if (armor.values().stream().filter(Objects::nonNull).count() >= 4L) {
                           armor.values().forEach(it -> {
                              ItemStack var10000 = items.put(slots.remove(it), it);
                           });
                           break;
                        }
                     }
                  }

                  if (items.isEmpty()) {
                     player.sendMessage(ChatColor.RED + "You have no other sets in your inventory.");
                  } else {
                     AtomicInteger tick = new AtomicInteger(2);
                     items.forEach((key, value) -> Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        Item.ArmorPart partx = Item.ArmorPart.valueOf(value.getType().name().split("_")[1]);
                        if (partx == Item.ArmorPart.HELMET) {
                           player.getInventory().setItem(key, player.getInventory().getHelmet());
                           player.getInventory().setHelmet(value);
                        } else if (partx == Item.ArmorPart.CHESTPLATE) {
                           player.getInventory().setItem(key, player.getInventory().getChestplate());
                           player.getInventory().setChestplate(value);
                        } else if (partx == Item.ArmorPart.LEGGINGS) {
                           player.getInventory().setItem(key, player.getInventory().getLeggings());
                           player.getInventory().setLeggings(value);
                        } else {
                           player.getInventory().setItem(key, player.getInventory().getBoots());
                           player.getInventory().setBoots(value);
                        }
                     }, tick.getAndIncrement()));
                     player.updateInventory();
                     this.applyCooldown(player);
                  }
               }
            }
         }
      }
   }
}
