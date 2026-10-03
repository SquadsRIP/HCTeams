package net.frozenorb.foxtrot.gameplay.clickitem.type;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class RandomArmorClass extends ClickItem {
   @Override
   public String getId() {
      return "random-armor-class";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Random Armor Class";
   }

   @Override
   public List<String> getLore() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Right Click to receive a random armor class.");
      return toReturn;
   }

   @Override
   public Material getMaterial() {
      return Material.CHEST;
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      boolean activate = this.redeem(event.getPlayer(), event);
      if (activate) {
         ArmorClassHandler armorClassHandler = Foxtrot.getInstance().getArmorClassHandler();
         ArrayList<ArmorClass> armorClasses = new ArrayList<>(armorClassHandler.getArmorClasses());
         ArmorClass randomClass = armorClasses.get(ThreadLocalRandom.current().nextInt(armorClasses.size()));
         player.getInventory().addItem(randomClass.getRedeemItem().clone());
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         player.sendMessage(ChatColor.GREEN + "You have received a " + randomClass.getDisplayName() + " Armor Class " + ChatColor.GREEN + "!");
      }
   }
}
