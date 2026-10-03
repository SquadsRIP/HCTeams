package net.frozenorb.foxtrot.gameplay.extra.settings;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum SettingType {
   LC_TEAM_VIEW(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Lunar Client Team View",
      Arrays.asList(ChatColor.GRAY + "Toggle the team view", ChatColor.GRAY + "arrows on Lunar Client."),
      Material.BEACON,
      11
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getLcTeamViewMap().isLCTeamView(player.getUniqueId());
         LunarClientAPI.getInstance().sendTeammates(player, new LCPacketTeammates(player.getUniqueId(), 10L, new HashMap()));
         Foxtrot.getInstance().getLcTeamViewMap().setLCTeamView(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW
               + "You are now "
               + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable")
               + ChatColor.YELLOW
               + " to see Lunar Client team view arrows."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getLcTeamViewMap().isLCTeamView(player.getUniqueId());
      }
   },
   DTR_DISPLAY(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "DTR Display",
      Arrays.asList(ChatColor.GRAY + "Toggle seeing DTR in", ChatColor.GRAY + "the form of hearts or not."),
      Material.APPLE,
      12
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId());
         Foxtrot.getInstance().getDTRDisplayMap().setHearts(player.getUniqueId(), value);
         LunarClientListener.updateNametag(player);
         player.sendMessage(
            ChatColor.YELLOW
               + "You now are "
               + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable")
               + ChatColor.YELLOW
               + " to view DTR in the form of hearts."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId());
      }
   },
   ABILITY_COOLDOWN(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Ability Cooldowns Scoreboard",
      Arrays.asList(ChatColor.GRAY + "Toggle ability cooldowns from", ChatColor.GRAY + "showing up on the scoreboard."),
      Material.BLAZE_ROD,
      14
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().isScoreboard(player.getUniqueId());
         Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().setStatus(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW
               + "You are now "
               + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable")
               + ChatColor.YELLOW
               + " to see ability cooldowns on your scoreboard."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().isScoreboard(player.getUniqueId());
      }
   },
   MOB_DROPS(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Mob Drops",
      Arrays.asList(ChatColor.GRAY + "Toggle being able to", ChatColor.GRAY + "pickup mob drops."),
      Material.SPIDER_EYE,
      15
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getMobDropsPickupMap().isMobPickup(player.getUniqueId());
         Foxtrot.getInstance().getMobDropsPickupMap().setMobPickup(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable") + ChatColor.YELLOW + " to pickup mob drops."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getMobDropsPickupMap().isMobPickup(player.getUniqueId());
      }
   },
   DEATH_MESSAGES(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Death Messages",
      Arrays.asList(ChatColor.GRAY + "Toggle being able to see", ChatColor.GRAY + "death messages in chat."),
      Material.SKULL_ITEM,
      29
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());
         Foxtrot.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW
               + "You are now "
               + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable")
               + ChatColor.YELLOW
               + " to see death messages in chat."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId());
      }
   },
   TIPS(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Tips",
      Arrays.asList(ChatColor.GRAY + "Toggle being able to", ChatColor.GRAY + "see tips in chat."),
      Material.LEASH,
      30
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getTipsMap().isTips(player.getUniqueId());
         Foxtrot.getInstance().getTipsMap().setTips(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW + "You are now " + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable") + ChatColor.YELLOW + " to see tips in chat."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getTipsMap().isTips(player.getUniqueId());
      }
   },
   TEAM_FIGHT(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Team Fight Scoreboard",
      Arrays.asList(ChatColor.GRAY + "Toggle the team fight data", ChatColor.GRAY + "showing up on the scoreboard."),
      Material.DIAMOND_CHESTPLATE,
      31
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId());
         Foxtrot.getInstance().getTeamfightModeMap().setTeamfight(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW
               + "You have "
               + (value ? ChatColor.GREEN + "enabled" : ChatColor.YELLOW + "disabled")
               + ChatColor.YELLOW
               + " the teamfight scoreboard."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId());
      }
   },
   ANNOYING_MESSAGES(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Annoying Messages",
      Arrays.asList(ChatColor.GRAY + "Toggle being able to see vote,", ChatColor.GRAY + "sale and more messages in chat."),
      Material.SIGN,
      32
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getAnnoyingBroadcastMap().isAnnoyingBroadcast(player.getUniqueId());
         Foxtrot.getInstance().getAnnoyingBroadcastMap().setAnnoyingBroadcast(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW
               + "You are now "
               + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable")
               + ChatColor.YELLOW
               + " to see vote, sale, freerank messages in chat."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getAnnoyingBroadcastMap().isAnnoyingBroadcast(player.getUniqueId());
      }
   },
   CUSTOM_TIMERS(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Sale Timers Scoreboard",
      Arrays.asList(ChatColor.GRAY + "Toggle sale timers from showing up", ChatColor.GRAY + "on the scoreboard outside of Spawn or SOTW."),
      Material.GOLD_INGOT,
      33
   ) {
      @Override
      public void toggle(Player player) {
         boolean value = !Foxtrot.getInstance().getSaleTimersScoreboardMap().isSaleTimers(player.getUniqueId());
         Foxtrot.getInstance().getSaleTimersScoreboardMap().setSaleTimers(player.getUniqueId(), value);
         player.sendMessage(
            ChatColor.YELLOW
               + "You are now "
               + (value ? ChatColor.GREEN + "able" : ChatColor.YELLOW + "unable")
               + ChatColor.YELLOW
               + " to see sale timers on your scoreboard."
         );
      }

      @Override
      public boolean isEnabled(Player player) {
         return Foxtrot.getInstance().getSaleTimersScoreboardMap().isSaleTimers(player.getUniqueId());
      }
   };

   String displayName;
   List<String> description;
   Material material;
   int slot;

   public abstract void toggle(Player var1);

   public abstract boolean isEnabled(Player var1);

   @ConstructorProperties({"displayName", "description", "material", "slot"})
   SettingType(String displayName, List<String> description, Material material, int slot) {
      this.displayName = displayName;
      this.description = description;
      this.material = material;
      this.slot = slot;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public List<String> getDescription() {
      return this.description;
   }

   public Material getMaterial() {
      return this.material;
   }

   public int getSlot() {
      return this.slot;
   }
}
