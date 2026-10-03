package net.frozenorb.foxtrot.integration.buycraft;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import net.buycraft.plugin.data.Coupon;
import org.junit.Test;
import static org.junit.Assert.*;

/** Offline contract tests only: all API calls below are in-memory fixtures. */
public class LegacyBuycraftBridgeTest {
   public static class Plugin {
      private final Object api;
      public Plugin(Object api) { this.api = api; }
      public Object getApiClient() { return this.api; }
   }
   public static class Builder {
      String note;
      boolean unlimited = true;
      int discount;
      public Builder note(String note) { this.note = note; return this; }
      public Builder redeemUnlimited(boolean value) { this.unlimited = value; return this; }
      public Builder discountMethod(int value) { this.discount = value; return this; }
   }
   public static class Response {
      final boolean success;
      final Object body;
      Response(boolean success, Object body) { this.success = success; this.body = body; }
      public boolean isSuccessful() { return this.success; }
      public Object body() { return this.body; }
   }
   public interface Call { Response execute() throws IOException; }
   // Retrofit request implementations can be package-private; invoke their public interface.
   private static class Request implements Call {
      final Response response;
      int executions;
      Request(Response response) { this.response = response; }
      public Response execute() { this.executions++; return this.response; }
   }
   public static class GiftCard {
      final String code;
      GiftCard(String code) { this.code = code; }
      public String getCode() { return this.code; }
   }
   public static class Listing {
      final Object data;
      Listing(Object data) { this.data = data; }
      public Object getData() { return this.data; }
   }
   public static class CustomApi {
      Request request;
      BigDecimal amount;
      String note;
      int deleted = -1;
      CustomApi(Object body) { this.request = new Request(new Response(true, body)); }
      public Call createGiftCard(BigDecimal amount, String note) {
         this.amount = amount; this.note = note; return this.request;
      }
      public Call createCoupon(Coupon coupon) { return this.request; }
      public Call getAllCoupons() { return this.request; }
      public Call deleteCoupon(int id) { this.deleted = id; return this.request; }
   }
   public static class StockApi {
      final List<Coupon> coupons = Arrays.asList(Coupon.builder().code("TEST").build());
      int deleted = -1;
      public List<Coupon> getAllCoupons() { return this.coupons; }
      public Coupon createCoupon(Coupon coupon) { return coupon; }
      public void deleteCoupon(int id) { this.deleted = id; }
   }
   public static class FailedApi {
      public Call createGiftCard(BigDecimal amount, String note) throws IOException {
         throw new IOException("offline fixture failure");
      }
   }
   private LegacyBuycraftBridge bridge(Object api) throws IOException {
      return LegacyBuycraftBridge.fromPlugin(new Plugin(api));
   }

