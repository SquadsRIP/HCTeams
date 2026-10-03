package net.frozenorb.foxtrot.gameplay.boosters;

import cc.fyre.proton.util.UUIDUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Booster implements Listener {
   private long activatedAt = 0L;
   private UUID activatedBy = null;
   private long lastDeactivateAt = 0L;
   private boolean active;

   public abstract String getId();

   public abstract String getDisplayName();

   public abstract int getSlot();

   public abstract List<String> getDescription();

   public abstract ItemStack getItemDisplay();

   public boolean isOnCooldown() {
      return this.isActive() && this.activatedBy != null ? false : this.lastDeactivateAt + TimeUnit.HOURS.toHours(1L) >= System.currentTimeMillis();
   }

   public int inQueue() {
      return (int)Foxtrot.getInstance().getNetworkBoosterHandler().getBoostersQueued().values().stream().filter(it -> it.contains(this)).count();
   }

   public void activate(UUID target) {
      Foxtrot instance = Foxtrot.getInstance();
      this.activatedAt = System.currentTimeMillis();
      this.activatedBy = target;
      this.active = true;
      List<Booster> queued = instance.getNetworkBoosterHandler().getBoostersQueued().getOrDefault(target, new ArrayList<>());
      queued.remove(this);
      instance.getNetworkBoosterHandler().getBoostersQueued().put(target, queued);

      for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
         onlinePlayer.sendMessage("");
         onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4████&7██ &4&l" + ChatColor.stripColor(this.getDisplayName()) + " Booster"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█&7███&4█&7█ &7" + this.getDescription().get(0)));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4████&7██ &7" + this.getDescription().get(1)));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█&7███&4█&7█"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4████&7██ &cActivated By: " + ChatColor.WHITE + UUIDUtils.name(target)));
         onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
         onlinePlayer.sendMessage("");
         onlinePlayer.sendMessage(ChatColor.GREEN + "You can purchase a booster at " + ChatColor.WHITE + "Coming Soon!" + ChatColor.GREEN + "!");
      }
   }

   public void deactivate() {
      Foxtrot instance = Foxtrot.getInstance();
      NetworkBoosterHandler networkBoosterHandler = instance.getNetworkBoosterHandler();
      this.lastDeactivateAt = System.currentTimeMillis();
      this.active = false;
      this.activatedBy = null;
      UUID uuid = networkBoosterHandler.getActiveBoosters().remove(this);
      HandlerList.unregisterAll(this);
      String name = UUIDUtils.name(uuid);

      for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
         onlinePlayer.sendMessage("");
         onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Network Boosters");
         onlinePlayer.sendMessage(ChatColor.RED + "The " + this.getDisplayName() + " Booster " + ChatColor.RED + " has expired!");
         onlinePlayer.sendMessage(ChatColor.GRAY + "Thanks to " + ChatColor.WHITE + name + ChatColor.GRAY + " for activating this booster!");
         onlinePlayer.sendMessage("");
      }

      Player player = Foxtrot.getInstance().getServer().getPlayer(uuid);
      if (player != null) {
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         player.sendMessage(ChatColor.GREEN + "Your booster has been deactivated! Thank you for activating this booster!");
      }
   }

   public long getActivatedAt() {
      return this.activatedAt;
   }

   public void setActivatedAt(long activatedAt) {
      this.activatedAt = activatedAt;
   }

   public UUID getActivatedBy() {
      return this.activatedBy;
   }

   public void setActivatedBy(UUID activatedBy) {
      this.activatedBy = activatedBy;
   }

   public long getLastDeactivateAt() {
      return this.lastDeactivateAt;
   }

   public void setLastDeactivateAt(long lastDeactivateAt) {
      this.lastDeactivateAt = lastDeactivateAt;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }
}
