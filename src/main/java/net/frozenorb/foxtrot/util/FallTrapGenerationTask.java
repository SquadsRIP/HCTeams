package net.frozenorb.foxtrot.util;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

public class FallTrapGenerationTask {
   private FallTrapGenerationTask.PhaseType phase = FallTrapGenerationTask.PhaseType.WALLS;
   private BukkitTask task;
   private long start;
   private final Team team;
   private final Claim claim;
   private final Material material;
   private final byte data;
   private FastBlockUpdate fastBlockUpdate = new FastBlockUpdate(Foxtrot.getInstance(), 1);

   public void start() {
      this.task = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), this::onTimer, 0L, 20L);
   }

   public void cancel() {
      this.task.cancel();
   }

   private void onTimer() {
      if (this.phase.equals(FallTrapGenerationTask.PhaseType.WALLS)) {
         if (this.fastBlockUpdate.isComplete()) {
            this.fastBlockUpdate.cancel();
            this.team.sendMessage(ChatColor.GREEN + "Faller phase " + ChatColor.WHITE + this.phase.getName() + ChatColor.GREEN + " complete!");
            this.phase = FallTrapGenerationTask.PhaseType.EMPTY;
         } else if (this.fastBlockUpdate.isRunning()) {
            double percentage = 100.0
               - (double)this.fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size() / this.fastBlockUpdate.getBlocks().size() * 100.0;
            if (percentage % 5.0 == 0.0) {
               this.team.sendMessage(ChatColor.GREEN + "FallTrap generation is at " + ChatColor.WHITE + "(" + (int)percentage + "%)" + ChatColor.GREEN + ".");
               this.team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + this.phase.getName());
            }
         } else {
            Location min = this.claim.getMinimumPoint();
            Location max = this.claim.getMaximumPoint();
            World world = min.getWorld();
            Cuboid cuboid = new Cuboid(min, max);

            for (Cuboid.CuboidDirection direction : Arrays.asList(
               Cuboid.CuboidDirection.North, Cuboid.CuboidDirection.East, Cuboid.CuboidDirection.South, Cuboid.CuboidDirection.West
            )) {
               for (Block block : cuboid.getFace(direction)) {
                  for (int i = world.getHighestBlockYAt(max) + 11; i > 0; i--) {
                     Location location = new Location(world, block.getX(), i, block.getZ());
                     if (i < 63 || !Arrays.asList(Material.AIR).contains(location.getBlock().getType())) {
                        this.fastBlockUpdate.addBlock(location, this.material, this.data);
                     }
                  }
               }
            }

            this.fastBlockUpdate.start();
            this.start = System.currentTimeMillis();
            this.team.sendMessage(ChatColor.GREEN + "Starting Faller generation around your selection.");
            this.team.sendMessage(ChatColor.GREEN + "Faller phase " + ChatColor.GREEN + this.phase.getName() + ChatColor.GREEN + " started!");
         }
      } else {
         if (this.phase.equals(FallTrapGenerationTask.PhaseType.EMPTY)) {
            if (this.fastBlockUpdate.isComplete()) {
               this.fastBlockUpdate.cancel();
               this.cancel();
               this.team
                  .sendMessage(
                     ChatColor.GREEN
                        + "Finished Faller generation around your selection. It took "
                        + ChatColor.WHITE
                        + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - this.start, true, true)
                        + ChatColor.GREEN
                        + " to make it."
                  );
               return;
            }

            if (this.fastBlockUpdate.isRunning()) {
               double percentage = 100.0
                  - (double)this.fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size() / this.fastBlockUpdate.getBlocks().size() * 100.0;
               if (percentage % 5.0 == 0.0) {
                  this.team.sendMessage(ChatColor.GREEN + "Faller generation is at " + ChatColor.GREEN + "(" + (int)percentage + "%)" + ChatColor.GREEN + ".");
                  this.team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.GREEN + this.phase.getName());
               }

               return;
            }

            Location min = this.claim.getMinimumPoint();
            Location max = this.claim.getMaximumPoint();
            World world = min.getWorld();
            int minX = min.getBlockX();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxZ = max.getBlockZ();

            for (int x = minX; x <= maxX; x++) {
               for (int y = world.getHighestBlockYAt(max) + 11; y > 0; y--) {
                  for (int z = minZ; z <= maxZ; z++) {
                     Location location = new Location(world, x, y, z);
                     if (!Arrays.asList(Material.AIR, this.material).contains(location.getBlock().getType())) {
                        this.fastBlockUpdate.addBlock(location, Material.AIR, (byte)0);
                     }
                  }
               }
            }

            this.fastBlockUpdate.start();
            this.team.sendMessage(ChatColor.GREEN + "Faller phase " + ChatColor.WHITE + this.phase.getName() + ChatColor.GREEN + " started!");
         }
      }
   }

   @ConstructorProperties({"team", "claim", "material", "data"})
   public FallTrapGenerationTask(Team team, Claim claim, Material material, byte data) {
      this.team = team;
      this.claim = claim;
      this.material = material;
      this.data = data;
   }

   public FallTrapGenerationTask.PhaseType getPhase() {
      return this.phase;
   }

   public enum PhaseType {
      EMPTY("Clear Blocks"),
      WALLS("Wall Blocks");

      private String name;

      PhaseType(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}
