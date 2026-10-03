package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class HelpMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Squads" + ChatColor.GRAY + " - Help";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(11, storeButton());
      toReturn.put(13, discordButton());
      toReturn.put(15, coinShopButton());
      toReturn.put(20, abilityItemsButton());
      toReturn.put(22, classesButton());
      toReturn.put(24, kitsButton());
      toReturn.put(29, leaderboardsButton());
      toReturn.put(31, battlePassButton());
      toReturn.put(40, factionsInfoButton());
      return toReturn;
   }

   private static Button storeButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Store";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7Purchase ranks, keys, items, and more");
            toReturn.add("&7to support the server and get setup.");
            toReturn.add("");
            toReturn.add("&aClick here to open the store");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.GOLD_INGOT;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/buy");
         }
      };
   }

   private static Button discordButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Discord";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7Join our Discord for updates, support,");
            toReturn.add("&7giveaways, and community events.");
            toReturn.add("");
            toReturn.add("&aClick here to join the Discord");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.REDSTONE;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/discord");
         }
      };
   }

   private static Button coinShopButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Coin Shop";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7Spend your coins on items, blocks,");
            toReturn.add("&7gear, and other useful things.");
            toReturn.add("");
            toReturn.add("&aClick here to open the Coin Shop");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.EMERALD;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/coinshop");
         }
      };
   }

   private static Button abilityItemsButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Ability Items";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7View all the partner ability items,");
            toReturn.add("&7their effects, and cooldowns.");
            toReturn.add("");
            toReturn.add("&aClick here to view Ability Items");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.BLAZE_ROD;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/abilityitems");
         }
      };
   }

   private static Button classesButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Classes";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7View all the PvP classes available,");
            toReturn.add("&7their abilities, and how to use them.");
            toReturn.add("");
            toReturn.add("&aClick here to view Classes");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.DIAMOND_SWORD;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/classes");
         }
      };
   }

   private static Button kitsButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Kits";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7View all available kits, their contents,");
            toReturn.add("&7and their cooldowns.");
            toReturn.add("");
            toReturn.add("&aClick here to view Kits");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.BOOK;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/kits");
         }
      };
   }

   private static Button leaderboardsButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Leaderboards";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7View the top players by kills, deaths,");
            toReturn.add("&7killstreaks, KOTH captures, and more.");
            toReturn.add("");
            toReturn.add("&aClick here to view Leaderboards");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.SKULL_ITEM;
         }

         @Override
         public byte getDamageValue(Player player) {
            return 3;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/lb");
         }
      };
   }

   private static Button battlePassButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Battle Pass";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7View the Battle Pass tiers, challenges,");
            toReturn.add("&7and the rewards you can earn.");
            toReturn.add("");
            toReturn.add("&aClick here to view the Battle Pass");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.SKULL_ITEM;
         }

         @Override
         public byte getDamageValue(Player player) {
            return 3;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.chat("/bp");
         }
      };
   }

   private static Button factionsInfoButton() {
      return new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Factions";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("&7Create a faction: &f/f create <name>");
            toReturn.add("&7View faction info: &f/f who <name>");
            toReturn.add("&7Claim land: &f/f claim");
            toReturn.add("&7Set your home: &f/f sethome");
            toReturn.add("&7Top factions: &f/f top");
            toReturn.add("");
            toReturn.add("&cPvP help: &f/pvp");
            return toReturn.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.IRON_SWORD;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
         }
      };
   }
}
