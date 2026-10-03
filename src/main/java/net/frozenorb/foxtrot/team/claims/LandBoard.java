package net.frozenorb.foxtrot.team.claims;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.util.com.google.common.collect.HashMultimap;
import net.minecraft.util.com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class LandBoard implements Listener {
   private static LandBoard instance;
   private boolean claimsEnabled = true;
   private final Map<String, Multimap<CoordinateSet, Entry<Claim, Team>>> buckets = new ConcurrentHashMap<>();

   public LandBoard() {
      for (World world : Foxtrot.getInstance().getServer().getWorlds()) {
         this.buckets.put(world.getName(), HashMultimap.create());
      }

      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
   }

   public void loadFromTeams() {
      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         for (Claim claim : team.getClaims()) {
            this.setTeamAt(claim, team);
         }
      }
   }

   public Set<Entry<Claim, Team>> getRegionData(Location center, int xDistance, int yDistance, int zDistance) {
      Location loc1 = new Location(center.getWorld(), center.getBlockX() - xDistance, center.getBlockY() - yDistance, center.getBlockZ() - zDistance);
      Location loc2 = new Location(center.getWorld(), center.getBlockX() + xDistance, center.getBlockY() + yDistance, center.getBlockZ() + zDistance);
      return this.getRegionData(loc1, loc2);
   }

   public Set<Entry<Claim, Team>> getRegionData(Location min, Location max) {
      if (!this.claimsEnabled) {
         return new HashSet<>();
      }

      Set<Entry<Claim, Team>> regions = new HashSet<>();
      int step = 64;

      for (int x = min.getBlockX(); x < max.getBlockX() + step; x += step) {
         for (int z = min.getBlockZ(); z < max.getBlockZ() + step; z += step) {
            CoordinateSet coordinateSet = new CoordinateSet(x, z);

            for (Entry<Claim, Team> regionEntry : this.buckets.get(min.getWorld().getName()).get(coordinateSet)) {
               if (!regions.contains(regionEntry)
                  && max.getBlockX() >= regionEntry.getKey().getX1()
                  && min.getBlockX() <= regionEntry.getKey().getX2()
                  && max.getBlockZ() >= regionEntry.getKey().getZ1()
                  && min.getBlockZ() <= regionEntry.getKey().getZ2()
                  && max.getBlockY() >= regionEntry.getKey().getY1()
                  && min.getBlockY() <= regionEntry.getKey().getY2()) {
                  regions.add(regionEntry);
               }
            }
         }
      }

      return regions;
   }

   public Entry<Claim, Team> getRegionData(Location location) {
      if (!this.claimsEnabled) {
         return null;
      }

      Entry<Claim, Team> keepInMind = null;

      for (Entry<Claim, Team> data : this.buckets.get(location.getWorld().getName()).get(new CoordinateSet(location.getBlockX(), location.getBlockZ()))) {
         if (data.getKey().contains(location)) {
            if (!data.getValue().hasDTRBitmask(DTRBitmask.ROAD)) {
               keepInMind = data;
               break;
            }

            keepInMind = data;
         }
      }

      return keepInMind;
   }

   public Claim getClaim(Location location) {
      Entry<Claim, Team> regionData = this.getRegionData(location);
      return regionData == null ? null : regionData.getKey();
   }

   public Team getTeam(Location location) {
      Entry<Claim, Team> regionData = this.getRegionData(location);
      return regionData == null ? null : regionData.getValue();
   }

   public void setTeamAt(Claim claim, Team team) {
      Entry<Claim, Team> regionData = new SimpleEntry<>(claim, team);
      int step = 64;

      for (int x = regionData.getKey().getX1(); x < regionData.getKey().getX2() + step; x += step) {
         for (int z = regionData.getKey().getZ1(); z < regionData.getKey().getZ2() + step; z += step) {
            Multimap<CoordinateSet, Entry<Claim, Team>> worldMap = this.buckets.get(regionData.getKey().getWorld());
            if (worldMap != null) {
               if (regionData.getValue() == null) {
                  CoordinateSet coordinateSet = new CoordinateSet(x, z);
                  Iterator<Entry<Claim, Team>> claimIterator = worldMap.get(coordinateSet).iterator();

                  while (claimIterator.hasNext()) {
                     Entry<Claim, Team> entry = claimIterator.next();
                     if (entry.getKey().equals(regionData.getKey())) {
                        claimIterator.remove();
                     }
                  }
               } else {
                  worldMap.put(new CoordinateSet(x, z), regionData);
               }
            }
         }
      }

      this.updateClaim(claim);
   }

   public void updateClaim(Claim modified) {
      ArrayList<VisualClaim> visualClaims = new ArrayList<>();
      visualClaims.addAll(VisualClaim.getCurrentMaps().values());

      for (VisualClaim visualClaim : visualClaims) {
         if (modified.isWithin(visualClaim.getPlayer().getLocation().getBlockX(), visualClaim.getPlayer().getLocation().getBlockZ(), 50, modified.getWorld())) {
            visualClaim.draw(true);
            visualClaim.draw(true);
         }
      }
   }

   public void updateSubclaim(Subclaim modified) {
      ArrayList<VisualClaim> visualClaims = new ArrayList<>();
      visualClaims.addAll(VisualClaim.getCurrentSubclaimMaps().values());

      for (VisualClaim visualClaim : visualClaims) {
         if (modified.getLoc1().distanceSquared(visualClaim.getPlayer().getLocation()) < 2500.0
            || modified.getLoc2().distanceSquared(visualClaim.getPlayer().getLocation()) < 2500.0) {
            visualClaim.draw(true);
            visualClaim.draw(true);
         }
      }
   }

   public void clear(Team team) {
      for (Claim claim : team.getClaims()) {
         this.setTeamAt(claim, null);
      }
   }

   public static LandBoard getInstance() {
      if (instance == null) {
         instance = new LandBoard();
      }

      return instance;
   }

   @EventHandler
   public void onWorldLoadEvent(WorldLoadEvent event) {
      this.buckets.put(event.getWorld().getName(), HashMultimap.create());
   }

   public boolean isClaimsEnabled() {
      return this.claimsEnabled;
   }

   public void setClaimsEnabled(boolean claimsEnabled) {
      this.claimsEnabled = claimsEnabled;
   }
}
