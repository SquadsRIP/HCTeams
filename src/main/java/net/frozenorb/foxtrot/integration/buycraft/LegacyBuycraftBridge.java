package net.frozenorb.foxtrot.integration.buycraft;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;

/**
 * Bridges the original custom Buycraft API without requiring its absent classes at compile time.
 * Stock Buycraft can list/delete coupons, but does not implement the custom gift-card API or
 * coupon redemption flags. Those operations fail explicitly rather than dropping their settings.
 */
public final class LegacyBuycraftBridge {
   private final Object api;

   private LegacyBuycraftBridge(Object api) {
      this.api = api;
   }

   public static LegacyBuycraftBridge fromPlugin(Object plugin) throws IOException {
      if (plugin == null) {
         throw new IOException("BuycraftX is unavailable. Enable the compatible BuycraftX plugin first.");
      }
      Object api = invoke(plugin, "getApiClient", new Class<?>[0]);
      if (api == null) {
         throw new IOException("BuycraftX has no configured API client.");
      }
      return new LegacyBuycraftBridge(api);
   }

   public static <T> T configureCouponBuilder(T builder, String note) throws IOException {
      // Keep every original setting: stock builders lack these custom methods.
      method(builder, "note", String.class);
      method(builder, "redeemUnlimited", boolean.class);
      method(builder, "discountMethod", int.class);
      invoke(builder, "note", new Class<?>[]{String.class}, note);
      invoke(builder, "redeemUnlimited", new Class<?>[]{boolean.class}, false);
      invoke(builder, "discountMethod", new Class<?>[]{int.class}, 2);
      return builder;
   }

   public void createCoupon(Object coupon) throws IOException {
      Object result = invoke(this.api, "createCoupon", new Class<?>[]{coupon.getClass()}, coupon);
      Object body = execute(result);
      if (body == null) {
         throw new IOException("BuycraftX returned no coupon confirmation; the voucher was not marked redeemed.");
      }
      Object confirmed = findMethod(body.getClass(), "getCode", new Class<?>[0]) != null
         ? body : invoke(body, "getData", new Class<?>[0]);
      Object requestedCode = invoke(coupon, "getCode", new Class<?>[0]);
      Object confirmedCode = invoke(confirmed, "getCode", new Class<?>[0]);
      if (!(requestedCode instanceof String) || ((String)requestedCode).trim().isEmpty()
         || !requestedCode.equals(confirmedCode)) {
         throw new IOException("BuycraftX did not confirm the requested coupon code; the voucher was not marked redeemed.");
      }
   }

   public List<?> getAllCoupons() throws IOException {
      Object result = execute(invoke(this.api, "getAllCoupons", new Class<?>[0]));
      Object data = result instanceof List ? result : invoke(result, "getData", new Class<?>[0]);
      if (!(data instanceof List)) {
         throw new IOException("BuycraftX returned an invalid coupon listing.");
      }
      return (List<?>)data;
   }

   public void deleteCoupon(int id) throws IOException {
      Object result = invoke(this.api, "deleteCoupon", new Class<?>[]{int.class}, id);
      // Stock API returns void; the custom API returns a request that must be executed.
      if (result != null) {
         execute(result);
      }
   }

   public void requireGiftCardSupport() throws IOException {
      method(this.api, "createGiftCard", BigDecimal.class, String.class);
   }

   public String createGiftCard(BigDecimal amount, String note) throws IOException {
      Object body = execute(invoke(this.api, "createGiftCard", new Class<?>[]{BigDecimal.class, String.class}, amount, note));
      Object data = invoke(body, "getData", new Class<?>[0]);
      Object code = invoke(data, "getCode", new Class<?>[0]);
      if (!(code instanceof String) || ((String)code).trim().isEmpty()) {
         throw new IOException("BuycraftX returned no gift-card code; no gift-card book was created.");
      }
      return (String)code;
   }

   private static Object execute(Object result) throws IOException {
      if (result == null) {
         return null;
      }
      Method execute = findMethod(result.getClass(), "execute", new Class<?>[0]);
      if (execute == null) {
         return result; // Stock ApiClient executes requests directly.
      }
      Object response = invoke(result, execute);
      if (response == null) {
         throw new IOException("BuycraftX returned no API response.");
      }
      Object successful = invoke(response, "isSuccessful", new Class<?>[0]);
      if (!Boolean.TRUE.equals(successful)) {
         throw new IOException("BuycraftX rejected the request; no success was recorded.");
      }
      return invoke(response, "body", new Class<?>[0]);
   }

   private static Method method(Object target, String name, Class<?>... types) throws IOException {
      Method method = target == null ? null : findMethod(target.getClass(), name, types);
      if (method == null) {
         throw new IOException("The installed BuycraftX does not provide the required custom API: " + name
            + ". Install the original compatible BuycraftX build; stock BuycraftX/Tebex is not a replacement.");
      }
      return method;
   }

   private static Method findMethod(Class<?> type, String name, Class<?>[] types) {
      try {
         Method method = type.getMethod(name, types);
         if (Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            return method;
         }
      } catch (NoSuchMethodException ignored) {
      }
      for (Class<?> contract : type.getInterfaces()) {
         Method method = findMethod(contract, name, types);
         if (method != null) {
            return method;
         }
      }
      return type.getSuperclass() == null ? null : findMethod(type.getSuperclass(), name, types);
   }

   private static Object invoke(Object target, String name, Class<?>[] types, Object... arguments) throws IOException {
      return invoke(target, method(target, name, types), arguments);
   }

   private static Object invoke(Object target, Method method, Object... arguments) throws IOException {
      try {
         return method.invoke(target, arguments);
      } catch (InvocationTargetException e) {
         Throwable cause = e.getCause();
         if (cause instanceof IOException) {
            throw (IOException)cause;
         }
         throw new IOException("BuycraftX API call failed: " + method.getName(), cause);
      } catch (ReflectiveOperationException | IllegalArgumentException e) {
         throw new IOException("Unable to use the installed BuycraftX API: " + method.getName(), e);
      }
   }
}
