package net.frozenorb.foxtrot.nametag;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.Proton;
import cc.fyre.proton.nametag.construct.NameTagInfo;
import cc.fyre.proton.nametag.provider.NameTagProvider;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike.MineStrikeGame;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FoxtrotNametagProvider extends NameTagProvider {
   public FoxtrotNametagProvider() {
      super("Foxtrot Provider", 5);
   }

   @Override
   public NameTagInfo fetchNameTag(Player target, Player viewer) {
      Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(target.getUniqueId());
      Team viewerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(viewer);
      String tag = "";
      if (profile != null && profile.getActivePrefix() != null && !target.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
         String display = profile.getActivePrefix().getDisplay();
         String noColor = ChatColor.stripColor(profile.getActivePrefix().getDisplay());
         if (display.endsWith(ChatColor.DARK_GRAY + "]") && display.startsWith(ChatColor.DARK_GRAY + "[") && noColor.length() == 3) {
            tag = display.replace(ChatColor.DARK_GRAY + "[", "").replace(ChatColor.DARK_GRAY + "]", "");
         } else if (noColor.length() == 1) {
            tag = display;
         }
      }

      NameTagInfo nametagInfo = null;
      if (viewerTeam != null) {
         if (viewerTeam.isMember(target.getUniqueId())) {
            nametagInfo = this.generateTag(tag + Foxtrot.getInstance().getTeamColorMap().getChatColor(viewer.getUniqueId()));
         } else if (viewerTeam.isAlly(target.getUniqueId())) {
            nametagInfo = this.generateTag(tag + Foxtrot.getInstance().getArcherTagColorMap().getChatColor(viewer.getUniqueId()));
         }
      }

      if (nametagInfo == null) {
         if (ArcherClass.getMarkedPlayers().containsKey(target.getName()) && ArcherClass.getMarkedPlayers().get(target.getName()) > System.currentTimeMillis()) {
            nametagInfo = this.generateTag(tag + Foxtrot.getInstance().getArcherTagColorMap().getChatColor(viewer.getUniqueId()));
         } else if (viewerTeam != null && viewerTeam.getFocused() != null && viewerTeam.getFocused().equals(target.getUniqueId())) {
            nametagInfo = this.generateTag(tag + Foxtrot.getInstance().getFocusColorMap().getChatColor(viewer.getUniqueId()));
         } else if (viewerTeam != null && viewerTeam.getFocusedTeam() != null && viewerTeam.getFocusedTeam().getMembers().contains(target.getUniqueId())) {
            nametagInfo = this.generateTag(tag + Foxtrot.getInstance().getTeamFocusColorMap().getChatColor(viewer.getUniqueId()));
         } else if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(target.getUniqueId())
            || Foxtrot.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())) {
            nametagInfo = this.generateTag(tag + ChatColor.GOLD);
         }
      }

      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();
         if (gameHandler.getOngoingGame() != null && gameHandler.getOngoingGame() instanceof MineStrikeGame) {
            MineStrikeGame mineStrikeGame = (MineStrikeGame)gameHandler.getOngoingGame();
            if (mineStrikeGame.getRedTeam().contains(target.getUniqueId())) {
               nametagInfo = this.generateTag(tag + ChatColor.YELLOW);
            }

            if (mineStrikeGame.getBlueTeam().contains(target.getUniqueId())) {
               nametagInfo = this.generateTag(tag + ChatColor.BLUE);
            }

            if (mineStrikeGame.getBlueTeam().contains(target.getUniqueId()) && mineStrikeGame.getRedTeam().contains(viewer.getUniqueId())) {
               return Proton.getInstance().getNameTagHandler().getINVISIBLE();
            }

            if (mineStrikeGame.getBlueTeam().contains(viewer.getUniqueId()) && mineStrikeGame.getRedTeam().contains(target.getUniqueId())) {
               return Proton.getInstance().getNameTagHandler().getINVISIBLE();
            }
         } else if (target.hasPotionEffect(PotionEffectType.INVISIBILITY)
            && !ModHandler.INSTANCE.isInModMode(viewer.getPlayer().getUniqueId())
            && (viewerTeam == null || !viewerTeam.isMember(target.getUniqueId()))) {
            return Proton.getInstance().getNameTagHandler().getINVISIBLE();
         }
      } else if (target.hasPotionEffect(PotionEffectType.INVISIBILITY)
         && !ModHandler.INSTANCE.isInModMode(viewer.getPlayer().getUniqueId())
         && (viewerTeam == null || !viewerTeam.isMember(target.getUniqueId()))) {
         return Proton.getInstance().getNameTagHandler().getINVISIBLE();
      }

      if (viewer == target) {
         nametagInfo = this.generateTag(tag + Foxtrot.getInstance().getTeamColorMap().getChatColor(viewer.getUniqueId()));
      }

      return nametagInfo == null ? this.generateTag(tag + Foxtrot.getInstance().getEnemyColorMap().getChatColor(viewer.getUniqueId())) : nametagInfo;
   }

   private NameTagInfo generateTag(String prefix) {
      return this.createNameTag(prefix, "");
   }
}
