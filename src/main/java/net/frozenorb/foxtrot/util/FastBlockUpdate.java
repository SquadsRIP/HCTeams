package net.frozenorb.foxtrot.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FastBlockUpdate {
   private JavaPlugin javaPlugin;
   private int blocksPerTick;
   private Map<Location, Map<Material, Byte>> blocks = Maps.newLinkedHashMap();
   private FastBlockUpdate.FastBlockUpdateTask fastBlockUpdateTask = null;

   public FastBlockUpdate(JavaPlugin javaPlugin, int blocksPerTick) {
      this.javaPlugin = javaPlugin;
      this.blocksPerTick = blocksPerTick;
   }

   public void addBlock(Location location, Material material, Byte b) {
      HashMap inner = Maps.newHashMap();
      inner.put(material, b);
      this.getBlocks().put(location, inner);
   }

   public void start() {
      this.fastBlockUpdateTask = new FastBlockUpdate.FastBlockUpdateTask(this.getBlocksPerTick(), this.getBlocks());
      this.fastBlockUpdateTask.start();
   }

   public void cancel() {
      if (this.isComplete()) {
         this.getBlocks().clear();
         this.fastBlockUpdateTask = null;
      }
   }

   public boolean isComplete() {
      return this.fastBlockUpdateTask != null && this.fastBlockUpdateTask.isComplete();
   }

   public boolean isRunning() {
      return this.fastBlockUpdateTask != null && this.fastBlockUpdateTask.isRunning();
   }

   public int getBlocksPerTick() {
      return this.blocksPerTick;
   }

   public void setBlocksPerTick(int blocksPerTick) {
      this.blocksPerTick = blocksPerTick;
   }

   public Map<Location, Map<Material, Byte>> getBlocks() {
      return this.blocks;
   }

   public void setBlocks(Map<Location, Map<Material, Byte>> blocks) {
      this.blocks = blocks;
   }

   public FastBlockUpdate.FastBlockUpdateTask getFastBlockUpdateTask() {
      return this.fastBlockUpdateTask;
   }

   public class FastBlockUpdateTask {
      Map<Location, Map<Material, Byte>> blocksRemaining = Maps.newLinkedHashMap();
      private List<BukkitRunnable> tasks = Lists.newArrayList();
      private int blocksPerTicks;
      private boolean complete;
      private boolean running = true;

      FastBlockUpdateTask(int blocksPerSecond, Map<Location, Map<Material, Byte>> blocks) {
         this.blocksPerTicks = blocksPerSecond;
         this.blocksRemaining.putAll(blocks);
      }

      public void start() {
         for (int i = 0; i < this.blocksRemaining.size() / this.blocksPerTicks + 1; i++) {
            this.tasks
               .add(
                  new BukkitRunnable() {
                     @Override
                     public void run() {
                        for (int blocks = 0;
                           blocks < FastBlockUpdateTask.this.blocksPerTicks && FastBlockUpdateTask.this.blocksRemaining.entrySet().iterator().hasNext();
                           blocks++
                        ) {
                           Entry<Location, Map<Material, Byte>> entry = FastBlockUpdateTask.this.blocksRemaining.entrySet().iterator().next();
                           Location key = entry.getKey();
                           Map<Material, Byte> value = entry.getValue();
                           Material material = null;
                           Iterator<Material> iterator = value.keySet().iterator();

                           while (iterator.hasNext()) {
                              material = iterator.next();
                           }

                           Byte data = value.get(material);
                           key.getBlock().setTypeIdAndData(material.getId(), data, false);
                           key.getBlock().getWorld().playEffect(key.getBlock().getLocation(), Effect.STEP_SOUND, key.getBlock().getType());
                           FastBlockUpdateTask.this.blocksRemaining.remove(key);
                        }

                        if (FastBlockUpdateTask.this.blocksRemaining.size() < 1) {
                           FastBlockUpdateTask.this.setComplete(true);
                           FastBlockUpdateTask.this.setRunning(false);

                           for (BukkitRunnable runnable : FastBlockUpdateTask.this.tasks) {
                              runnable.cancel();
                           }
                        }
                     }
                  }
               );
         }

         for (int var3 = 0; var3 < this.tasks.size(); var3++) {
            BukkitRunnable runnable = this.tasks.get(var3);
            runnable.runTaskLater(FastBlockUpdate.this.javaPlugin, var3 + 1);
         }
      }

      public Map<Location, Map<Material, Byte>> getBlocksRemaining() {
         return this.blocksRemaining;
      }

      public boolean isComplete() {
         return this.complete;
      }

      public void setComplete(boolean complete) {
         this.complete = complete;
      }

      public boolean isRunning() {
         return this.running;
      }

      public void setRunning(boolean running) {
         this.running = running;
      }
   }
}
