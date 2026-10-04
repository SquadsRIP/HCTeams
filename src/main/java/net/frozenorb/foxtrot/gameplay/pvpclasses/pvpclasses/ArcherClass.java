package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import cc.fyre.proton.util.TimeUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.trackers.ArrowTracker;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.archer.ArcherColor;
import net.frozenorb.foxtrot.gameplay.pvpclasses.energy.EnergyEffect;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArcherClass extends PvPClass {
   private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
   private static final Map<String, Long> lastJumpUsage = new HashMap<>();
   private static final Map<String, Long> lastResistanceUsage = new HashMap<>();
   public static final int MARK_SECONDS = 8;
   private static final int ULTIMATE_PER_MARK = 10;
   private static final int EFFECT_COOLDOWN = 10000;
   private static final Map<String, Long> markedPlayers = new ConcurrentHashMap<>();
   private static final Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();
   public final Map<Material, EnergyEffect> ARCHER_EFFECTS = new HashMap<>();

   public ArcherClass() {
      super("Archer", Arrays.asList(Material.SUGAR, Material.FEATHER, Material.IRON_INGOT), false);
      this.getPermanentEffects().add(PotionEffectType.SPEED);
      this.getPermanentEffects().add(PotionEffectType.DAMAGE_RESISTANCE);
      this.ARCHER_EFFECTS.put(Material.SUGAR, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 220, 3), 35));
      this.ARCHER_EFFECTS.put(Material.FEATHER, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 220, 6), 35));
      this.ARCHER_EFFECTS.put(Material.IRON_INGOT, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2), 35));
   }

   @Override
   public boolean qualifies(Player player, PlayerInventory armor) {
      return this.wearingAllArmor(armor)
         && armor.getHelmet().getType() == Material.LEATHER_HELMET
         && armor.getChestplate().getType() == Material.LEATHER_CHESTPLATE
         && armor.getLeggings().getType() == Material.LEATHER_LEGGINGS
         && armor.getBoots().getType() == Material.LEATHER_BOOTS;
   }

   @Override
   public void apply(Player player) {
      super.apply(player);
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
   }

   @Override
   public void tick(Player player) {
      if (!player.hasPotionEffect(PotionEffectType.SPEED)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.SPEED) && it.getAmplifier() < 2)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
      }

      if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) && it.getAmplifier() < 1)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
      }

      super.tick(player);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityArrowHit(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
         Arrow arrow = (Arrow)event.getDamager();
         final Player victim = (Player)event.getEntity();
         if (!(arrow.getShooter() instanceof Player)) {
            return;
         }

         Player shooter = (Player)arrow.getShooter();
         float pullback = arrow.getMetadata("Pullback").get(0).asFloat();
         if (!PvPClassHandler.hasKitOn(shooter, this)) {
            return;
         }

         int limit = Foxtrot.getInstance().getMapHandler().isKitMap() ? 2 : 1;
         if (PvPClassHandler.countMembersWithKitOn(shooter, this) > limit) {
            shooter.sendMessage(ChatColor.RED + "Your team has too many archers!");
            return;
         }

         int damage = isMarked(victim) ? 3 : 2;
         if (pullback < 0.6) {
            damage = 1;
         }

         if (victim.getHealth() - damage <= 0.0) {
            event.setCancelled(true);
         } else {
            event.setDamage(0.0);
         }

         Location shotFrom = (Location)arrow.getMetadata("ShotFromDistance").get(0).value();
         double distance = shotFrom.distance(victim.getLocation());
         boolean inArcher = PvPClassHandler.hasKitOn(victim, this);
         boolean isAllowed = !Foxtrot.getInstance().getServerHandler().isWarzone(shooter.getLocation())
            && shooter.getWorld().getEnvironment() == Environment.NORMAL;
         if (pullback >= 0.6F && !inArcher && isAllowed) {
            ArcherColor.findByPlayer(shooter).ifPresent(it -> {
               int chance = (int)distance;
               int random = ThreadLocalRandom.current().nextInt(100);
               if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                  chance = (int)(chance * 4.5);
               }

               if (chance > random) {
                  it.getPredicate().test(victim);
                  shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Archer" + ChatColor.YELLOW + "] " + it.getMessage() + ".");
               }
            });
         }

         if (inArcher) {
            damage = 1;
         }

         victim.setMetadata("ARCHER_TAG", new FixedMetadataValue(Foxtrot.getInstance(), true));
         DeathMessageHandler.addDamage(
            victim, new ArrowTracker.ArrowDamageByPlayer(victim.getName(), damage, ((Player)arrow.getShooter()).getName(), shotFrom, distance)
         );
         victim.setHealth(Math.max(0.0, victim.getHealth() - damage));
         if (inArcher) {
            shooter.sendMessage(
               ChatColor.YELLOW
                  + "["
                  + ChatColor.BLUE
                  + "Arrow Range"
                  + ChatColor.YELLOW
                  + " ("
                  + ChatColor.RED
                  + (int)distance
                  + ChatColor.YELLOW
                  + ")] "
                  + ChatColor.RED
                  + "Cannot mark other Archers. "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + "("
                  + damage / 2
                  + ".5 heart"
                  + (damage / 2 == 1 ? "" : "s")
                  + ")"
            );
         } else if (!(pullback >= 0.6F)) {
            shooter.sendMessage(
               ChatColor.YELLOW
                  + "["
                  + ChatColor.BLUE
                  + "Arrow Range"
                  + ChatColor.YELLOW
                  + " ("
                  + ChatColor.RED
                  + (int)distance
                  + ChatColor.YELLOW
                  + ")] "
                  + ChatColor.RED
                  + "Bow wasn't fully drawn back. "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + "(0.5 hearts)"
            );
         } else {
            shooter.sendMessage(
               ChatColor.YELLOW
                  + "["
                  + ChatColor.BLUE
                  + "Arrow Range"
                  + ChatColor.YELLOW
                  + " ("
                  + ChatColor.RED
                  + (int)distance
                  + ChatColor.YELLOW
                  + ")] "
                  + ChatColor.GOLD
                  + "Marked player for "
                  + 8
                  + " seconds. "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + "("
                  + damage / 2
                  + (damage % 2 == 0 ? "" : ".5")
                  + " heart"
                  + (damage / 2 == 1 ? "" : "s")
                  + ")"
            );
            if (!isMarked(victim)) {
               victim.sendMessage(
                  ChatColor.RED.toString()
                     + ChatColor.BOLD
                     + "Marked! "
                     + ChatColor.YELLOW
                     + "An archer has shot you and marked you (+25% damage) for "
                     + 8
                     + " seconds."
               );
            }

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
               Foxtrot.getInstance().getMasteryUpgradeHandler().addUpgrade(shooter, "Archer", 1);
            }

            PotionEffect invis = null;

            for (PotionEffect potionEffect : victim.getActivePotionEffects()) {
               if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                  invis = potionEffect;
                  break;
               }
            }

            if (invis != null) {
               PvPClass playerClass = PvPClassHandler.getPvPClass(victim);
               victim.removePotionEffect(invis.getType());
               final PotionEffect invisFinal = invis;
               if (playerClass instanceof MinerClass) {
                  ((MinerClass)playerClass).getInvis().put(victim.getName(), 8);
               } else {
                  (new BukkitRunnable() {
                     @Override
                     public void run() {
                        victim.removeMetadata("ARCHER_TAG", Foxtrot.getInstance());
                        victim.addPotionEffect(invisFinal);
                     }
                  }).runTaskLater(Foxtrot.getInstance(), 165L);
               }
            }

            getMarkedPlayers().put(victim.getName(), System.currentTimeMillis() + 8000L);
            getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
            getMarkedBy().get(shooter.getName()).add(new Pair<>(victim.getName(), System.currentTimeMillis() + 8000L));
            LunarClientListener.updateNametag(victim);
            (new BukkitRunnable() {
               @Override
               public void run() {
                  LunarClientListener.updateNametag(victim);
               }
            }).runTaskLater(Foxtrot.getInstance(), 165L);
         }

         if (Foxtrot.getInstance().getBattlePassHandler() != null) {
            Foxtrot.getInstance().getBattlePassHandler().useProgress(shooter.getUniqueId(), progress -> {
               progress.setArcherTags(progress.getArcherTags() + 1);
               progress.requiresSave();
               Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(shooter);
            });
         }
      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (isMarked(player)) {
            Player damager = null;
            if (event.getDamager() instanceof Player) {
               damager = (Player)event.getDamager();
            } else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
               damager = (Player)((Projectile)event.getDamager()).getShooter();
            }

            if (damager != null && !this.canUseMark(damager, player)) {
               return;
            }

            if (Foxtrot.getInstance().getMapHandler().isKitMap() && player.hasMetadata("THIRTY_PERCENT")) {
               event.setDamage(event.getDamage() * 1.3);
               return;
            }

            event.setDamage(event.getDamage() * 1.25);
         }
      }
   }

   @EventHandler
   public void onEntityShootBow(EntityShootBowEvent event) {
      event.getProjectile().setMetadata("Pullback", new FixedMetadataValue(Foxtrot.getInstance(), event.getForce()));
   }

   @Override
   public boolean itemConsumed(Player player, Material material) {
      if (material == Material.SUGAR) {
         if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
            long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
            String msg = TimeUtils.formatIntoDetailedString((int)millisLeft / 1000);
            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return false;
         } else {
            boolean flamingSkeleton = player.hasMetadata("FLAMING_SKELETON") && player.getWorld().getEnvironment().equals(Environment.NETHER);
            lastSpeedUsage.put(
               player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(player.hasMetadata("HAWKEYE") ? 20L : (flamingSkeleton ? 40L : 30L))
            );
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, flamingSkeleton ? 4 : 3), true);
            return true;
         }
      } else if (material == Material.IRON_INGOT) {
         if (lastResistanceUsage.containsKey(player.getName()) && lastResistanceUsage.get(player.getName()) > System.currentTimeMillis()) {
            long millisLeft = lastResistanceUsage.get(player.getName()) - System.currentTimeMillis();
            String msg = TimeUtils.formatIntoDetailedString((int)millisLeft / 1000);
            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return false;
         } else {
            lastResistanceUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2), true);
            return true;
         }
      } else if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         player.sendMessage(ChatColor.RED + "You can't use this in spawn!");
         return false;
      } else if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
         long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
         String msg = TimeUtils.formatIntoDetailedString((int)millisLeft / 1000);
         player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
         return false;
      } else {
         lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1L));
         player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 6), true);
         SpawnTagHandler.addPassiveSeconds(player, SpawnTagHandler.getMaxTagTime(player));
         return false;
      }
   }

   public void giveArcherEffect(Player player, EnergyEffect archerEffect, boolean persistOldValues) {
      if (archerEffect.getPotionEffect() != null) {
         smartAddPotion(player, archerEffect.getPotionEffect(), persistOldValues, this);
      }
   }

   public static boolean isMarked(Player player) {
      return getMarkedPlayers().containsKey(player.getName()) && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis();
   }

   private boolean canUseMark(Player player, Player victim) {
      int limit = Foxtrot.getInstance().getMapHandler().isKitMap() ? 2 : 1;
      if (Foxtrot.getInstance().getTeamHandler().getTeam(player) != null) {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         int amount = 0;

         for (Player member : team.getOnlineMembers()) {
            if (PvPClassHandler.hasKitOn(member, this)) {
               if (++amount > limit) {
                  break;
               }
            }
         }

         if (amount > limit) {
            player.sendMessage(ChatColor.RED + "Your team has too many archers. Archer mark was not applied.");
            return false;
         }
      }

      if (markedBy.containsKey(player.getName())) {
         for (Pair<String, Long> pair : markedBy.get(player.getName())) {
            if (victim.getName().equals(pair.first) && pair.second > System.currentTimeMillis()) {
               return false;
            }
         }
      }

      return true;
   }

   public static Map<String, Long> getMarkedPlayers() {
      return markedPlayers;
   }

   public static Map<String, Set<Pair<String, Long>>> getMarkedBy() {
      return markedBy;
   }
}
