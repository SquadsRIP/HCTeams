package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TNTTagGame extends Game {
   private boolean started = false;
   private List<UUID> tagged = new ArrayList<>();
   private int startingSeconds = 60;
   private int nextExplosion = 0;
   private int round = 0;

   public TNTTagGame(UUID host, List<GameArena> arenaOptions) {
      super(host, GameType.TNT_TAG, arenaOptions);
   }

   @Override
   public void startGame() {
      super.startGame();
      this.started = false;
      if (this.getVotedArena().getBounds() != null) {
         this.getVotedArena().createSnapshot();
      }

      (new BukkitRunnable() {
         @Override
         public void run() {
            if (TNTTagGame.this.state == GameState.ENDED) {
               this.cancel();
            } else {
               if (TNTTagGame.this.state == GameState.RUNNING) {
                  TNTTagGame.this.startTNTTag();
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
   }

   public List<Player> findTaggedPlayers() {
      return this.tagged
         .stream()
         .filter(it -> Foxtrot.getInstance().getServer().getPlayer(it) != null)
         .map(it -> Foxtrot.getInstance().getServer().getPlayer(it))
         .collect(Collectors.toList());
   }

   @Override
   public void endGame() {
      super.endGame();
      if (this.getVotedArena().getBounds() != null) {
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this.getVotedArena()::restoreSnapshot, 100L);
      }
   }

   public List<Player> selectTagged() {
      List<Player> nonTagged = new ArrayList<>(this.getPlayers());

      for (int i = 0; i < Math.ceil(this.getPlayers().size()) / 5.0; i++) {
         Player player = nonTagged.get(ThreadLocalRandom.current().nextInt(nonTagged.size()));
         if (this.tagged.contains(player.getUniqueId())) {
            i--;
         } else {
            nonTagged.remove(player);
            this.setTagged(player, true);
         }
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (Player onlinePlayer : this.findTaggedPlayers()) {
         stringBuilder.append(stringBuilder.length() == 0 ? "" : ChatColor.WHITE + ", ")
            .append(Neutron.getInstance().getProfileHandler().findDisplayName(onlinePlayer.getUniqueId()));
      }

      this.sendMessages(
         "",
         "&4&lTNT Tag",
         "&7A new round has started!",
         stringBuilder.toString() + " &f" + (this.findTaggedPlayers().size() > 1 ? "are" : "is") + " now &cit&f!",
         ""
      );

      for (Player player : nonTagged) {
         this.setTagged(player, false);
      }

      return this.findTaggedPlayers();
   }

   public void setTagged(Player player, boolean state) {
      if (!state) {
         this.tagged.remove(player.getUniqueId());
         player.getInventory().clear();
         player.getInventory().setArmorContents(null);
         player.updateInventory();
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
      } else {
         this.tagged.add(player.getUniqueId());
         player.getInventory().clear();
         player.getEquipment().setHelmet(new ItemStack(Material.TNT));
         player.getEquipment().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(ColorUtil.COLOR_MAP.get(ChatColor.RED).getColor()).build());
         player.getEquipment().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(ColorUtil.COLOR_MAP.get(ChatColor.RED).getColor()).build());
         player.getEquipment().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(ColorUtil.COLOR_MAP.get(ChatColor.RED).getColor()).build());
         player.removePotionEffect(PotionEffectType.SPEED);
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
         player.updateInventory();

         for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, new ItemStack(Material.TNT));
         }

         player.getInventory().setItem(4, new ItemStack(Material.BLAZE_ROD));
         player.updateInventory();
         VoucherCommand.spawnFireworks(player.getLocation(), 1, 1, Color.RED, Type.STAR);
      }
   }

   private void startTNTTag() {
      this.started = false;
      this.round = 1;

      for (Player player : this.getPlayers()) {
         InventoryUtils.resetInventoryNow(player);
         player.teleport(this.getVotedArena().getPointA());
      }

      this.setStartedAt(System.currentTimeMillis());
      (new BukkitRunnable() {
         private int i = 6;

         @Override
         public void run() {
            if (TNTTagGame.this.state != GameState.ENDED && !TNTTagGame.this.started) {
               this.i--;
               TNTTagGame.this.sendSound(Sound.NOTE_PLING, 1.0F, this.i == 0 ? 2.0F : 1.0F);
               TNTTagGame.this.started = this.i == 0;
               TNTTagGame.this.startingSeconds = 60;
               TNTTagGame.this.nextExplosion = 60;
               if (this.i == 0) {
                  TNTTagGame.this.sendMessages(ChatColor.GREEN + "TNT Tag event has begun!");
                  TNTTagGame.this.selectTagged();
               } else {
                  TNTTagGame.this.sendMessages("&6&lTNT Tag &eevent begins in &f" + this.i + " second" + (this.i == 1 ? "" : "s") + "&e...");
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
      (new BukkitRunnable() {
            @Override
            public void run() {
               if (TNTTagGame.this.started) {
                  if (TNTTagGame.this.state == GameState.ENDED) {
                     this.cancel();
                  } else if (TNTTagGame.this.getPlayers().size() < 2) {
                     TNTTagGame.this.endGame();
                     this.cancel();
                  } else {
                     if (TNTTagGame.this.tagged.isEmpty()) {
                        TNTTagGame.this.selectTagged();
                     }

                     TNTTagGame.this.nextExplosion--;
                     if (TNTTagGame.this.nextExplosion > 0
                        && TNTTagGame.this.nextExplosion <= 30
                        && (TNTTagGame.this.nextExplosion % 10 == 0 || TNTTagGame.this.nextExplosion <= 3)) {
                        TNTTagGame.this.sendMessages("&cAll tagged players will explode in &f" + TNTTagGame.this.nextExplosion + " seconds&c.");
                     }

                     if (TNTTagGame.this.nextExplosion <= 0) {
                        for (Player taggedPlayer : TNTTagGame.this.findTaggedPlayers()) {
                           taggedPlayer.getLocation()
                              .getWorld()
                              .createExplosion(
                                 taggedPlayer.getLocation().getBlockX(),
                                 taggedPlayer.getLocation().getBlockY(),
                                 taggedPlayer.getLocation().getBlockZ(),
                                 2.0F,
                                 false,
                                 false
                              );
                           TNTTagGame.this.eliminatePlayer(taggedPlayer, null);
                        }

                        TNTTagGame.this.selectTagged();
                        TNTTagGame.this.round = TNTTagGame.this.round + 1;
                        TNTTagGame.this.startingSeconds = TNTTagGame.this.startingSeconds - 5;
                        TNTTagGame.this.nextExplosion = TNTTagGame.this.startingSeconds;
                        if (TNTTagGame.this.startingSeconds < 20) {
                           TNTTagGame.this.startingSeconds = 20;
                           TNTTagGame.this.nextExplosion = 20;
                        }

                        if (TNTTagGame.this.getPlayers().size() <= 8) {
                           for (Player player : TNTTagGame.this.getPlayers()) {
                              player.teleport(TNTTagGame.this.getVotedArena().getPointA());
                           }
                        }
                     }
                  }
               }
            }
         })
         .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   @Override
   public void eliminatePlayer(Player player, Player killer) {
      this.tagged.remove(player.getUniqueId());
      super.eliminatePlayer(player, killer);
      this.addSpectator(player);
      if (this.players.size() == 1) {
         this.endGame();
      }
   }

   public double getDeathHeight() {
      return Math.min(this.getVotedArena().getPointA().getBlockY(), this.getVotedArena().getPointB().getBlockY()) - 2.9;
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
      } else {
         if (this.state == GameState.RUNNING) {
            lines.add(" &6Remaining: &f" + this.players.size() + "&7/&f" + this.getStartedWith());
            lines.add(" &6Round: &f" + this.round);
            lines.add("");
            lines.add(" &6Explosion: &f" + this.nextExplosion + "s");
            lines.add(" &6Tagged Players: &f" + this.tagged.size());
         } else if (this.winningPlayer == null) {
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
            .then("█████")
            .color(ChatColor.DARK_RED)
            .then("█")
            .color(ChatColor.GRAY)
            .then(" " + this.getGameType().getDisplayName() + " Event")
            .color(ChatColor.DARK_RED)
            .style(ChatColor.BOLD),
         new FancyMessage("").then("███").color(ChatColor.GRAY).then("█").color(ChatColor.DARK_RED).then("███").color(ChatColor.GRAY),
         new FancyMessage("")
            .then("███")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
         new FancyMessage("")
            .then("███")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
         new FancyMessage("")
            .then("███")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then(" Click to join the event")
            .color(ChatColor.GREEN)
            .command("/game join")
            .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW)),
         new FancyMessage("███████").color(ChatColor.GRAY)
      );
   }

   @Override
   public boolean isStarted() {
      return this.started;
   }

   public List<UUID> getTagged() {
      return this.tagged;
   }

   public int getStartingSeconds() {
      return this.startingSeconds;
   }

   public int getNextExplosion() {
      return this.nextExplosion;
   }

   public int getRound() {
      return this.round;
   }
}
