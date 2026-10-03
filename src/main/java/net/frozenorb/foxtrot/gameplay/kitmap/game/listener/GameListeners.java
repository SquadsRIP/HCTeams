package net.frozenorb.foxtrot.gameplay.kitmap.game.listener;

import cc.fyre.proton.util.PlayerUtils;
import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameItems;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa.FFAGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.knockout.KnockoutGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike.MineStrikeGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq.OITQGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle.ShuffleGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.spleef.SpleefGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag.TNTTagGame;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListeners implements Listener {
   private static final List<Material> CONTAINER_TYPES = Arrays.asList(
      Material.CHEST,
      Material.TRAPPED_CHEST,
      Material.ENDER_CHEST,
      Material.FURNACE,
      Material.BURNING_FURNACE,
      Material.DISPENSER,
      Material.HOPPER,
      Material.DROPPER,
      Material.BREWING_STAND
   );
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (player.getLocation().getWorld().getName().equals("kits_events")) {
         player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
      }
   }

   @EventHandler
   public void onPlayerInteractEvent(PlayerInteractEvent event) {
      if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
         && this.gameHandler.isOngoingGame()
         && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
         if (event.hasItem() && event.getItem().equals(GameItems.LEAVE_EVENT)) {
            if (this.gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
               this.gameHandler.getOngoingGame().removePlayer(event.getPlayer());
            } else if (this.gameHandler.getOngoingGame().isSpectating(event.getPlayer().getUniqueId())) {
               this.gameHandler.getOngoingGame().removeSpectator(event.getPlayer());
            }

            event.getPlayer().teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
         }

         if (event.getAction() == Action.RIGHT_CLICK_BLOCK && CONTAINER_TYPES.contains(event.getClickedBlock().getType())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onBlockBreakEvent(BlockBreakEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
         if (this.gameHandler.getOngoingGame() instanceof SpleefGame
            && this.gameHandler.getOngoingGame().isStarted()
            && this.gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
            event.setCancelled(System.currentTimeMillis() < this.gameHandler.getOngoingGame().getStartedAt() + 6000L);
            return;
         }

         event.setCancelled(true);
      } else if (event.getBlock().getWorld().equals(this.gameHandler.getWorld())) {
         event.setCancelled(!event.getPlayer().hasMetadata("build"));
      }
   }

   @EventHandler
   public void onBlockPlaceEvent(BlockPlaceEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
         event.setCancelled(true);
      } else if (event.getBlock().getWorld().equals(this.gameHandler.getWorld())) {
         event.setCancelled(!event.getPlayer().hasMetadata("build"));
      }
   }

   @EventHandler
   public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerBucketEmptyEvent(PlayerBucketFillEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
         if (this.gameHandler.getOngoingGame() instanceof TNTTagGame) {
            event.setCancelled(true);
            return;
         }

         if (this.gameHandler.getOngoingGame() instanceof FFAGame) {
            if (!this.gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
               event.setCancelled(true);
               return;
            }

            if (InventoryUtils.isArmor(event.getItemDrop().getItemStack())) {
               event.setCancelled(true);
               return;
            }

            event.setCancelled(false);
            return;
         }

         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
      if (event.getEntity() instanceof Player
         && this.gameHandler.isOngoingGame()
         && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getEntity().getUniqueId())) {
         event.setCancelled(true);
         ((Player)event.getEntity()).setFoodLevel(20);
      }
   }

   @EventHandler
   public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
      if (this.gameHandler.isOngoingGame() && event.getEntity() instanceof Player) {
         if (!event.getEntity().getLocation().getWorld().getName().equals("kits_events")) {
            return;
         }

         if (this.gameHandler.getOngoingGame() instanceof SpleefGame && event.getDamager() instanceof Snowball) {
            return;
         }

         Game ongoingGame = this.gameHandler.getOngoingGame();
         if (this.gameHandler.getOngoingGame() instanceof TNTTagGame && ongoingGame.isStarted()) {
            return;
         }

         if (this.gameHandler.getOngoingGame() instanceof OITQGame && ongoingGame.isStarted()) {
            return;
         }

         if (this.gameHandler.getOngoingGame() instanceof MineStrikeGame && ongoingGame.isStarted()) {
            return;
         }

         if (this.gameHandler.getOngoingGame() instanceof KnockoutGame && ongoingGame.isStarted()) {
            event.setDamage(0.0);
            return;
         }

         Player victim = (Player)event.getEntity();
         Player damager = PlayerUtils.getDamageSource(event.getDamager());
         if (damager == null) {
            return;
         }

         boolean victimInGame = ongoingGame.isPlayingOrSpectating(victim.getUniqueId());
         boolean damagerInGame = ongoingGame.isPlayingOrSpectating(damager.getUniqueId());
         if (!victimInGame && !damagerInGame) {
            return;
         }

         if (victimInGame && damagerInGame) {
            ongoingGame.handleDamage(victim, damager, event);
         } else {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onEntityDamageEvent(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.gameHandler.isOngoingGame()) {
            if (this.gameHandler.getOngoingGame().isPlaying(player.getUniqueId()) && player.getWorld().getName().equalsIgnoreCase("kits_events")) {
               if (this.gameHandler.getOngoingGame() instanceof TNTTagGame) {
                  if (this.gameHandler.getOngoingGame().isStarted()) {
                     if (event.getCause() == DamageCause.ENTITY_ATTACK) {
                        event.setDamage(0.0);
                     } else {
                        event.setCancelled(true);
                     }
                  } else {
                     event.setCancelled(true);
                  }
               }

               if (this.gameHandler.getOngoingGame() instanceof MineStrikeGame || this.gameHandler.getOngoingGame() instanceof KnockoutGame) {
                  if (event.getCause() == DamageCause.FALL) {
                     event.setCancelled(true);
                  }

                  return;
               }

               if (this.gameHandler.getOngoingGame() instanceof OITQGame) {
                  OITQGame oitqGame = (OITQGame)this.gameHandler.getOngoingGame();
                  if (event.getCause() == DamageCause.FALL) {
                     event.setCancelled(true);
                     return;
                  }

                  if (oitqGame.isStarted() && event.getCause() == DamageCause.PROJECTILE) {
                     event.setDamage(0.0);
                     return;
                  }

                  if (!oitqGame.isStarted()) {
                     event.setCancelled(true);
                  }

                  return;
               }

               if (!(event instanceof EntityDamageByEntityEvent)
                  && !(this.gameHandler.getOngoingGame() instanceof FFAGame)
                  && !(this.gameHandler.getOngoingGame() instanceof TNTTagGame)) {
                  if (this.gameHandler.getOngoingGame() instanceof ShuffleGame) {
                     if (((ShuffleGame)this.gameHandler.getOngoingGame()).currentRound >= 10) {
                        event.setDamage(0.0);
                     } else {
                        event.setCancelled(true);
                     }
                  } else {
                     event.setCancelled(true);
                  }
               }
            } else if (this.gameHandler.getOngoingGame().isSpectating(player.getUniqueId())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onInventoryClickEvent(InventoryClickEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getWhoClicked().getUniqueId())) {
         if (this.gameHandler.getOngoingGame() instanceof FFAGame || this.gameHandler.getOngoingGame() instanceof TNTTagGame) {
            if (!this.gameHandler.getOngoingGame().isPlaying(event.getWhoClicked().getUniqueId())) {
               event.setCancelled(true);
               return;
            }

            event.setCancelled(event.getSlotType() == SlotType.ARMOR);
            return;
         }

         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerQuitEvent(PlayerQuitEvent event) {
      if (this.gameHandler.isOngoingGame()) {
         Game ongoingGame = this.gameHandler.getOngoingGame();
         if (ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
            this.gameHandler.getOngoingGame().eliminatePlayer(event.getPlayer(), null);
         } else if (ongoingGame.isSpectating(event.getPlayer().getUniqueId())) {
            this.gameHandler.getOngoingGame().removeSpectator(event.getPlayer());
         }
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (this.gameHandler.isOngoingGame()) {
         if (this.gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            if (event.getAction() == Action.PHYSICAL
               && (event.getClickedBlock().getType() == Material.CROPS || event.getClickedBlock().getType() == Material.SOIL)) {
               event.setCancelled(true);
            }
         }
      }
   }
}
