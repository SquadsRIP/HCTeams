package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.param.Parameter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CaveNiteAddSpawnCommand {
   public static void execute(Player player) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      Location location = player.getLocation().clone();
      if (caveNiteHandler.getLocations().contains(location)) {
         player.sendMessage(ChatColor.RED + "There is already a Spawn at that location!");
      } else {
         player.sendMessage(
            ChatColor.translate(
               "&6Added a spawn at &f"
                  + location.getBlockX()
                  + ", "
                  + location.getBlockY()
                  + ", "
                  + location.getBlockZ()
                  + "&6! &e["
                  + caveNiteHandler.getLocations().size()
                  + "]"
            )
         );
         caveNiteHandler.getLocations().add(location);
      }
   }

   public static void listSpawn(Player player) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

      for (Location location : caveNiteHandler.getLocations()) {
         new FancyMessage(
               "&6Location #"
                  + caveNiteHandler.getLocations().indexOf(location)
                  + " at &f"
                  + location.getBlockX()
                  + ", "
                  + location.getBlockY()
                  + ", "
                  + location.getBlockZ()
            )
            .tooltip(ChatColor.GREEN + "Click to teleport")
            .command("/tppos " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ())
            .send(player);
      }
   }

   public static void listSpawn(Player player, @Parameter(name = "id") int id) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      Location indexOf = caveNiteHandler.getLocations().get(id);
      if (indexOf == null) {
         player.sendMessage(ChatColor.RED + "&7Coming Soon!");
      } else {
         caveNiteHandler.getLocations().remove(indexOf);
         player.sendMessage(ChatColor.RED + "Removed location with id " + ChatColor.WHITE + id);
      }
   }
}
