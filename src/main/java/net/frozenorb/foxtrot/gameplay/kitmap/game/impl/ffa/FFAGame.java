package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa;

import cc.fyre.neutron.Neutron;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FFAGame extends Game {
   private boolean started;
   private Map<UUID, Integer> kills = new HashMap<>();

   public FFAGame(UUID host, List<GameArena> arenaOptions) {
      super(host, GameType.FFA, arenaOptions);
   }

   @Override
   public void startGame() {
      super.startGame();
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (FFAGame.this.state == GameState.ENDED) {
               this.cancel();
            } else {
               if (FFAGame.this.state == GameState.RUNNING) {
                  FFAGame.this.startFFA();
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
   }

   @Override
   public void endGame() {
      super.endGame();
      this.started = false;
      if (this.winningPlayer != null) {
         this.sendMessages(
            this.winningPlayer.getDisplayName()
               + ChatColor.YELLOW
               + " won the FFA with "
               + ChatColor.DARK_RED
               + this.kills.get(this.winningPlayer.getUniqueId())
               + ChatColor.YELLOW
               + " kills."
         );
      }

      if (this.getVotedArena().getBounds() != null) {
         List<Chunk> arenaChunks = this.getVotedArena().getBounds().getChunks();
         arenaChunks.stream().filter(chunk -> !chunk.isLoaded()).forEach(Chunk::load);
         arenaChunks.forEach(chunk -> {
            for (Entity entity : chunk.getEntities()) {
               if (entity instanceof Item && this.getVotedArena().getBounds().contains(entity.getLocation())) {
                  entity.remove();
               }
            }
         });
      }

      (new BukkitRunnable() {
         @Override
         public void run() {
            FFAGame.this.kills.clear();
         }
      }).runTaskLater(Foxtrot.getInstance(), 100L);
   }

   private void startFFA() {
      DefaultKit pvpKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("PvP");

      for (Player player : this.getPlayers()) {
         InventoryUtils.resetInventoryNow(player);
         player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
         pvpKit.apply(player);
      }

      int midIndex = (this.getPlayers().size() - 1) / 2;
      List<List<Player>> split = new ArrayList<>(
         this.getPlayers().stream().collect(Collectors.partitioningBy(s -> this.getPlayers().indexOf(s) > midIndex)).values()
      );
      split.get(0).forEach(player -> player.teleport(this.getVotedArena().getPointA()));
      split.get(1).forEach(player -> player.teleport(this.getVotedArena().getPointB()));
      this.started = false;
      this.setStartedAt(System.currentTimeMillis());
      (new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
               if (FFAGame.this.state != GameState.ENDED && !FFAGame.this.started) {
                  this.i--;
                  FFAGame.this.sendSound(Sound.NOTE_PLING, 1.0F, this.i == 0 ? 2.0F : 1.0F);
                  FFAGame.this.started = this.i == 0;
                  if (this.i == 0) {
                     FFAGame.this.sendMessages("&6&l" + FFAGame.this.getGameType().getDisplayName() + " &eevent has begun!");
                  } else {
                     FFAGame.this.sendMessages(
                        "&6&l"
                           + FFAGame.this.getGameType().getDisplayName()
                           + " &eevent begins in &f"
                           + this.i
                           + " second"
                           + (this.i == 1 ? "" : "s")
                           + "&e..."
                     );
                  }
               } else {
                  this.cancel();
               }
            }
         })
         .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   @Override
   public void addPlayer(Player player) throws IllegalStateException {
      super.addPlayer(player);
      this.kills.putIfAbsent(player.getUniqueId(), 0);
   }

   @Override
   public void removePlayer(Player player) {
      super.removePlayer(player);
   }

   @Override
   public void eliminatePlayer(Player player, Player killer) {
      super.eliminatePlayer(player, killer);
      if (killer != null) {
         this.sendMessages(
            ChatColor.DARK_RED.toString()
               + player.getName()
               + ChatColor.YELLOW
               + " has been eliminated by "
               + ChatColor.DARK_RED
               + killer.getName()
               + ChatColor.YELLOW
               + "! "
               + ChatColor.GRAY
               + "("
               + this.getPlayers().size()
               + "/"
               + this.getStartedWith()
               + " players remaining)"
         );
         this.kills.put(killer.getUniqueId(), this.kills.get(killer.getUniqueId()) + 1);
         ItemStack crapples = new ItemStack(Material.GOLDEN_APPLE, 2);
         if (killer.getInventory().firstEmpty() == -1
            && Stream.of(killer.getInventory().getContents()).filter(Objects::nonNull).noneMatch(item -> item.isSimilar(crapples))) {
            killer.getInventory().setItem(7, crapples);
         } else {
            player.getInventory().addItem(crapples);
         }

         killer.setHealth(killer.getMaxHealth());
         killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 160, 0));
         killer.updateInventory();
      }

      this.removeSpectator(player);
      if (this.players.size() == 1) {
         this.endGame();
      }
   }

   public boolean isGracePeriod() {
      return this.getStartedAt() == null || System.currentTimeMillis() <= this.getStartedAt() + 6000L;
   }

   @Override
   public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
      if (this.isGracePeriod()) {
         event.setCancelled(true);
      } else {
         if (this.state == GameState.RUNNING) {
            if (!this.isPlaying(victim.getUniqueId()) && !this.isPlaying(damager.getUniqueId())) {
               event.setCancelled(true);
            }
         } else {
            event.setCancelled(true);
         }
      }
   }

   public List<Player> getTopThree() {
      return this.getPlayers()
         .stream()
         .sorted(Comparator.<Player>comparingInt(p -> this.kills.get(p.getUniqueId())).reversed())
         .limit(3L)
         .collect(Collectors.toList());
   }

   @Override
   public Player findWinningPlayer() {
      return this.players.size() == 1 ? Bukkit.getPlayer(this.players.iterator().next()) : null;
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
      } else if (this.state == GameState.RUNNING) {
         lines.add(" &6Remaining: &f" + this.players.size() + "&7/&f" + this.getStartedWith());
         int potions = ItemUtils.countStacksMatching(player.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
         lines.add(" &6Pots: &f" + potions);
         lines.add(" &6Kills: &f" + this.kills.get(player.getUniqueId()));
      } else {
         if (this.winningPlayer == null) {
            lines.add("&6Winner: &fNone");
         } else {
            lines.add("&6Winner: &f" + this.winningPlayer.getName());
         }

         lines.add("&6Kills: &f" + this.kills.get(player.getUniqueId()));
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
            .then("████")
            .color(ChatColor.DARK_RED)
            .then("██")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█████")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█████")
            .color(ChatColor.GRAY)
            .then(" Click to join the event")
            .color(ChatColor.GREEN)
            .command("/game join")
            .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW))
            .then("]")
            .color(ChatColor.GRAY),
         new FancyMessage("███████").color(ChatColor.GRAY)
      );
   }

   public Map<UUID, Integer> getKills() {
      return this.kills;
   }

   @Override
   public boolean isStarted() {
      return this.started;
   }
}
