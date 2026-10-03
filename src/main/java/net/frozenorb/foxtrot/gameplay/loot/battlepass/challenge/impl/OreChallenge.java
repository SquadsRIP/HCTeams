package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.entity.Player;

public class OreChallenge extends Challenge {
   private OreChallenge.OreType oreType;
   private int amount;

   public OreChallenge(String id, String name, int experience, boolean daily, OreChallenge.OreType oreType, int amount) {
      super(id, name, experience, daily);
      this.oreType = oreType;
      this.amount = amount;
   }

   @Override
   public Type getAbstractType() {
      return OreChallenge.class;
   }

   @Override
   public String getText() {
      return "Mine " + this.amount + " " + this.oreType.plural;
   }

   @Override
   public boolean hasStarted(Player player) {
      return this.oreType.getCount(player) > 0;
   }

   @Override
   public boolean meetsCompletionCriteria(Player player) {
      return this.oreType.getCount(player) >= this.amount;
   }

   @Override
   public String getProgressText(Player player) {
      int count = this.oreType.getCount(player);
      int remaining = this.amount - count;
      return CC.GRAY
         + "You need to mine "
         + Formats.formatNumber(remaining)
         + " more "
         + (remaining == 1 ? this.oreType.rendered : this.oreType.plural)
         + CC.GRAY
         + ".";
   }

   public OreChallenge.OreType getOreType() {
      return this.oreType;
   }

   public int getAmount() {
      return this.amount;
   }

   public enum OreType {
      COAL(CC.DARK_GRAY + "coal", CC.DARK_GRAY + "coal"),
      IRON(CC.GRAY + "iron", CC.GRAY + "iron"),
      GOLD(CC.GOLD + "gold", CC.GOLD + "gold"),
      EMERALD(CC.GREEN + "emerald", CC.GREEN + "emerald"),
      DIAMOND(CC.AQUA + "diamond", CC.AQUA + "diamond"),
      REDSTONE(CC.YELLOW + "redstone", CC.YELLOW + "redstone"),
      LAPIS(CC.BLUE + "lapis", CC.BLUE + "lapis");

      private String rendered;
      private String plural;

      public int getCount(Player player) {
         switch (this) {
            case COAL:
               return Foxtrot.getInstance().getCoalMinedMap().getMined(player.getUniqueId());
            case IRON:
               return Foxtrot.getInstance().getIronMinedMap().getMined(player.getUniqueId());
            case GOLD:
               return Foxtrot.getInstance().getGoldMinedMap().getMined(player.getUniqueId());
            case EMERALD:
               return Foxtrot.getInstance().getEmeraldMinedMap().getMined(player.getUniqueId());
            case DIAMOND:
               return Foxtrot.getInstance().getDiamondMinedMap().getMined(player.getUniqueId());
            case REDSTONE:
               return Foxtrot.getInstance().getRedstoneMinedMap().getMined(player.getUniqueId());
            case LAPIS:
               return Foxtrot.getInstance().getLapisMinedMap().getMined(player.getUniqueId());
            default:
               return 0;
         }
      }

      @ConstructorProperties({"rendered", "plural"})
      OreType(String rendered, String plural) {
         this.rendered = rendered;
         this.plural = plural;
      }
   }
}
