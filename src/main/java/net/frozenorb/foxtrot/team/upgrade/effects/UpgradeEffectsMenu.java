package net.frozenorb.foxtrot.team.upgrade.effects;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UpgradeEffectsMenu extends Menu {
   private Team team;

   @Override
   public String getTitle(Player player) {
      return "Purchase Effects";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 27; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
      }

      for (final PurchaseableEffects value : PurchaseableEffects.values()) {
         toReturn.put(
            value.getSlot(),
            new Button() {
               @Override
               public String getName(Player player) {
                  return value.getDisplayName();
               }

               @Override
               public List<String> getDescription(Player player) {
                  List<String> lore = new ArrayList<>(value.getDescription());
                  lore.add("");
                  if (value.getCost() != -1) {
                     lore.add(ChatColor.translate("&4&l┃ &fCost: &c" + value.getCost() + " gems"));
                     lore.add("");
                  }

                  if (UpgradeEffectsMenu.this.team.getPurchasedEffects().contains(value.getPotionEffectType())) {
                     lore.add(ChatColor.RED + "Purchased");
                     return lore;
                  } else {
                     lore.add(ChatColor.RED + "Click to purchase this potion effect");
                     return lore;
                  }
               }

               @Override
               public Material getMaterial(Player player) {
                  return value.getMaterial();
               }

               @Override
               public void clicked(Player player, int slot, ClickType clickType) {
                  if (UpgradeEffectsMenu.this.team.getPurchasedEffects().contains(value.getPotionEffectType())) {
                     player.sendMessage(ChatColor.RED + "You have already purchased this potion effect!");
                  } else if (UpgradeEffectsMenu.this.team.getGems() < value.getCost()) {
                     player.sendMessage(ChatColor.RED + "Insufficient Gems balance");
                  } else {
                     UpgradeEffectsMenu.this.team.setRemovedGems(UpgradeEffectsMenu.this.team.getRemovedGems() + value.getCost());
                     UpgradeEffectsMenu.this.team.getPurchasedEffects().add(value.getPotionEffectType());
                     UpgradeEffectsMenu.this.team.flagForSave();
                     UpgradeEffectsMenu.this.team
                        .sendMessage(ChatColor.translate("&6Your team has purchased &f" + value.getDisplayName() + " &6for &f" + value.getCost() + " gems&6!"));

                     for (Player onlinePlayer : Foxtrot.getInstance()
                        .getServer()
                        .getOnlinePlayers()
                        .stream()
                        .filter(it -> LandBoard.getInstance().getTeam(it.getLocation()) == UpgradeEffectsMenu.this.team)
                        .collect(Collectors.toList())) {
                        if (!onlinePlayer.hasPotionEffect(value.getPotionEffectType())) {
                           onlinePlayer.addPotionEffect(
                              new PotionEffect(value.getPotionEffectType(), Integer.MAX_VALUE, value.getPotionEffectType() == PotionEffectType.SPEED ? 1 : 0)
                           );
                        }
                     }
                  }
               }
            }
         );
      }

      return toReturn;
   }

   @ConstructorProperties("team")
   public UpgradeEffectsMenu(Team team) {
      this.team = team;
   }
}
