package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.event.CrowbarSpawnerBreakEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrowbarListener implements Listener {
   @EventHandler(ignoreCancelled = true)
   public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
      if (playerInteractEvent.getItem() != null
         && InventoryUtils.isSimilar(playerInteractEvent.getItem(), InventoryUtils.CROWBAR_NAME)
         && (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK)) {
         Team team;
         if (!Foxtrot.getInstance()
               .getServerHandler()
               .isUnclaimedOrRaidable(playerInteractEvent.getPlayer(), playerInteractEvent.getClickedBlock().getLocation())
            && !Foxtrot.getInstance().getServerHandler().isAdminOverride(playerInteractEvent.getPlayer())
            && (team = LandBoard.getInstance().getTeam(playerInteractEvent.getClickedBlock().getLocation())) != null
            && !team.isMember(playerInteractEvent.getPlayer().getUniqueId())) {
            playerInteractEvent.getPlayer()
               .sendMessage(
                  ChatColor.YELLOW
                     + "You cannot crowbar in "
                     + ChatColor.YELLOW
                     + team.getName(playerInteractEvent.getPlayer())
                     + ChatColor.YELLOW
                     + "'s territory!"
               );
         } else if (DTRBitmask.SAFE_ZONE.appliesAt(playerInteractEvent.getClickedBlock().getLocation())) {
            playerInteractEvent.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot crowbar spawn!");
         } else {
            if (playerInteractEvent.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
               int n = InventoryUtils.getCrowbarUsesPortal(playerInteractEvent.getItem());
               if (n == 0) {
                  playerInteractEvent.getPlayer().sendMessage(ChatColor.YELLOW + "This crowbar has no more uses on end portals!");
                  return;
               }

               playerInteractEvent.getClickedBlock()
                  .getWorld()
                  .playEffect(playerInteractEvent.getClickedBlock().getLocation(), Effect.STEP_SOUND, playerInteractEvent.getClickedBlock().getTypeId());
               playerInteractEvent.getClickedBlock().setType(Material.AIR);
               playerInteractEvent.getClickedBlock().getState().update();
               playerInteractEvent.getClickedBlock()
                  .getWorld()
                  .dropItemNaturally(playerInteractEvent.getClickedBlock().getLocation(), new ItemStack(Material.ENDER_PORTAL_FRAME));
               playerInteractEvent.getClickedBlock().getWorld().playSound(playerInteractEvent.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);

               for (int n2 = -3; n2 < 3; n2++) {
                  for (int n3 = -3; n3 < 3; n3++) {
                     Block block = playerInteractEvent.getClickedBlock().getLocation().add(n2, 0.0, n3).getBlock();
                     if (block.getType() == Material.ENDER_PORTAL) {
                        block.setType(Material.AIR);
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.ENDER_PORTAL.getId());
                     }
                  }
               }

               int var20 = InventoryUtils.getCrowbarUsesSpawner(playerInteractEvent.getItem());
               if (--n == 0 && var20 == 0) {
                  playerInteractEvent.getPlayer().setItemInHand(null);
                  playerInteractEvent.getClickedBlock()
                     .getLocation()
                     .getWorld()
                     .playSound(playerInteractEvent.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                  return;
               }

               ItemMeta itemMeta = playerInteractEvent.getItem().getItemMeta();
               itemMeta.setLore(InventoryUtils.getCrowbarLore(n, var20));
               playerInteractEvent.getItem().setItemMeta(itemMeta);
               double d = Material.DIAMOND_HOE.getMaxDurability();
               double d2 = n + var20;
               double d3 = 7.0;
               double d4 = d / d3 * d2;
               playerInteractEvent.getItem().setDurability((short)(d - d4));
               playerInteractEvent.getPlayer().setItemInHand(playerInteractEvent.getItem());
            } else if (playerInteractEvent.getClickedBlock().getType() == Material.MOB_SPAWNER) {
               CreatureSpawner creatureSpawner = (CreatureSpawner)playerInteractEvent.getClickedBlock().getState();
               int n = InventoryUtils.getCrowbarUsesSpawner(playerInteractEvent.getItem());
               if (n == 0) {
                  playerInteractEvent.getPlayer().sendMessage(ChatColor.YELLOW + "This crowbar has no more uses on mob spawners!");
                  return;
               }

               if (playerInteractEvent.getClickedBlock().getWorld().getEnvironment() == Environment.NETHER) {
                  playerInteractEvent.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break spawners in the nether!");
                  playerInteractEvent.setCancelled(true);
                  return;
               }

               if (playerInteractEvent.getClickedBlock().getWorld().getEnvironment() == Environment.THE_END) {
                  playerInteractEvent.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break spawners in the end!");
                  playerInteractEvent.setCancelled(true);
                  return;
               }

               CrowbarSpawnerBreakEvent crowbarSpawnerBreakEvent = new CrowbarSpawnerBreakEvent(
                  playerInteractEvent.getPlayer(), playerInteractEvent.getClickedBlock()
               );
               Foxtrot.getInstance().getServer().getPluginManager().callEvent(crowbarSpawnerBreakEvent);
               if (crowbarSpawnerBreakEvent.isCancelled()) {
                  return;
               }

               playerInteractEvent.getClickedBlock()
                  .getLocation()
                  .getWorld()
                  .playEffect(playerInteractEvent.getClickedBlock().getLocation(), Effect.STEP_SOUND, playerInteractEvent.getClickedBlock().getTypeId());
               playerInteractEvent.getClickedBlock().setType(Material.AIR);
               playerInteractEvent.getClickedBlock().getState().update();
               ItemStack itemStack = new ItemStack(Material.MOB_SPAWNER);
               ItemMeta itemMeta = itemStack.getItemMeta();
               itemMeta.setDisplayName(
                  ChatColor.RESET + StringUtils.capitaliseAllWords(creatureSpawner.getSpawnedType().toString().toLowerCase().replaceAll("_", " ")) + " Spawner"
               );
               itemStack.setItemMeta(itemMeta);
               playerInteractEvent.getClickedBlock().getLocation().getWorld().dropItemNaturally(playerInteractEvent.getClickedBlock().getLocation(), itemStack);
               playerInteractEvent.getClickedBlock()
                  .getLocation()
                  .getWorld()
                  .playSound(playerInteractEvent.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
               int n4 = InventoryUtils.getCrowbarUsesPortal(playerInteractEvent.getItem());
               if (--n == 0 && n4 == 0) {
                  playerInteractEvent.getPlayer().setItemInHand(null);
                  playerInteractEvent.getClickedBlock()
                     .getLocation()
                     .getWorld()
                     .playSound(playerInteractEvent.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                  return;
               }

               ItemMeta itemMeta2 = playerInteractEvent.getItem().getItemMeta();
               itemMeta2.setLore(InventoryUtils.getCrowbarLore(n4, n));
               playerInteractEvent.getItem().setItemMeta(itemMeta2);
               double d = Material.DIAMOND_HOE.getMaxDurability();
               double d5 = n4 + n;
               double d6 = 7.0;
               double d7 = d / d6 * d5;
               playerInteractEvent.getItem().setDurability((short)(d - d7));
               playerInteractEvent.getPlayer().setItemInHand(playerInteractEvent.getItem());
            } else {
               playerInteractEvent.getPlayer().sendMessage(ChatColor.YELLOW + "Crowbars can only break end portals and mob spawners!");
               playerInteractEvent.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
      if (blockBreakEvent.getPlayer().getWorld().getEnvironment() == Environment.NETHER && blockBreakEvent.getBlock().getType() == Material.MOB_SPAWNER) {
         blockBreakEvent.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break spawners in the nether!");
         blockBreakEvent.setCancelled(true);
      } else if (blockBreakEvent.getBlock().getType() == Material.MOB_SPAWNER) {
         blockBreakEvent.getPlayer().sendMessage(ChatColor.YELLOW + "This is too strong for you to break! Try using a crowbar instead.");
         blockBreakEvent.setCancelled(true);
      }
   }
}
