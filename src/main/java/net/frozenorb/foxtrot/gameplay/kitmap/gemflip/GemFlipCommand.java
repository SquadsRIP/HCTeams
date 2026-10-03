package net.frozenorb.foxtrot.gameplay.kitmap.gemflip;

import cc.fyre.proton.command.param.Parameter;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipSide;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu.WagerListMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu.WagerMenu;
import net.frozenorb.foxtrot.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

public class GemFlipCommand {
   public static Set<Pair<Integer, GemFlipSide>> entryInfo = new HashSet<>();

   public static void gemFlip(Player player, @Parameter(name = "gems") int gems) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         player.sendMessage(SpigotConfig.unknownCommandMessage);
      } else {
         GemFlipHandler gemFlipHandler = Foxtrot.getInstance().getGemFlipHandler();
         long balance = Foxtrot.getInstance().getGemMap().getGems(player.getUniqueId());
         if (gems < 10) {
            player.sendMessage(ChatColor.YELLOW + "You must have a minimum of 10 Gems to wager!");
         } else if (gemFlipHandler.getEntry(player) == null && gemFlipHandler.getActiveWager(player) == null) {
            if (gems > balance) {
               player.sendMessage(ChatColor.YELLOW + "Insufficient Gems!");
            } else if (Foxtrot.getInstance().getGemFlipHandler().getQueue().size() >= 8) {
               player.sendMessage(ChatColor.YELLOW + "The wager queue is currently full. Please try again later.");
            } else {
               new WagerMenu(entryInfo, gems).openMenu(player);
            }
         } else {
            player.sendMessage(ChatColor.YELLOW + "You already have an active gem flip wager.");
         }
      }
   }

   public static void execute(Player player) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         player.sendMessage(SpigotConfig.unknownCommandMessage);
      } else {
         new WagerListMenu().openMenu(player);
      }
   }

   public static void cancel(Player player) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         player.sendMessage(SpigotConfig.unknownCommandMessage);
      } else {
         GemFlipEntry entry = Foxtrot.getInstance().getGemFlipHandler().removeEntry(player);
         if (entry == null) {
            player.sendMessage(ChatColor.YELLOW + "You do not have an active gem flip wager.");
         } else {
            player.sendMessage(ChatColor.YELLOW + "You have cancelled your gem flip wager.");
            Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), entry.getAmount(), true);
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
         }
      }
   }

   public static void total(Player player) {
      player.sendMessage("");
      player.sendMessage(ChatColor.DARK_GREEN + "Total Wagered: " + ChatColor.GREEN + Foxtrot.getInstance().getGemFlipHandler().getTotalWagered());
   }

   static {
      entryInfo.add(new Pair<>(3, GemFlipSide.HEADS));
      entryInfo.add(new Pair<>(5, GemFlipSide.TAILS));
   }
}
