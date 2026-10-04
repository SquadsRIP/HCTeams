package net.frozenorb.foxtrot.gameplay.coupondrops.listener;

import cc.fyre.proton.event.HourEvent;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import java.beans.ConstructorProperties;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.frozenorb.foxtrot.apollo.ApolloService;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.coupondrops.CouponDropsHandler;
import net.frozenorb.foxtrot.gameplay.coupondrops.tasks.CouponDropsTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CouponDropsListener implements Listener {
   private Foxtrot instance;
   private CouponDropsHandler couponDropsHandler;
   public static LCWaypoint couponDropWaypoint;

   @EventHandler
   private void onPickupCoupon(PlayerPickupItemEvent event) {
      ItemStack itemStack = event.getItem().getItemStack();
      String displayName;
      if (itemStack != null
         && itemStack.getType() == Material.BOOK_AND_QUILL
         && itemStack.hasItemMeta()
         && (displayName = itemStack.getItemMeta().getDisplayName()) != null) {
         boolean contains = CouponDropsTask.ACTIVE_COUPONS.contains(displayName);
         if (contains && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
         }

         boolean removed = CouponDropsTask.ACTIVE_COUPONS.remove(displayName);
         Foxtrot.getInstance().getCouponDropsHandler().setDropsIn(0L);
         Foxtrot.getInstance().getCouponDropsHandler().setLocation(null);
         if (removed) {
            for (Player player : getOnlinePlayersList()) {
               player.sendMessage(" ");
               player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Discount Drops");
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7The book containing a coupon has been picked up!"));
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePicked up By: &f" + event.getPlayer().getDisplayName()));
               player.sendMessage(" ");
               ApolloService.removeWaypoint(player, couponDropWaypoint, true);
            }
         }
      }
   }

   @EventHandler
   private void onHour(HourEvent event) {
   }

   @ConstructorProperties({"instance", "couponDropsHandler"})
   public CouponDropsListener(Foxtrot instance, CouponDropsHandler couponDropsHandler) {
      this.instance = instance;
      this.couponDropsHandler = couponDropsHandler;
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }
}
