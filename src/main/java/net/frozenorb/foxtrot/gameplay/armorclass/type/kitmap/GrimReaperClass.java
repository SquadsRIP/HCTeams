package net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall.PiercingShot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GrimReaperClass extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "grimreaper";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Grim Reaper";
   }

   @Override
   public int getSlot() {
      return 13;
   }

   @Override
   public Material getDisplayItem() {
      return Material.IRON_HOE;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.DARK_RED;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("10% more damage towards kits.");
      toReturn.add("Shift Right CLick to give every enemy Blindness III,");
      toReturn.add("Slowness III and they take 10% more damage for 7 seconds.");
      toReturn.add("Reapers Revenge & Final Breath cannot be used within 10 blocks.");
      return toReturn;
   }

   @Override
   public Category getCategory() {
      return Category.DIAMOND;
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         Player player = event.getPlayer();
         if (this.isWearing(player)) {
            if (player.isSneaking()) {
               if (this.cooldown.containsKey(player.getUniqueId()) && this.cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                  long millisLeft = (this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L * 1000L;
                  String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
                  player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
               } else {
                  int count = 0;

                  for (Player nearByEnemy : getNearByEnemies(player, 20)) {
                     nearByEnemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 2));
                     nearByEnemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 2));
                     PiercingShot.cache.put(nearByEnemy.getUniqueId(), player.getUniqueId());
                     Foxtrot.getInstance()
                        .getServer()
                        .getScheduler()
                        .runTaskLater(Foxtrot.getInstance(), () -> PiercingShot.cache.remove(nearByEnemy.getUniqueId()), 140L);
                  }

                  player.sendMessage(ChatColor.translate("&aSuccessfully effected &f" + count + " &aplayers!"));
                  this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3L));
               }
            }
         }
      }
   }

   @EventHandler
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player damager = (Player)event.getDamager();
         Player target = (Player)event.getEntity();
         if (this.isWearing(damager)) {
            if (PvPClassHandler.getPvPClass(target) != null) {
               event.setDamage(event.getDamage() * 1.1);
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
      return itemBuilder.build();
   }

   @Override
   public void apply(Player player) {
   }

   @Override
   public void unapply(Player player) {
   }

   public static List<Player> getNearByEnemies(Player player, int radius) {
      List<Player> toReturn = new ArrayList<>();

      for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
         if (entity instanceof Player) {
            Player target = (Player)entity;
            if (!player.getUniqueId().equals(target.getUniqueId())
               && !target.isDead()
               && (
                  Foxtrot.getInstance().getTeamHandler().getTeam(player) == null
                     || !Foxtrot.getInstance().getTeamHandler().getTeam(player).getMembers().contains(target.getUniqueId())
               )
               && !Foxtrot.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())
               && (!CustomTimerCreateCommand.isSOTWTimer() || CustomTimerCreateCommand.hasSOTWEnabled(target.getUniqueId()))) {
               toReturn.add(target);
            }
         }
      }

      return toReturn;
   }
}
