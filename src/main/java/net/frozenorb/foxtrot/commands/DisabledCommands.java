package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DisabledCommands {
   private static final String UNKNOWN_COMMAND = "Unknown command. Type \"/help\" for help.";

   @Command(names = {"help", "?"}, permission = "")
   public static void help(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "settings", permission = "")
   public static void settings(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "armorcolor", permission = "")
   public static void armorColor(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "schedule", permission = "")
   public static void schedule(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "skins", permission = "")
   public static void skins(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "freerank", permission = "")
   public static void freeRank(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "vote", permission = "")
   public static void vote(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"team filter", "t filter", "f filter", "faction filter", "fac filter"}, permission = "")
   public static void filter(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"team top raidable", "t top raidable", "f top raidable", "faction top raidable", "fac top raidable"}, permission = "")
   public static void topRaidable(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"grounds", "ground", "spawnergrounds", "spawners"}, permission = "")
   public static void grounds(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "bosses", permission = "")
   public static void bosses(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "conquest", permission = "")
   public static void conquest(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void caveniteJoin(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void caveniteSpec(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void caveniteStart(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "fury start", permission = "op")
   public static void furyStart(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "outpost", permission = "")
   public static void outpost(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void customEnchants(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void coinShop(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"coupondrops", "discountdrops", "discountdrop", "coupondrop"}, permission = "op")
   public static void couponDrops(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void caveSays(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"gems", "gem"}, permission = "")
   public static void gems(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"team upgrade", "t upgrade", "f upgrade", "faction upgrade", "fac upgrade"}, permission = "")
   public static void factionUpgrade(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"brewer", "fancybrewer"}, permission = "")
   public static void fancyBrewer(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "boss", permission = "")
   public static void boss(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void totem(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"trivia", "trivia start"}, permission = "op")
   public static void trivia(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void treasureLoot(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"xpshop", "xp shop"}, permission = "")
   public static void xpshop(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static void baseBuild(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "menu", permission = "")
   public static void menu(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = "discord", permission = "")
   public static void discord(Player player) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Discord: &cComing Soon!"));
   }
}
