package net.frozenorb.foxtrot.gameplay.kitmap.game;

import cc.fyre.proton.command.param.ParameterType;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.util.com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum GameType {
   SUMO("Sumo", "Try and knock your opponent(s) off of the platform!", new ItemStack(Material.STICK), 8, 16, 64, false, ""),
   SPLEEF(
      "Spleef",
      "Break the floor of snow below others before they spleef you. Last man standing wins.",
      new ItemStack(Material.DIAMOND_SPADE),
      8,
      8,
      32,
      false,
      ""
   ),
   OITQ("OITQ", "Shoot players with a bow to instantly kill them! First to 20 kills wins!", new ItemStack(Material.ARROW), 8, 8, 32, false, ""),
   WOOL_SHUFFLE(
      "Wool Shuffle",
      "Each round, a color of wool is chosen. Run and stand on that color before the others disappear.",
      new ItemStack(Material.WOOL),
      8,
      8,
      32,
      false,
      ""
   ),
   TNT_RUN("TNT Run", "Run across the TNT to effectively spleef your enemies!", new ItemStack(Material.TNT), 8, 8, 32, false, ""),
   TNT_TAG("TNT Tag", "Tag players, and after half a minute tagged players will be eliminated!", new ItemStack(Material.TNT), 8, 8, 32, false, ""),
   KNOCK_OUT("Knockout", "Knock out all the players off the platform!", new ItemStack(Material.BLAZE_ROD), 8, 16, 64, false, ""),
   MINE_STRIKE("MineStrike", "Shoot and kill your enemies!", new ItemStack(Material.DIAMOND_BARDING), 8, 16, 64, false, ""),
   FFA(
      "FFA",
      "Free For All. Invisible with PvP Kit, every man for themselves. Last man standing wins.",
      new ItemStack(Material.DIAMOND_SWORD),
      8,
      12,
      48,
      false,
      ""
   ),
   PARKOUR("Parkour", "Make it to the end first to win!", new ItemStack(Material.IRON_PLATE), 8, 16, 64, false, ""),
   THIMBLE("Thimble", "Jump down into a pit of water! Avoid the blocks!", new ItemStack(Material.WATER_BUCKET), 8, 16, 64, false, "");

   private final String displayName;
   private final String description;
   private final ItemStack icon;
   private final int minForceStartPlayers;
   private final int minPlayers;
   private final int maxPlayers;
   private final boolean disabled;
   private final String rank;

   public boolean canHost(Player player) {
      return player.hasPermission("kitmap.game.host." + this.name().toLowerCase());
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getDescription() {
      return this.description;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public int getMinForceStartPlayers() {
      return this.minForceStartPlayers;
   }

   public int getMinPlayers() {
      return this.minPlayers;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   public String getRank() {
      return this.rank;
   }

   @ConstructorProperties({"displayName", "description", "icon", "minForceStartPlayers", "minPlayers", "maxPlayers", "disabled", "rank"})
   GameType(String displayName, String description, ItemStack icon, int minForceStartPlayers, int minPlayers, int maxPlayers, boolean disabled, String rank) {
      this.displayName = displayName;
      this.description = description;
      this.icon = icon;
      this.minForceStartPlayers = minForceStartPlayers;
      this.minPlayers = minPlayers;
      this.maxPlayers = maxPlayers;
      this.disabled = disabled;
      this.rank = rank;
   }

   public static class Type implements ParameterType<GameType> {
      public GameType transform(CommandSender sender, String source) {
         try {
            return GameType.valueOf(source.toUpperCase());
         } catch (Exception e) {
            sender.sendMessage(ChatColor.YELLOW + "Game Type '" + source + "' couldn't be found.");
            return null;
         }
      }

      @Override
      public List<String> tabComplete(Player player, Set<String> flags, String source) {
         ArrayList completions = Lists.newArrayList();

         for (GameType gameType : GameType.values()) {
            if (StringUtils.startsWithIgnoreCase(gameType.name(), source)) {
               completions.add(gameType.name());
            }
         }

         return completions;
      }
   }
}
