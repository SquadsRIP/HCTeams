package net.frozenorb.foxtrot.gameplay.grounds;

import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GroundsArea implements Listener {
   private boolean active;
   private List<Location> guardLocations = new ArrayList<>();
   private Map<Entity, Location> takenLocations = new HashMap<>();

   public abstract String getGroundsID();

   public abstract String getGroundsDisplayName();

   public abstract EntityType getSpawnerType();

   public abstract List<EntityType> getGuardTypes();

   public void reset() {
      Team team = this.findTeam();
      if (team == null) {
         System.out.println("**********************");
         System.out.println();
         System.out.println("SPAWNER GROUNDS " + this.getGroundsID() + " has no spawn!");
         System.out.println();
         System.out.println("**********************");
      } else {
         Location location = team.getHQ();
         Block below = location.getBlock().getRelative(BlockFace.DOWN);
         below.setType(Material.MOB_SPAWNER);
         below.setMetadata("GROUNDS", new FixedMetadataValue(Foxtrot.getInstance(), true));
         CreatureSpawner spawner = (CreatureSpawner)below.getState();
         spawner.setSpawnedType(this.getSpawnerType());
         spawner.update();
      }
   }

   public void startGuardService() {
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
      (new BukkitRunnable() {
            @Override
            public void run() {
               Map<Entity, Location> newMap = new HashMap<>(GroundsArea.this.takenLocations);

               for (Entry<Entity, Location> entry : newMap.entrySet()) {
                  if (entry.getKey().isDead()) {
                     GroundsArea.this.takenLocations.remove(entry.getKey());
                  }
               }

               for (Location guardLocation : GroundsArea.this.guardLocations) {
                  if (!GroundsArea.this.takenLocations.containsValue(guardLocation)) {
                     Monster monster = (Monster)guardLocation.getWorld().spawnEntity(guardLocation.clone(), GroundsArea.this.getSpawnerType());
                     monster.setCustomNameVisible(true);
                     monster.setCustomName(GroundsArea.this.getGroundsDisplayName() + " Guard");
                     monster.setMetadata("GUARD", new FixedMetadataValue(Foxtrot.getInstance(), true));
                     if (monster instanceof Zombie || monster instanceof Skeleton) {
                        monster.getEquipment()
                           .setHelmet(
                              ItemBuilder.of(Material.DIAMOND_HELMET)
                                 .name(ChatColor.DARK_RED + "Guard Armor")
                                 .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                 .build()
                           );
                        monster.getEquipment()
                           .setChestplate(
                              ItemBuilder.of(Material.DIAMOND_CHESTPLATE)
                                 .name(ChatColor.DARK_RED + "Guard Armor")
                                 .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                 .build()
                           );
                        monster.getEquipment()
                           .setLeggings(
                              ItemBuilder.of(Material.DIAMOND_LEGGINGS)
                                 .name(ChatColor.DARK_RED + "Guard Armor")
                                 .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                 .build()
                           );
                        monster.getEquipment()
                           .setBoots(
                              ItemBuilder.of(Material.DIAMOND_BOOTS)
                                 .name(ChatColor.DARK_RED + "Guard Armor")
                                 .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                 .build()
                           );
                        monster.getEquipment()
                           .setItemInHand(
                              ItemBuilder.of(Material.DIAMOND_SWORD).name(ChatColor.DARK_RED + "Guard Armor").enchant(Enchantment.DAMAGE_ALL, 2).build()
                           );
                     }

                     GroundsArea.this.takenLocations.put(monster, guardLocation);
                  }
               }
            }
         })
         .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   public Team findTeam() {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(this.getGroundsID());
      return team != null && team.getOwner() == null && team.getHQ() != null ? team : null;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public List<Location> getGuardLocations() {
      return this.guardLocations;
   }

   public void setGuardLocations(List<Location> guardLocations) {
      this.guardLocations = guardLocations;
   }

   public Map<Entity, Location> getTakenLocations() {
      return this.takenLocations;
   }

   public void setTakenLocations(Map<Entity, Location> takenLocations) {
      this.takenLocations = takenLocations;
   }
}
