package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PyromaniacClass extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "pyromaniac";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.RED + ChatColor.BOLD.toString() + "Pyromaniac";
   }

   @Override
   public int getSlot() {
      return 12;
   }

   @Override
   public Material getDisplayItem() {
      return Material.FIREBALL;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.RED;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("SHIFT + Click to throw shockwaves");
      toReturn.add("1% chance of putting them on Fire and giving them Wither I for 8 seconds.");
      return toReturn;
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.isWearing(player)) {
            if (ArmorClassHandler.isAllowed(player.getLocation())) {
               if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
                  event.setDamage(event.getDamage() * 1.1);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         Player player = event.getPlayer();
         if (this.isWearing(player)) {
            if (player.isSneaking()) {
               if (ArmorClassHandler.isAllowed(player.getLocation())) {
                  if (this.cooldown.containsKey(player.getUniqueId()) && this.cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                     long millisLeft = (this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L * 1000L;
                     String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
                     player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                  } else {
                     Fireball fireball = player.launchProjectile(Fireball.class);
                     fireball.setMetadata("SHOCK_WAVE", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
                     fireball.setIsIncendiary(false);
                     fireball.setYield(0.0F);
                     fireball.setShooter(player);
                     this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player damager = (Player)event.getDamager();
         if (this.isWearing(damager)) {
            if (ArmorClassHandler.isAllowed(damager.getLocation())) {
               Player target = (Player)event.getEntity();
               if (ThreadLocalRandom.current().nextInt(1, 200) == 38) {
                  target.sendMessage(ChatColor.translate(damager.getName() + " &chas used the Butcher Armor Class ability on you!"));
                  target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));
               }
            }
         }
      }
   }

   @Override
   public ItemStack createPiece(ArmorPiece armorPiece) {
      ItemBuilder itemBuilder = ItemBuilder.of(armorPiece.getDefaultMaterial())
         .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
         .enchant(Enchantment.DURABILITY, 3)
         .name(this.getDisplayName() + " " + this.getPieceName(armorPiece))
         .addToLore("", this.getChatColor() + "Armor Class: &f" + ChatColor.stripColor(this.getDisplayName()), this.getChatColor() + "Perks:");

      for (String perk : this.getPerks()) {
         itemBuilder.addToLore(this.getChatColor() + "❙ &f" + perk);
      }

      itemBuilder.addToLore("");
      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_CHESTPLATE)) {
         itemBuilder.addToLore("&cFireResistance I");
      }

      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_BOOTS)) {
         itemBuilder.enchant(Enchantment.PROTECTION_FALL, 4);
         itemBuilder.addToLore("&cSpeed II");
      }

      return itemBuilder.build();
   }

   @EventHandler
   private void onLand(ProjectileHitEvent event) {
      if (event.getEntity() instanceof Fireball && event.getEntity().getShooter() instanceof Player && event.getEntity().hasMetadata("SHOCK_WAVE")) {
         Projectile fireBall = event.getEntity();
         Player player = (Player)fireBall.getShooter();
         fireBall.getNearbyEntities(10.0, 10.0, 10.0).stream().filter(it -> it instanceof Player && this.isAllowedAtLocation(it.getLocation())).forEach(it -> {
            it.setVelocity(it.getLocation().getDirection().multiply(-2.5));
            ((Player)it).setHealth(((Player)it).getHealth() - 1.0);
            ((Player)it).sendMessage("");
            ((Player)it).sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            ((Player)it).sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.GRAY + "!");
            ((Player)it).sendMessage(ChatColor.RED + "You have been sent flying in the air!");
            ((Player)it).sendMessage("");
         });
      }
   }
}
