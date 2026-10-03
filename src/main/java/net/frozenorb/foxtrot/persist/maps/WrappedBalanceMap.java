package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class WrappedBalanceMap extends PersistMap<Double> {
   public WrappedBalanceMap() {
      super("WrappedBalances", "Balance");
   }

   public String getRedisValue(Double balance) {
      return String.valueOf(balance);
   }

   public Double getJavaObject(String str) {
      return Double.parseDouble(str);
   }

   public Object getMongoValue(Double balance) {
      return balance;
   }

   public double getBalance(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0.0;
   }

   public void setBalance(UUID update, double balance) {
      this.updateValueAsync(update, balance);
   }
}
