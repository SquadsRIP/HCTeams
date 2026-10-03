package net.frozenorb.foxtrot.gameplay.kitmap.duel;

import java.beans.ConstructorProperties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;

public class DuelInvite {
   @NonNull
   private UUID sender;
   @NonNull
   private UUID target;
   @NonNull
   private int wager;
   private long sent = System.currentTimeMillis();

   public boolean hasExpired() {
      return System.currentTimeMillis() >= this.sent + TimeUnit.SECONDS.toMillis(30L);
   }

   @NonNull
   public UUID getSender() {
      return this.sender;
   }

   @NonNull
   public UUID getTarget() {
      return this.target;
   }

   @NonNull
   public int getWager() {
      return this.wager;
   }

   public long getSent() {
      return this.sent;
   }

   @ConstructorProperties({"sender", "target", "wager"})
   public DuelInvite(@NonNull UUID sender, @NonNull UUID target, @NonNull int wager) {
      if (sender == null) {
         throw new NullPointerException("sender");
      }

      if (target == null) {
         throw new NullPointerException("target");
      }

      this.sender = sender;
      this.target = target;
      this.wager = wager;
   }
}
