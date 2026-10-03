package net.frozenorb.foxtrot.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.EnchantmentRules;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentLimiterListener implements Listener {
   public static final Set<Character> ITEM_NAME_CHARACTER_BLACKLIST = new HashSet<>(Arrays.asList('卍'));
   private Map<String, Long> lastArmorCheck = new HashMap<>();
   private Map<String, Long> lastSwordCheck = new HashMap<>();

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      Player player;
      if (event.getDamager() instanceof Player && (player = (Player)event.getDamager()).getItemInHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
         event.setCancelled(true);
         player.getItemInHand().removeEnchantment(Enchantment.ARROW_KNOCKBACK);
         player.updateInventory();
         player.sendMessage(ChatColor.YELLOW + "You may not use a Punch Bow!");
      }

      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         player = (Player)event.getDamager();
         ItemStack hand = player.getItemInHand();
         if (!hand.containsEnchantment(Enchantment.KNOCKBACK)) {
            return;
         }

         if (DTRBitmask.CITADEL.appliesAt(player.getLocation())
            || DTRBitmask.CONQUEST.appliesAt(player.getLocation())
            || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
            String landAt = LandBoard.getInstance().getTeam(player.getLocation()).getName(player);
            event.setCancelled(true);
            player.sendMessage(CC.translateAlternateColorCodes("&eYou may not use knockback items at " + landAt + "&e!"));
            return;
         }

         if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            event.setCancelled(true);
            player.sendMessage(CC.translateAlternateColorCodes("&eYou may not use knockback items while &6EOTW &eis active!"));
            return;
         }

         if (player.getLocation().getWorld().getEnvironment() == Environment.THE_END || player.getLocation().getWorld().getEnvironment() == Environment.NETHER) {
            player.sendMessage(CC.translateAlternateColorCodes("&eYou may not use knockback items in The End/Nether!"));
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getInventory() instanceof AnvilInventory) {
         InventoryView view = event.getView();
         if (event.getCurrentItem() == null || event.getRawSlot() != view.convertSlot(event.getRawSlot()) || event.getRawSlot() != 2) {
            return;
         }

         ItemStack item = event.getCurrentItem();
         ItemMeta meta = item.getItemMeta();
         if (meta != null && meta.hasDisplayName()) {
            ItemStack previous = event.getInventory().getItem(0);
            if (previous != null
               && previous.hasItemMeta()
               && previous.getItemMeta().hasDisplayName()
               && this.containsColor(previous.getItemMeta().getDisplayName())) {
               String original = previous.getItemMeta().getDisplayName();
               if (!this.isUnchangedName(original, meta.getDisplayName())) {
                  event.setCancelled(true);
                  if (event.getWhoClicked() instanceof Player) {
                     ((Player)event.getWhoClicked()).sendMessage(ChatColor.RED + "You cannot rename this item.");
                  }

                  return;
               }

               meta.setDisplayName(original);
               item.setItemMeta(meta);
               event.setCurrentItem(item);
               return;
            }

            meta.setDisplayName(this.fixName(meta.getDisplayName()));
            item.setItemMeta(meta);
            event.setCurrentItem(item);
         }
      }
   }

   private boolean containsColor(String displayName) {
      return !ChatColor.stripColor(displayName).equals(displayName);
   }

   private boolean isUnchangedName(String original, String result) {
      return result.equals(original) || result.equals(original.replace("§", "")) || result.equals(ChatColor.stripColor(original));
   }

   private String fixName(String name) {
      StringBuilder result = new StringBuilder();

      for (char nameCharacter : name.toCharArray()) {
         boolean blacklisted = ITEM_NAME_CHARACTER_BLACKLIST.contains(nameCharacter);
         if (!blacklisted) {
            result.append(nameCharacter);
         }
      }

      return result.toString();
   }

   public boolean checkArmor(Player player) {
      boolean check = !this.lastArmorCheck.containsKey(player.getName()) || System.currentTimeMillis() - this.lastArmorCheck.get(player.getName()) > 5000L;
      if (check) {
         this.lastArmorCheck.put(player.getName(), System.currentTimeMillis());
         enforceEnchantmentLimits(player);
      }

      return check;
   }

   public boolean checkSword(Player player) {
      boolean check = !this.lastSwordCheck.containsKey(player.getName()) || System.currentTimeMillis() - this.lastSwordCheck.get(player.getName()) > 5000L;
      if (check) {
         this.lastSwordCheck.put(player.getName(), System.currentTimeMillis());
         enforceEnchantmentLimits(player);
      }

      return check;
   }

   public static void enforceEnchantmentLimits(Player player) {
      EnchantmentRules rules = new EnchantmentRules(Foxtrot.getInstance().getConfig());
      boolean changed = false;

      for (ItemStack item : player.getInventory().getContents()) {
         changed |= rules.limitItem(item);
      }

      for (ItemStack item : player.getInventory().getArmorContents()) {
         changed |= rules.limitItem(item);
      }

      if (changed) {
         player.updateInventory();
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onEnchantItem(EnchantItemEvent event) {
      EnchantmentRules rules = new EnchantmentRules(Foxtrot.getInstance().getConfig());
      rules.applyTableRules(event.getItem(), event.getEnchantsToAdd(), event.getExpLevelCost(), ThreadLocalRandom.current());
      if (event.getEnchantsToAdd().isEmpty()) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onAnvilResult(InventoryClickEvent event) {
      if (event.getInventory() instanceof AnvilInventory && event.getRawSlot() == 2) {
         EnchantmentRules rules = new EnchantmentRules(Foxtrot.getInstance().getConfig());
         if (rules.exceedsLimits(event.getCurrentItem(), event.getInventory().getItem(0), event.getInventory().getItem(1))) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player) {
               ((Player)event.getWhoClicked())
                  .sendMessage(
                     ChatColor.translateAlternateColorCodes(
                        '&',
                        Foxtrot.getInstance()
                           .getConfig()
                           .getString("enchantingTable.anvil-limit-message", "&cYou cannot combine items above the maximum enchantment level.")
                     )
                  );
            }
         }
      }
   }
}
