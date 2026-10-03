package net.frozenorb.foxtrot.nametag.modsuite;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.util.PotionUtil;
import net.frozenorb.foxtrot.util.RomanUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class InventorySeeHandler implements Listener {
   private static final ItemStack POTION_EFFECTS_ITEM = ItemBuilder.of(Material.BREWING_STAND_ITEM).name(ChatColor.GREEN + "Potion Effects").build();
   private static final ItemStack EDIT_INVENTORY_ITEM = ItemBuilder.of(Material.CHEST).name(ChatColor.GREEN + "Edit Inventory").build();
   private static final ItemStack AIR = new ItemStack(Material.AIR);

   public void openInventory(Player player, Player target) {
      new InventorySeeHandler.InventorySeeMenu(target).openMenu(player);
   }

   private static final class EditInventoryButton extends Button {
      private final Player target;

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         player.openInventory(this.target.getInventory());
      }

      @Override
      public ItemStack getButtonItem(Player player) {
         return InventorySeeHandler.EDIT_INVENTORY_ITEM.clone();
      }

      @Override
      public String getName(Player player) {
         return null;
      }

      @Override
      public List<String> getDescription(Player player) {
         return null;
      }

      @Override
      public Material getMaterial(Player player) {
         return null;
      }

      @ConstructorProperties("target")
      public EditInventoryButton(Player target) {
         this.target = target;
      }
   }

   private static class InventorySeeMenu extends Menu {
      private final Player target;

      public InventorySeeMenu(Player target) {
         this.target = target;
         this.setAutoUpdate(true);
      }

      @Override
      public String getTitle(Player player) {
         return this.target.getName() + "'s Inventory";
      }

      @Override
      public Map<Integer, Button> getButtons(Player player) {
         Map<Integer, Button> buttonMap = new HashMap<>();
         ItemStack[] contents = this.target.getInventory().getContents();
         ItemStack[] armor = this.target.getInventory().getArmorContents();

         for (int i = 0; i < contents.length; i++) {
            buttonMap.put(i, new InventorySeeHandler.ItemButton(contents[i] == null ? InventorySeeHandler.AIR : contents[i].clone()));
         }

         for (int i = 0; i < armor.length; i++) {
            buttonMap.put(41 + i, new InventorySeeHandler.ItemButton(armor[i] == null ? InventorySeeHandler.AIR : armor[i].clone()));
         }

         buttonMap.put(36, new InventorySeeHandler.PotionEffectButton(this.target));
         double health = Math.round(this.target.getHealth() * 10.0) / 10.0;
         double foodLevel = Math.round(this.target.getFoodLevel() * 10) / 10.0;
         buttonMap.put(
            37,
            new InventorySeeHandler.ItemButton(
               ItemBuilder.of(Material.SPECKLED_MELON)
                  .name(ChatColor.RED + "Health: " + ChatColor.WHITE + health + "/" + (int)this.target.getMaxHealth())
                  .amount((int)Math.max(1L, Math.round(health)))
                  .build()
            )
         );
         buttonMap.put(
            38,
            new InventorySeeHandler.ItemButton(
               ItemBuilder.of(Material.COOKED_BEEF)
                  .name(ChatColor.GOLD + "Food Level: " + ChatColor.WHITE + foodLevel + "/20")
                  .amount((int)Math.max(1L, Math.round(foodLevel)))
                  .build()
            )
         );
         if (player.hasPermission("command.invadmin.edit")) {
            buttonMap.put(39, new InventorySeeHandler.EditInventoryButton(this.target));
         }

         return buttonMap;
      }
   }

   private static final class ItemButton extends Button {
      private final ItemStack itemStack;

      @Override
      public ItemStack getButtonItem(Player player) {
         return this.itemStack;
      }

      @Override
      public String getName(Player player) {
         return null;
      }

      @Override
      public List<String> getDescription(Player player) {
         return null;
      }

      @Override
      public Material getMaterial(Player player) {
         return null;
      }

      @ConstructorProperties("itemStack")
      public ItemButton(ItemStack itemStack) {
         this.itemStack = itemStack;
      }
   }

   private static final class PotionEffectButton extends Button {
      private final Player target;

      @Override
      public ItemStack getButtonItem(Player player) {
         ItemBuilder potion = ItemBuilder.copyOf(InventorySeeHandler.POTION_EFFECTS_ITEM.clone());
         if (this.target.getActivePotionEffects().isEmpty()) {
            potion.addToLore(ChatColor.GREEN + "No active potion effects.");
         } else {
            for (PotionEffect effect : this.target.getActivePotionEffects()) {
               String name = PotionUtil.getFancyName(effect.getType());
               String amplifier = RomanUtil.toRoman(effect.getAmplifier() + 1);
               String duration = effect.getDuration() > 1728000 ? "Infinite" : TimeUtils.formatIntoMMSS(effect.getDuration() / 20);
               potion.addToLore(name + " " + amplifier + " (" + duration + ")");
            }
         }

         return potion.build();
      }

      @Override
      public String getName(Player player) {
         return null;
      }

      @Override
      public List<String> getDescription(Player player) {
         return null;
      }

      @Override
      public Material getMaterial(Player player) {
         return null;
      }

      @ConstructorProperties("target")
      public PotionEffectButton(Player target) {
         this.target = target;
      }
   }
}
