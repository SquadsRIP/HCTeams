package net.frozenorb.foxtrot.gameplay.loot.battlepass.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.menu.ChallengesMenu;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.reward.Reward;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Formats;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BattlePassMenu extends Menu {
   private static BattlePassHandler handler = Foxtrot.getInstance().getBattlePassHandler();
   private final BattlePassProgress progress;
   private int page = 1;

   public BattlePassMenu(BattlePassProgress progress, int page) {
      this.progress = progress;
      this.page = page;
   }

   @Override
   public String getTitle(Player player) {
      return "BattlePass";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();
      buttons.put(2, new BattlePassMenu.GoToChallengesButton(false));
      buttons.put(4, new BattlePassMenu.InfoButton());
      buttons.put(6, new BattlePassMenu.GoToChallengesButton(true));
      IntRange range = this.page == 1 ? new IntRange(1, 7) : new IntRange((this.page - 1) * 7 + 1, this.page * 7);
      int slotOffset = 0;

      for (int tierNumber : range.toArray()) {
         Tier tier = handler.getTier(tierNumber);
         if (tier != null) {
            buttons.put(
               28 + slotOffset - 9,
               tier.getPremiumReward() != null ? new BattlePassMenu.RewardButton(tier, tier.getPremiumReward()) : new BattlePassMenu.EmptyRewardButton(tier)
            );
            buttons.put(28 + slotOffset++, new BattlePassMenu.TierButton(tier));
            buttons.put(
               28 + slotOffset + 8,
               tier.getFreeReward() != null ? new BattlePassMenu.RewardButton(tier, tier.getFreeReward()) : new BattlePassMenu.EmptyRewardButton(tier)
            );
         }
      }

      buttons.put(27, new BattlePassMenu.PreviousPageButton());
      buttons.put(35, new BattlePassMenu.NextPageButton());

      for (int i = 0; i < 54; i++) {
         if (!buttons.containsKey(i)) {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, " "));
         }
      }

      return buttons;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @Override
   public boolean isUpdateAfterClick() {
      return true;
   }

   @ConstructorProperties("progress")
   public BattlePassMenu(BattlePassProgress progress) {
      this.progress = progress;
   }

   private class EmptyRewardButton extends Button {
      private Tier tier;

      @Override
      public String getName(Player player) {
         return CC.GOLD + CC.BOLD + "Tier " + this.tier.getNumber();
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.MINECART;
      }

      @ConstructorProperties("tier")
      public EmptyRewardButton(Tier tier) {
         this.tier = tier;
      }
   }

   private class GoToChallengesButton extends Button {
      private boolean daily;

      @Override
      public String getName(Player player) {
         return this.daily ? CC.GOLD + CC.BOLD + "Daily Challenges" : CC.GOLD + CC.BOLD + "Premium Challenges";
      }

      @Override
      public List<String> getDescription(Player player) {
         return !this.daily && !BattlePassMenu.this.progress.isPremium()
            ? Formats.renderLines(
               CC.YELLOW,
               "You don't have access to the Premium BattlePass challenges! Purchase on our store at " + ConfigMessages.link("store", "Coming Soon!") + "."
            )
            : Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return this.daily ? Material.GOLD_INGOT : Material.DIAMOND;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (!this.daily && !BattlePassMenu.this.progress.isPremium()) {
            player.sendMessage(
               CC.YELLOW
                  + "You don't have access to the Premium BattlePass challenges! Purchase on our store at "
                  + ConfigMessages.link("store", "Coming Soon!")
                  + "."
            );
         } else {
            new ChallengesMenu(this.daily, BattlePassMenu.this.progress).openMenu(player);
         }
      }

      @ConstructorProperties("daily")
      public GoToChallengesButton(boolean daily) {
         this.daily = daily;
      }
   }

   private class InfoButton extends Button {
      private InfoButton() {
      }

      @Override
      public String getName(Player player) {
         return CC.GOLD + CC.BOLD + "BattlePass";
      }

      @Override
      public List<String> getDescription(Player player) {
         return Arrays.asList(
            CC.GRAY + "Current Tier: " + CC.GOLD + BattlePassMenu.this.progress.getCurrentTier().getNumber(),
            CC.GRAY + "Current XP: " + CC.GOLD + Formats.formatNumber(BattlePassMenu.this.progress.getExperience())
         );
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.NETHER_STAR;
      }

      @Override
      public ItemStack getButtonItem(Player player) {
         ItemStack itemStack = super.getButtonItem(player);
         itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
         return itemStack;
      }
   }

   private class NextPageButton extends Button {
      private NextPageButton() {
      }

      @Override
      public String getName(Player player) {
         return BattlePassMenu.this.page < 4 ? CC.YELLOW + CC.BOLD + "Next Page" : CC.GRAY + CC.BOLD + "No Next Page";
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.LEVER;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (clickType.isLeftClick() && BattlePassMenu.this.page < 4) {
            BattlePassMenu.this.page = BattlePassMenu.this.page + 1;
            BattlePassMenu menu = new BattlePassMenu(BattlePassMenu.this.progress, BattlePassMenu.this.page);
            player.closeInventory();
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> menu.openMenu(player), 1L);
         }
      }
   }

   private class PreviousPageButton extends Button {
      private PreviousPageButton() {
      }

      @Override
      public String getName(Player player) {
         return BattlePassMenu.this.page > 1 ? CC.YELLOW + CC.BOLD + "Previous Page" : CC.GRAY + CC.BOLD + "No Previous Page";
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.LEVER;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (clickType.isLeftClick() && BattlePassMenu.this.page > 1) {
            BattlePassMenu.this.page = BattlePassMenu.this.page - 1;
            BattlePassMenu menu = new BattlePassMenu(BattlePassMenu.this.progress, BattlePassMenu.this.page);
            player.closeInventory();
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> menu.openMenu(player), 1L);
         }
      }
   }

   private class RewardButton extends Button {
      private Tier tier;
      private Reward reward;

      @Override
      public String getName(Player player) {
         if (BattlePassMenu.this.progress.getExperience() >= this.tier.getRequiredExperience()) {
            if (this.reward.hasClaimed(BattlePassMenu.this.progress)) {
               return CC.GREEN + CC.BOLD + "Claimed Reward";
            } else {
               return !this.reward.isFreeReward() && !BattlePassMenu.this.progress.isPremium()
                  ? CC.YELLOW + CC.BOLD + "Reward Locked"
                  : CC.YELLOW + CC.BOLD + "Claim Reward";
            }
         } else {
            return CC.YELLOW + CC.BOLD + "Reward Locked";
         }
      }

      @Override
      public List<String> getDescription(Player player) {
         ArrayList<String> description = new ArrayList<>();

         for (String line : this.reward.getText()) {
            description.add(CC.GRAY + "● " + line);
         }

         if (!this.reward.isFreeReward() && !BattlePassMenu.this.progress.isPremium()) {
            description.add("");
            description.addAll(
               Formats.renderLines(
                  CC.YELLOW,
                  "You don't have access to the Premium BattlePass rewards! Purchase on our store at " + ConfigMessages.link("store", "Coming Soon!") + "."
               )
            );
         }

         return description;
      }

      @Override
      public Material getMaterial(Player player) {
         if (BattlePassMenu.this.progress.getExperience() >= this.tier.getRequiredExperience()) {
            if (this.reward.hasClaimed(BattlePassMenu.this.progress)) {
               return Material.MINECART;
            } else {
               return !this.reward.isFreeReward() && !BattlePassMenu.this.progress.isPremium() ? Material.HOPPER_MINECART : Material.STORAGE_MINECART;
            }
         } else {
            return Material.HOPPER_MINECART;
         }
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if ((this.tier.getNumber() != 1 || BattlePassMenu.this.progress.getExperience() >= this.tier.getRequiredExperience())
            && this.tier.getRequiredExperience() >= this.tier.getRequiredExperience()) {
            if (BattlePassMenu.this.progress.getExperience() >= this.tier.getRequiredExperience()) {
               if (this.reward.isFreeReward() && !BattlePassMenu.this.progress.getClaimedRewardsFree().contains(this.tier)) {
                  BattlePassMenu.this.progress.getClaimedRewardsFree().add(this.tier);
                  BattlePassMenu.this.progress.requiresSave();
                  this.reward.execute(player);
               } else if (BattlePassMenu.this.progress.isPremium() && !BattlePassMenu.this.progress.getClaimedRewardsPremium().contains(this.tier)) {
                  BattlePassMenu.this.progress.getClaimedRewardsPremium().add(this.tier);
                  BattlePassMenu.this.progress.requiresSave();
                  this.reward.execute(player);
               }
            }
         }
      }

      @ConstructorProperties({"tier", "reward"})
      public RewardButton(Tier tier, Reward reward) {
         this.tier = tier;
         this.reward = reward;
      }
   }

   private class TierButton extends Button {
      private Tier tier;

      @Override
      public String getName(Player player) {
         return CC.GOLD + CC.BOLD + "Tier " + this.tier.getNumber();
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.singletonList(
            Formats.formatExperience(Math.min(BattlePassMenu.this.progress.getExperience(), this.tier.getRequiredExperience()))
               + CC.GRAY
               + "/"
               + Formats.formatExperience(this.tier.getRequiredExperience())
         );
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.STAINED_GLASS_PANE;
      }

      @Override
      public byte getDamageValue(Player player) {
         if (BattlePassMenu.this.progress.getExperience() >= this.tier.getRequiredExperience()) {
            return 5;
         } else {
            return (byte)((this.tier.getNumber() != 1 || BattlePassMenu.this.progress.getExperience() >= this.tier.getRequiredExperience())
                  && BattlePassMenu.this.progress.getCurrentTier().getNumber() != this.tier.getNumber()
               ? 14
               : 4);
         }
      }

      @ConstructorProperties("tier")
      public TierButton(Tier tier) {
         this.tier = tier;
      }
   }
}
