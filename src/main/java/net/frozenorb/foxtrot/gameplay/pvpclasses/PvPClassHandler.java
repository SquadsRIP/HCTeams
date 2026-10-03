package net.frozenorb.foxtrot.gameplay.pvpclasses;

import cc.fyre.piston.PistonConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.pvpclasses.energy.service.EnergyService;
import net.frozenorb.foxtrot.gameplay.pvpclasses.event.BardRestoreEvent;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.MinerClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.RogueClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.WitherSkeletonClass;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPClassHandler extends BukkitRunnable implements Listener {
   private static final Map<UUID, Float> ultimate = new HashMap<>();
   private static final Map<String, PvPClass> equippedKits = new HashMap<>();
   private static final Map<String, PvPClassHandler.KitTask> warmupTasks = new HashMap<>();
   private static final Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();
   private final EnergyService energyService;
   private final List<PvPClass> pvpClasses = new ArrayList<>();

   public PvPClassHandler() {
      this.pvpClasses.add(new MinerClass());
      if (Foxtrot.getInstance().getConfig().getBoolean("pvpClasses.archer")) {
         this.pvpClasses.add(new ArcherClass());
      }

      if (Foxtrot.getInstance().getConfig().getBoolean("pvpClasses.bard")) {
         this.pvpClasses.add(new BardClass());
      }

      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         this.pvpClasses.add(new WitherSkeletonClass());
      }

      if (Foxtrot.getInstance().getConfig().getBoolean("pvpClasses.rogue")) {
         this.pvpClasses.add(new RogueClass());
      }

      for (PvPClass pvpClass : this.pvpClasses) {
         Foxtrot.getInstance().getServer().getPluginManager().registerEvents(pvpClass, Foxtrot.getInstance());
      }

      Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), this, 5L, 5L);
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
      this.energyService = new EnergyService();
      this.energyService.runTaskTimer(Foxtrot.getInstance(), 15L, 20L);
   }

   @Override
   public void run() {
      for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         if (warmupTasks.containsKey(player.getName())) {
            PvPClass trying = warmupTasks.get(player.getName()).getPvpClass();
            if (!trying.qualifies(player, player.getInventory())) {
               warmupTasks.remove(player.getName()).cancel();
            }
         }

         if (equippedKits.containsKey(player.getName())) {
            PvPClass equippedPvPClass = equippedKits.get(player.getName());
            if (!equippedPvPClass.qualifies(player, player.getInventory())) {
               equippedKits.remove(player.getName());
               ultimate.remove(player.getUniqueId());
               equippedPvPClass.remove(player);
               PvPClass.removeInfiniteEffects(player);
            } else if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)
               && (
                  equippedPvPClass instanceof BardClass
                     || !warmupTasks.containsKey(player.getName())
                     || warmupTasks.get(player.getName()).getPvpClass() != equippedPvPClass
               )) {
               equippedPvPClass.tick(player);
            }
         } else {
            for (PvPClass pvpClass : this.pvpClasses) {
               if (pvpClass.qualifies(player, player.getInventory()) && pvpClass.canApply(player) && !player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
                  if (!(pvpClass instanceof BardClass) && !CustomTimerCreateCommand.isSOTWTimer() && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
                     if (warmupTasks.containsKey(player.getName()) && warmupTasks.get(player.getName()).getPvpClass() == pvpClass) {
                        break;
                     }

                     if (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass) {
                        continue;
                     }

                     this.startWarmup(player, pvpClass);
                     break;
                  }

                  pvpClass.apply(player);
                  getEquippedKits().put(player.getName(), pvpClass);
                  getUltimate().put(player.getUniqueId(), 0.0F);
                  break;
               }
            }
         }
      }

      this.checkSavedPotions();
   }

   public void startWarmup(Player player, PvPClass pvpClass) {
      player.sendMessage(ChatColor.translate("&aStarting class warmup of &f5 seconds&a!"));
      getWarmupTasks().put(player.getName(), new PvPClassHandler.KitTask(player, pvpClass));
      getWarmupTasks().get(player.getName()).runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   public void checkSavedPotions() {
      Iterator<Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();

      while (idIterator.hasNext()) {
         Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
         Player player = Bukkit.getPlayer(id.getKey());
         if (player != null && player.isOnline()) {
            Bukkit.getPluginManager().callEvent(new BardRestoreEvent(player, id.getValue()));
            if (id.getValue().getTime() < System.currentTimeMillis() && !id.getValue().isPerm()) {
               if (player.hasPotionEffect(id.getValue().getPotionEffect().getType())) {
                  player.getActivePotionEffects()
                     .forEach(
                        potion -> {
                           PotionEffect restore = id.getValue().getPotionEffect();
                           if (potion.getType() == restore.getType()
                              && potion.getDuration() < restore.getDuration()
                              && potion.getAmplifier() <= restore.getAmplifier()) {
                              player.removePotionEffect(restore.getType());
                           }
                        }
                     );
               }

               if (player.addPotionEffect(id.getValue().getPotionEffect(), true)) {
                  Bukkit.getLogger()
                     .info(
                        id.getValue().getPotionEffect().getType()
                           + ", "
                           + id.getValue().getPotionEffect().getDuration()
                           + ", "
                           + id.getValue().getPotionEffect().getAmplifier()
                     );
                  idIterator.remove();
               }
            }
         } else {
            idIterator.remove();
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getPlayer().getItemInHand() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
         for (PvPClass pvPClass : this.pvpClasses) {
            if (hasKitOn(event.getPlayer(), pvPClass)
               && pvPClass.getConsumables() != null
               && pvPClass.getConsumables().contains(event.getPlayer().getItemInHand().getType())
               && pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
               if (event.getPlayer().getItemInHand().getAmount() > 1) {
                  event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
               } else {
                  event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
               }
            }
         }
      }
   }

   public static PvPClass getPvPClass(Player player) {
      return equippedKits.getOrDefault(player.getName(), null);
   }

   public static boolean hasKitOn(Player player, PvPClass pvpClass) {
      return equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass;
   }

   public static int countMembersWithKitOn(Player player, PvPClass pvpClass) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team == null) {
         return hasKitOn(player, pvpClass) ? 1 : 0;
      }

      int amount = 0;

      for (Player member : team.getOnlineMembers()) {
         if (hasKitOn(member, pvpClass)) {
            amount++;
         }
      }

      return amount;
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      if (equippedKits.containsKey(event.getPlayer().getName())) {
         equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
         equippedKits.remove(event.getPlayer().getName());
      }
   }

   @EventHandler
   public void onPlayerKick(PlayerKickEvent event) {
      if (equippedKits.containsKey(event.getPlayer().getName())) {
         equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
         equippedKits.remove(event.getPlayer().getName());
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      if (equippedKits.containsKey(event.getPlayer().getName())) {
         equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
         equippedKits.remove(event.getPlayer().getName());
      }

      for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
         if (potionEffect.getDuration() > 1000000) {
            event.getPlayer().removePotionEffect(potionEffect.getType());
         }
      }
   }

   public EnergyService getEnergyService() {
      return this.energyService;
   }

   public List<PvPClass> getPvpClasses() {
      return this.pvpClasses;
   }

   public static Map<UUID, Float> getUltimate() {
      return ultimate;
   }

   public static Map<String, PvPClass> getEquippedKits() {
      return equippedKits;
   }

   public static Map<String, PvPClassHandler.KitTask> getWarmupTasks() {
      return warmupTasks;
   }

   public static Map<UUID, PvPClass.SavedPotion> getSavedPotions() {
      return savedPotions;
   }

   public static class KitTask extends BukkitRunnable {
      Player player;
      PvPClass pvpClass;
      long time;

      public KitTask(Player player, PvPClass pvpClass) {
         this.player = player;
         this.pvpClass = pvpClass;
         this.time = System.currentTimeMillis() + 5000L;
      }

      @Override
      public void run() {
         if (!this.player.isOnline()) {
            this.cancel();
            PvPClassHandler.getWarmupTasks().remove(this.player.getName());
         }

         if (System.currentTimeMillis() >= this.time) {
            this.pvpClass.apply(this.player);
            PvPClassHandler.getEquippedKits().put(this.player.getName(), this.pvpClass);
            PvPClassHandler.getWarmupTasks().remove(this.player.getName());
            this.cancel();
         }
      }

      public PvPClass getPvpClass() {
         return this.pvpClass;
      }

      public long getTime() {
         return this.time;
      }
   }
}
