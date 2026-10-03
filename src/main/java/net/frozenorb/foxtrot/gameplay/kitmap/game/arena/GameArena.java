package net.frozenorb.foxtrot.gameplay.kitmap.game.arena;

import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.cuboid.Cuboid;
import java.util.ArrayList;
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

public class GameArena {
   private String name;
   private Location pointA;
   private Location pointB;
   private Location spectatorSpawn;
   private List<Location> alternateSpawns = new ArrayList<>();
   private Cuboid bounds;
   private final transient Map<Long, ChunkSnapshot> chunkSnapshots;
   private final List<String> compatibleGameTypes = new ArrayList<>();

   public GameArena() {
      this.chunkSnapshots = new HashMap<>();
   }

   public GameArena(String name) {
      this();
      this.name = name;
   }

   public boolean isSetup() {
      return this.pointA != null && this.pointB != null && this.spectatorSpawn != null;
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

   public Location getSpectatorSpawn() {
      return this.spectatorSpawn;
   }

   public Cuboid getBounds() {
      return this.bounds;
   }

   public Map<Long, ChunkSnapshot> getChunkSnapshots() {
      return this.chunkSnapshots;
   }

   public List<String> getCompatibleGameTypes() {
      return this.compatibleGameTypes;
   }

   public void setPointA(Location pointA) {
      this.pointA = pointA;
   }

   public void setPointB(Location pointB) {
      this.pointB = pointB;
   }

   public void setSpectatorSpawn(Location spectatorSpawn) {
      this.spectatorSpawn = spectatorSpawn;
   }

   public List<Location> getAlternateSpawns() {
      return this.alternateSpawns;
   }

   public void setBounds(Cuboid bounds) {
      this.bounds = bounds;
   }

   public static class Type implements ParameterType<GameArena> {
      public GameArena transform(CommandSender sender, String source) {
         GameArena arena = Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().getArenaByName(source);
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

         for (GameArena arena : Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().getArenas()) {
            if (StringUtils.startsWith(arena.getName(), source)) {
               completions.add(arena.getName());
            }
         }

         return completions;
      }
   }
}
