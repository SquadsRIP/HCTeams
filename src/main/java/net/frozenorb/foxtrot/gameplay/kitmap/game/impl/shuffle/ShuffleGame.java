package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle;

import cc.fyre.neutron.Neutron;
import cc.fyre.piston.command.admin.ScreenShareCommand;
import cc.fyre.proton.util.ItemBuilder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.Cuboid;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ShuffleGame extends Game {
   private List<Block> floor = new ArrayList<>();
   private List<DyeColor> colors = Arrays.asList(
      DyeColor.ORANGE,
      DyeColor.MAGENTA,
      DyeColor.LIGHT_BLUE,
      DyeColor.YELLOW,
      DyeColor.LIME,
      DyeColor.GRAY,
      DyeColor.CYAN,
      DyeColor.PURPLE,
      DyeColor.BLUE,
      DyeColor.GREEN,
      DyeColor.RED
   );
   private static SecureRandom RANDOM;
   private DyeColor currentColor;
   public int currentRound;
   private long roundEndsAt;

   public ShuffleGame(UUID host, List<GameArena> arenaOptions) {
      super(host, GameType.WOOL_SHUFFLE, arenaOptions);
   }

   @Override
   public void startGame() {
      super.startGame();
      if (this.getVotedArena().getBounds() != null) {
         this.getVotedArena().createSnapshot();
      }

      this.scanFloor();
      (new BukkitRunnable() {
            @Override
            public void run() {
               if (ShuffleGame.this.state == GameState.ENDED) {
                  this.cancel();
               } else {
                  if (ShuffleGame.this.state == GameState.RUNNING) {
                     int midIndex = (ShuffleGame.this.getPlayers().size() - 1) / 2;
                     List<List<Player>> split = new ArrayList<>(
                        ShuffleGame.this.getPlayers()
                           .stream()
                           .collect(Collectors.partitioningBy(s -> ShuffleGame.this.getPlayers().indexOf(s) > midIndex))
                           .values()
                     );
                     split.get(0).forEach(player -> player.teleport(ShuffleGame.this.getVotedArena().getPointA()));
                     split.get(1).forEach(player -> player.teleport(ShuffleGame.this.getVotedArena().getPointB()));
                     ShuffleGame.this.startRound();
                     this.cancel();
                  }
               }
            }
         })
         .runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
   }

   @Override
   public void endGame() {
      super.endGame();
      if (this.getVotedArena().getBounds() != null) {
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this.getVotedArena()::restoreSnapshot, 100L);
      }
   }

   public void scanFloor() {
      for (Block block : this.getVotedArena().getBounds()) {
         if (block.getType() == Material.WOOL && block.getData() == 0) {
            this.floor.add(block);
         }
      }
   }

   public void shuffleFloor() {
      Location start = this.floor.get(0).getLocation();
      Location end = this.floor.get(this.floor.size() - 1).getLocation();
      int startX = start.getBlockX();
      int startZ = start.getBlockZ();
      int endX = end.getBlockX();
      int endZ = end.getBlockZ();
      List<DyeColor> colors = new ArrayList<>(this.colors);
      colors.remove(this.currentColor);

      for (int minX = startX; minX <= endX; minX += 5) {
         for (int minZ = startZ; minZ <= endZ; minZ += 5) {
            DyeColor color = colors.get(this.random(colors.size()));
            Location min = new Location(start.getWorld(), minX, start.getBlockY(), minZ);
            Location max = new Location(start.getWorld(), minX + 5, start.getBlockY(), minZ + 5);
            Cuboid cuboid = new Cuboid(min, max);

            for (Block block : cuboid.getBlocks()) {
               if (this.floor.contains(block)) {
                  block.setType(Material.WOOL);
                  block.setData(color.getWoolData());
               }
            }
         }
      }

      for (int i = 0; i < 3; i++) {
         int tileMaxX = Math.abs(endX - startX) / 5;
         int tileMaxZ = Math.abs(endZ - startZ) / 5;
         int meowX = startX + ThreadLocalRandom.current().nextInt(tileMaxX) * 5;
         int meowZ = startZ + ThreadLocalRandom.current().nextInt(tileMaxZ) * 5;
         Location min = new Location(start.getWorld(), meowX, start.getBlockY(), meowZ);
         Location max = new Location(start.getWorld(), meowX + 4, start.getBlockY(), meowZ + 4);
         Cuboid cuboid = new Cuboid(min, max);

         for (Block block : cuboid.getBlocks()) {
            if (this.floor.contains(block)) {
               block.setType(Material.WOOL);
               block.setData(this.currentColor.getWoolData());
            }
         }
      }

      this.sendMessages(ChatColor.GOLD + "The floor has been shuffled!");
   }

   public void pickColor() {
      List<DyeColor> colors = new ArrayList<>(this.colors);
      colors.remove(this.currentColor);
      this.currentColor = colors.get(this.random(colors.size()));
      String colorName = WordUtils.capitalize(this.currentColor.name().toLowerCase().replace("_", " "));
      ItemStack colorItem = ItemBuilder.of(Material.WOOL)
         .name(this.dyeToChatColor(this.currentColor) + colorName)
         .data(this.currentColor.getWoolData())
         .enchant(Enchantment.KNOCKBACK, 1)
         .build();

      for (Player player : this.getPlayers()) {
         for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, colorItem);
         }

         player.sendMessage(ChatColor.GOLD + "The new color is " + this.dyeToChatColor(this.currentColor) + colorName + ChatColor.GOLD + ".");
         if (this.currentRound >= 10) {
            ScreenShareCommand.sendDangerSign(
               player,
               "",
               "",
               ChatColor.DARK_RED + ChatColor.BOLD.toString() + "WARNING",
               "",
               ChatColor.YELLOW + "PvP has been",
               ChatColor.YELLOW + "enabled for this match!",
               "",
               ""
            );
         }

         this.sendSound(Sound.NOTE_PLING, 5.0F, 2.0F);
      }
   }

   public void dropFloor() {
      Location start = this.floor.get(0).getLocation();
      Location end = this.floor.get(this.floor.size() - 1).getLocation();

      for (Block block : new Cuboid(start, end)) {
         if (block.getData() != this.currentColor.getWoolData()) {
            block.setType(Material.AIR);
         }
      }

      for (Player player : this.getPlayers()) {
         boolean eliminate = true;

         for (int offset = 0; offset <= 3; offset++) {
            Block below = player.getLocation().getBlock().getRelative(BlockFace.DOWN, offset);
            if (below != null && below.getData() == this.currentColor.getWoolData()) {
               eliminate = false;
               break;
            }
         }

         if (eliminate && this.players.size() > 1) {
            this.eliminatePlayer(player, null);
         }
      }

      this.sendMessages("", ChatColor.GOLD + "The floor has dropped!", "");
      if (this.players.size() <= 1) {
         this.endGame();
      }
   }

   public void startRound() {
      if (this.state != GameState.ENDED) {
         this.currentRound++;
         this.setStartedAt(System.currentTimeMillis());
         long seconds = 11L;
         if (this.currentRound > 12) {
            seconds = 4L;
         } else if (this.currentRound > 7) {
            seconds = 6L;
         } else if (this.currentRound > 3) {
            seconds = 8L;
         }

         this.roundEndsAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
         this.pickColor();
         Bukkit.getScheduler().runTask(Foxtrot.getInstance(), this::shuffleFloor);
         final int secondsInt = (int)seconds;
         (new BukkitRunnable() {
               private final List<Integer> announceAt = Arrays.asList(15, 10, 5, 4, 3, 2, 1);
               private final List<Integer> intervals = this.announceAt.stream().filter(t -> t <= secondsInt).collect(Collectors.toList());

               @Override
               public void run() {
                  if (ShuffleGame.this.state == GameState.ENDED) {
                     this.cancel();
                  } else {
                     if (ShuffleGame.this.state == GameState.RUNNING) {
                        if (System.currentTimeMillis() >= ShuffleGame.this.roundEndsAt) {
                           ShuffleGame.this.stopRound();
                           this.cancel();
                           return;
                        }

                        if (this.intervals.isEmpty()) {
                           return;
                        }

                        int first = this.intervals.get(0);
                        int secondsRemaining = (int)Math.round((ShuffleGame.this.roundEndsAt - System.currentTimeMillis()) / 1000.0);
                        if (secondsRemaining > 0 && secondsRemaining <= first) {
                           this.intervals.remove(0);
                           ShuffleGame.this.sendMessages(
                              ChatColor.GOLD
                                 + "The floor will drop in "
                                 + ChatColor.WHITE
                                 + secondsRemaining
                                 + " second"
                                 + (secondsRemaining == 1 ? "" : "s")
                                 + "&6!"
                           );
                        }
                     }
                  }
               }
            })
            .runTaskTimerAsynchronously(Foxtrot.getInstance(), 10L, 10L);
      }
   }

   public void stopRound() {
      Bukkit.getScheduler().runTask(Foxtrot.getInstance(), this::dropFloor);
      if (this.players.size() == 1) {
         this.endGame();
      } else {
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this::startRound, 100L);
      }
   }

   @Override
   public void eliminatePlayer(Player player, Player killer) {
      super.eliminatePlayer(player, killer);
      this.addSpectator(player);
   }

   public double getDeathHeight() {
      return Math.min(this.getVotedArena().getPointA().getBlockY(), this.getVotedArena().getPointB().getBlockY()) - 1.9;
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
         lines.add(" &6Round: &f" + this.currentRound);
         if (this.currentColor != null) {
            String colorName = WordUtils.capitalize(this.currentColor.name().toLowerCase().replace("_", " "));
            lines.add(" &6Color: &f" + this.dyeToChatColor(this.currentColor) + colorName);

            try {
               int secondsRemaining = (int)Math.round((this.roundEndsAt - System.currentTimeMillis()) / 1000.0);
               if (secondsRemaining > 0) {
                  lines.add(" &6Dropping in: &f" + secondsRemaining);
               }
            } catch (Exception var5) {
            }
         } else {
            lines.add("");
            lines.add("&aPicking new color...");
         }
      } else {
         if (this.winningPlayer == null) {
            lines.add(" &6Winner: &fNone");
         } else {
            lines.add(" &6Winner: &f" + this.winningPlayer.getName());
         }

         lines.add(" &6Rounds: &f" + this.currentRound);
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

   public ChatColor dyeToChatColor(DyeColor color) {
      switch (color) {
         case ORANGE:
            return ChatColor.GOLD;
         case MAGENTA:
            return ChatColor.LIGHT_PURPLE;
         case LIGHT_BLUE:
            return ChatColor.AQUA;
         case YELLOW:
            return ChatColor.YELLOW;
         case LIME:
            return ChatColor.GREEN;
         case GRAY:
            return ChatColor.GRAY;
         case CYAN:
            return ChatColor.DARK_AQUA;
         case PURPLE:
            return ChatColor.DARK_PURPLE;
         case BLUE:
            return ChatColor.BLUE;
         case GREEN:
            return ChatColor.DARK_GREEN;
         case RED:
            return ChatColor.RED;
         default:
            return ChatColor.WHITE;
      }
   }

   private int random(int bound) {
      return RANDOM.nextInt(bound);
   }

   static {
      try {
         RANDOM = SecureRandom.getInstance("SHA1PRNG", "SUN");
      } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
         RANDOM = new SecureRandom();
      }
   }
}
