package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.util.BaseGenerationTask;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BaseMenu extends Menu {
   Team team;
   Claim claim;

   @Override
   public String getTitle(Player player) {
      return "Choose a color";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = Maps.newHashMap();

      for (int i = 0; i < 16; i++) {
         final ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)i);
         buttons.put(i, new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.GREEN + "Click to select " + CraftItemStack.asNMSCopy(item).getName() + ".";
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               if (!VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                  player.closeInventory();
               } else if (BaseMenu.this.team.getBaseTokens() <= 0) {
                  player.sendMessage(ChatColor.RED + "Insufficient amount of base tokens! Purchases: Coming Soon!");
                  player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
               } else {
                  player.closeInventory();
                  BaseMenu.this.team.setBaseTokens(BaseMenu.this.team.getBaseTokens() - 1);
                  new BaseGenerationTask(BaseMenu.this.team, BaseMenu.this.claim, Material.STAINED_GLASS, item.getData().getData()).start();
               }
            }

            @Override
            public List<String> getDescription(Player player) {
               return Lists.newArrayList();
            }

            @Override
            public Material getMaterial(Player player) {
               return item.getType();
            }

            @Override
            public byte getDamageValue(Player player) {
               return item.getData().getData();
            }
         });
      }

      return buttons;
   }

   @ConstructorProperties({"team", "claim"})
   public BaseMenu(Team team, Claim claim) {
      this.team = team;
      this.claim = claim;
   }
}
