package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamStuckCommand implements Listener {
   private static final double MAX_DISTANCE = 5.0;
   private static final Set<Integer> warn = new HashSet<>();
   private static Map<String, Long> warping = new ConcurrentHashMap<>();
   private static List<String> damaged = Lists.newArrayList();

   @Command(
      names = {
            "team stuck",
            "t stuck",
            "f stuck",
            "faction stuck",
            "fac stuck",
            "stuck",
            "team unstuck",
            "t unstuck",
            "f unstuck",
            "faction unstuck",
            "fac unstuck",
            "unstuck"
      },
      permission = ""
   )
   public static void teamStuck(final Player sender) {
      if (warping.containsKey(sender.getName())) {
         sender.sendMessage(ChatColor.RED + "You are already being warped!");
      } else if (sender.getWorld().getEnvironment() != Environment.NORMAL) {
         sender.sendMessage(ChatColor.RED + "You can only use this command from the over-world.");
      } else if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
         sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
      } else if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
         && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
         && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a game!");
      } else {
         int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 2 : 60;
         warping.put(sender.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));
         (new BukkitRunnable() {
               private int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 2 : 60;
               private Location loc = sender.getLocation();
               private int xStart = (int)this.loc.getX();
               private int yStart = (int)this.loc.getY();
               private int zStart = (int)this.loc.getZ();
               private Location nearest;

               @Override
               public void run() {
                  if (TeamStuckCommand.damaged.contains(sender.getName())) {
                     sender.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
                     TeamStuckCommand.damaged.remove(sender.getName());
                     TeamStuckCommand.warping.remove(sender.getName());
                     this.cancel();
                  } else if (!sender.isOnline()) {
                     TeamStuckCommand.warping.remove(sender.getName());
                     this.cancel();
                  } else {
                     if (this.seconds == 5) {
                        (new BukkitRunnable() {
                           @Override
                           public void run() {
                              nearest = TeamStuckCommand.nearestSafeLocation(sender.getLocation());
                           }
                        }).runTask(Foxtrot.getInstance());
                     }

                     Location loc = sender.getLocation();
                     if (this.seconds <= 0) {
                        if (this.nearest == null) {
                           TeamStuckCommand.kick(sender);
                        } else {
                           Foxtrot.getInstance()
                              .getLogger()
                              .info(
                                 "Moved "
                                    + sender.getName()
                                    + " "
                                    + loc.distance(this.nearest)
                                    + " blocks from "
                                    + TeamStuckCommand.toStr(loc)
                                    + " to "
                                    + TeamStuckCommand.toStr(this.nearest)
                              );
                           sender.teleport(this.nearest);
                           sender.sendMessage(ChatColor.YELLOW + "Teleported you to the nearest safe area!");
                        }

                        TeamStuckCommand.warping.remove(sender.getName());
                        this.cancel();
                     } else if (!(loc.getX() >= this.xStart + 5.0)
                        && !(loc.getX() <= this.xStart - 5.0)
                        && !(loc.getY() >= this.yStart + 5.0)
                        && !(loc.getY() <= this.yStart - 5.0)
                        && !(loc.getZ() >= this.zStart + 5.0)
                        && !(loc.getZ() <= this.zStart - 5.0)) {
                        this.seconds--;
                     } else {
                        sender.sendMessage(ChatColor.RED + "You moved more than " + 5.0 + " blocks, teleport cancelled!");
                        TeamStuckCommand.warping.remove(sender.getName());
                        this.cancel();
                     }
                  }
               }
            })
            .runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
      }
   }

   private static String toStr(Location loc) {
      return "{x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + "}";
   }

   public static Location nearestSafeLocation(Location origin) {
      LandBoard landBoard = LandBoard.getInstance();
      if (landBoard.getClaim(origin) == null) {
         return getActualHighestBlock(origin.getBlock()).getLocation().add(0.0, 1.0, 0.0);
      }

      int xPos = 2;

      for (int xNeg = -2; xPos < 250; xNeg -= 2) {
         int zPos = 2;

         for (int zNeg = -2; zPos < 250; zNeg -= 2) {
            Location atPos = origin.clone().add(xPos, 0.0, zPos);
            if (landBoard.getClaim(atPos) == null && !isAdjacentClaimed(atPos)) {
               return getActualHighestBlock(atPos.getBlock()).getLocation().add(0.0, 1.0, 0.0);
            }

            Location atNeg = origin.clone().add(xNeg, 0.0, zNeg);
            if (landBoard.getClaim(atNeg) == null && !isAdjacentClaimed(atNeg)) {
               return getActualHighestBlock(atNeg.getBlock()).getLocation().add(0.0, 1.0, 0.0);
            }

            zPos += 2;
         }

         xPos += 2;
      }

      return null;
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onPlayerDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (warping.containsKey(player.getName())) {
            damaged.add(player.getName());
         }
      }
   }

   private static Block getActualHighestBlock(Block block) {
      block = block.getWorld().getHighestBlockAt(block.getLocation());

      while (block.getType() == Material.AIR && block.getY() > 0) {
         block = block.getRelative(BlockFace.DOWN);
      }

      return block;
   }

   private static void kick(Player player) {
      player.setMetadata("loggedout", new FixedMetadataValue(Foxtrot.getInstance(), true));
      player.kickPlayer(
         ChatColor.RED
            + "We couldn't find a safe location, so we safely logged you out for now. Contact a staff member before logging back on! "
            + ChatColor.BLUE
            + "Support: "
            + Foxtrot.getInstance().getServerHandler().getNetworkWebsite()
      );
   }

   private static List<Location> getAdjacent(Location base) {
      List<Location> adjacent = new ArrayList<>();

      for (BlockFace face : BlockFace.values()) {
         if (face != BlockFace.DOWN && face != BlockFace.UP) {
            adjacent.add(base.getBlock().getRelative(face).getLocation());
         }
      }

      return adjacent;
   }

   private static boolean isAdjacentClaimed(Location location) {
      for (Location adjacent : getAdjacent(location)) {
         if (LandBoard.getInstance().getClaim(adjacent) != null) {
            return true;
         }
      }

      return false;
   }

   public static Map<String, Long> getWarping() {
      return warping;
   }

   static {
      warn.add(300);
      warn.add(270);
      warn.add(240);
      warn.add(210);
      warn.add(180);
      warn.add(150);
      warn.add(120);
      warn.add(90);
      warn.add(60);
      warn.add(30);
      warn.add(10);
      warn.add(5);
      warn.add(4);
      warn.add(3);
      warn.add(2);
      warn.add(1);
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new TeamStuckCommand(), Foxtrot.getInstance());
   }
}
