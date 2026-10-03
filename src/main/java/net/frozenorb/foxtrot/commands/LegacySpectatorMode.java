package net.frozenorb.foxtrot.commands;

import cc.fyre.modsuite.mod.ModHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.PluginDisableEvent;

public final class LegacySpectatorMode implements Listener {
   private static final Map<UUID, LegacySpectatorMode.State> ACTIVE = new HashMap<>();

   public static void toggle(Player player) {
      if (isActive(player)) {
         exit(player);
         player.sendMessage(ChatColor.GREEN + "Spectator mode disabled.");
      } else {
         EntityPlayer handle = ((CraftPlayer)player).getHandle();
         if (handle.playerConnection.networkManager.getVersion() < 47) {
            player.sendMessage(ChatColor.RED + "Join with a Minecraft 1.8 client to use spectator mode.");
         } else if (player.isDead()) {
            player.sendMessage(ChatColor.RED + "Respawn before entering spectator mode.");
         } else {
            LegacySpectatorMode.State state = new LegacySpectatorMode.State(player, handle);
            player.setGameMode(GameMode.CREATIVE);
            if (player.getGameMode() != GameMode.CREATIVE) {
               player.sendMessage(ChatColor.RED + "Your gamemode change was blocked.");
            } else {
               player.leaveVehicle();
               player.closeInventory();
               ACTIVE.put(player.getUniqueId(), state);
               apply(player);

               for (Player viewer : Bukkit.getOnlinePlayers()) {
                  hide(viewer, player, state);
               }

               player.sendMessage(ChatColor.GREEN + "Spectator mode enabled. Use /gmsp to return to your starting position.");
            }
         }
      }
   }

   public static boolean isActive(Player player) {
      return ACTIVE.containsKey(player.getUniqueId());
   }

   private static void apply(Player player) {
      EntityPlayer handle = ((CraftPlayer)player).getHandle();
      handle.X = true;
      handle.allowServerSidePhase = true;
      player.spigot().setCollidesWithEntities(false);
      player.setCanPickupItems(false);
      player.setAllowFlight(true);
      player.setFlying(true);
      handle.playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, 3.0F));
   }

   private static void hide(Player viewer, Player player, LegacySpectatorMode.State state) {
      if (!viewer.equals(player) && viewer.canSee(player)) {
         viewer.hidePlayer(player);
         state.hiddenFrom.add(viewer.getUniqueId());
      }
   }

   public static void exit(Player player) {
      LegacySpectatorMode.State state = ACTIVE.remove(player.getUniqueId());
      if (state != null) {
         restore(player, state, state.gameMode);
      }
   }

   private static void restore(Player player, LegacySpectatorMode.State state, GameMode gameMode) {
      player.teleport(state.location);
      EntityPlayer handle = ((CraftPlayer)player).getHandle();
      handle.X = state.noClip;
      handle.allowServerSidePhase = state.phase;
      player.spigot().setCollidesWithEntities(state.collides);
      player.setCanPickupItems(state.pickup);
      player.setGameMode(gameMode);
      boolean originalMode = player.getGameMode() == state.gameMode;
      player.setAllowFlight(originalMode ? state.allowFlight : player.getGameMode() == GameMode.CREATIVE);
      player.setFlying(player.getAllowFlight() && originalMode && state.flying);
      handle.playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, player.getGameMode().getValue()));

      for (UUID viewerId : state.hiddenFrom) {
         Player viewer = Bukkit.getPlayer(viewerId);
         if (viewer != null && !ModHandler.INSTANCE.isInVanish(player.getUniqueId())) {
            viewer.showPlayer(player);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent event) {
      for (Entry<UUID, LegacySpectatorMode.State> entry : ACTIVE.entrySet()) {
         Player player = Bukkit.getPlayer(entry.getKey());
         if (player != null) {
            hide(event.getPlayer(), player, entry.getValue());
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onQuit(PlayerQuitEvent event) {
      exit(event.getPlayer());

      for (LegacySpectatorMode.State state : ACTIVE.values()) {
         state.hiddenFrom.remove(event.getPlayer().getUniqueId());
      }
   }

   @EventHandler
   public void onDisable(PluginDisableEvent event) {
      if (event.getPlugin() == Foxtrot.getInstance()) {
         for (UUID uuid : new HashSet<>(ACTIVE.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
               exit(player);
            }
         }

         ACTIVE.clear();
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onTeleport(PlayerTeleportEvent event) {
      LegacySpectatorMode.State state = ACTIVE.get(event.getPlayer().getUniqueId());
      if (state != null) {
         Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
            if (event.getPlayer().isOnline() && ACTIVE.get(event.getPlayer().getUniqueId()) == state) {
               apply(event.getPlayer());
            }
         });
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onGameModeChange(PlayerGameModeChangeEvent event) {
      LegacySpectatorMode.State state = ACTIVE.get(event.getPlayer().getUniqueId());
      if (state != null) {
         Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
            Player player = event.getPlayer();
            if (player.isOnline() && ACTIVE.get(player.getUniqueId()) == state) {
               ACTIVE.remove(player.getUniqueId());
               restore(player, state, player.getGameMode());
            }
         });
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player && isActive((Player)event.getEntity())) {
         event.setCancelled(true);
      }

      if (event instanceof EntityDamageByEntityEvent) {
         Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
         if (damager instanceof Player && isActive((Player)damager)) {
            event.setCancelled(true);
         }

         if (damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Player && isActive((Player)((Projectile)damager).getShooter())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onTarget(EntityTargetEvent event) {
      if (event.getTarget() instanceof Player && isActive((Player)event.getTarget())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onInteract(PlayerInteractEvent event) {
      if (isActive(event.getPlayer())) {
         event.setUseInteractedBlock(Result.DENY);
         event.setUseItemInHand(Result.DENY);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onInteractEntity(PlayerInteractEntityEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onBreak(BlockBreakEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPlace(BlockPlaceEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onBucketFill(PlayerBucketFillEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onBucketEmpty(PlayerBucketEmptyEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onDrop(PlayerDropItemEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPickup(PlayerPickupItemEvent event) {
      if (isActive(event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player && isActive((Player)event.getWhoClicked())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onInventoryDrag(InventoryDragEvent event) {
      if (event.getWhoClicked() instanceof Player && isActive((Player)event.getWhoClicked())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onFlight(PlayerToggleFlightEvent event) {
      if (isActive(event.getPlayer()) && !event.isFlying()) {
         event.setCancelled(true);
      }
   }

   private static final class State {
      private final GameMode gameMode;
      private final Location location;
      private final boolean allowFlight;
      private final boolean flying;
      private final boolean collides;
      private final boolean pickup;
      private final boolean noClip;
      private final boolean phase;
      private final Set<UUID> hiddenFrom = new HashSet<>();

      private State(Player player, EntityPlayer handle) {
         this.gameMode = player.getGameMode();
         this.location = player.getLocation();
         this.allowFlight = player.getAllowFlight();
         this.flying = player.isFlying();
         this.collides = player.spigot().getCollidesWithEntities();
         this.pickup = player.getCanPickupItems();
         this.noClip = handle.X;
         this.phase = handle.allowServerSidePhase;
      }
   }
}
