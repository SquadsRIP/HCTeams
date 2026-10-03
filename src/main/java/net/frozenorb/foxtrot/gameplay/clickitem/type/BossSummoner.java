package net.frozenorb.foxtrot.gameplay.clickitem.type;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.BossHandler;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BossSummoner extends ClickItem {
   @Override
   public String getId() {
      return "boss-summoner";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.RED + ChatColor.BOLD.toString() + "Boss Summoner";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Right Click to spawn a random boss");
      return toReturn;
   }

   @Override
   public Material getMaterial() {
      return Material.REDSTONE_TORCH_ON;
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getAction().name().contains("RIGHT")) {
         ItemStack itemStack = player.getItemInHand();
         if (this.isSimilar(itemStack)) {
            event.setCancelled(true);
            BossHandler bossHandler = Foxtrot.getInstance().getBossHandler();
            if (bossHandler.getActiveBoss() != null && !bossHandler.getActiveBoss().getEntity().isDead()) {
               player.sendMessage(ChatColor.RED + "There is already an ongoing boss!");
            } else {
               boolean activate = this.redeem(event.getPlayer(), event);
               if (activate) {
                  bossHandler.activateRandom();
                  Bukkit.broadcastMessage("");
                  Bukkit.broadcastMessage(player.getName() + ChatColor.translate(" &ahas summoned a boss!"));
                  Bukkit.broadcastMessage("");
               }
            }
         }
      }
   }
}
