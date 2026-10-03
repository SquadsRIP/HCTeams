package net.frozenorb.foxtrot.gameplay.kitmap.duel.arena;

import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.cuboid.Cuboid;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.util.com.google.common.collect.Lists;
import net.valorhcf.ChunkSnapshot;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.util.LongHash;
import org.bukkit.entity.Player;

public class DuelArena {
   private final String name;
   private Location pointA;
   private Location pointB;
   private Cuboid bounds;
   private final Map<Long, ChunkSnapshot> chunkSnapshots = new HashMap<>();

   public boolean isSetup() {
      return this.pointA != null && this.pointB != null;
   }

   public void createSnapshot() {
      synchronized (this.chunkSnapshots) {
         this.bounds.getChunks().forEach(chunk -> {
            ChunkSnapshot var10000 = this.chunkSnapshots.put(LongHash.toLong(chunk.getX(), chunk.getZ()), chunk.takeSnapshot());
         });
      }
   }

   public void restoreSnapshot() {
      synchronized (this.chunkSnapshots) {
         World world = this.bounds.getWorld();
         this.chunkSnapshots.forEach((key, value) -> world.getChunkAt(LongHash.msw(key), LongHash.lsw(key)).restoreSnapshot(value));
         this.chunkSnapshots.clear();
      }
   }

   public String getName() {
      return this.name;
   }

   public Location getPointA() {
      return this.pointA;
   }

   public Location getPointB() {
      return this.pointB;
   }

   public Cuboid getBounds() {
      return this.bounds;
   }

   public Map<Long, ChunkSnapshot> getChunkSnapshots() {
      return this.chunkSnapshots;
   }

   @ConstructorProperties("name")
   public DuelArena(String name) {
      this.name = name;
   }

   public void setPointA(Location pointA) {
      this.pointA = pointA;
   }

   public void setPointB(Location pointB) {
      this.pointB = pointB;
   }

   public void setBounds(Cuboid bounds) {
      this.bounds = bounds;
   }

   public static class Type implements ParameterType<DuelArena> {
      public DuelArena transform(CommandSender sender, String source) {
         DuelArena arena = Foxtrot.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenaByName(source);
         if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena named '" + source + "' couldn't be found.");
            return null;
         } else {
            return arena;
         }
      }

      @Override
      public List<String> tabComplete(Player player, Set<String> flags, String source) {
         List<String> completions = Lists.newArrayList();

         for (DuelArena arena : Foxtrot.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenas()) {
            if (StringUtils.startsWith(arena.getName(), source)) {
               completions.add(arena.getName());
            }
         }

         return completions;
      }
   }
}
