package net.frozenorb.foxtrot.gameplay.events.cavenite;

import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.PlayerUtils;
import cc.fyre.proton.util.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.listener.CaveNiteListener;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.PersistableLocation;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CaveNiteHandler {
   private Foxtrot instance;
   private long started = 0L;
   private final List<UUID> playersRemaining = new ArrayList<>();
   private final List<UUID> spectators = new ArrayList<>();
   private final List<Location> locations = new ArrayList<>();
   private int scatterIn = 0;
   private int startIn = 0;
   private int startedWith = 0;
   private CaveNiteState gameState = CaveNiteState.INACTIVE;
   private final List<Location> chestLocations = new ArrayList<>();
   private final List<ItemStack> chestLoot = new ArrayList<>();
   private File file;
   private FileConfiguration data;

   public CaveNiteHandler(Foxtrot instance) {
      this.instance = instance;
      this.instance.getServer().getPluginManager().registerEvents(new CaveNiteListener(this.instance, this), this.instance);
      new GameTask(this.instance, this).runTaskTimer(this.instance, 60L, 60L);
      this.loadLocations();
   }

   public void loadLocations() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/cavenite.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.get("locations") != null) {
         this.instance
            .getServer()
            .getScheduler()
            .runTaskLater(
               this.instance,
               () -> this.data
                  .getConfigurationSection("locations")
                  .getKeys(false)
                  .forEach(it -> this.locations.add(((PersistableLocation)this.data.get("locations." + it)).getLocation())),
               30L
            );
      }
   }

   public void saveLocations() {
      this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));
      int i = 0;

      for (Location location : this.locations) {
         this.data.set("locations.location_" + ++i, new PersistableLocation(location));
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void start() {
      final Server server = Foxtrot.getInstance().getServer();
      this.scatterIn = 90;
      this.gameState = CaveNiteState.WAITING;
      this.respawnChests();
      (new BukkitRunnable() {
            @Override
            public void run() {
               if (CaveNiteHandler.this.scatterIn <= 0) {
                  CaveNiteHandler.this.scatter();
                  server.broadcastMessage(ChatColor.GREEN + "Squads Nite has started!");
                  CaveNiteHandler.this.startIn = 30;
                  CaveNiteHandler.this.gameState = CaveNiteState.SCATTERING;
                  CaveNiteHandler.this.startedWith = CaveNiteHandler.this.getOnlinePlayers().size();
                  this.cancel();
               } else {
                  CaveNiteHandler.this.scatterIn--;
                  if (CaveNiteHandler.this.scatterIn % 10 == 0 || CaveNiteHandler.this.scatterIn <= 5) {
                     for (Player onlinePlayer : CaveNiteHandler.this.instance.getServer().getOnlinePlayers()) {
                        onlinePlayer.sendMessage("");
                        onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
                        new FancyMessage(ChatColor.translate("&7█&4█████&7█ &4&lSquads Nite"))
                           .tooltip(ChatColor.GREEN + "Click to join")
                           .command("&7Coming Soon!")
                           .send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█&4█&7█████ &f"))
                           .tooltip(ChatColor.GREEN + "Click to join")
                           .command("&7Coming Soon!")
                           .send(onlinePlayer);
                        new FancyMessage(
                              ChatColor.translate(
                                 "&7█&4█&7█████ &7Players: &f"
                                    + CaveNiteHandler.this.getOnlinePlayers().size()
                                    + "/"
                                    + CaveNiteHandler.this.getLocations().size()
                              )
                           )
                           .tooltip(ChatColor.GREEN + "Click to join")
                           .command("&7Coming Soon!")
                           .send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█&4█&7█████ &7Starts In: &f" + CaveNiteHandler.this.scatterIn + " seconds"))
                           .tooltip(ChatColor.GREEN + "Click to join")
                           .command("&7Coming Soon!")
                           .send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█&4█████&7█ &aClick here to join the event"))
                           .tooltip(ChatColor.GREEN + "Click to join")
                           .command("&7Coming Soon!")
                           .send(onlinePlayer);
                        onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
                        onlinePlayer.sendMessage("");
                     }
                  }
               }
            }
         })
         .runTaskTimer(this.instance, 20L, 20L);
   }

   public void scatter() {
      final Server server = this.instance.getServer();
      (new BukkitRunnable() {
            @Override
            public void run() {
               if (CaveNiteHandler.this.startIn <= 0) {
                  this.cancel();
                  CaveNiteHandler.this.gameState = CaveNiteState.RUNNING;
                  CaveNiteHandler.this.started = System.currentTimeMillis();
                  server.broadcastMessage("");
                  server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Squads Nite");
                  server.broadcastMessage(ChatColor.GRAY + "Squads Nite has started! Good luck have fun!");
                  server.broadcastMessage("");
                  CaveNiteHandler.this.getOnlinePlayers().forEach(it -> {
                     it.closeInventory();
                     it.playSound(it.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
                     it.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                     it.removePotionEffect(PotionEffectType.SLOW);
                     it.removePotionEffect(PotionEffectType.BLINDNESS);
                  });
                  CaveNiteHandler.this.getOnlinePlayers().forEach(it -> it.playSound(it.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F));
               } else {
                  CaveNiteHandler.this.startIn--;
                  if (CaveNiteHandler.this.startIn == 20 || CaveNiteHandler.this.startIn == 10 || CaveNiteHandler.this.startIn <= 5) {
                     CaveNiteHandler.this.getOnlinePlayers().forEach(it -> it.playSound(it.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F));
                     server.broadcastMessage("");
                     server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Squads Nite");
                     server.broadcastMessage(
                        ChatColor.GRAY + "Squads Nite begins in " + ChatColor.WHITE + CaveNiteHandler.this.startIn + " seconds" + ChatColor.GRAY + "."
                     );
                     server.broadcastMessage("");
                  }
               }
            }
         })
         .runTaskTimer(this.instance, 20L, 20L);
      List<Player> toTeleport = new ArrayList<>(this.getOnlinePlayers());
      List<Location> toUse = new ArrayList<>(this.getLocations());

      for (Player onlinePlayer : this.getOnlinePlayers()) {
         toTeleport.remove(onlinePlayer);
         onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.PORTAL_TRAVEL, 1.0F, 1.0F);
         onlinePlayer.sendMessage(ChatColor.GREEN + "You have been scattered across the map!");
         onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255));
         onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255));
         onlinePlayer.teleport(toUse.get(0));
         onlinePlayer.getInventory().clear();
         server.dispatchCommand(server.getConsoleSender(), "kit apply Diamond2 " + onlinePlayer.getName());
      }
   }

   public void addPlayer(Player player) {
      PlayerUtils.resetInventory(player, GameMode.ADVENTURE);
      this.getPlayersRemaining().add(player.getUniqueId());
      this.getOnlinePlayers()
         .forEach(
            it -> {
               it.playSound(it.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
               it.sendMessage(
                  ChatColor.translate(
                     player.getDisplayName() + " &chas joined the game! &7[" + this.getOnlinePlayers().size() + "/" + this.getLocations().size() + "]"
                  )
               );
            }
         );
   }

   public void addSpectator(Player player) {
      this.spectators.add(player.getUniqueId());
      PlayerUtils.resetInventory(player, GameMode.ADVENTURE);
      player.setAllowFlight(true);
      player.setFlying(true);
      player.getInventory().setItem(4, ItemBuilder.of(Material.WATCH).name(ChatColor.GOLD + ChatColor.BOLD.toString() + "Spectate Menu").build());
      player.spigot().setCollidesWithEntities(false);
      this.getOnlinePlayers().forEach(it -> it.hidePlayer(player));
   }

   public void disqualify(Player player) {
      this.playersRemaining.remove(player.getUniqueId());
      if (this.getOnlinePlayers().size() <= 1) {
         this.endGame(false);
      }
   }

   public void endGame(boolean force) {
      Server server = this.instance.getServer();
      server.broadcastMessage(ChatColor.RED + "Game over!");
      this.gameState = CaveNiteState.ENDING;
      server.dispatchCommand(server.getConsoleSender(), "reboot 15s");
      if (force) {
         server.broadcastMessage("");
         server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Squads Nite");
         server.broadcastMessage(ChatColor.WHITE + "The game is now over!");
         server.broadcastMessage(ChatColor.GRAY + "The game has been forcefully cancelled");
         server.broadcastMessage("");
      } else {
         this.getOnlinePlayers().stream().findFirst().ifPresent(it -> {
            this.getOnlineSpectators().forEach(spectator -> spectator.teleport(it.getLocation().clone()));
            VoucherCommand.spawnFireworks(it.getLocation(), 3, 2, Color.RED, Type.BALL_LARGE);
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Squads Nite");
            server.broadcastMessage(ChatColor.WHITE + "The game is now over!");
            server.broadcastMessage(ChatColor.translate(it.getName() + " &7has won the game!"));
            server.broadcastMessage("");
         });
      }
   }

   public void scanLoot() {
      this.chestLocations.clear();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         if (team.getOwner() == null && team.getName().equalsIgnoreCase("CaveNite")) {
            for (Claim claim : team.getClaims()) {
               for (Location location : new CuboidRegion("CaveNite", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                  if (location.getBlock().getType() == Material.CHEST) {
                     this.chestLocations.add(location);
                  }
               }
            }
         }
      }
   }

   public void respawnChests() {
      for (Location location : this.chestLocations) {
         if (location.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest)location.getBlock().getState();
            chest.getInventory().clear();

            for (int i = 0; i < 4; i++) {
               ItemStack randomItem = Foxtrot.getInstance()
                  .getCaveNiteHandler()
                  .getChestLoot()
                  .get(ThreadLocalRandom.current().nextInt(Foxtrot.getInstance().getCaveNiteHandler().getChestLoot().size()));
               chest.getInventory().addItem(randomItem.clone());
            }
         }
      }
   }

   public List<Player> getOnlinePlayers() {
      return this.playersRemaining
         .stream()
         .filter(it -> this.instance.getServer().getPlayer(it) != null)
         .map(it -> this.instance.getServer().getPlayer(it))
         .collect(Collectors.toList());
   }

   public List<Player> getOnlineSpectators() {
      return this.spectators
         .stream()
         .filter(it -> this.instance.getServer().getPlayer(it) != null)
         .map(it -> this.instance.getServer().getPlayer(it))
         .collect(Collectors.toList());
   }

   public List<String> getScoreboardLines() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("&4&lSquads Nite");
      if (this.gameState == CaveNiteState.ENDING) {
         Player lastPlayer = this.getOnlinePlayers().get(0);
         toReturn.add("&7Winner: &f" + (lastPlayer == null ? "N/A" : lastPlayer.getDisplayName()));
         toReturn.add("&7Spectators: &f" + this.getOnlineSpectators().size());
         toReturn.add("");
         toReturn.add(ChatColor.RED + "The game is now over!");
      }

      if (this.gameState == CaveNiteState.RUNNING) {
         long difference = System.currentTimeMillis() - this.started;
         toReturn.add("&7Remaining: &f" + this.getOnlinePlayers().size() + "/" + this.startedWith);
         toReturn.add("&7Time Elapsed: &f" + ScoreFunction.TIME_FANCY.apply((float)(difference / 1000L)));
         toReturn.add("");
         toReturn.add("&7Coming Soon!");
      }

      if (this.gameState == CaveNiteState.SCATTERING) {
         toReturn.add("&7Queued: &f" + this.getOnlinePlayers().size());
         toReturn.add("&7Starting In: &f" + TimeUtils.formatIntoMMSS(this.startIn));
         toReturn.add("");
         toReturn.add("&7Coming Soon!");
      }

      if (this.gameState == CaveNiteState.WAITING) {
         toReturn.add("&7Queued: &f" + this.getOnlinePlayers().size());
         toReturn.add("&7Scatter In: &f" + TimeUtils.formatIntoMMSS(this.scatterIn));
         toReturn.add("");
         toReturn.add("&7Coming Soon!");
      }

      return toReturn;
   }

   public long getStarted() {
      return this.started;
   }

   public void setStarted(long started) {
      this.started = started;
   }

   public List<UUID> getPlayersRemaining() {
      return this.playersRemaining;
   }

   public List<UUID> getSpectators() {
      return this.spectators;
   }

   public List<Location> getLocations() {
      return this.locations;
   }

   public int getScatterIn() {
      return this.scatterIn;
   }

   public void setScatterIn(int scatterIn) {
      this.scatterIn = scatterIn;
   }

   public int getStartIn() {
      return this.startIn;
   }

   public void setStartIn(int startIn) {
      this.startIn = startIn;
   }

   public int getStartedWith() {
      return this.startedWith;
   }

   public CaveNiteState getGameState() {
      return this.gameState;
   }

   public void setGameState(CaveNiteState gameState) {
      this.gameState = gameState;
   }

   public List<Location> getChestLocations() {
      return this.chestLocations;
   }

   public List<ItemStack> getChestLoot() {
      return this.chestLoot;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }
}
