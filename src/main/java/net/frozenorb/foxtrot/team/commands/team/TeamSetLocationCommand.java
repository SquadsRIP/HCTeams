package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamSetLocationCommand implements Listener {
   public static final ItemStack LOCATION_WAND = new ItemStack(Material.DIAMOND_HOE);
   private static final Map<UUID, String> activeSetters = new HashMap<>();

   @Command(names = {"team setlocation", "t setlocation", "f setlocation", "faction setlocation", "fac setlocation"}, permission = "op")
   public static void setLocation(Player sender, @Parameter(name = "faction/event name", wildcard = true) String factionName) {
      if (factionName != null && !factionName.trim().isEmpty()) {
         factionName = factionName.trim();
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(factionName);
         if (team == null) {
            sender.sendMessage(ChatColor.RED + "No faction found with name '" + factionName + "'.");
            sender.sendMessage(ChatColor.GRAY + "Create it first with /f system claim " + factionName);
         } else {
            sender.getInventory().remove(LOCATION_WAND);
            int slot = -1;

            for (int i = 0; i < 9; i++) {
               if (sender.getInventory().getItem(i) == null) {
                  slot = i;
                  break;
               }
            }

            if (slot == -1) {
               sender.sendMessage(ChatColor.RED + "Your hotbar is full! Clear a slot first.");
            } else {
               sender.getInventory().setItem(slot, LOCATION_WAND.clone());
               activeSetters.put(sender.getUniqueId(), factionName);
               sender.sendMessage(ChatColor.GREEN + "Location wand given for '" + ChatColor.WHITE + factionName + ChatColor.GREEN + "'.");
               sender.sendMessage(ChatColor.GRAY + "Click a block to set the location. A Lunar waypoint will be created there.");
            }
         }
      } else {
         sender.sendMessage(ChatColor.RED + "Usage: /f setlocation <faction/event name>");
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
   public void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      String factionName = activeSetters.get(player.getUniqueId());
      if (factionName != null) {
         ItemStack item = player.getItemInHand();
         if (item != null && item.isSimilar(LOCATION_WAND)) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
               event.setCancelled(true);
               Location clicked = event.getClickedBlock().getLocation();
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(factionName);
               if (team == null) {
                  player.sendMessage(ChatColor.RED + "Faction '" + factionName + "' no longer exists!");
                  activeSetters.remove(player.getUniqueId());
                  player.getInventory().remove(LOCATION_WAND);
               } else {
                  team.setHQ(clicked);

                  try {
                     LunarClientAPI lunar = LunarClientAPI.getInstance();
                     LCWaypoint waypoint = new LCWaypoint(ChatColor.DARK_RED + factionName + ChatColor.WHITE, clicked, Color.RED.getRGB(), true);

                     for (Player online : getOnlinePlayersList()) {
                        lunar.sendWaypoint(online, waypoint);
                     }
                  } catch (Exception var11) {
                  }

                  player.sendMessage(
                     ChatColor.GREEN
                        + "Location set for '"
                        + ChatColor.WHITE
                        + factionName
                        + ChatColor.GREEN
                        + "' at "
                        + ChatColor.WHITE
                        + clicked.getBlockX()
                        + ", "
                        + clicked.getBlockY()
                        + ", "
                        + clicked.getBlockZ()
                        + ChatColor.GREEN
                        + " in "
                        + ChatColor.WHITE
                        + clicked.getWorld().getName()
                  );
                  player.getInventory().remove(LOCATION_WAND);
                  activeSetters.remove(player.getUniqueId());
                  player.sendMessage(ChatColor.GRAY + "Lunar Client waypoint sent to all online players.");
               }
            }
         }
      }
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }

   static {
      ItemMeta meta = LOCATION_WAND.getItemMeta();
      meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Location Setter Wand");
      meta.setLore(
         Arrays.asList(
            "",
            ChatColor.GRAY + "Left/Right click a block to set",
            ChatColor.GRAY + "the location for this faction/event.",
            "",
            ChatColor.DARK_RED + "The location will be used for",
            ChatColor.DARK_RED + "Lunar Client waypoints."
         )
      );
      LOCATION_WAND.setItemMeta(meta);
   }
}
