package net.frozenorb.foxtrot.gameplay.kitmap.game;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.gameplay.kitmap.game.menu.MapVoteMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.ItemUtils;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Game {
   private final UUID host;
   private final GameType gameType;
   protected GameState state = GameState.WAITING;
   private Long nextAnnouncement = System.currentTimeMillis();
   private boolean hasExpired;
   private Long startedAt;
   private boolean started = false;
   private int startedWith;
   private int maxPlayers;
   private boolean gemRequiredToJoin = true;
   private boolean hostForceStarted;
   private int voteStart;
   protected Set<UUID> players = new HashSet<>();
   private Set<UUID> spectators = new HashSet<>();
   protected Set<UUID> usedMessage = new HashSet<>();
   private Set<UUID> playerGems = new HashSet<>();
   private Map<GameArena, AtomicInteger> arenaOptions = new HashMap<>();
   private Map<UUID, GameArena> playerVotes = new HashMap<>();
   private GameArena votedArena;
   protected Player winningPlayer;

   public Game(UUID host, GameType gameType, List<GameArena> arenaOptions) {
      this.host = host;
      this.gameType = gameType;
      this.maxPlayers = gameType.getMaxPlayers();
      if (arenaOptions.size() == 1) {
         this.votedArena = arenaOptions.get(0);
      } else {
         for (GameArena arena : arenaOptions) {
            this.arenaOptions.put(arena, new AtomicInteger(0));
         }
      }
   }

   public void hostForceStart() {
      if (!this.hostForceStarted && this.host != null) {
         this.hostForceStarted = true;
         this.sendMessages("", ChatColor.GREEN + "The event has been started by " + Proton.getInstance().getUuidCache().name(this.host) + "!", "");
      }
   }

   public void forceStart() {
      this.state = GameState.RUNNING;
      this.startedWith = this.players.size();
      this.gameBegun();
      this.sendMessages("", ChatColor.GOLD + "The event has been forcefully started by an administrator!", "");
   }

   public void startGame() {
      List<UUID> list = new ArrayList<>(this.players);
      Collections.shuffle(list);
      this.players = new HashSet<>(list);

      for (Player player : this.getPlayersAndSpectators()) {
         player.teleport(this.votedArena.getSpectatorSpawn());
      }

      (new BukkitRunnable() {
            @Override
            public void run() {
               if (Game.this.state != GameState.WAITING) {
                  this.cancel();
               } else {
                  if (System.currentTimeMillis() > Game.this.nextAnnouncement) {
                     Game.this.nextAnnouncement = System.currentTimeMillis() + 15000L;
                     List<FancyMessage> notificationMessages = Game.this.createHostNotification();

                     for (Player player : Bukkit.getOnlinePlayers()) {
                        for (FancyMessage message : notificationMessages) {
                           message.send(player);
                        }
                     }
                  }

                  if (Game.this.startedAt == null) {
                     if (Game.this.players.size() >= Game.this.gameType.getMinPlayers()
                        || Game.this.hostForceStarted
                        || Game.this.voteStart >= Game.this.players.size() && Game.this.players.size() > 0) {
                        Game.this.startedAt = System.currentTimeMillis() + 30000L;
                     }
                  } else if (System.currentTimeMillis() > Game.this.startedAt) {
                     Game.this.state = GameState.RUNNING;
                     Game.this.startedWith = Game.this.players.size();
                     Game.this.gameBegun();
                  } else if (System.currentTimeMillis() > Game.this.startedAt - 5000L && Game.this.votedArena == null) {
                     Game.this.votedArena = (GameArena)Game.this.getArenaOptions()
                        .entrySet()
                        .stream()
                        .sorted((o1, o2) -> o1.getValue().get())
                        .collect(Collectors.toList())
                        .get(0)
                        .getKey();
                     Game.this.sendMessages(ChatColor.GOLD.toString() + Game.this.votedArena.getName() + ChatColor.YELLOW.toString() + " has won the map vote!");
                  }
               }
            }
         })
         .runTaskTimerAsynchronously(Foxtrot.getInstance(), 10L, 10L);
      (new BukkitRunnable() {
            private final long expiresAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3L);
            private final List<Integer> broadcasts = new ArrayList<>(Arrays.asList(5, 10, 15));

            @Override
            public void run() {
               if (Game.this.state == GameState.WAITING && Game.this.getStartedAt() == null) {
                  long diff = this.expiresAt - System.currentTimeMillis();
                  int diffSeconds = (int)(diff / 1000L);
                  if (diff <= 0L) {
                     Game.this.sendMessages("", ChatColor.DARK_RED + "The event has been cancelled because it couldn't get enough players!", "");
                     Game.this.hasExpired = true;
                     Game.this.endGame();
                     Foxtrot.getInstance().getMapHandler().getGameHandler().endGame();
                     this.cancel();
                  } else if (this.broadcasts.remove(Integer.valueOf(diffSeconds))) {
                     Game.this.sendMessages(
                        "",
                        ChatColor.RED
                           + "The event will be automatically cancelled for lack of players in "
                           + TimeUtils.formatIntoDetailedString(diffSeconds)
                           + "!",
                        ""
                     );
                  }
               } else {
                  this.cancel();
               }
            }
         })
         .runTaskTimerAsynchronously(Foxtrot.getInstance(), 10L, 10L);
   }

   public void endGame() {
      this.state = GameState.ENDED;
      this.winningPlayer = this.findWinningPlayer();
      Foxtrot.getInstance().getMapHandler().getGameHandler().setOngoingGame(null);
      if (this.winningPlayer != null) {
         Foxtrot.getInstance().getGemMap().addGems(this.winningPlayer.getUniqueId(), this.playerGems.size());
         this.winningPlayer
            .sendMessage(
               ChatColor.GREEN
                  + "You have received "
                  + ChatColor.DARK_GREEN
                  + "+"
                  + this.playerGems.size()
                  + " Gems"
                  + ChatColor.GREEN
                  + " for winning the event!"
            );
      }

      this.playerGems.clear();
      (new BukkitRunnable() {
            private int i = 5;

            @Override
            public void run() {
               this.i--;
               if (Game.this.winningPlayer != null && this.i <= 3 && this.i > 0) {
                  Bukkit.broadcastMessage(
                     Game.this.winningPlayer.getDisplayName() + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the event!"
                  );
                  Game.this.winningPlayer.playSound(Game.this.winningPlayer.getLocation(), Sound.FIREWORK_BLAST, 2.0F, 1.0F);
               }

               if (this.i <= 0) {
                  if (Game.this.winningPlayer != null) {
                     Bukkit.broadcastMessage(
                        ChatColor.translate(
                           Game.this.winningPlayer.getDisplayName() + " &ewas awarded &2&l" + Game.this.players.size() + " Gems &efor winning the event!"
                        )
                     );
                  }

                  this.cancel();

                  try {
                     (new BukkitRunnable() {
                        @Override
                        public void run() {
                           for (Player player : Game.this.getPlayers()) {
                              Game.this.removePlayer(player);
                           }

                           for (Player spectator : Game.this.getSpectators()) {
                              Game.this.removeSpectator(spectator);
                           }
                        }
                     }).runTask(Foxtrot.getInstance());
                  } catch (Exception e) {
                     e.printStackTrace();
                  }

                  Foxtrot.getInstance().getMapHandler().getGameHandler().endGame();
               }
            }
         })
         .runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
   }

   public boolean isPlaying(UUID player) {
      return this.players.contains(player);
   }

   public boolean isSpectating(UUID player) {
      return this.spectators.contains(player);
   }

   public boolean isPlayingOrSpectating(UUID player) {
      return this.isPlaying(player) || this.isSpectating(player);
   }

   public void addPlayer(Player player) throws IllegalStateException {
      if (this.state != GameState.WAITING) {
         throw new IllegalStateException("That event has already started. Try spectating instead with /game spectate.");
      }

      if (!player.isOp() && this.getMaxPlayers() <= this.players.size()) {
         player.sendMessage(ChatColor.RED + "You may not join the event whilst its full!");
      } else if (ModHandler.INSTANCE.isInModMode(player.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You can't join the event while in mod-mode.");
      } else if (SpawnTagHandler.isTagged(player)) {
         player.sendMessage(ChatColor.RED + "You can't join the event while spawn-tagged.");
      } else if (!ItemUtils.hasEmptyInventory(player)) {
         player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
      } else if (this.isGemRequiredToJoin() && !Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), 1L)) {
         player.sendMessage(ChatColor.RED + "You must have at least 1 gem to join the event.");
      } else {
         this.players.add(player.getUniqueId());
         this.playerGems.add(player.getUniqueId());
         GameUtils.resetPlayer(player);
         if (Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().getLobbySpawn() != null) {
            player.teleport(Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().getLobbySpawn());
         }

         if (!this.usedMessage.contains(player.getUniqueId())) {
            this.sendMessages(
               player.getDisplayName()
                  + ChatColor.GOLD.toString()
                  + " has joined the "
                  + ChatColor.WHITE
                  + this.gameType.getDisplayName()
                  + ChatColor.GOLD
                  + " event! ["
                  + ChatColor.WHITE
                  + this.players.size()
                  + "&7/&f"
                  + this.maxPlayers
                  + ChatColor.GOLD
                  + "]"
            );
         }

         if (this.getVotedArena() == null && this.getArenaOptions().size() > 1) {
            new MapVoteMenu(this).openMenu(player);
         }
      }
   }

   public void removePlayer(Player player) {
      this.players.remove(player.getUniqueId());
      SpawnTagHandler.removeTag(player);
      InventoryUtils.resetInventoryNow(player);
      player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
      if (this.state == GameState.WAITING) {
         if (!this.usedMessage.contains(player.getUniqueId())) {
            this.usedMessage.add(player.getUniqueId());
            this.sendMessages(
               player.getDisplayName()
                  + ChatColor.GOLD.toString()
                  + " has left the "
                  + ChatColor.WHITE
                  + this.gameType.getDisplayName()
                  + ChatColor.GOLD
                  + " event! [&f"
                  + this.players.size()
                  + "&7/&f"
                  + this.maxPlayers
                  + "&6]"
            );
         }

         this.playerGems.remove(player.getUniqueId());
         Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), 1L);
      }
   }

   public void eliminatePlayer(Player player, Player killer) {
      this.players.remove(player.getUniqueId());
      InventoryUtils.resetInventoryNow(player);
      if (killer == null) {
         if (Bukkit.getPlayer(player.getUniqueId()) != null) {
            this.sendMessages(
               player.getDisplayName() + ChatColor.GOLD + " has been eliminated! [&f" + this.getPlayers().size() + "&7/&f" + this.getStartedWith() + "&6]"
            );
         } else {
            this.sendMessages(
               player.getDisplayName()
                  + ChatColor.GOLD
                  + " has disconnected and has been disqualified! [&f"
                  + this.getPlayers().size()
                  + "&7/&f"
                  + this.getStartedWith()
                  + "&6]"
            );
         }
      }

      if (killer != null) {
         this.addSpectator(player);
      }
   }

   public void addSpectator(Player player) {
      this.spectators.add(player.getUniqueId());
      Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
         GameUtils.resetPlayer(player);
         player.teleport(this.votedArena.getSpectatorSpawn());
      });
   }

   public void removeSpectator(Player player) {
      this.spectators.remove(player.getUniqueId());
      SpawnTagHandler.removeTag(player);
      Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
         InventoryUtils.resetInventoryNow(player);
         player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
      });
   }

   public String getHostName() {
      return Proton.getInstance().getUuidCache().name(this.host);
   }

   public List<Player> getPlayers() {
      if (this.players.isEmpty()) {
         return ImmutableList.of();
      }

      List<Player> players = new ArrayList<>();

      for (UUID uuid : this.players) {
         Player player = Bukkit.getPlayer(uuid);
         if (player != null) {
            players.add(player);
         }
      }

      return players;
   }

   public List<Player> getSpectators() {
      if (this.spectators.isEmpty()) {
         return ImmutableList.of();
      }

      List<Player> spectators = new ArrayList<>();

      for (UUID uuid : this.spectators) {
         Player player = Bukkit.getPlayer(uuid);
         if (player != null) {
            spectators.add(player);
         }
      }

      return spectators;
   }

   public List<Player> getPlayersAndSpectators() {
      List<Player> playersAndSpectators = new ArrayList<>();
      playersAndSpectators.addAll(this.getPlayers());
      playersAndSpectators.addAll(this.getSpectators());
      return playersAndSpectators;
   }

   public void sendMessages(String... messages) {
      for (Player player : this.getPlayersAndSpectators()) {
         for (String message : messages) {
            player.sendMessage(ChatColor.translate(message));
         }
      }
   }

   public void sendSound(Sound sound, float volume, float pitch) {
   }

   public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
      event.setCancelled(true);
   }

   public abstract Player findWinningPlayer();

   public abstract void getScoreboardLines(Player var1, LinkedList<String> var2);

   public abstract List<FancyMessage> createHostNotification();

   public void gameBegun() {
   }

   public UUID getHost() {
      return this.host;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public GameState getState() {
      return this.state;
   }

   public Long getNextAnnouncement() {
      return this.nextAnnouncement;
   }

   public boolean isHasExpired() {
      return this.hasExpired;
   }

   public Long getStartedAt() {
      return this.startedAt;
   }

   public int getStartedWith() {
      return this.startedWith;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isGemRequiredToJoin() {
      return this.gemRequiredToJoin;
   }

   public boolean isHostForceStarted() {
      return this.hostForceStarted;
   }

   public int getVoteStart() {
      return this.voteStart;
   }

   public Set<UUID> getUsedMessage() {
      return this.usedMessage;
   }

   public Set<UUID> getPlayerGems() {
      return this.playerGems;
   }

   public Map<GameArena, AtomicInteger> getArenaOptions() {
      return this.arenaOptions;
   }

   public Map<UUID, GameArena> getPlayerVotes() {
      return this.playerVotes;
   }

   public GameArena getVotedArena() {
      return this.votedArena;
   }

   public Player getWinningPlayer() {
      return this.winningPlayer;
   }

   public void setState(GameState state) {
      this.state = state;
   }

   public void setStartedAt(Long startedAt) {
      this.startedAt = startedAt;
   }

   public boolean isStarted() {
      return this.started;
   }

   public void setStarted(boolean started) {
      this.started = started;
   }

   public void setStartedWith(int startedWith) {
      this.startedWith = startedWith;
   }

   public void setMaxPlayers(int maxPlayers) {
      this.maxPlayers = maxPlayers;
   }

   public void setGemRequiredToJoin(boolean gemRequiredToJoin) {
      this.gemRequiredToJoin = gemRequiredToJoin;
   }

   public void setHostForceStarted(boolean hostForceStarted) {
      this.hostForceStarted = hostForceStarted;
   }

   public void setVoteStart(int voteStart) {
      this.voteStart = voteStart;
   }
}
