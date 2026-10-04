package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.MobUtil;
import net.frozenorb.foxtrot.gameplay.killtags.KillTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PVPTracker implements Listener {
   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
      EntityDamageByEntityEvent e;
      if (event.getCause() instanceof EntityDamageByEntityEvent && (e = (EntityDamageByEntityEvent)event.getCause()).getDamager() instanceof Player) {
         Player damager = (Player)e.getDamager();
         Player damaged = event.getPlayer();
         event.setTrackerDamage(new PVPTracker.PVPDamage(damaged.getName(), event.getDamage(), damager.getName(), damager.getItemInHand()));
      }
   }

   public static class PVPDamage extends PlayerDamage {
      private String itemString = "Error";

      public PVPDamage(String damaged, double damage, String damager, ItemStack itemStack) {
         super(damaged, damage, damager);
         this.itemString = itemStack.getType() == Material.AIR ? "their fists" : MobUtil.getItemName(itemStack, false);
      }

      @Override
      public String getDeathMessage() {
         String extension = Foxtrot.getInstance().getInDuelPredicate().test(Bukkit.getPlayer(this.getDamaged()))
            ? " during a duel."
            : " using " + ChatColor.DARK_RED + this.itemString + ChatColor.RED + ".";
         Player player = Foxtrot.getInstance().getServer().getPlayer(this.getDamager());
         if (player == null) {
            return this.wrapName(this.getDamaged()) + " was slain by " + this.wrapName(this.getDamager()) + extension;
         }

         KillTags killTags = Foxtrot.getInstance().getKillTagMap().getKillTag(player.getUniqueId());
         return this.wrapName(this.getDamaged()) + killTags.getDeathTag() + this.wrapName(this.getDamager()) + extension;
      }
   }
}
