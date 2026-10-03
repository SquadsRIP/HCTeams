package net.frozenorb.foxtrot.server.deaths.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class DeathMainMenu extends Menu {
   private OfflinePlayer target;

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(2, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + "Past Deaths";
         }

         @Override
         public List<String> getDescription(Player player) {
            return Arrays.asList("", ChatColor.GREEN + "Click to view all of " + DeathMainMenu.this.target.getName() + "'s past kills");
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.REDSTONE_BLOCK;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            new DeathsMenu(DeathMainMenu.this.target, new SimpleDateFormat("M dd yyyy h:mm a")).openMenu(player);
         }
      });
      toReturn.put(6, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Past Kills";
         }

         @Override
         public List<String> getDescription(Player player) {
            return Arrays.asList("", ChatColor.GREEN + "Click to view all of " + DeathMainMenu.this.target.getName() + "'s past kills");
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.EMERALD_BLOCK;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            new KillsMenu(DeathMainMenu.this.target, new SimpleDateFormat("M dd yyyy h:mm a")).openMenu(player);
         }
      });
      return toReturn;
   }

   @Override
   public String getTitle(Player player) {
      return "Select an option";
   }

   @ConstructorProperties("target")
   public DeathMainMenu(OfflinePlayer target) {
      this.target = target;
   }
}