   @Test(expected = IOException.class)
   public void missingPluginFailsClearly() throws Exception { LegacyBuycraftBridge.fromPlugin(null); }
   @Test(expected = IOException.class)
   public void unconfiguredPluginFailsClearly() throws Exception { bridge(null); }
   @Test public void preservesAllCustomCouponOptions() throws Exception {
      Builder builder = new Builder();
      assertSame(builder, LegacyBuycraftBridge.configureCouponBuilder(builder, "test note"));
      assertEquals("test note", builder.note);
      assertFalse(builder.unlimited);
      assertEquals(2, builder.discount);
   }
   @Test public void stockCouponBuilderCannotSilentlyDropOptions() throws Exception {
      try {
         LegacyBuycraftBridge.configureCouponBuilder(Coupon.builder(), "test note");
         fail("Missing custom flags must fail before a coupon is submitted");
      } catch (IOException expected) {
         assertTrue(expected.getMessage().contains("required custom API"));
      }
   }
   @Test public void stockGiftCardsAreExplicitlyUnavailable() throws Exception {
      try { bridge(new StockApi()).requireGiftCardSupport(); fail("Stock API has no gift cards"); }
      catch (IOException expected) { assertTrue(expected.getMessage().contains("createGiftCard")); }
   }
   @Test public void customGiftCardUsesOriginalArgumentsAndOneRequest() throws Exception {
      CustomApi api = new CustomApi(new Listing(new GiftCard("TEST-GIFT")));
      LegacyBuycraftBridge bridge = bridge(api);
      bridge.requireGiftCardSupport();
      assertEquals(0, api.request.executions);
      assertEquals("TEST-GIFT", bridge.createGiftCard(new BigDecimal("5"), "test note"));
      assertEquals(new BigDecimal("5"), api.amount);
      assertEquals("test note", api.note);
      assertEquals(1, api.request.executions);
   }
   @Test(expected = IOException.class)
   public void blankGiftCardNeverSucceeds() throws Exception {
      bridge(new CustomApi(new Listing(new GiftCard("  ")))).createGiftCard(BigDecimal.ONE, "test");
   }
   @Test(expected = IOException.class)
   public void missingGiftCardBodyNeverSucceeds() throws Exception {
      bridge(new CustomApi(null)).createGiftCard(BigDecimal.ONE, "test");
   }
   @Test(expected = IOException.class)
   public void failedHttpStatusNeverSucceeds() throws Exception {
      CustomApi api = new CustomApi(new Listing(new GiftCard("INVALID")));
      api.request = new Request(new Response(false, new Listing(new GiftCard("INVALID"))));
      bridge(api).createGiftCard(BigDecimal.ONE, "test");
   }
   @Test public void originalIoFailureIsPropagated() throws Exception {
      try { bridge(new FailedApi()).createGiftCard(BigDecimal.ONE, "test"); fail("Expected IOException"); }
      catch (IOException expected) { assertEquals("offline fixture failure", expected.getMessage()); }
   }
   @Test public void stockCouponListingAndDeletionAreSupported() throws Exception {
      StockApi api = new StockApi();
      assertSame(api.coupons, bridge(api).getAllCoupons());
      bridge(api).deleteCoupon(7);
      assertEquals(7, api.deleted);
   }
   @Test public void customCouponListingExecutesOnce() throws Exception {
      List<Coupon> coupons = Arrays.asList(Coupon.builder().code("TEST").build());
      CustomApi api = new CustomApi(new Listing(coupons));
      assertSame(coupons, bridge(api).getAllCoupons());
      assertEquals(1, api.request.executions);
   }
   @Test public void customCouponDeletionAcceptsSuccessfulEmptyBody() throws Exception {
      CustomApi api = new CustomApi(null);
      bridge(api).deleteCoupon(9);
      assertEquals(9, api.deleted);
      assertEquals(1, api.request.executions);
   }
   @Test public void couponCreationSupportsBothApiShapes() throws Exception {
      Coupon coupon = Coupon.builder().code("TEST").build();
      bridge(new StockApi()).createCoupon(coupon);
      CustomApi api = new CustomApi(coupon);
      bridge(api).createCoupon(coupon);
      assertEquals(1, api.request.executions);
   }
   @Test(expected = IOException.class)
   public void emptyCouponCreationCannotConsumeVoucher() throws Exception {
      bridge(new CustomApi(null)).createCoupon(Coupon.builder().code("TEST").build());
   }
   @Test(expected = IOException.class)
   public void rejectedCouponDeletionCannotReportSuccess() throws Exception {
      CustomApi api = new CustomApi(null);
      api.request = new Request(new Response(false, null));
      bridge(api).deleteCoupon(1);
   }
   @Test public void nestedCustomCouponConfirmationIsVerified() throws Exception {
      Coupon coupon = Coupon.builder().code("TEST").build();
      bridge(new CustomApi(new Listing(coupon))).createCoupon(coupon);
   }
   @Test(expected = IOException.class)
   public void malformedCouponConfirmationCannotConsumeVoucher() throws Exception {
      bridge(new CustomApi(new Object())).createCoupon(Coupon.builder().code("TEST").build());
   }
   @Test(expected = IOException.class)
   public void mismatchedCouponConfirmationCannotConsumeVoucher() throws Exception {
      bridge(new CustomApi(Coupon.builder().code("OTHER").build())).createCoupon(Coupon.builder().code("TEST").build());
   }
   @Test(expected = IOException.class)
   public void missingNestedCouponCannotConsumeVoucher() throws Exception {
      bridge(new CustomApi(new Listing(null))).createCoupon(Coupon.builder().code("TEST").build());
   }
}
