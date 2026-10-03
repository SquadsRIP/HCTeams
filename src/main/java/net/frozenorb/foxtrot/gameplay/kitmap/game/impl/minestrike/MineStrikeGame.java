package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MineStrikeGame extends Game {
   private List<UUID> redTeam = new ArrayList<>();
   private List<UUID> blueTeam = new ArrayList<>();
   private int redStartedOutWith;
   private int blueStartedOutWith;
   private Set<UUID> playerGems = new HashSet<>();
   private ItemStack shotGun = ItemBuilder.of(Material.GOLD_BARDING).name("&6Shotgun Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
   private ItemStack smg = ItemBuilder.of(Material.IRON_BARDING).name("&6SMG Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
   private ItemStack rifle = ItemBuilder.of(Material.DIAMOND_BARDING).name("&6Rifle Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
   private boolean started = false;

   public MineStrikeGame(UUID host, List<GameArena> arenaOptions) {
      super(host, GameType.MINE_STRIKE, arenaOptions);
   }

   @Override
   public void startGame() {
      super.startGame();
      if (this.getVotedArena().getBounds() != null) {
         this.getVotedArena().createSnapshot();
      }

      (new BukkitRunnable() {
         @Override
         public void run() {
            if (MineStrikeGame.this.state == GameState.ENDED) {
               this.cancel();
            } else {
               if (MineStrikeGame.this.state == GameState.RUNNING) {
                  MineStrikeGame.this.startMineStrike();
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
   }

   @Override
   public void endGame() {
      this.state = GameState.ENDED;
      Foxtrot.getInstance().getMapHandler().getGameHandler().setOngoingGame(null);
      String team = ChatColor.RED + "Red Team";
      if (this.redTeam.isEmpty() && !this.blueTeam.isEmpty()) {
         team = ChatColor.BLUE + "Blue Team";
      }

      final String finalTeam = team;
      (new BukkitRunnable() {
         private int i = 5;

         @Override
         public void run() {
            this.i--;
            if (this.i <= 3 && this.i > 0) {
               Bukkit.broadcastMessage(finalTeam + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the event!");
            }

            if (this.i <= 0) {
               this.cancel();
               (new BukkitRunnable() {
                  @Override
                  public void run() {
                     MineStrikeGame.this.blueTeam.clear();
                     MineStrikeGame.this.redTeam.clear();

                     for (Player player : MineStrikeGame.this.getPlayers()) {
                        MineStrikeGame.this.removePlayer(player);
                     }

                     for (Player spectator : MineStrikeGame.this.getSpectators()) {
                        MineStrikeGame.this.removeSpectator(spectator);
                     }
                  }
               }).runTask(Foxtrot.getInstance());
               Foxtrot.getInstance().getMapHandler().getGameHandler().endGame();
            }
         }
      }).runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
      this.started = false;
      if (this.getVotedArena().getBounds() != null) {
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this.getVotedArena()::restoreSnapshot, 100L);
      }
   }

   private void startMineStrike() {
      for (Player player : this.getPlayers()) {
         InventoryUtils.resetInventoryNow(player);
         player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
         Foxtrot.getInstance().getCsUtility().giveWeapon(player, "Rifle", 1);
         Foxtrot.getInstance().getCsUtility().giveWeapon(player, "SMG", 1);
      }

      int midIndex = (this.getPlayers().size() - 1) / 2;
      List<List<Player>> split = new ArrayList<>(
         this.getPlayers().stream().collect(Collectors.partitioningBy(s -> this.getPlayers().indexOf(s) > midIndex)).values()
      );
      List<Player> red = split.get(0);
      List<Player> blue = split.get(1);
      red.forEach(playerx -> {
         playerx.getInventory().setHelmet(ItemBuilder.of(Material.REDSTONE_BLOCK).build());
         playerx.getInventory().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.RED).build());
         playerx.getInventory().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.RED).build());
         playerx.getInventory().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.RED).build());
         this.redTeam.add(playerx.getUniqueId());
         playerx.teleport(this.getVotedArena().getPointA());
      });
      blue.forEach(playerx -> {
         playerx.getInventory().setHelmet(ItemBuilder.of(Material.LAPIS_BLOCK).build());
         playerx.getInventory().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.BLUE).build());
         playerx.getInventory().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.BLUE).build());
         playerx.getInventory().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.BLUE).build());
         this.blueTeam.add(playerx.getUniqueId());
         playerx.teleport(this.getVotedArena().getPointB());
      });

      for (Player player : this.getPlayers()) {
         LunarClientListener.updateNametag(player);
      }

      this.redStartedOutWith = this.redTeam.size();
      this.blueStartedOutWith = this.blueTeam.size();
      this.started = false;
      this.setStartedAt(System.currentTimeMillis());
      (new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
               if (MineStrikeGame.this.state != GameState.ENDED && !MineStrikeGame.this.started) {
                  this.i--;
                  MineStrikeGame.this.sendSound(Sound.NOTE_PLING, 1.0F, this.i == 0 ? 2.0F : 1.0F);
                  MineStrikeGame.this.started = this.i == 0;
                  if (this.i == 0) {
                     MineStrikeGame.this.sendMessages("&6&l" + MineStrikeGame.this.getGameType().getDisplayName() + " &eevent has begun!");
                  } else {
                     MineStrikeGame.this.sendMessages(
                        "&6&l"
                           + MineStrikeGame.this.getGameType().getDisplayName()
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
   public void eliminatePlayer(Player player, Player killer) {
      super.eliminatePlayer(player, killer);
      this.sendMessages(player.getDisplayName() + " &ewas shot to death by &4" + killer.getDisplayName());
      this.addSpectator(player);
      this.blueTeam.remove(player.getUniqueId());
      this.redTeam.remove(player.getUniqueId());
      if (this.blueTeam.isEmpty() || this.redTeam.isEmpty()) {
         this.endGame();
      }

      LunarClientListener.updateTeammates(player);
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
         lines.add("");
         lines.add("&6&lTeams:");
         lines.add(" &cRed: &f" + this.redTeam.size() + "&7/&f" + this.getRedStartedOutWith());
         lines.add(" &9Blue: &f" + this.blueTeam.size() + "&7/&f" + this.getBlueStartedOutWith());
      } else {
         if (this.winningPlayer == null) {
            lines.add(" &6Winner: &fNone");
         } else {
            lines.add(" &6Winner: &f" + this.winningPlayer.getName());
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
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(" " + this.getGameType().getDisplayName() + " Event")
            .color(ChatColor.DARK_RED)
            .style(ChatColor.BOLD),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("██")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then("██")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then("█")
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

   public List<UUID> getRedTeam() {
      return this.redTeam;
   }

   public List<UUID> getBlueTeam() {
      return this.blueTeam;
   }

   public int getRedStartedOutWith() {
      return this.redStartedOutWith;
   }

   public int getBlueStartedOutWith() {
      return this.blueStartedOutWith;
   }

   public ItemStack getShotGun() {
      return this.shotGun;
   }

   public ItemStack getSmg() {
      return this.smg;
   }

   public ItemStack getRifle() {
      return this.rifle;
   }

   @Override
   public boolean isStarted() {
      return this.started;
   }
}
