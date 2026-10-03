package net.frozenorb.foxtrot.gameplay.events.mini;

import cc.fyre.neutron.Neutron;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public abstract class MiniEvent implements Listener {
   private final Map<UUID, Integer> points = new HashMap<>();
   private long started;
   private long endsAt;
   private BukkitTask bukkitTask;

   public abstract String getEventID();

   public abstract int getSeconds();

   public abstract String getObjective();

   public abstract List<String> getDescription();

   public void addProgress(Player player, boolean sendMessage) {
      if (player.getGameMode() != GameMode.CREATIVE) {
         this.points.put(player.getUniqueId(), this.points.getOrDefault(player.getUniqueId(), 0) + 1);
         if (sendMessage) {
            player.sendMessage(ChatColor.translate("&aYou have obtained a point towards this event!"));
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
         }
      }
   }

   public void addProgress(Player player, boolean sendMessage, int amount) {
      if (player.getGameMode() != GameMode.CREATIVE) {
         this.points.put(player.getUniqueId(), this.points.getOrDefault(player.getUniqueId(), 0) + amount);
         if (sendMessage) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            player.sendMessage(ChatColor.translate("&aYou have obtained a point towards this event!"));
         }
      }
   }

   public void activate() {
      Foxtrot instance = Foxtrot.getInstance();
      instance.getServer().getPluginManager().registerEvents(this, instance);
      List<String> tooltip = Arrays.asList(
         ChatColor.translate("&4&lRewards"),
         ChatColor.translate("&4&l┃ &f1st Place: &e&l2x Omega Chests &fand &b&l10x Airdrops"),
         ChatColor.translate("&4&l┃ &f2nd Place: &b&l10x Airdrops"),
         ChatColor.translate("&4&l┃ &f3rd Place: &6&l10x Halloween Keys")
      );
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         tooltip = Arrays.asList(
            ChatColor.translate("&4&lRewards"),
            ChatColor.translate("&4&l┃ &f1st Place: &4&l2x Treasure Chests &b&l5x Airdrops &fand &a&l1000 Gems"),
            ChatColor.translate("&4&l┃ &f2nd Place: &b&l3x Airdrops &fand &a&l1000 Gems"),
            ChatColor.translate("&4&l┃ &f3rd Place: &a&l500 Gems")
         );
      }

      this.started = System.currentTimeMillis();
      this.endsAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.getSeconds());
      this.bukkitTask = instance.getServer().getScheduler().runTaskLater(instance, () -> {
         if (instance.getMiniEventsHandler().getActiveEvent().getEventID().equalsIgnoreCase(this.getEventID())) {
            this.deactivate(false);
         }
      }, 20L * this.getSeconds());

      for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
         onlinePlayer.sendMessage("");
         onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
         new FancyMessage(ChatColor.translate("&7█&4█████&7█ &4&l" + this.getObjective() + " Event")).tooltip(tooltip).send(onlinePlayer);
         new FancyMessage(ChatColor.translate("&7█&4█&7█████ &7" + this.getDescription().get(0))).tooltip(tooltip).send(onlinePlayer);
         new FancyMessage(ChatColor.translate("&7█&4████&7██ &7" + this.getDescription().get(1))).tooltip(tooltip).send(onlinePlayer);
         new FancyMessage(ChatColor.translate("&7█&4█&7█████")).tooltip(tooltip).send(onlinePlayer);
         new FancyMessage(ChatColor.translate("&7█&4█████&7█ &aHover to view all prizes")).tooltip(tooltip).send(onlinePlayer);
         onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
         onlinePlayer.sendMessage("");
      }
   }

   public void deactivate(boolean force) {
      Foxtrot instance = Foxtrot.getInstance();
      Server server = instance.getServer();
      MiniEventsHandler miniEventsHandler = instance.getMiniEventsHandler();
      List<UUID> placing = new ArrayList<>(this.getSortedList());
      this.endsAt = 0L;
      this.started = 0L;
      this.bukkitTask.cancel();
      miniEventsHandler.setActiveEvent(null);
      HandlerList.unregisterAll(this);
      if (force) {
         this.points.clear();
         server.broadcastMessage("");
         server.broadcastMessage("&4&lMini-Event");
         server.broadcastMessage("&7The latest mini-event has been cancelled!");
         server.broadcastMessage("");
      } else {
         UUID firstPlace = placing.isEmpty() ? null : placing.remove(0);
         UUID secondPlace = placing.isEmpty() ? null : placing.remove(0);
         UUID thirdPlace = placing.isEmpty() ? null : placing.remove(0);
         String first = firstPlace == null ? "N/A" : Neutron.getInstance().getProfileHandler().findDisplayName(firstPlace);
         String second = secondPlace == null ? "N/A" : Neutron.getInstance().getProfileHandler().findDisplayName(secondPlace);
         String third = thirdPlace == null ? "N/A" : Neutron.getInstance().getProfileHandler().findDisplayName(thirdPlace);
         this.givePrizes(firstPlace, secondPlace, thirdPlace);
         List<String> tooltip = Arrays.asList(
            ChatColor.translate("&4&lRewards"),
            ChatColor.translate("&4&l┃ &f1st Place: &e&l2x Omega Chests &fand &b&l10x Airdrops"),
            ChatColor.translate("&4&l┃ &f2nd Place: &b&l10x Airdrops"),
            ChatColor.translate("&4&l┃ &f3rd Place: &6&l10x Halloween Keys")
         );
         if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            tooltip = Arrays.asList(
               ChatColor.translate("&4&lRewards"),
               ChatColor.translate("&4&l┃ &f1st Place: &4&l2x Treasure Chests &b&l5x Airdrops &fand &a&l1000 Gems"),
               ChatColor.translate("&4&l┃ &f2nd Place: &b&l3x Airdrops &fand &a&l1000 Gems"),
               ChatColor.translate("&4&l┃ &f3rd Place: &a&l500 Gems")
            );
         }

         for (Player onlinePlayer : server.getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█&4█████&7█ &4&lEvent Winners"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█&4█&7█████"));
            new FancyMessage(ChatColor.translate("&7█&4████&7██ &71st Place - &f" + first)).tooltip(tooltip).send(onlinePlayer);
            new FancyMessage(ChatColor.translate("&7█&4█&7█████ &72nd Place - &f" + second)).tooltip(tooltip).send(onlinePlayer);
            new FancyMessage(ChatColor.translate("&7█&4█████&7█ &73rd Place - &f" + third)).tooltip(tooltip).send(onlinePlayer);
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage("");
         }

         for (UUID uuid : this.getSortedList()) {
            Player target = server.getPlayer(uuid);
            if (target != null && target.isOnline() && this.points.containsKey(uuid)) {
               target.playSound(target.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
               target.sendMessage("");
               target.sendMessage(ChatColor.translate("&4&lEvent Summary"));
               target.sendMessage(ChatColor.translate("&7You placed &f" + NumberUtil.getOrdinal(this.getSortedList().indexOf(uuid) + 1) + " &7in the event!"));
               target.sendMessage(ChatColor.translate("&cTotal Points: &f" + this.points.get(uuid)));
               target.sendMessage("");
            }
         }

         this.points.clear();
      }
   }

   public void givePrizes(UUID firstPlace, UUID secondPlace, UUID thirdPlace) {
      Server server = Foxtrot.getInstance().getServer();
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         if (firstPlace != null) {
            server.dispatchCommand(
               server.getConsoleSender(), "chest give Omega " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 2"
            );
            server.dispatchCommand(
               server.getConsoleSender(), "airdrops give " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 10"
            );
         }

         if (secondPlace != null) {
            server.dispatchCommand(
               server.getConsoleSender(), "airdrops give " + Foxtrot.getInstance().getServer().getOfflinePlayer(secondPlace).getName() + " 10"
            );
         }

         if (thirdPlace != null) {
            server.dispatchCommand(
               server.getConsoleSender(), "cr givekey " + Foxtrot.getInstance().getServer().getOfflinePlayer(thirdPlace).getName() + " Seasonal 10"
            );
         }
      } else {
         if (firstPlace != null) {
            server.dispatchCommand(
               server.getConsoleSender(), "chest give Treasure " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 2"
            );
            server.dispatchCommand(
               server.getConsoleSender(), "airdrops give " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 10"
            );
            server.dispatchCommand(server.getConsoleSender(), "gems add " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 1000");
         }

         if (secondPlace != null) {
            server.dispatchCommand(
               server.getConsoleSender(), "airdrops give " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 5"
            );
            server.dispatchCommand(server.getConsoleSender(), "gems add " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 1000");
         }

         if (thirdPlace != null) {
            server.dispatchCommand(server.getConsoleSender(), "gems add " + Foxtrot.getInstance().getServer().getOfflinePlayer(firstPlace).getName() + " 500");
         }
      }
   }

   public int findPlacing(UUID uuid) {
      return !this.getSortedList().contains(uuid) ? this.getSortedList().size() + 1 : this.getSortedList().indexOf(uuid) + 1;
   }

   public LinkedHashMap<UUID, Integer> getSortedMap() {
      LinkedList<Entry<UUID, Integer>> list = new LinkedList<>(this.points.entrySet());
      list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      LinkedHashMap<UUID, Integer> sortedHashMap = new LinkedHashMap<>();

      for (Entry<UUID, Integer> entry : list) {
         sortedHashMap.put(entry.getKey(), entry.getValue());
      }

      return sortedHashMap;
   }

   public LinkedList<UUID> getSortedList() {
      return new LinkedList<>(this.getSortedMap().keySet());
   }

   public Map<UUID, Integer> getPoints() {
      return this.points;
   }

   public long getStarted() {
      return this.started;
   }

   public long getEndsAt() {
      return this.endsAt;
   }

   public BukkitTask getBukkitTask() {
      return this.bukkitTask;
   }
}
