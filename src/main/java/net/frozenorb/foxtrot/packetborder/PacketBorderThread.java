package net.frozenorb.foxtrot.packetborder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PacketBorderThread extends Thread {
   public static final int REGION_DISTANCE = 8;
   public static final int REGION_DISTANCE_SQUARED = 64;
   private static Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<>();

   public PacketBorderThread() {
      super("Foxtrot - Packet Border Thread");
   }

   @Override
   public void run() {
      while (true) {
         for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            try {
               this.checkPlayer(player);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         try {
            Thread.sleep(250L);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   private void checkPlayer(Player player) {
      try {
         List<Claim> claims = new LinkedList<>();
         if (player.getGameMode() == GameMode.CREATIVE) {
            return;
         }

         Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         boolean tagged = SpawnTagHandler.isTagged(player);
         boolean hasPvPTimer = Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
         if (!CustomTimerCreateCommand.isSOTWTimer() && !tagged && !hasPvPTimer && playerTeam != null && playerTeam.getOnlineMembers().size() >= 4) {
            clearPlayer(player);
            return;
         }

         for (Entry<Claim, Team> regionDataEntry : LandBoard.getInstance().getRegionData(player.getLocation(), 8, 8, 8)) {
            Claim claim = regionDataEntry.getKey();
            Team team = regionDataEntry.getValue();
            if (!claim.contains(player)) {
               if (team.getOwner() == null) {
                  if (team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && tagged) {
                     claims.add(claim);
                  } else if ((team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(DTRBitmask.OUTPOST))
                     && hasPvPTimer) {
                     claims.add(claim);
                  } else if (!Foxtrot.getInstance().getMapHandler().isKitMap()
                     && (team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(DTRBitmask.FURY))
                     && (playerTeam == null || playerTeam.getOnlineMembers().size() < 4)) {
                     claims.add(claim);
                  }
               } else if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()) && !team.isMember(player.getUniqueId())) {
                  claims.add(claim);
               } else if (!team.isMember(player.getUniqueId()) && CustomTimerCreateCommand.isSOTWTimer() && team.isClaimLocked()) {
                  claims.add(claim);
               }
            }
         }

         if (claims.size() == 0) {
            clearPlayer(player);
         } else {
            if (!sentBlockChanges.containsKey(player.getName())) {
               sentBlockChanges.put(player.getName(), new HashMap<>());
            }

            Iterator<Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

            while (bordersIterator.hasNext()) {
               Entry<Location, Long> border = bordersIterator.next();
               if (System.currentTimeMillis() >= border.getValue()) {
                  Location loc = border.getKey();
                  if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                     Block block = loc.getBlock();
                     player.sendBlockChange(loc, block.getType(), block.getData());
                     bordersIterator.remove();
                  }
               }
            }

            for (Claim claim : claims) {
               this.sendClaimToPlayer(player, claim);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void sendClaimToPlayer(Player player, Claim claim) {
      for (Coordinate coordinate : claim) {
         Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());
         if (!(onPlayerY.distanceSquared(player.getLocation()) > 64.0)) {
            for (int i = -4; i < 5; i++) {
               Location check = onPlayerY.clone().add(0.0, i, 0.0);
               if (check.getWorld().isChunkLoaded(check.getBlockX() >> 4, check.getBlockZ() >> 4)
                  && check.getBlock().getType().isTransparent()
                  && check.distanceSquared(onPlayerY) < 64.0) {
                  player.sendBlockChange(check, Material.STAINED_GLASS, (byte)14);
                  sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 4000L);
               }
            }
         }
      }
   }

   private static void clearPlayer(Player player) {
      if (sentBlockChanges.containsKey(player.getName())) {
         for (Location changedLoc : sentBlockChanges.get(player.getName()).keySet()) {
            if (changedLoc.getWorld().isChunkLoaded(changedLoc.getBlockX() >> 4, changedLoc.getBlockZ() >> 4)) {
               Block block = changedLoc.getBlock();
               player.sendBlockChange(changedLoc, block.getType(), block.getData());
            }
         }

         sentBlockChanges.remove(player.getName());
      }
   }
}
