package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MinerClass extends PvPClass implements Listener {
   private static final int Y_HEIGHT = 20;
   private final Map<String, Integer> noDamage = new HashMap<>();
   private final Map<String, Integer> invis = new HashMap<>();

   public MinerClass() {
      super("Miner", null);
      this.getPermanentEffects().add(PotionEffectType.NIGHT_VISION);
      this.getPermanentEffects().add(PotionEffectType.FAST_DIGGING);
      this.getPermanentEffects().add(PotionEffectType.FIRE_RESISTANCE);

      (new BukkitRunnable() {
         @Override
         public void run() {
            for (String key : new HashMap<>(MinerClass.this.noDamage).keySet()) {
               int left = MinerClass.this.noDamage.remove(key);
               Player player = Foxtrot.getInstance().getServer().getPlayerExact(key);
               if (player != null) {
                  if (left == 0) {
                     if (player.getLocation().getY() <= 20.0) {
                        MinerClass.this.invis.put(player.getName(), 10);
                        player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                     }
                  } else {
                     MinerClass.this.noDamage.put(player.getName(), left - 1);
                  }
               }
            }

            for (String key : new HashMap<>(MinerClass.this.invis).keySet()) {
               Player player = Foxtrot.getInstance().getServer().getPlayerExact(key);
               if (player != null) {
                  int secs = MinerClass.this.invis.get(player.getName());
                  if (secs == 0) {
                     if (player.getLocation().getY() <= 20.0 && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been enabled!");
                        player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                     }
                  } else {
                     MinerClass.this.invis.put(player.getName(), secs - 1);
                  }
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   @Override
   public boolean qualifies(Player player, PlayerInventory armor) {
      return this.wearingAllArmor(armor)
         && armor.getHelmet().getType() == Material.IRON_HELMET
         && armor.getChestplate().getType() == Material.IRON_CHESTPLATE
         && armor.getLeggings().getType() == Material.IRON_LEGGINGS
         && armor.getBoots().getType() == Material.IRON_BOOTS;
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
   }

   @Override
   public void tick(Player player) {
      if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
      }

      if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
      }

      int diamonds = Foxtrot.getInstance().getDiamondMinedMap().getMined(player.getUniqueId());
      if (this.shouldApplyPotion(player, PotionEffectType.FAST_DIGGING, diamonds > 50 ? 2 : 1)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, diamonds > 50 ? 2 : 1), true);
      }

      int speedLevel = diamonds >= 250 ? 1 : (diamonds >= 100 ? 0 : -1);
      if (speedLevel != -1 && this.shouldApplyPotion(player, PotionEffectType.SPEED, speedLevel)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedLevel), true);
      }

      if (diamonds >= 500 && this.shouldApplyPotion(player, PotionEffectType.INVISIBILITY, 0)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
      }

      int resistanceLevel = diamonds >= 1000 ? 1 : (diamonds >= 750 ? 0 : -1);
      if (resistanceLevel != -1 && this.shouldApplyPotion(player, PotionEffectType.DAMAGE_RESISTANCE, resistanceLevel)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, resistanceLevel), true);
      }

      if (diamonds >= 1500 && this.shouldApplyPotion(player, PotionEffectType.REGENERATION, 0)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
      }

      super.tick(player);
   }

   public boolean shouldApplyPotion(Player player, PotionEffectType eff, int level) {
      int potionLevel = -1;

      for (PotionEffect effect : player.getActivePotionEffects()) {
         if (effect.getType().equals(eff)) {
            potionLevel = effect.getAmplifier();
            break;
         }
      }

      return !player.hasPotionEffect(eff) || potionLevel < level;
   }

   @Override
   public void remove(Player player) {
      removeInfiniteEffects(player);
      this.noDamage.remove(player.getName());
      this.invis.remove(player.getName());
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (PvPClassHandler.hasKitOn(player, this)) {
            this.noDamage.put(player.getName(), 15);
            if (this.invis.containsKey(player.getName()) && this.invis.get(player.getName()) != 0) {
               this.invis.put(player.getName(), 10);
               player.removePotionEffect(PotionEffectType.INVISIBILITY);
               player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player player = (Player)event.getDamager();
         if (PvPClassHandler.hasKitOn(player, this)) {
            this.noDamage.put(player.getName(), 15);
            if (this.invis.containsKey(player.getName()) && this.invis.get(player.getName()) != 0) {
               this.invis.put(player.getName(), 10);
               player.removePotionEffect(PotionEffectType.INVISIBILITY);
               player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onPlayerMove(PlayerMoveEvent event) {
      if (event.getFrom().getBlockY() != event.getTo().getBlockY()) {
         Player player = event.getPlayer();
         if (PvPClassHandler.hasKitOn(player, this)) {
            if (event.getTo().getBlockY() <= 20) {
               if (!this.invis.containsKey(player.getName())) {
                  this.invis.put(player.getName(), 10);
                  player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
               }
            } else if (event.getTo().getBlockY() > 20 && this.invis.containsKey(player.getName())) {
               this.noDamage.remove(player.getName());
               this.invis.remove(player.getName());
               player.removePotionEffect(PotionEffectType.INVISIBILITY);
               player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been removed!");
            }
         }
      }
   }

   @EventHandler
   public void onPlayerPickupItem(PlayerPickupItemEvent event) {
      Player player = event.getPlayer();
      if (PvPClassHandler.hasKitOn(player, this) && event.getItem().getItemStack().getType() == Material.COBBLESTONE) {
         if (!Foxtrot.getInstance().getCobblePickupMap().isCobblePickup(player.getUniqueId())) {
            event.setCancelled(true);
         }
      }
   }

   public Map<String, Integer> getInvis() {
      return this.invis;
   }
}
