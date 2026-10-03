package net.frozenorb.foxtrot.util;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
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
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

public class BaseGenerationTask {
   private BaseGenerationTask.PhaseType phase = BaseGenerationTask.PhaseType.EMPTY;
   private BukkitTask task;
   private long start;
   private final Team team;
   private final Claim claim;
   private final Material baseMaterial;
   private final byte baseId;
   private int highest;
   private FastBlockUpdate fastBlockUpdate = new FastBlockUpdate(Foxtrot.getInstance(), 1);

   public void start() {
      this.task = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), this::onTimer, 0L, 20L);
   }

   public void cancel() {
      this.task.cancel();
   }

   private void onTimer() {
      if (this.phase.equals(BaseGenerationTask.PhaseType.EMPTY)) {
         if (this.fastBlockUpdate.isComplete()) {
            this.fastBlockUpdate.cancel();
            this.team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + this.phase.getName() + ChatColor.GREEN + " complete!");
            this.phase = BaseGenerationTask.PhaseType.CELLING;
         } else if (this.fastBlockUpdate.isRunning()) {
            double percentage = 100.0
               - (double)this.fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size() / this.fastBlockUpdate.getBlocks().size() * 100.0;
            if (percentage % 5.0 == 0.0) {
               this.team.sendMessage(ChatColor.GREEN + "Base generation is at " + ChatColor.WHITE + "(" + (int)percentage + "%)" + ChatColor.GREEN + ".");
               this.team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + this.phase.getName());
            }
         } else {
            Location min = this.claim.getMinimumPoint();
            Location max = this.claim.getMaximumPoint();
            World world = min.getWorld();
            int minX = min.getBlockX();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxZ = max.getBlockZ();

            for (int x = minX; x <= maxX; x++) {
               for (int y = 100; y > 60; y--) {
                  for (int z = minZ; z <= maxZ; z++) {
                     Location location = new Location(world, x, y, z);
                     if (Arrays.asList(
                           Material.LOG,
                           Material.LONG_GRASS,
                           Material.RED_ROSE,
                           Material.YELLOW_FLOWER,
                           Material.DOUBLE_PLANT,
                           Material.DEAD_BUSH,
                           Material.CACTUS,
                           Material.LEAVES,
                           Material.LEAVES_2,
                           Material.FENCE
                        )
                        .contains(location.getBlock().getType())) {
                        this.fastBlockUpdate.addBlock(location, Material.AIR, (byte)0);
                     }
                  }
               }
            }

            this.fastBlockUpdate.start();
            this.start = System.currentTimeMillis();
            this.team.sendMessage(ChatColor.GREEN + "Starting Base generation around your selection.");
            this.team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + this.phase.getName() + ChatColor.GREEN + " started!");
         }
      } else if (this.phase.equals(BaseGenerationTask.PhaseType.CELLING)) {
         if (this.fastBlockUpdate.isComplete()) {
            this.fastBlockUpdate.cancel();
            this.team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.LIGHT_PURPLE + this.phase.getName() + ChatColor.GREEN + " complete!");
            this.phase = BaseGenerationTask.PhaseType.WALLS;
         } else if (this.fastBlockUpdate.isRunning()) {
            double percentage = 100.0
               - (double)this.fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size() / this.fastBlockUpdate.getBlocks().size() * 100.0;
            if (percentage % 5.0 == 0.0) {
               this.team.sendMessage(ChatColor.GREEN + "Base generation is at " + ChatColor.WHITE + "(" + (int)percentage + "%)" + ChatColor.GREEN + ".");
               this.team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + this.phase.getName());
            }
         } else {
            Location min = this.claim.getMinimumPoint();
            Location max = this.claim.getMaximumPoint();
            World world = min.getWorld();
            int minX = min.getBlockX();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxZ = max.getBlockZ();
            this.highest = world.getHighestBlockYAt(max) + 11;

            for (int x = minX; x <= maxX; x++) {
               for (int z = minZ; z <= maxZ; z++) {
                  Location location = new Location(world, x, this.highest, z);
                  this.fastBlockUpdate.addBlock(location, this.baseMaterial, this.baseId);
               }
            }

            this.fastBlockUpdate.start();
            this.team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + this.phase.getName() + ChatColor.GREEN + " started!");
         }
      } else {
         if (this.phase.equals(BaseGenerationTask.PhaseType.WALLS)) {
            if (this.fastBlockUpdate.isComplete()) {
               this.fastBlockUpdate.cancel();
               this.cancel();
               this.team
                  .sendMessage(
                     ChatColor.GREEN
                        + "Finished Base generation around your selection. It took "
                        + ChatColor.WHITE
                        + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - this.start, true, true)
                        + ChatColor.GREEN
                        + "."
                  );
               return;
            }

            if (this.fastBlockUpdate.isRunning()) {
               double percentage = 100.0
                  - (double)this.fastBlockUpdate.getFastBlockUpdateTask().getBlocksRemaining().size() / this.fastBlockUpdate.getBlocks().size() * 100.0;
               if (percentage % 5.0 == 0.0) {
                  this.team.sendMessage(ChatColor.GREEN + "Base generation is at " + ChatColor.WHITE + "(" + (int)percentage + "%)" + ChatColor.GREEN + ".");
                  this.team.sendMessage(ChatColor.GREEN + "  Phase: " + ChatColor.WHITE + this.phase.getName());
               }

               return;
            }

            Location min = this.claim.getMinimumPoint();
            Location max = this.claim.getMaximumPoint();
            World world = min.getWorld();
            Cuboid cuboid = new Cuboid(min, max);
            List<Material> override = Arrays.asList(
               Material.AIR,
               Material.LOG,
               Material.LONG_GRASS,
               Material.RED_ROSE,
               Material.YELLOW_FLOWER,
               Material.DOUBLE_PLANT,
               Material.DEAD_BUSH,
               Material.CACTUS,
               Material.LEAVES,
               Material.LEAVES_2,
               Material.FENCE
            );

            for (Cuboid.CuboidDirection direction : Arrays.asList(
               Cuboid.CuboidDirection.North, Cuboid.CuboidDirection.East, Cuboid.CuboidDirection.South, Cuboid.CuboidDirection.West
            )) {
               for (Block block : cuboid.getFace(direction)) {
                  for (int i = this.highest; i > 0; i--) {
                     Location location = new Location(world, block.getX(), i, block.getZ());
                     if (i >= 63 && override.contains(location.getBlock().getType())) {
                        this.fastBlockUpdate.addBlock(location, this.baseMaterial, this.baseId);
                        Block downBlock = location.getBlock().getRelative(BlockFace.DOWN);
                        if (!override.contains(downBlock.getType())) {
                           this.fastBlockUpdate.addBlock(downBlock.getLocation(), Material.WOOL, this.baseId);
                        }
                     }
                  }
               }
            }

            this.fastBlockUpdate.start();
            this.team.sendMessage(ChatColor.GREEN + "Base phase " + ChatColor.WHITE + this.phase.getName() + ChatColor.GREEN + " started!");
         }
      }
   }

   @ConstructorProperties({"team", "claim", "baseMaterial", "baseId"})
   public BaseGenerationTask(Team team, Claim claim, Material baseMaterial, byte baseId) {
      this.team = team;
      this.claim = claim;
      this.baseMaterial = baseMaterial;
      this.baseId = baseId;
   }

   public BaseGenerationTask.PhaseType getPhase() {
      return this.phase;
   }

   public enum PhaseType {
      EMPTY("Clear Blocks"),
      CELLING("Ceiling Blocks"),
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
