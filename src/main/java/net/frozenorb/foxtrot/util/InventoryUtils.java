package net.frozenorb.foxtrot.util;

import cc.fyre.proton.util.ItemBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class InventoryUtils {
   public static final SimpleDateFormat DEATH_TIME_FORMAT = new SimpleDateFormat("MM.dd.yy HH:mm");
   public static final String KILLS_LORE_IDENTIFIER = ChatColor.GOLD.toString() + ChatColor.BOLD + "Kills: " + ChatColor.WHITE.toString() + ChatColor.BOLD;
   public static final ItemStack CROWBAR = new ItemStack(Material.DIAMOND_HOE);
   public static final ItemStack ANTIDOTE = ItemBuilder.of(Material.POTION)
      .data((short)8196)
      .name(ChatColor.GREEN + "Antidote")
      .setLore(new ArrayList<>())
      .addToLore(ChatColor.GRAY + "Drink to relieve yourself of potion debuffs.")
      .build();
   public static final ItemStack ZOMBIE_SPAWNER = ItemBuilder.of(Material.MOB_SPAWNER).name(CC.GOLD + CC.BOLD + "Zombie Spawner").build();
   public static final ItemStack SKELETON_SPAWNER = ItemBuilder.of(Material.MOB_SPAWNER).name(CC.GOLD + CC.BOLD + "Skeleton Spawner").build();
   public static final ItemStack SPIDER_SPAWNER = ItemBuilder.of(Material.MOB_SPAWNER).name(CC.GOLD + CC.BOLD + "Spider Spawner").build();
   public static final ItemStack CAVE_SPIDER_SPAWNER = ItemBuilder.of(Material.MOB_SPAWNER).name(CC.GOLD + CC.BOLD + "Cave Spider Spawner").build();
   public static final String CROWBAR_NAME = ChatColor.YELLOW + "Crowbar";
   public static final int CROWBAR_PORTALS = 6;
   public static final int CROWBAR_SPAWNERS = 1;
   public static final long RESET_DELAY_TICKS = 2L;

   public static boolean conformEnchants(ItemStack item) {
      return false;
   }

   public static void fillBucket(Player player) {
      for (int i = 0; i < player.getInventory().getSize(); i++) {
         ItemStack item = player.getInventory().getItem(i);
         if (item != null && item.getType() == Material.BUCKET) {
            item.setType(Material.WATER_BUCKET);
            player.updateInventory();
            break;
         }
      }
   }

   public static ItemStack addToPart(ItemStack item, String title, String key, int max) {
      ItemMeta meta = item.getItemMeta();
      if (meta.hasLore() && meta.getLore().size() != 0) {
         List lore = meta.getLore();
         if (!lore.contains(title)) {
            lore.add("");
            lore.add(title);
            lore.add(key);
         } else {
            int titleIndex = lore.indexOf(title);
            int keys = 0;

            for (int i = titleIndex; i < lore.size() && !((String)lore.get(i)).equals(""); i++) {
               keys++;
            }

            lore.add(titleIndex + 1, key);
            if (keys > max) {
               lore.remove(titleIndex + keys);
            }
         }

         meta.setLore(lore);
      } else {
         ArrayList<String> lore = new ArrayList<>();
         lore.add("");
         lore.add(title);
         lore.add(key);
         meta.setLore(lore);
      }

      item.setItemMeta(meta);
      return item;
   }

   public static List<String> getCrowbarLore(int portals, int spawners) {
      ArrayList<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.GRAY + "Can Break:");
      lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "End Portals: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + portals + ChatColor.YELLOW + "}");
      lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Spawners: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + spawners + ChatColor.YELLOW + "}");
      return lore;
   }

   public static List<String> getKOTHRewardKeyLore(String koth) {
      ArrayList<String> lore = new ArrayList<>();
      SimpleDateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
      lore.add("");
      lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Obtained from: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + koth + ChatColor.YELLOW + "}");
      lore.add(
         ChatColor.WHITE
            + " - "
            + ChatColor.AQUA
            + "Time: "
            + ChatColor.YELLOW
            + "{"
            + ChatColor.BLUE
            + sdf.format(new Date()).replace(" AM", "").replace(" PM", "")
            + ChatColor.YELLOW
            + "}"
      );
      return lore;
   }

   public static ItemStack generateKOTHRewardKey(String koth) {
      ItemStack key = new ItemStack(Material.GOLD_NUGGET);
      ItemMeta meta = key.getItemMeta();
      meta.setDisplayName(ChatColor.YELLOW + "KOTH Reward Key");
      meta.setLore(getKOTHRewardKeyLore(koth));
      key.setItemMeta(meta);
      return key;
   }

   public static int getCrowbarUsesPortal(ItemStack item) {
      return Integer.parseInt(getLoreData(item, 2));
   }

   public static int getCrowbarUsesSpawner(ItemStack item) {
      return Integer.parseInt(getLoreData(item, 3));
   }

   public static boolean isArmor(ItemStack item) {
      return item.getTypeId() > 297 && item.getTypeId() < 318;
   }

   public static boolean isBoots(ItemStack item) {
      return item.getTypeId() == 301 || item.getTypeId() == 305 || item.getTypeId() == 309 || item.getTypeId() == 313 || item.getTypeId() == 317;
   }

   public static String getLoreData(ItemStack item, int index) {
      List lore = item.getItemMeta().getLore();
      if (lore != null && index < lore.size()) {
         String str = ChatColor.stripColor((String)lore.get(index));
         return str.split("\\{")[1].replace("}", "");
      } else {
         return "";
      }
   }

   public static boolean isSimilar(ItemStack item, String name) {
      return item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName().equals(name) : false;
   }

   public static void removeAmountFromInventory(Inventory inventory, ItemStack itemStack, int amount) {
      changeInventoryAmount(inventory, itemStack, amount, false);
   }

   public static boolean addAmountToInventory(Inventory inventory, ItemStack itemStack, int amount) {
      return changeInventoryAmount(inventory, itemStack, amount, true);
   }

   private static boolean changeInventoryAmount(Inventory inventory, ItemStack itemStack, int amount, boolean add) {
      boolean added = false;

      for (int i = 0; i < inventory.getSize(); i++) {
         ItemStack item = inventory.getItem(i);
         if (item != null && item.isSimilar(itemStack)) {
            int itemAmount = item.getAmount();
            if (!add) {
               int sum = itemAmount - amount;
               item.setAmount(sum);
               inventory.setItem(i, sum > 0 ? item : null);
            } else {
               int total = itemAmount + amount;
               if (total <= item.getType().getMaxStackSize()) {
                  item.setAmount(total);
                  inventory.setItem(i, item);
                  added = true;
               }
            }
            break;
         }
      }

      if (add && !added && inventory.firstEmpty() != -1) {
         itemStack.setAmount(amount);
         inventory.setItem(inventory.firstEmpty(), itemStack);
         added = true;
      }

      return added;
   }

   public static void resetInventoryDelayed(Player player) {
      Runnable task = () -> resetInventoryNow(player);
      Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), task, 2L);
   }

   public static void resetInventoryNow(Player player) {
      player.getInventory().setHeldItemSlot(0);
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      player.setFireTicks(0);

      for (PotionEffect effect : player.getActivePotionEffects()) {
         player.removePotionEffect(effect.getType());
      }

      player.updateInventory();
   }

   static {
      ItemMeta meta = CROWBAR.getItemMeta();
      meta.setDisplayName(CROWBAR_NAME);
      meta.setLore(getCrowbarLore(6, 1));
      CROWBAR.setItemMeta(meta);
   }
}
