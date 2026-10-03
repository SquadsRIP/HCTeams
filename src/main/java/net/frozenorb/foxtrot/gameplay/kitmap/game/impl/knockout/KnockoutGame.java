package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.knockout;

import cc.fyre.neutron.Neutron;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class KnockoutGame extends Game {
   private boolean started = false;

   public KnockoutGame(UUID host, List<GameArena> arenaOptions) {
      super(host, GameType.KNOCK_OUT, arenaOptions);
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
            if (KnockoutGame.this.state == GameState.ENDED) {
               this.cancel();
            } else {
               if (KnockoutGame.this.state == GameState.RUNNING) {
                  KnockoutGame.this.startKnockout();
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
      if (this.getVotedArena().getBounds() != null) {
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this.getVotedArena()::restoreSnapshot, 100L);
      }
   }

   private void startKnockout() {
      for (Player player : this.getPlayers()) {
         InventoryUtils.resetInventoryNow(player);
         ItemStack itemStack = new ItemStack(Material.STICK, 1);
         itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
         player.getInventory().addItem(itemStack);
         player.teleport(this.getVotedArena().getPointA());
      }

      this.started = false;
      this.setStartedAt(System.currentTimeMillis());
      (new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
               if (KnockoutGame.this.state != GameState.ENDED && !KnockoutGame.this.started) {
                  this.i--;
                  KnockoutGame.this.sendSound(Sound.NOTE_PLING, 1.0F, this.i == 0 ? 2.0F : 1.0F);
                  KnockoutGame.this.started = this.i == 0;
                  if (this.i == 0) {
                     KnockoutGame.this.sendMessages("&6&l" + KnockoutGame.this.getGameType().getDisplayName() + " &eevent has begun!");
                  } else {
                     KnockoutGame.this.sendMessages(
                        "&6&l"
                           + KnockoutGame.this.getGameType().getDisplayName()
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
      this.addSpectator(player);
      if (this.players.size() == 1) {
         this.endGame();
      }
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
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("██")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("██")
            .color(ChatColor.GRAY),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("███")
            .color(ChatColor.DARK_RED)
            .then("███")
            .color(ChatColor.GRAY)
            .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
         new FancyMessage("")
            .then("█")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("██")
            .color(ChatColor.GRAY)
            .then("█")
            .color(ChatColor.DARK_RED)
            .then("██")
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

   @Override
   public boolean isStarted() {
      return this.started;
   }
}
