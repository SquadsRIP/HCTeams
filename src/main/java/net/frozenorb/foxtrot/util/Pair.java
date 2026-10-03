package net.frozenorb.foxtrot.util;

import java.util.Objects;

public class Pair<K, V> {
   public K first;
   public V second;

   public Pair(K first, V second) {
      this.first = first;
      this.second = second;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Pair pair = (Pair)o;
         return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.first, this.second);
   }

   @Override
   public String toString() {
      return "Pair(first=" + this.first + ", second=" + this.second + ")";
   }
}
