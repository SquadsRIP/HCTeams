package net.frozenorb.foxtrot.server.keyalls;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class KeyAll {
   private String id;
   private List<ItemStack> items;
   private String displayName;
   private boolean giving;
   private String scoreboardDisplay;
   private long giveAllTime;
   private long end;
   private List<UUID> redeemed;

   public KeyAll(String id) {
      this.id = id;
      this.items = new ArrayList<>();
      this.displayName = id;
      this.scoreboardDisplay = "";
      this.giveAllTime = 0L;
      this.end = 0L;
      this.redeemed = new ArrayList<>();
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public void setItems(List<ItemStack> items) {
      this.items = items;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public boolean isGiving() {
      return this.giving;
   }

   public void setGiving(boolean giving) {
      this.giving = giving;
   }

   public String getScoreboardDisplay() {
      return this.scoreboardDisplay;
   }

   public void setScoreboardDisplay(String scoreboardDisplay) {
      this.scoreboardDisplay = scoreboardDisplay;
   }

   public long getGiveAllTime() {
      return this.giveAllTime;
   }

   public void setGiveAllTime(long giveAllTime) {
      this.giveAllTime = giveAllTime;
   }

   public long getEnd() {
      return this.end;
   }

   public void setEnd(long end) {
      this.end = end;
   }

   public List<UUID> getRedeemed() {
      return this.redeemed;
   }

   public void setRedeemed(List<UUID> redeemed) {
      this.redeemed = redeemed;
   }
}
