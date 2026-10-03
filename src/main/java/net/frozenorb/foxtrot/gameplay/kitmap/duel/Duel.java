package net.frozenorb.foxtrot.gameplay.kitmap.duel;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.util.PlayerUtils;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.arena.DuelArena;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Duel {
   @NonNull
   private UUID player1;
   @NonNull
   private UUID player2;
   @NonNull
   private DuelArena arena;
   private final int wager;
   private DuelState state;
   private long startedAt = -1L;
   private long endedAt = -1L;
   private UUID winner;

   public void setup() {
      this.state = DuelState.COUNTDOWN;

      for (Player other : Bukkit.getOnlinePlayers()) {
         for (Player player : this.getPlayers()) {
            if (!this.contains(other)) {
               player.hidePlayer(other);
               other.hidePlayer(player);
            }
         }
      }

      for (Player player : this.getPlayers()) {
         player.teleport(player.getUniqueId().equals(this.player1) ? this.arena.getPointA() : this.arena.getPointB());
         PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
         DefaultKit pvpKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Diamond");
         if (pvpKit != null) {
            pvpKit.apply(player);
         } else {
            player.sendMessage(ChatColor.RED + "Failed to find Diamond kit.");
         }
      }

      (new BukkitRunnable() {
            int timer = 5;

            @Override
            public void run() {
               if (Duel.this.state == DuelState.FINISHED) {
                  this.cancel();
               } else if (this.timer == 0) {
                  this.cancel();
                  Duel.this.playSound(Sound.NOTE_PLING, 2.0F);
                  Duel.this.start();
               } else {
                  Duel.this.sendMessage(
                     ChatColor.GRAY + "Match will begin in " + ChatColor.RED + this.timer + ChatColor.GRAY + " second" + (this.timer != 1 ? "s" : "") + "!"
                  );
                  Duel.this.playSound(Sound.NOTE_PLING, 1.0F);
                  this.timer--;
               }
            }
         })
         .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   public void start() {
      this.state = DuelState.FIGHTING;
      this.startedAt = System.currentTimeMillis();
      this.sendMessage(ChatColor.GREEN + "The match has begun.");
   }

   public void end(Player winner) {
      if (this.state != DuelState.FINISHED) {
         this.state = DuelState.FINISHED;
         this.endedAt = System.currentTimeMillis();
         this.winner = winner.getUniqueId();
         Player loser = Bukkit.getPlayer(this.getOpponent(winner.getUniqueId()));
         Foxtrot.getInstance().getGemMap().addGems(winner.getUniqueId(), this.wager * 2, true);
         Foxtrot.getInstance().getServer().broadcastMessage("");
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Duels");
         Foxtrot.getInstance()
            .getServer()
            .broadcastMessage(
               winner.getDisplayName()
                  + ChatColor.RED
                  + " has won a duel against "
                  + ChatColor.WHITE
                  + loser.getDisplayName()
                  + ChatColor.RED
                  + " for "
                  + ChatColor.GREEN
                  + ChatColor.BOLD
                  + this.wager
                  + " Gems"
                  + ChatColor.RED
                  + "!"
            );
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "Duel a player by typing /duel <player>");
         Foxtrot.getInstance().getServer().broadcastMessage("");
         int winnerPots = ItemUtils.countStacksMatching(winner.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
         int loserPots = ItemUtils.countStacksMatching(loser.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
         winner.sendMessage("");
         winner.sendMessage(ChatColor.GREEN + loser.getName() + " had " + loserPots + " potions left.");
         winner.sendMessage("");
         loser.sendMessage("");
         loser.sendMessage(ChatColor.GREEN + winner.getName() + " had " + winnerPots + " potions left.");
         loser.sendMessage("");
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this::finish, 60L);
      }
   }

   private void finish() {
      DuelHandler duelHandler = Foxtrot.getInstance().getMapHandler().getDuelHandler();

      for (Player online : Bukkit.getOnlinePlayers()) {
         for (Player player : this.getPlayers()) {
            if (!ModHandler.INSTANCE.isInVanish(player.getUniqueId())) {
               player.showPlayer(online);
            }

            online.showPlayer(player);
         }
      }

      duelHandler.removeDuel(this);
      this.getPlayers().forEach(playerx -> {
         SpawnTagHandler.removeTag(playerx);
         PlayerUtils.resetInventory(playerx);
         playerx.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
      });
   }

   public void disconnect(Player player) {
      this.end(Bukkit.getPlayer(this.getOpponent(player.getUniqueId())));
      PlayerUtils.resetInventory(player);
      player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
   }

   public void eliminate(Player player) {
      this.end(Bukkit.getPlayer(this.getOpponent(player.getUniqueId())));
      PlayerUtils.resetInventory(player);
   }

   public void sendMessage(String message) {
      this.getPlayers().forEach(player -> player.sendMessage(message));
   }

   public void playSound(Sound sound, float pitch) {
      this.getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, pitch));
   }

   public UUID getOpponent(UUID uuid) {
      return uuid.equals(this.player1) ? this.player2 : this.player1;
   }

   public boolean contains(Player player) {
      return player.getUniqueId().equals(this.player1) || player.getUniqueId().equals(this.player2);
   }

   public List<Player> getPlayers() {
      return Stream.of(Bukkit.getPlayer(this.player1), Bukkit.getPlayer(this.player2)).filter(Objects::nonNull).collect(Collectors.toList());
   }

   @ConstructorProperties({"player1", "player2", "arena", "wager"})
   public Duel(@NonNull UUID player1, @NonNull UUID player2, @NonNull DuelArena arena, int wager) {
      if (player1 == null) {
         throw new NullPointerException("player1");
      }

      if (player2 == null) {
         throw new NullPointerException("player2");
      }

      if (arena == null) {
         throw new NullPointerException("arena");
      }

      this.player1 = player1;
      this.player2 = player2;
      this.arena = arena;
      this.wager = wager;
   }

   @NonNull
   public UUID getPlayer1() {
      return this.player1;
   }

   @NonNull
   public UUID getPlayer2() {
      return this.player2;
   }

   @NonNull
   public DuelArena getArena() {
      return this.arena;
   }

   public int getWager() {
      return this.wager;
   }

   public DuelState getState() {
      return this.state;
   }

   public long getStartedAt() {
      return this.startedAt;
   }

   public long getEndedAt() {
      return this.endedAt;
   }

   public UUID getWinner() {
      return this.winner;
   }
}
