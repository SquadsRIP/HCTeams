package net.frozenorb.foxtrot.gameplay.ability.type;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.menu.PortableBardMenu;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortableBard extends Ability {
   @Override
   public String getDescription() {
      return "Bard";
   }

   @Override
   public Category getCategory() {
      return Category.AIRDROPS;
   }

   public PortableBard() {
      this.hassanStack.setDurability((short)14);
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.INK_SACK;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GOLD.toString() + ChatColor.BOLD + "Portable Bard";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fReceive &c&l5 &fportable bard effects!"));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in an &b&lAirdrop&f!"));
      return toReturn;
   }

   @Override
   public Boolean isAllowedAtLocation(Location location) {
      return Foxtrot.getInstance().getMapHandler().isKitMap()
         ? !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location)
         : !Foxtrot.getInstance().getServerHandler().isWarzone(location)
            && location.getWorld().getEnvironment() == Environment.NORMAL
            && !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   @Override
   public long getCooldown() {
      return 90000L;
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR
         || event.getAction() == Action.RIGHT_CLICK_BLOCK
         || event.getAction() == Action.LEFT_CLICK_AIR
         || event.getAction() == Action.LEFT_CLICK_BLOCK) {
         Player player = event.getPlayer();
         if (this.isSimilar(event.getItem())) {
            event.setCancelled(true);
            new PortableBardMenu().openMenu(player);
         }
      }
   }
}
