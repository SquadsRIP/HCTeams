package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public final class EditKitMenu extends Menu {
   private static final int EDITOR_X_OFFSET = 2;
   private static final int EDITOR_Y_OFFSET = 2;
   private final Kit kit;

   public EditKitMenu(Kit kit) {
      this.setNoncancellingInventory(true);
      this.setUpdateAfterClick(false);
      this.kit = kit;
   }

   @Override
   public void onOpen(Player player) {
      Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
         player.getInventory().setContents(this.kit.getInventoryContents());
         player.updateInventory();
      }, 1L);
   }

   @Override
   public void onClose(Player player) {
      InventoryUtils.resetInventoryNow(player);
      Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> new KitsMenu().openMenu(player), 1L);
   }

   @Override
   public String getTitle(Player player) {
      return "Editing " + this.kit.getName();
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();

      for (int i = 0; i <= 5; i++) {
         buttons.put(this.getSlot(1, i), Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7));
      }

      for (int var10 = 0; var10 <= 8; var10++) {
         buttons.put(this.getSlot(var10, 1), Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7));
      }

      buttons.put(this.getSlot(3, 0), Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7));
      buttons.put(this.getSlot(4, 0), Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7));
      buttons.put(this.getSlot(5, 0), Button.placeholder(Material.STAINED_GLASS_PANE, (byte)7));
      buttons.put(this.getSlot(0, 0), new KitInfoButton(this.kit));
      buttons.put(this.getSlot(2, 0), new SaveButton(this.kit));
      buttons.put(this.getSlot(6, 0), new LoadDefaultKitButton(this.kit.getOriginal()));
      buttons.put(this.getSlot(7, 0), new ClearInventoryButton());
      buttons.put(this.getSlot(8, 0), new CancelKitEditButton());

      for (ItemStack armorItem : this.kit.getOriginal().getArmorContents()) {
         int armorYOffset = 2;
         int armorSlot = -1;
         if (armorItem.getType().name().contains("HELMET")) {
            armorSlot = 0;
         } else if (armorItem.getType().name().contains("CHESTPLATE")) {
            armorSlot = 1;
         } else if (armorItem.getType().name().contains("LEGGINGS")) {
            armorSlot = 2;
         } else if (armorItem.getType().name().contains("BOOTS")) {
            armorSlot = 3;
         }

         buttons.put(this.getSlot(0, armorSlot + armorYOffset), new ArmorButton(armorItem));
      }

      short splashHealPotionDura = -1;
      int x = 0;
      int y = 0;

      for (ItemStack editorItem : this.kit.getOriginal().getEditorItems()) {
         if (editorItem != null) {
            Potion potion;
            if (editorItem.getType() == Material.POTION
               && editorItem.getDurability() != 0
               && (potion = Potion.fromItemStack(editorItem)).isSplash()
               && potion.getType() == PotionType.INSTANT_HEAL) {
               splashHealPotionDura = editorItem.getDurability();
            }

            if (editorItem.getType() != Material.AIR) {
               buttons.put(this.getSlot(x + 2, y + 2), new TakeItemButton(editorItem));
            }
         }

         if (++x > 6) {
            x = 0;
            if (++y >= 4) {
            }
         }
      }

      if (splashHealPotionDura > 0) {
         buttons.put(this.getSlot(8, 5), new FillHealPotionsButton(splashHealPotionDura));
      }

      return buttons;
   }
}
