package net.frozenorb.foxtrot.gameplay.ability.type;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.DirectionUtil;
import net.frozenorb.foxtrot.util.SafetyUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LazyBrewer extends Ability {
   final File file = new File(System.getProperty("user.dir") + "/plugins/WorldEdit/schematics/lazybrewer.schematic");

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.BREWING_STAND_ITEM;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Lazy Brewer";
   }

   @Override
   public List<String> getLore() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translateAlternateColorCodes('&', "&4❙ &fSpawn in 3 double chests of chests with pots."));
      toReturn.add("");
      toReturn.add(ChatColor.translateAlternateColorCodes('&', "&fCan be found in the &4&lOctober Mystery Box&f!"));
      return toReturn;
   }

   @Override
   public Boolean isAllowedAtLocation(Location location) {
      return !DTRBitmask.KOTH.appliesAt(location)
         && !DTRBitmask.CONQUEST.appliesAt(location)
         && !DTRBitmask.CITADEL.appliesAt(location)
         && !DTRBitmask.DTC.appliesAt(location)
         && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   @Override
   public long getCooldown() {
      return 0L;
   }

   @Override
   public Category getCategory() {
      return Category.AIRDROPS;
   }

   @Override
   public String getDescription() {
      return "Spawned a bunch of potions!";
   }

   @EventHandler
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (this.isSimilar(player.getItemInHand()) && event.getAction() == Action.RIGHT_CLICK_AIR) {
         if (this.hasCooldown(player)) {
            event.setCancelled(true);
         } else if (!this.file.exists()) {
            event.setCancelled(true);
            player.sendMessage(
               ChatColor.RED
                  + "The Lazy Brewer schematic is not installed on this server. Please contact an admin to upload /plugins/WorldEdit/schematics/lazybrewer.schematic"
            );
         } else {
            List<Location> surroundingLocations = SafetyUtils.getSurroundingBlocks(player.getLocation());
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());
            boolean notSafe = surroundingLocations.stream()
               .anyMatch(loc -> !SafetyUtils.isPassable(loc.getBlock()) || team == null || LandBoard.getInstance().getTeam(player.getLocation()) != team);
            if (notSafe) {
               player.sendMessage(ChatColor.YELLOW + "You may not do that here.");
            } else {
               if (player.getItemInHand().getAmount() == 1) {
                  player.setItemInHand(null);
               } else {
                  player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
               }

               player.updateInventory();
               this.pasteSchematic(player.getLocation(), DirectionUtil.getDirection(player), this.file);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      if (this.isSimilar(event.getItemInHand())) {
         if (this.hasCooldown(player)) {
            event.setCancelled(true);
         } else {
            event.setCancelled(true);
            if (!this.file.exists()) {
               player.sendMessage(
                  ChatColor.RED
                     + "The Lazy Brewer schematic is not installed on this server. Please contact an admin to upload /plugins/WorldEdit/schematics/lazybrewer.schematic"
               );
            } else {
               List<Location> surroundingLocations = SafetyUtils.getSurroundingBlocks(player.getLocation());
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());
               boolean allow = surroundingLocations.stream()
                  .anyMatch(loc -> !SafetyUtils.isPassable(loc.getBlock()) || team == null || LandBoard.getInstance().getTeam(player.getLocation()) != team);
               if (allow) {
                  player.sendMessage(ChatColor.YELLOW + "You may not do that here.");
               } else {
                  if (player.getItemInHand().getAmount() == 1) {
                     player.setItemInHand(null);
                  } else {
                     player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                  }

                  player.updateInventory();
                  this.pasteSchematic(player.getLocation(), DirectionUtil.getDirection(player), this.file);
               }
            }
         }
      }
   }

   private void pasteSchematic(Location location, DirectionUtil directionUtils, File file) {
      BukkitWorld world = new BukkitWorld(location.getWorld());

      try {
         CuboidClipboard cuboidSelection = MCEditSchematicFormat.getFormat(file).load(file);
         cuboidSelection.rotate2D(directionUtils.getDegrees());
         cuboidSelection.paste(
            Foxtrot.getInstance().getWorldEdit().getWorldEdit().getEditSessionFactory().getEditSession(world, 100), BukkitUtil.toVector(location), true
         );
      } catch (MaxChangedBlocksException | DataException | IOException e) {
         e.printStackTrace();
      }
   }
}
