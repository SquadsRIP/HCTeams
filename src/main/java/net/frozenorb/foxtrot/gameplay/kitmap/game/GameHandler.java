package net.frozenorb.foxtrot.gameplay.kitmap.game;

import cc.fyre.proton.Proton;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa.FFAGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike.MineStrikeGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq.OITQGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.parkour.ParkourGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle.ShuffleGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.spleef.SpleefGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo.SumoGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun.TNTRunGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag.TNTTagGame;
import net.frozenorb.foxtrot.util.ItemUtils;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class GameHandler {
   private static final long GAME_COOLDOWN = TimeUnit.MINUTES.toMillis(3L);
   private static final File CONFIG_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/kitmap-games-config.json");
   private static final Type CONFIG_DATA_TYPE = (new TypeToken<GameHandlerConfig>() {}).getType();
   private GameHandlerConfig config = new GameHandlerConfig();
   private Game ongoingGame;
   private Long nextRuntime = System.currentTimeMillis() - GAME_COOLDOWN;
   private boolean disabled;
   private World world;

   public GameHandler() {
      this.loadWorld();
      this.loadConfig();
      Proton.getInstance().getCommandHandler().registerParameterType(GameArena.class, new GameArena.Type());
      Proton.getInstance().getCommandHandler().registerParameterType(GameType.class, new GameType.Type());
   }

   private void loadWorld() {
      WorldCreator worldCreator = new WorldCreator("kits_events");
      worldCreator.generator(new ChunkGenerator() {
         @Override
         public byte[] generate(World world, Random random, int x, int z) {
            return new byte[32768];
         }

         @Override
         public boolean canSpawn(World world, int x, int z) {
            return false;
         }

         @Override
         public List<BlockPopulator> getDefaultPopulators(World world) {
            return new ArrayList<>();
         }

         @Override
         public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 0.5, 100.0, 0.5);
         }
      });
      this.world = Bukkit.createWorld(worldCreator);
   }

   private void loadConfig() {
      if (CONFIG_FILE.exists()) {
         try {
            BufferedReader reader = Files.newReader(CONFIG_FILE, Charsets.UTF_8);

            try {
               this.config = Proton.PLAIN_GSON.fromJson(reader, CONFIG_DATA_TYPE);
            } catch (Throwable var5) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
            Foxtrot.getInstance().getLogger().severe(ChatColor.YELLOW + "Failed to import kitmap-games-config.json!");
         }
      }
   }

   public void saveConfig() {
      try {
         Files.write(Proton.PLAIN_GSON.toJson(this.config, CONFIG_DATA_TYPE), CONFIG_FILE, Charsets.UTF_8);
      } catch (IOException e) {
         e.printStackTrace();
         Foxtrot.getInstance().getLogger().severe(ChatColor.YELLOW + "Failed to export kitmap-games-config.json!");
      }
   }

   public int getCooldownSeconds() {
      return System.currentTimeMillis() < this.nextRuntime ? (int)((float)(this.nextRuntime - System.currentTimeMillis()) / 1000.0F) : 0;
   }

   public boolean isOngoingGame() {
      return this.ongoingGame != null;
   }

   public String getStatus() {
      if (this.isJoinable()) {
         return "Waiting";
      } else {
         return this.isOngoingGame() ? "Active" : "Inactive";
      }
   }

   public boolean isHostCooldown() {
      return System.currentTimeMillis() < this.nextRuntime;
   }

   public boolean isJoinable() {
      return this.ongoingGame != null && this.ongoingGame.getState() == GameState.WAITING;
   }

   public boolean canStartGame(Player player, GameType gameType) {
      if (this.disabled) {
         player.sendMessage(ChatColor.YELLOW + "Events are currently disabled.");
         return false;
      } else if (!gameType.canHost(player)) {
         player.sendMessage(ChatColor.YELLOW + "You don't have permission to host " + gameType.getDisplayName() + " events.");
         return false;
      } else if (this.isOngoingGame()) {
         player.sendMessage(ChatColor.YELLOW + "There is an ongoing game, and only one game can run at a time.");
         return false;
      } else if (this.findArenas(gameType).isEmpty()) {
         player.sendMessage(ChatColor.YELLOW + "There are no arenas compatible with that game type!");
         return false;
      } else if (!player.hasPermission("kitmap.game.host.cooldown-bypass") && this.isHostCooldown()) {
         double cooldown = Math.round(10.0 * ((this.nextRuntime - System.currentTimeMillis()) / 1000.0)) / 10.0;
         player.sendMessage(ChatColor.YELLOW + "Another game can't be hosted for another " + cooldown + "s.");
         return false;
      } else if (!ItemUtils.hasEmptyInventory(player)) {
         player.sendMessage(ChatColor.YELLOW + "You need to have an empty inventory to join the event.");
         return false;
      } else {
         return true;
      }
   }

   public Game startGame(UUID host, GameType gameType) throws IllegalStateException {
      if (this.ongoingGame != null) {
         throw new IllegalStateException("There is an ongoing game!");
      }

      if (gameType == GameType.SUMO) {
         this.ongoingGame = new SumoGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.FFA) {
         this.ongoingGame = new FFAGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.SPLEEF) {
         this.ongoingGame = new SpleefGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.WOOL_SHUFFLE) {
         this.ongoingGame = new ShuffleGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.OITQ) {
         this.ongoingGame = new OITQGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.TNT_RUN) {
         this.ongoingGame = new TNTRunGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.TNT_TAG) {
         this.ongoingGame = new TNTTagGame(host, this.findArenas(gameType));
      } else if (gameType == GameType.MINE_STRIKE) {
         this.ongoingGame = new MineStrikeGame(host, this.findArenas(gameType));
      } else {
         if (gameType != GameType.PARKOUR) {
            throw new IllegalStateException("Game type not supported yet!");
         }

         this.ongoingGame = new ParkourGame(host, this.findArenas(gameType));
      }

      this.ongoingGame.startGame();
      return this.ongoingGame;
   }

   public Game startGame(Player host, GameType gameType) throws IllegalStateException {
      if (this.ongoingGame != null) {
         throw new IllegalStateException("There is an ongoing game!");
      }

      if (gameType == GameType.SUMO) {
         this.ongoingGame = new SumoGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.FFA) {
         this.ongoingGame = new FFAGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.SPLEEF) {
         this.ongoingGame = new SpleefGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.WOOL_SHUFFLE) {
         this.ongoingGame = new ShuffleGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.OITQ) {
         this.ongoingGame = new OITQGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.TNT_RUN) {
         this.ongoingGame = new TNTRunGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.TNT_TAG) {
         this.ongoingGame = new TNTTagGame(host.getUniqueId(), this.findArenas(gameType));
      } else if (gameType == GameType.MINE_STRIKE) {
         this.ongoingGame = new MineStrikeGame(host.getUniqueId(), this.findArenas(gameType));
      } else {
         if (gameType != GameType.PARKOUR) {
            throw new IllegalStateException("Game type not supported yet!");
         }

         this.ongoingGame = new ParkourGame(host.getUniqueId(), this.findArenas(gameType));
      }

      this.ongoingGame.startGame();
      return this.ongoingGame;
   }

   public void endGame() {
      if (this.ongoingGame == null || !this.ongoingGame.isHasExpired()) {
         this.nextRuntime = System.currentTimeMillis() + GAME_COOLDOWN;
      }

      this.ongoingGame = null;
   }

   public List<GameArena> findArenas(GameType gameType) {
      ArrayList<GameArena> compatible = new ArrayList<>();

      for (GameArena arena : this.config.getArenas()) {
         if (arena.isSetup() && arena.getCompatibleGameTypes().contains(gameType.name().toLowerCase())) {
            compatible.add(arena);
         }
      }

      return compatible;
   }

   public GameHandlerConfig getConfig() {
      return this.config;
   }

   public Game getOngoingGame() {
      return this.ongoingGame;
   }

   public Long getNextRuntime() {
      return this.nextRuntime;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   public World getWorld() {
      return this.world;
   }

   public void setOngoingGame(Game ongoingGame) {
      this.ongoingGame = ongoingGame;
   }

   public void setDisabled(boolean disabled) {
      this.disabled = disabled;
   }
}
