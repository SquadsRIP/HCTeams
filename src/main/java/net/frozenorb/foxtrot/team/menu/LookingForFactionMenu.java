package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.LookingForCommands;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class LookingForFactionMenu extends Menu {
   private final List<String> classes = new ArrayList<>();
   private final Set<String> selected = new LinkedHashSet<>();

   public LookingForFactionMenu() {
      this.setPlaceholder(true);
      // "Diamond" isn't a PvPClass (it's no class); the rest follow whatever classes are enabled on this map.
      this.classes.add("Diamond");

      for (PvPClass pvpClass : Foxtrot.getInstance().getPvpClassHandler().getPvpClasses()) {
         this.classes.add(pvpClass.getName());
      }
   }

   @Override
   public String getTitle(Player player) {
      return ChatColor.GOLD + "Looking for a Faction";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> buttons = new HashMap<>();
      int start = 9 + (9 - this.classes.size()) / 2;

      for (int i = 0; i < this.classes.size(); i++) {
         buttons.put(start + i, new ClassButton(this.classes.get(i)));
      }

      buttons.put(22, new PostButton());
      return buttons;
   }

   private static Material iconFor(String className) {
      switch (className) {
         case "Diamond":
            return Material.DIAMOND_CHESTPLATE;
         case "Bard":
            return Material.GOLD_CHESTPLATE;
         case "Archer":
            return Material.LEATHER_CHESTPLATE;
         case "Rogue":
            return Material.CHAINMAIL_CHESTPLATE;
         case "Miner":
            return Material.IRON_CHESTPLATE;
         default:
            return Material.IRON_SWORD;
      }
   }

   private class ClassButton extends Button {
      private final String className;

      ClassButton(String className) {
         this.className = className;
      }

      private boolean isSelected() {
         return LookingForFactionMenu.this.selected.contains(this.className);
      }

      @Override
      public String getName(Player player) {
         return (this.isSelected() ? ChatColor.GREEN : ChatColor.YELLOW) + ChatColor.BOLD.toString() + this.className;
      }

      @Override
      public List<String> getDescription(Player player) {
         List<String> lore = new ArrayList<>();
         lore.add("");
         lore.add(this.isSelected() ? ChatColor.GREEN + "✔ Selected" : ChatColor.GRAY + "Not selected");
         lore.add("");
         lore.add(ChatColor.GRAY + "Click to " + (this.isSelected() ? "remove" : "add") + " this class.");
         return lore;
      }

      @Override
      public Material getMaterial(Player player) {
         return LookingForFactionMenu.iconFor(this.className);
      }

      @Override
      public boolean isGlow() {
         return this.isSelected();
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (!LookingForFactionMenu.this.selected.remove(this.className)) {
            LookingForFactionMenu.this.selected.add(this.className);
         }

         Button.playNeutral(player);
      }
   }

   private class PostButton extends Button {
      @Override
      public String getName(Player player) {
         return ChatColor.GREEN + ChatColor.BOLD.toString() + "Post to Chat";
      }

      @Override
      public List<String> getDescription(Player player) {
         List<String> lore = new ArrayList<>();
         lore.add("");
         if (LookingForFactionMenu.this.selected.isEmpty()) {
            lore.add(ChatColor.GRAY + "Select one or more classes first.");
         } else {
            lore.add(ChatColor.GRAY + "Classes: " + ChatColor.WHITE + String.join(", ", LookingForFactionMenu.this.selected));
         }

         lore.add("");
         lore.add(ChatColor.GRAY + "Click to advertise that you're");
         lore.add(ChatColor.GRAY + "looking for a faction.");
         return lore;
      }

      @Override
      public Material getMaterial(Player player) {
         return LookingForFactionMenu.this.selected.isEmpty() ? Material.REDSTONE : Material.EMERALD;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (LookingForFactionMenu.this.selected.isEmpty()) {
            Button.playFail(player);
            player.sendMessage(ChatColor.RED + "Select at least one class first.");
            return;
         }

         player.closeInventory();
         // Re-check: they may have joined a faction or posted from another menu since this one opened.
         if (LookingForCommands.canUseLff(player)) {
            LookingForCommands.broadcastLff(player, LookingForFactionMenu.this.selected);
            Button.playSuccess(player);
         }
      }
   }
}
