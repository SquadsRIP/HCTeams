package net.frozenorb.foxtrot.team.track;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;

public enum TrackCategory {
   STAFF("Staff Actions", Material.DIAMOND_AXE, Collections.emptyList(), (byte)0, 0),
   GENERAL("General Logs", Material.GRASS, Collections.emptyList(), (byte)0, 10),
   CHAT("Chat History", Material.PAPER, Collections.emptyList(), (byte)0, 12),
   FINANCIAL_LAND("Financial & Land", Material.GOLD_INGOT, Collections.emptyList(), (byte)0, 14),
   CONNECTIONS("Connections", Material.COMMAND, Collections.emptyList(), (byte)0, 16),
   MEMBERS("Members Logs", Material.SKULL_ITEM, Collections.emptyList(), (byte)3, 28),
   DEATHS("Deaths", Material.SKULL_ITEM, Collections.emptyList(), (byte)0, 30),
   KILLS("Kills", Material.DIAMOND_SWORD, Collections.emptyList(), (byte)0, 32),
   ALL("All Logs", Material.BOOK, Collections.emptyList(), (byte)0, 34);

   String displayName;
   Material material;
   List<String> lore;
   byte data;
   int slot;

   @ConstructorProperties({"displayName", "material", "lore", "data", "slot"})
   TrackCategory(String displayName, Material material, List<String> lore, byte data, int slot) {
      this.displayName = displayName;
      this.material = material;
      this.lore = lore;
      this.data = data;
      this.slot = slot;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public Material getMaterial() {
      return this.material;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public byte getData() {
      return this.data;
   }

   public int getSlot() {
      return this.slot;
   }
}
