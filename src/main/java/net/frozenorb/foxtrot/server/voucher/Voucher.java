package net.frozenorb.foxtrot.server.voucher;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;

public class Voucher {
   private UUID target;
   private String voucher;
   private String addedBy;
   private long addedTime;
   private String usedBy;
   private String code;
   private String bundleID;
   private int amount;
   private boolean rank;
   private boolean bundle;
   private boolean generating;
   private String rankName;
   private String rankDuration;
   private long usedTime;
   private boolean used;

   public Voucher(UUID target, String voucher, String addedBy) {
      this.target = target;
      this.voucher = voucher;
      this.addedBy = addedBy;
      this.addedTime = System.currentTimeMillis();
      this.usedBy = "";
      this.code = "";
      this.bundleID = "";
      this.usedTime = 0L;
      this.bundle = false;
      this.used = false;
      this.rank = false;
      this.rankName = "";
      this.rankDuration = "";
      this.amount = 0;
      Foxtrot.getInstance().getVoucherHandler().getCache().add(this);
   }

   public Voucher(
      UUID target,
      String voucher,
      String addedBy,
      long addedTime,
      String usedBy,
      String code,
      long usedTime,
      boolean used,
      boolean rank,
      boolean bundle,
      String rankname,
      String rankDuration,
      String bundleID,
      int amount
   ) {
      this.target = target;
      this.voucher = voucher;
      this.addedBy = addedBy;
      this.addedTime = addedTime;
      this.usedBy = usedBy;
      this.code = code;
      this.usedTime = usedTime;
      this.used = used;
      this.bundle = bundle;
      this.bundleID = bundleID;
      this.rank = rank;
      this.rankName = rankname;
      this.rankDuration = rankDuration;
      this.amount = amount;
      Foxtrot.getInstance().getVoucherHandler().getCache().add(this);
   }

   public UUID getTarget() {
      return this.target;
   }

   public String getVoucher() {
      return this.voucher;
   }

   public String getAddedBy() {
      return this.addedBy;
   }

   public long getAddedTime() {
      return this.addedTime;
   }

   public String getUsedBy() {
      return this.usedBy;
   }

   public void setUsedBy(String usedBy) {
      this.usedBy = usedBy;
   }

   public String getCode() {
      return this.code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getBundleID() {
      return this.bundleID;
   }

   public void setBundleID(String bundleID) {
      this.bundleID = bundleID;
   }

   public int getAmount() {
      return this.amount;
   }

   public void setAmount(int amount) {
      this.amount = amount;
   }

   public boolean isRank() {
      return this.rank;
   }

   public void setRank(boolean rank) {
      this.rank = rank;
   }

   public boolean isBundle() {
      return this.bundle;
   }

   public void setBundle(boolean bundle) {
      this.bundle = bundle;
   }

   public boolean isGenerating() {
      return this.generating;
   }

   public void setGenerating(boolean generating) {
      this.generating = generating;
   }

   public String getRankName() {
      return this.rankName;
   }

   public void setRankName(String rankName) {
      this.rankName = rankName;
   }

   public String getRankDuration() {
      return this.rankDuration;
   }

   public void setRankDuration(String rankDuration) {
      this.rankDuration = rankDuration;
   }

   public long getUsedTime() {
      return this.usedTime;
   }

   public void setUsedTime(long usedTime) {
      this.usedTime = usedTime;
   }

   public boolean isUsed() {
      return this.used;
   }

   public void setUsed(boolean used) {
      this.used = used;
   }
}
