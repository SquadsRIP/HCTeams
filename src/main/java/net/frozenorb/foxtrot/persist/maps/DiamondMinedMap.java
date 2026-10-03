package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class DiamondMinedMap extends PersistMap<Integer> {
   public DiamondMinedMap() {
      super("DiamondMined", "MiningStats.Diamond");
   }

   public String getRedisValue(Integer kills) {
      return String.valueOf(kills);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer mined) {
      return mined;
   }

   public int getMined(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setMined(Player update, int mined) {
      this.updateValueAsync(update.getUniqueId(), mined);
      if (mined == 50) {
         update.sendMessage(this.getMessage("Haste III", 50));
      } else if (mined == 100) {
         update.sendMessage(this.getMessage("Speed I", 100));
      } else if (mined == 250) {
         update.sendMessage(this.getMessage("Speed II", 250));
      } else if (mined == 500) {
         update.sendMessage(this.getMessage("Permanent Invisibility", 500));
      } else if (mined == 750) {
         update.sendMessage(this.getMessage("Resistance I", 750));
      } else if (mined == 1000) {
         update.sendMessage(this.getMessage("Resistance II", 1000));
      } else if (mined == 1500) {
         update.sendMessage(this.getMessage("Regeneration I", 1500));
      }
   }

   public String getMessage(String buff, int amount) {
      return ChatColor.YELLOW
         + "You have mined "
         + ChatColor.YELLOW
         + amount
         + ChatColor.YELLOW
         + " Diamond Ore. You now have "
         + ChatColor.YELLOW
         + buff
         + ChatColor.YELLOW
         + " in miner kit!";
   }
}
