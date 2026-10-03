package net.frozenorb.foxtrot.server.deathban;

import cc.fyre.proton.util.TimeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.server.deathban.listener.DeathbanArenaListener;
import net.frozenorb.foxtrot.server.deathban.listener.DeathbanListener;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

public class DeathbanArenaHandler {
   private Foxtrot instance;
   private Map<UUID, Integer> cache = new HashMap<>();
   private Map<UUID, Long> lifeCooldown = new HashMap<>();

   public DeathbanArenaHandler(Foxtrot instance) {
      this.instance = instance;
      if (!this.instance.getMapHandler().isKitMap()) {
         this.instance.getServer().getPluginManager().registerEvents(new DeathbanArenaListener(this.instance), this.instance);
         this.instance.getServer().getPluginManager().registerEvents(new DeathbanListener(this.instance), this.instance);
      }
   }

   public boolean isDeathbanArena(Player player) {
      return false;
   }

   public void deathban(Player player) {
      if (!this.instance.getMapHandler().isKitMap()) {
         if (player.getWorld().getEnvironment() == Environment.THE_END
            || DTRBitmask.CITADEL.appliesAt(player.getLocation())
            || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
            this.lifeCooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3L));
         } else if (player.getWorld().getEnvironment() == Environment.NETHER) {
            this.lifeCooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
         }

         player.removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
         Foxtrot.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
         LunarClientListener.updateNametag(player);
         World world = this.instance.getServer().getWorld("Deathban");
         player.teleport(world.getSpawnLocation().clone());
         player.sendMessage("");
         player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Deathban Arena");
         player.sendMessage(ChatColor.GRAY + "You are now entering the deathban arena!");
         player.sendMessage(ChatColor.GRAY + "PvP to kill time while waiting out your deathban!");
         player.sendMessage(ChatColor.GRAY + "Once you reach 5 kills you will be automatically revived.");
         player.sendMessage("");
         player.sendMessage(ChatColor.GREEN + "Click the sign to revive yourself using lives.");
         player.sendMessage("");
         this.cache.remove(player.getUniqueId());
      }
   }

   public void revive(UUID uuid) {
      if (!this.instance.getMapHandler().isKitMap()) {
         DeathbanMap var10000 = this.instance.getDeathbanMap();
         PendingReviveInventory.beforeRevive(uuid);
         var10000.revive(uuid);
         this.lifeCooldown.remove(uuid);
         this.cache.remove(uuid);
         Player player = this.instance.getServer().getPlayer(uuid);
         if (player != null) {
            clearReviveInventory(player);
            player.sendMessage(ChatColor.GREEN + "You have been revived!");
            this.instance
               .getServer()
               .getScheduler()
               .runTask(
                  this.instance,
                  () -> {
                     if (player.isOnline()
                        && Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId()) == player
                        && !Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
                        clearReviveInventory(player);
                        player.teleport(this.instance.getServerHandler().getSpawnLocation());
                        clearReviveInventory(player);
                        PendingReviveInventory.complete(player);
                     }
                  }
               );
            Bukkit.getScheduler().scheduleSyncDelayedTask(Foxtrot.getInstance(), () -> {
               player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
               Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), 1800);
            }, 20L);
         }
      }
   }

   public void startLifeCooldown(Player player) {
      if (player.getWorld().getEnvironment() == Environment.THE_END
         || DTRBitmask.CITADEL.appliesAt(player.getLocation())
         || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
         this.lifeCooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3L));
      } else if (player.getWorld().getEnvironment() == Environment.NETHER) {
         this.lifeCooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
      }
   }

   public boolean canUseLife(Player player) {
      return !this.instance.getServerHandler().isPreEOTW()
         && this.instance.getFriendLivesMap().getLives(player.getUniqueId()) > 0
         && this.lifeCooldown.getOrDefault(player.getUniqueId(), 0L) <= System.currentTimeMillis();
   }

   public void useLife(Player player) {
      UUID uuid = player.getUniqueId();
      int lives = this.instance.getFriendLivesMap().getLives(uuid);
      if (!this.instance.getDeathbanMap().isDeathbanned(uuid)) {
         player.sendMessage(ChatColor.RED + "You are not deathbanned!");
      } else if (this.instance.getServerHandler().isPreEOTW()) {
         player.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
      } else if (lives <= 0) {
         player.sendMessage(ChatColor.RED + "You don't have any lives!");
      } else {
         long cooldown = this.lifeCooldown.getOrDefault(uuid, 0L);
         if (cooldown > System.currentTimeMillis()) {
            int difference = (int)(cooldown - System.currentTimeMillis()) / 1000;
            player.sendMessage(
               ChatColor.translate(
                  "&cYou may not use a life for another &f"
                     + TimeUtils.formatIntoDetailedString(difference)
                     + " &cas you died in either Nether, End or an Event!"
               )
            );
         } else {
            this.instance.getFriendLivesMap().setLives(uuid, lives - 1);
            player.sendMessage(ChatColor.YELLOW + "You used a life. " + ChatColor.GRAY + "(" + (lives - 1) + " left)");
            this.revive(uuid);
         }
      }
   }

   public Map<UUID, Integer> getCache() {
      return this.cache;
   }

   public Map<UUID, Long> getLifeCooldown() {
      return this.lifeCooldown;
   }

   private static void clearReviveInventory(Player var0) {
      if (Bukkit.isPrimaryThread()) {
         var0.setItemOnCursor(null);
         Inventory var1 = var0.getOpenInventory().getTopInventory();
         if (var1 instanceof CraftingInventory) {
            var1.clear();
         }

         var0.closeInventory();
         InventoryUtils.resetInventoryNow(var0);
         var0.setItemInHand(null);
         var0.setItemOnCursor(null);
         var0.updateInventory();
      }
   }
}
