package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo;

import cc.fyre.neutron.Neutron;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameUtils;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SumoGame extends Game {
   private Player playerA;
   private Player playerB;
   private Map<UUID, Integer> roundsPlayed = new HashMap<>();
   private int currentRound = 0;

   public SumoGame(UUID host, List<GameArena> arenaOptions) {
      super(host, GameType.SUMO, arenaOptions);
   }

   @Override
   public void startGame() {
      super.startGame();
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (SumoGame.this.state == GameState.ENDED) {
               this.cancel();
            } else {
               if (SumoGame.this.state == GameState.RUNNING) {
                  SumoGame.this.determineNextPlayers();
                  SumoGame.this.startRound();
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
   }

   @Override
   public void gameBegun() {
      for (Player player : this.getPlayers()) {
         player.teleport(this.getVotedArena().getSpectatorSpawn());
      }
   }

   public void startRound() {
      if (this.playerA != null && this.playerB != null) {
         this.currentRound++;
         this.setStartedAt(System.currentTimeMillis());
         InventoryUtils.resetInventoryNow(this.playerA);
         this.playerA.teleport(this.getVotedArena().getPointA());
         InventoryUtils.resetInventoryNow(this.playerB);
         this.playerB.teleport(this.getVotedArena().getPointB());
         (new BukkitRunnable() {
               private int i = 6;

               @Override
               public void run() {
                  if (SumoGame.this.state == GameState.ENDED) {
                     this.cancel();
                  } else {
                     this.i--;
                     if (this.i == 0) {
                        SumoGame.this.sendMessages(ChatColor.GREEN + "The round has started!");
                        SumoGame.this.sendSound(Sound.NOTE_PLING, 1.0F, 2.0F);
                     } else {
                        SumoGame.this.sendMessages(
                           ChatColor.GOLD + "The round is starting in " + ChatColor.WHITE + this.i + " second" + (this.i == 1 ? "" : "s") + "&6..."
                        );
                        SumoGame.this.sendSound(Sound.NOTE_PLING, 1.0F, 1.0F);
                     }

                     if (this.i <= 0) {
                        this.cancel();
                     }
                  }
               }
            })
            .runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
      } else {
         throw new IllegalStateException("Cannot start round without both players");
      }
   }

   public void endRound() {
      if (this.players.size() == 1) {
         this.endGame();
      } else {
         if (this.playerA != null) {
            this.playerA.teleport(this.getVotedArena().getSpectatorSpawn());
            GameUtils.resetPlayer(this.playerA);
            this.playerA = null;
         }

         if (this.playerB != null) {
            this.playerB.teleport(this.getVotedArena().getSpectatorSpawn());
            GameUtils.resetPlayer(this.playerB);
            this.playerB = null;
         }

         Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            this.determineNextPlayers();
            this.startRound();
         }, 50L);
      }
   }

   @Override
   public void eliminatePlayer(Player player, Player killer) {
      if (this.state != GameState.ENDED) {
         super.eliminatePlayer(player, killer);
         if (killer != null) {
            this.sendMessages(
               player.getDisplayName()
                  + ChatColor.GOLD
                  + " has been eliminated by &f"
                  + killer.getDisplayName()
                  + "&6[&f"
                  + this.getPlayers().size()
                  + "&7/&f"
                  + this.getStartedWith()
                  + "&6]"
            );
         }

         if (this.isCurrentlyFighting(player)) {
            if (this.playerA.getUniqueId() == player.getUniqueId()) {
               this.playerA = null;
            } else if (this.playerB.getUniqueId() == player.getUniqueId()) {
               this.playerB = null;
            }

            this.endRound();
         }
      }
   }

   public boolean isCurrentlyFighting(Player player) {
      return this.playerA != null && this.playerA.getUniqueId() == player.getUniqueId()
         || this.playerB != null && this.playerB.getUniqueId() == player.getUniqueId();
   }

   public Player getOpponent(Player player) {
      if (this.playerA != null && this.playerA.getUniqueId() == player.getUniqueId()) {
         return this.playerB;
      } else {
         return this.playerB != null && this.playerB.getUniqueId() == player.getUniqueId() ? this.playerA : null;
      }
   }

   public void determineNextPlayers() {
      List<Player> players = this.getPlayers()
         .stream()
         .sorted(Comparator.comparingInt(player -> this.roundsPlayed.getOrDefault(player.getUniqueId(), 0)))
         .collect(Collectors.toList());
      this.playerA = players.get(0);
      this.playerB = players.get(1);
      this.roundsPlayed.putIfAbsent(this.playerA.getUniqueId(), 0);
      this.roundsPlayed.put(this.playerA.getUniqueId(), this.roundsPlayed.get(this.playerA.getUniqueId()) + 1);
      this.roundsPlayed.putIfAbsent(this.playerB.getUniqueId(), 0);
      this.roundsPlayed.put(this.playerB.getUniqueId(), this.roundsPlayed.get(this.playerB.getUniqueId()) + 1);
      this.sendMessages(
         ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Next Round",
         this.playerA.getDisplayName() + ChatColor.GRAY + " vs. " + ChatColor.RESET + this.playerB.getDisplayName()
      );
   }

   public double getDeathHeight() {
      return Math.min(this.getVotedArena().getPointA().getBlockY(), this.getVotedArena().getPointB().getBlockY()) - 2.9;
   }

   @Override
   public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
      if (this.state != GameState.RUNNING || !victim.getWorld().getName().equalsIgnoreCase("kits_events")) {
         event.setCancelled(true);
      } else if (this.isPlaying(victim.getUniqueId()) && this.isPlaying(damager.getUniqueId())) {
         event.setDamage(0.0);
         if (this.isCurrentlyFighting(victim) && this.isCurrentlyFighting(damager)) {
            victim.setHealth(victim.getMaxHealth());
            victim.updateInventory();
         } else {
            event.setCancelled(true);
         }
      } else {
         event.setCancelled(true);
      }
   }

   @Override
   public Player findWinningPlayer() {
      return this.playerA == null ? this.playerB : this.playerA;
   }

   @Override
   public void getScoreboardLines(Player player, LinkedList<String> lines) {
      lines.add("&6&l" + this.getGameType().getDisplayName() + ":");
      if (this.state == GameState.WAITING) {
         lines.add(" &6Players: &f" + this.players.size() + "&7/&f" + this.getMaxPlayers());
         if (this.getVotedArena() != null) {
            lines.add(" &6Map: &f" + this.getVotedArena().getName());
         } else {
            lines.add("");
            lines.add(" &6Map Vote");
            this.getArenaOptions()
               .entrySet()
               .stream()
               .sorted((o1, o2) -> o2.getValue().get())
               .forEach(
                  entry -> lines.add(
                     "&7» "
                        + (this.getPlayerVotes().getOrDefault(player.getUniqueId(), null) == entry.getKey() ? "&l" : "")
                        + entry.getKey().getName()
                        + " &7("
                        + entry.getValue().get()
                        + ")"
                  )
               );
         }

         if (this.getStartedAt() == null) {
            int playersNeeded = this.getGameType().getMinPlayers() - this.getPlayers().size();
            lines.add("");
            lines.add("&cWaiting for " + playersNeeded + " player" + (playersNeeded == 1 ? "" : "s"));
         } else {
            float remainingSeconds = (float)(this.getStartedAt() - System.currentTimeMillis()) / 1000.0F;
            lines.add("&aStarting in " + Math.round(10.0 * remainingSeconds) / 10.0 + "s");
         }
      } else if (this.state != GameState.RUNNING) {
         if (this.winningPlayer == null) {
            lines.add(" &6Winner: &fNone");
         } else {
            lines.add(" &6Winner: &f" + this.winningPlayer.getName());
         }

         lines.add(" &6Rounds: &f" + this.currentRound);
      } else {
         lines.add(" &6Remaining: &f" + this.players.size() + "&7/&f" + this.getStartedWith());
         lines.add(" &6Round: &f" + this.currentRound);
         if (this.playerA != null && this.playerB != null) {
            lines.add("");
            int namesLength = this.playerA.getName().length() + this.playerB.getName().length();
            if (namesLength <= 20) {
               lines.add(this.playerA.getName() + " &7vs. &r" + this.playerB.getName());
            } else {
               lines.add(this.playerA.getName());
               lines.add("&7vs.");
               lines.add(this.playerB.getName());
            }
         } else {
            lines.add("");
            lines.add("&eSelecting new players...");
         }
      }
   }

   @Override
   public List<FancyMessage> createHostNotification() {
      return Arrays.asList(
         new FancyMessage("███████").color(ChatColor.GRAY),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█████")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(" " + this.getGameType().getDisplayName() + " Event")
            .color(ChatColor.DARK_RED)
            .style(ChatColor.BOLD),
         new FancyMessage("").then("█").color(ChatColor.GRAY).then("█").color(ChatColor.DARK_RED).then("█████").color(ChatColor.GRAY),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█████")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
         new FancyMessage("")
            .then("█████")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█████")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(" Click to join the event")
            .color(ChatColor.GREEN)
            .command("/game join")
            .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW)),
         new FancyMessage("███████").color(ChatColor.GRAY)
      );
   }

   public Player getPlayerA() {
      return this.playerA;
   }

   public Player getPlayerB() {
      return this.playerB;
   }

   public Map<UUID, Integer> getRoundsPlayed() {
      return this.roundsPlayed;
   }

   public int getCurrentRound() {
      return this.currentRound;
   }
}
