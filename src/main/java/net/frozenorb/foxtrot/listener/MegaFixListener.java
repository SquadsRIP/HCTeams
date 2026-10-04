package net.frozenorb.foxtrot.listener;

import cc.fyre.neutron.util.PlayerUtil;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class MegaFixListener implements Listener {
   @EventHandler(priority = EventPriority.LOWEST)
   public void onJoin(PlayerJoinEvent event) {
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoinOverrideBalance(PlayerJoinEvent event) {
      final Player player = event.getPlayer();
      if (!player.hasPlayedBefore()) {
         Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 500.0);
         (new BukkitRunnable() {
            @Override
            public void run() {
               if (player.isOnline()) {
                  World spawnWorld = Bukkit.getWorld("Spawn");
                  if (spawnWorld != null) {
                     player.teleport(spawnWorld.getSpawnLocation().add(0.0, 1.0, 0.0));
                  } else {
                     World overworld = Bukkit.getWorld("world");
                     if (overworld != null) {
                        player.teleport(overworld.getSpawnLocation().add(0.0, 1.0, 0.0));
                     }
                  }
               }
            }
         }).runTaskLater(Foxtrot.getInstance(), 5L);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onRespawn(PlayerRespawnEvent event) {
      if (!Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getPlayer())) {
         event.setRespawnLocation(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
      } else {
         final Player player = event.getPlayer();
         (new BukkitRunnable() {
            @Override
            public void run() {
               if (player.isOnline()) {
                  MegaFixListener.this.giveDeathbanKit(player);
               }
            }
         }).runTaskLater(Foxtrot.getInstance(), 1L);
      }
   }

   private void giveDeathbanKit(Player player) {
      if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
         if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
            player.getInventory().clear();
            player.getInventory().setHelmet(this.createEnchantedItem(Material.DIAMOND_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
            player.getInventory().setChestplate(this.createEnchantedItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
            player.getInventory().setLeggings(this.createEnchantedItem(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
            player.getInventory().setBoots(this.createEnchantedItem(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            sword.setItemMeta(swordMeta);
            player.getInventory().setItem(0, sword);
            player.getInventory().setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
            ItemStack pot = new ItemStack(Material.POTION, 1);
            pot.setDurability((short)16421);

            for (int i = 2; i <= 6; i++) {
               ItemStack potStack = pot.clone();
               potStack.setAmount(1);
               player.getInventory().setItem(i, potStack);
            }

            player.getInventory().setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + "You have been given the deathban arena kit!");
         }
      }
   }

   private ItemStack createEnchantedItem(Material material, Enchantment ench, int level) {
      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      meta.addEnchant(ench, level, true);
      item.setItemMeta(meta);
      return item;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onEnterClaim(TeamEnterClaimEvent event) {
      Player player = event.getPlayer();
      Team teamTo = event.getToTeam();
      if (teamTo != null && teamTo.getOwner() == null) {
         if (teamTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            if (!player.getWorld().getName().equalsIgnoreCase("Spawn")) {
               Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                  if (player.isOnline()) {
                     try {
                        PlayerUtil.sendTitle(player, "", "");
                     } catch (Exception var2x) {
                     }
                  }
               }, 1L);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCommand(PlayerCommandPreprocessEvent event) {
      String msg = event.getMessage().toLowerCase();
      if (msg.startsWith("/grounds") || msg.startsWith("/ground")) {
         event.setCancelled(true);
         event.getPlayer().sendMessage("Unknown command. Type \"/help\" for help.");
      }
   }

   public static boolean isNetherWarzone(Location loc) {
      if (loc.getWorld().getEnvironment() != Environment.NETHER) {
         return false;
      }

      int radius = Foxtrot.getInstance().getConfig().getInt("netherSpawnProtectionRadius", 500);
      Location spawn = loc.getWorld().getSpawnLocation();
      return Math.abs(loc.getBlockX() - spawn.getBlockX()) <= radius && Math.abs(loc.getBlockZ() - spawn.getBlockZ()) <= radius;
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onNetherBlockBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      if (block.getWorld().getEnvironment() == Environment.NETHER) {
         if (!event.getPlayer().hasMetadata("ADMIN_BUILD") && !event.getPlayer().isOp()) {
            if (isNetherWarzone(block.getLocation())) {
               if (block.getType() == Material.GLOWSTONE && Foxtrot.getInstance().getGlowHandler() != null) {
                  Team team = LandBoard.getInstance().getTeam(block.getLocation());
                  if (team != null && team.getName().equals(GlowHandler.getGlowTeamName())) {
                     return;
                  }
               }

               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks in the nether warzone!");
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onNetherBlockPlace(BlockPlaceEvent event) {
      Block block = event.getBlock();
      if (block.getWorld().getEnvironment() == Environment.NETHER) {
         if (!event.getPlayer().hasMetadata("ADMIN_BUILD") && !event.getPlayer().isOp()) {
            if (isNetherWarzone(block.getLocation())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks in the nether warzone!");
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Player player = event.getPlayer();
         ItemStack item = player.getItemInHand();
         if (item != null && item.getType() == Material.EYE_OF_ENDER) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
               return;
            }

            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
   public void onPlayerTeleport(PlayerTeleportEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if (from != null && to != null) {
         World fromWorld = from.getWorld();
         World toWorld = to.getWorld();
         if (fromWorld != null && toWorld != null) {
            if (fromWorld.getEnvironment() == Environment.THE_END && toWorld.getName().equalsIgnoreCase("Spawn") && from.getY() < -50.0) {
               event.setTo(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoinClearFreerank(PlayerJoinEvent event) {
      final Player player = event.getPlayer();
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               for (int i = 0; i < 6; i++) {
                  player.sendMessage("");
               }
            }
         }
      }).runTaskLater(Foxtrot.getInstance(), 20L);
   }
}
