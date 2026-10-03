package net.frozenorb.foxtrot.gameplay.loot.partnercrate;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.PartnerKeyNPC;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.PartnerCrateMenu;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavepvp.entity.EntityHandler;
import org.cavepvp.entity.EntityVisibility;

public class PartnerCrateCommand {
   @Command(names = "partnercrate", permission = "")
   public static void execute(Player player) {
      if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         player.sendMessage(ChatColor.YELLOW + "You must be at " + ChatColor.GREEN + "Spawn" + ChatColor.YELLOW + " to use this!");
      } else {
         new PartnerCrateMenu().openMenu(player);
      }
   }

   @Command(names = "partnercrate npc", permission = "op")
   public static void npc(Player player) {
      if (EntityHandler.INSTANCE.getEntityByName("PartnerKey") != null) {
         player.sendMessage(ChatColor.YELLOW + "hey");
      } else {
         PartnerKeyNPC npc = new PartnerKeyNPC(player.getLocation().clone());
         npc.setTabVisibility(EntityVisibility.HIDDEN);
         npc.setTagVisibility(EntityVisibility.HIDDEN);
         EntityHandler.INSTANCE.register(npc);
         player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Hey!!!!!");
      }
   }

   public static boolean isSimilar(PartnerType partnerType, ItemStack itemStack) {
      if (itemStack != null
         && itemStack.getType() == Material.TRIPWIRE_HOOK
         && itemStack.getItemMeta() != null
         && itemStack.getItemMeta().getDisplayName() != null
         && itemStack.getItemMeta().getLore() != null) {
         ItemMeta itemMeta = itemStack.getItemMeta();
         return !itemMeta.getDisplayName().equalsIgnoreCase(partnerType.getChatColor() + ChatColor.BOLD.toString() + partnerType.getCrateName() + " Key")
            ? false
            : itemMeta.getLore().size() == 1
               && ChatColor.stripColor(itemMeta.getLore().get(0))
                  .equalsIgnoreCase("Right click a " + partnerType.getCrateName() + " Crate to receive rewards!");
      } else {
         return false;
      }
   }
}
