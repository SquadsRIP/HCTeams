package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.ArcherTagsChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.AttemptCaptureKOTHChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.KillEntityChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.KillstreakChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.MakeFactionRaidableChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.MineBlockChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.OreChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.PlayTimeChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.UsePartnerItemChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.ValuablesSoldChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitActiveKOTHChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitEndChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitGlowstoneMountain;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitNetherChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.menu.BattlePassMenu;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ChallengesMenu extends Menu {
   private static int[] SLOTS = new int[]{19, 20, 21, 22, 23, 24, 25};
   private int page = 1;
   private final boolean daily;
   private final BattlePassProgress progress;
   private static Comparator<Challenge> CHALLENGE_COMPARATOR = new Comparator<Challenge>() {
      public int compare(Challenge o1, Challenge o2) {
         if (o1.getClass() == o2.getClass()) {
            if (o1 instanceof PlayTimeChallenge) {
               return (int)((PlayTimeChallenge)o1).getPlayTime() - (int)((PlayTimeChallenge)o2).getPlayTime();
            }

            if (o1 instanceof OreChallenge) {
               return ((OreChallenge)o1).getAmount() - ((OreChallenge)o2).getAmount();
            }

            if (o1 instanceof MineBlockChallenge) {
               return ((MineBlockChallenge)o1).getAmount() - ((MineBlockChallenge)o2).getAmount();
            }

            if (o1 instanceof KillEntityChallenge) {
               return ((KillEntityChallenge)o1).getKills() - ((KillEntityChallenge)o2).getKills();
            }

            if (o1 instanceof UsePartnerItemChallenge) {
               return ((UsePartnerItemChallenge)o1).getUses() - ((UsePartnerItemChallenge)o2).getUses();
            }
         }

         return ChallengesMenu.PRIORITIES.get(o1.getClass()) - ChallengesMenu.PRIORITIES.get(o2.getClass());
      }
   };
   private static Map<Class<?>, Integer> PRIORITIES = new HashMap<>();

   @Override
   public String getTitle(Player player) {
      return (this.daily ? "Daily" : "Premium") + " Challenges";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> buttons = new HashMap<>();
      Collection<Challenge> challenges;
      if (this.daily) {
         challenges = Foxtrot.getInstance()
            .getBattlePassHandler()
            .getDailyChallenges()
            .getChallenges()
            .stream()
            .sorted(CHALLENGE_COMPARATOR)
            .collect(Collectors.toList());
      } else {
         challenges = Foxtrot.getInstance().getBattlePassHandler().getChallenges().stream().sorted(CHALLENGE_COMPARATOR).collect(Collectors.toList());
      }

      IntRange range;
      if (this.page == 1) {
         range = new IntRange(1, 7);
      } else {
         range = new IntRange((this.page - 1) * 7 + 1, this.page * 7);
      }

      int skipped = 0;
      int slotIndex = 0;

      for (Challenge challenge : challenges) {
         if (skipped < range.getMinimumInteger()) {
            skipped++;
         } else {
            buttons.put(SLOTS[slotIndex], new ChallengesMenu.ChallengeButton(challenge));
            buttons.put(SLOTS[slotIndex] + 9, new ChallengesMenu.ChallengeStatusButton(challenge));
            if (slotIndex >= 6) {
               break;
            }

            slotIndex++;
         }
      }

      buttons.put(4, new ChallengesMenu.InfoButton());
      buttons.put(49, new ChallengesMenu.BackButton());
      buttons.put(27, new ChallengesMenu.PreviousPageButton());
      buttons.put(35, new ChallengesMenu.NextPageButton());

      for (int i = 0; i < 54; i++) {
         if (!buttons.containsKey(i)) {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, " "));
         }
      }

      return buttons;
   }

   private int getMaxPages() {
      Collection<Challenge> challenges;
      if (this.daily) {
         challenges = Foxtrot.getInstance().getBattlePassHandler().getDailyChallenges().getChallenges();
      } else {
         challenges = Foxtrot.getInstance().getBattlePassHandler().getChallenges();
      }

      return challenges.size() == 0 ? 1 : (int)Math.ceil(challenges.size() / 7.0);
   }

   @ConstructorProperties({"daily", "progress"})
   public ChallengesMenu(boolean daily, BattlePassProgress progress) {
      this.daily = daily;
      this.progress = progress;
   }

   static {
      PRIORITIES.put(ValuablesSoldChallenge.class, 0);
      PRIORITIES.put(MineBlockChallenge.class, 1);
      PRIORITIES.put(KillEntityChallenge.class, 2);
      PRIORITIES.put(PlayTimeChallenge.class, 4);
      PRIORITIES.put(OreChallenge.class, 5);
      PRIORITIES.put(AttemptCaptureKOTHChallenge.class, 6);
      PRIORITIES.put(VisitActiveKOTHChallenge.class, 7);
      PRIORITIES.put(VisitNetherChallenge.class, 8);
      PRIORITIES.put(VisitEndChallenge.class, 9);
      PRIORITIES.put(VisitGlowstoneMountain.class, 10);
      PRIORITIES.put(ArcherTagsChallenge.class, 11);
      PRIORITIES.put(KillstreakChallenge.class, 12);
      PRIORITIES.put(MakeFactionRaidableChallenge.class, 13);
      PRIORITIES.put(UsePartnerItemChallenge.class, 14);
   }

   private class BackButton extends Button {
      private BackButton() {
      }

      @Override
      public String getName(Player player) {
         return CC.RED + CC.BOLD + "Go Back";
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.BED;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (ChallengesMenu.this.progress != null) {
            new BattlePassMenu(ChallengesMenu.this.progress).openMenu(player);
         } else {
            BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
            new BattlePassMenu(progress).openMenu(player);
         }
      }
   }

   private class ChallengeButton extends Button {
      private Challenge challenge;

      @Override
      public String getName(Player player) {
         if (ChallengesMenu.this.progress.hasCompletedChallenge(this.challenge)) {
            return CC.GREEN + CC.BOLD + this.challenge.getName();
         } else {
            return this.challenge.hasStarted(player) ? CC.YELLOW + CC.BOLD + this.challenge.getName() : CC.RED + CC.BOLD + this.challenge.getName();
         }
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.singletonList(CC.GRAY + this.challenge.getText());
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.BOOK;
      }

      @ConstructorProperties("challenge")
      public ChallengeButton(Challenge challenge) {
         this.challenge = challenge;
      }
   }

   private class ChallengeStatusButton extends Button {
      private Challenge challenge;

      @Override
      public String getName(Player player) {
         if (ChallengesMenu.this.progress.hasCompletedChallenge(this.challenge)) {
            return CC.GREEN + CC.BOLD + "Completed";
         } else {
            return this.challenge.hasStarted(player) ? CC.YELLOW + CC.BOLD + "Started" : CC.RED + CC.BOLD + "Not Started";
         }
      }

      @Override
      public List<String> getDescription(Player player) {
         if (ChallengesMenu.this.progress.hasCompletedChallenge(this.challenge)) {
            return Collections.singletonList(CC.GRAY + "You've completed this challenge!");
         }

         String progressText = this.challenge.getProgressText(player);
         return progressText != null ? Collections.singletonList(CC.GRAY + progressText) : Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.STAINED_GLASS_PANE;
      }

      @Override
      public byte getDamageValue(Player player) {
         if (ChallengesMenu.this.progress.hasCompletedChallenge(this.challenge)) {
            return 5;
         } else {
            return (byte)(this.challenge.hasStarted(player) ? 4 : 14);
         }
      }

      @ConstructorProperties("challenge")
      public ChallengeStatusButton(Challenge challenge) {
         this.challenge = challenge;
      }
   }

   private class InfoButton extends Button {
      private InfoButton() {
      }

      @Override
      public String getName(Player player) {
         return CC.GOLD + CC.BOLD + (ChallengesMenu.this.daily ? "Daily" : "Premium") + " Challenges";
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.emptyList();
      }

      @Override
      public Material getMaterial(Player player) {
         return ChallengesMenu.this.daily ? Material.GOLD_INGOT : Material.DIAMOND;
      }
   }

   private class NextPageButton extends Button {
      private NextPageButton() {
      }

      @Override
      public String getName(Player player) {
         return ChallengesMenu.this.page < ChallengesMenu.this.getMaxPages() ? CC.RED + CC.BOLD + "Next Page" : CC.GRAY + CC.BOLD + "No Next Page";
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
         if (clickType.isLeftClick() && ChallengesMenu.this.page < ChallengesMenu.this.getMaxPages()) {
            ChallengesMenu.this.page = ChallengesMenu.this.page + 1;
            ChallengesMenu.this.openMenu(player);
         }
      }
   }

   private class PreviousPageButton extends Button {
      private PreviousPageButton() {
      }

      @Override
      public String getName(Player player) {
         return ChallengesMenu.this.page > 1 ? CC.RED + CC.BOLD + "Previous Page" : CC.GRAY + CC.BOLD + "No Previous Page";
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
         if (clickType.isLeftClick() && ChallengesMenu.this.page > 1) {
            ChallengesMenu.this.page = ChallengesMenu.this.page - 1;
            ChallengesMenu.this.openMenu(player);
         }
      }
   }
}
