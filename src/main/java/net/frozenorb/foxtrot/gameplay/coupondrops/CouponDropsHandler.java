package net.frozenorb.foxtrot.gameplay.coupondrops;

import cc.fyre.proton.command.Command;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import net.buycraft.plugin.data.Coupon;
import net.buycraft.plugin.data.Coupon.Discount;
import net.buycraft.plugin.data.Coupon.Effective;
import net.buycraft.plugin.data.Coupon.Expire;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.integration.buycraft.LegacyBuycraftBridge;
import net.frozenorb.foxtrot.gameplay.coupondrops.listener.CouponDropsListener;
import net.frozenorb.foxtrot.gameplay.coupondrops.tasks.CouponDropsTask;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CouponDropsHandler {
   private Foxtrot instance;
   private long dropsIn;
   private Location location;

   public CouponDropsHandler(Foxtrot instance) {
      this.instance = instance;
      instance.getServer().getPluginManager().registerEvents(new CouponDropsListener(instance, this), instance);
   }

   public Coupon createCoupon() throws IOException {
      int[] percentages = new int[]{10, 15, 20};
      int percentage = percentages[ThreadLocalRandom.current().nextInt(percentages.length)];
      Discount discount = new Discount("percentage", new BigDecimal(percentage), new BigDecimal("0.0"));
      Calendar expiration = Calendar.getInstance();
      expiration.set(1, 2030);
      Coupon coupon = LegacyBuycraftBridge.configureCouponBuilder(
         Coupon.builder(), "Generated coupon drop at " + new Date(System.currentTimeMillis()).toLocaleString())
         .basketType("single")
         .code("DROP-" + RandomStringUtils.random(4, true, true))
         .userLimit(1)
         .startDate(new Date(System.currentTimeMillis()))
         .minimum(new BigDecimal("0.0"))
         .expire(new Expire("limit", 1, expiration.getTime()))
         .effective(new Effective("cart", Foxtrot.getInstance().getVoucherHandler().getEffectiveHandler().getEffectivePackages(), new ArrayList()))
         .startDate(new Date(System.currentTimeMillis()))
         .discount(discount)
         .build();
      LegacyBuycraftBridge.fromPlugin(Foxtrot.getInstance().getBuycraftPlugin()).createCoupon(coupon);
      return coupon;
   }

   @Command(names = {"coupondrops start", "discountdrops start", "discountdrop start", "coupondrop start"}, permission = "op")
   public static void execute(Player player) {
      Foxtrot instance = Foxtrot.getInstance();
      instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
         try {
            Coupon coupon = instance.getCouponDropsHandler().createCoupon();
            instance.getServer().getScheduler().runTask(instance, () -> {
               player.sendMessage(ChatColor.GREEN + "Started Coupon Drop!");
               new CouponDropsTask(instance, coupon).runTask(instance);
            });
         } catch (IOException e) {
            instance.getLogger().warning("Coupon drop creation failed: " + e.getMessage());
            instance.getServer().getScheduler().runTask(instance,
               () -> player.sendMessage(ChatColor.RED + "Could not start a coupon drop: " + e.getMessage()));
         }
      });
   }

   @Command(names = {"coupondrops fix", "discountdrops fix", "discountdrop fix", "coupondrop fix"}, permission = "op")
   public static void fix(Player player) {
      player.sendMessage(ChatColor.GREEN + "Fixed Coupon Drop!");
      Foxtrot.getInstance().getCouponDropsHandler().setDropsIn(0L);
      Foxtrot.getInstance().getCouponDropsHandler().setLocation(null);
   }

   public long getDropsIn() {
      return this.dropsIn;
   }

   public void setDropsIn(long dropsIn) {
      this.dropsIn = dropsIn;
   }

   public Location getLocation() {
      return this.location;
   }

   public void setLocation(Location location) {
      this.location = location;
   }
}
