package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import cc.fyre.proton.util.ItemUtils;
import java.lang.reflect.Type;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MineBlockChallenge extends Challenge {
   private Material material;
   private int amount;

   public MineBlockChallenge(String id, String text, int experience, boolean daily, Material material, int amount) {
      super(id, text, experience, daily);
      this.material = material;
      this.amount = amount;
   }

   public String getBlockName() {
      return ItemUtils.getName(new ItemStack(this.material));
   }

   public int getMined(Player player) {
      return this.getProgress(player).getBlocksMined(this.material, this.isDaily());
   }

   @Override
   public Type getAbstractType() {
      return MineBlockChallenge.class;
   }

   @Override
   public String getText() {
      return "Mine " + Formats.formatNumber(this.amount) + " " + this.getBlockName();
   }

   @Override
   public boolean hasStarted(Player player) {
      return this.getMined(player) > 0;
   }

   @Override
   public boolean meetsCompletionCriteria(Player player) {
      return this.getMined(player) >= this.amount;
   }

   @Override
   public String getProgressText(Player player) {
      int remaining = this.amount - this.getMined(player);
      return "You need to mine " + Formats.formatNumber(remaining) + " more " + this.getBlockName() + ".";
   }

   public Material getMaterial() {
      return this.material;
   }

   public int getAmount() {
      return this.amount;
   }
}
