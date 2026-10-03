package net.frozenorb.foxtrot.gameplay.loot.treasurechest.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import cc.fyre.proton.util.ItemUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChest;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChestHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.menu.TreasureChestMenu;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TreasureChestListener implements Listener {
   private Foxtrot instance;
   private TreasureChest currentlyOpening;
   private UUID currentPlayer = null;
   private int chestsOpened = 0;
   private boolean ending = false;
   private List<TreasureChestReward> rewardsWon = new ArrayList<>();
   private List<Location> openedChests = new ArrayList<>();

   public TreasureChestListener(Foxtrot instance) {
      this.instance = instance;
   }

   public TreasureChestHandler getTreasureChestHandler() {
      return this.instance.getTreasureChestHandler();
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getAction().name().contains("RIGHT")) {
         Block block = event.getClickedBlock();
         if (block != null && block.getType().equals(Material.CHEST)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
               if (this.currentlyOpening != null && this.currentlyOpening.getChests().contains(block.getLocation())) {
                  if (this.currentPlayer == null || !this.currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                     player.sendMessage(ChatColor.RED + "You may not open this chest someone else is using it!");
                  } else if (this.openedChests.contains(block.getLocation())) {
                     player.sendMessage(ChatColor.RED + "You can't open that chest as you've already opened it!");
                  } else if (this.ending) {
                     player.sendMessage(ChatColor.RED + "You've already opened them all");
                  } else {
                     event.setCancelled(true);
                     Chest chest = (Chest)block.getState();
                     TreasureChestReward treasureChestReward = this.getTreasureChestHandler().openChest(this.currentlyOpening, chest, player);
                     chest.getWorld().playSound(chest.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                     String item = treasureChestReward.getItemStack().getItemMeta().hasDisplayName()
                        ? treasureChestReward.getItemStack().getItemMeta().getDisplayName()
                        : treasureChestReward.getItemStack().getAmount() + "x " + ItemUtils.getName(treasureChestReward.getItemStack());
                     Hologram hologram = Proton.getInstance()
                        .getHologramHandler()
                        .createHologram()
                        .addLines(ChatColor.translate("&4&lReward"), item)
                        .at(chest.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5))
                        .build();
                     hologram.send();
                     this.openedChests.add(chest.getLocation().clone());
                     this.rewardsWon.add(treasureChestReward);
                     this.chestsOpened++;
                     if (this.chestsOpened >= 4) {
                        this.end(player);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (this.currentPlayer != null && this.currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
         this.end(player);
      }
   }

   public void end(Player player) {
      this.ending = true;
      this.chestsOpened = 0;
      this.currentPlayer = null;
      this.instance
         .getServer()
         .getScheduler()
         .runTaskLater(
            this.instance,
            () -> {
               Foxtrot.getInstance()
                  .getServer()
                  .getWorld(Foxtrot.getInstance().getMapHandler().isKitMap() ? "world" : "Spawn")
                  .getEntities()
                  .stream()
                  .filter(it -> it instanceof Item && it.hasMetadata("TREASURE_REWARD"))
                  .forEach(Entity::remove);

               for (Location location : this.currentlyOpening.getChests()) {
                  location.getBlock().setType(Material.AIR);
                  location.getWorld().playSound(location, Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
               }

               List<Hologram> holograms = new ArrayList<>(Proton.getInstance().getHologramHandler().getCache().values());
               holograms.stream().filter(it -> it.getLines().contains(ChatColor.translate("&4&lReward"))).forEach(Hologram::delete);
               String color = "&c";
               String link = "Coming Soon!";
               if (this.currentlyOpening.getId().equalsIgnoreCase("Illuminated")) {
                  color = "&b";
                  link = "Coming Soon!";
               }

               if (this.currentlyOpening.getId().equalsIgnoreCase("Treasure")) {
                  color = "&c";
                  link = "Coming Soon!";
               }

               for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                  onlinePlayer.sendMessage(
                     ChatColor.translate(
                        color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------"
                     )
                  );
                  onlinePlayer.sendMessage(
                     ChatColor.translate(color + "  &ki&4&l " + ChatColor.translate(this.currentlyOpening.getDisplayName()) + " " + color + "&ki")
                  );
                  onlinePlayer.sendMessage(ChatColor.translate("  &fOpened by " + color + player.getName()));
                  onlinePlayer.sendMessage(ChatColor.translate(""));
                  onlinePlayer.sendMessage(ChatColor.translate(color + "  &lREWARDS"));

                  for (TreasureChestReward treasureChestReward : this.rewardsWon) {
                     String item = treasureChestReward.getItemStack().getItemMeta().hasDisplayName()
                        ? (
                              treasureChestReward.getItemStack().getAmount() > 1
                                 ? treasureChestReward.getItemStack()
                                       .getItemMeta()
                                       .getDisplayName()
                                       .replace(ChatColor.stripColor(treasureChestReward.getItemStack().getItemMeta().getDisplayName()), "")
                                    + treasureChestReward.getItemStack().getAmount()
                                    + "x "
                                 : ""
                           )
                           + treasureChestReward.getItemStack().getItemMeta().getDisplayName()
                        : treasureChestReward.getItemStack().getAmount() + "x " + ItemUtils.getName(treasureChestReward.getItemStack());
                     onlinePlayer.sendMessage(ChatColor.translate("   " + item));
                  }

                  onlinePlayer.sendMessage(ChatColor.translate(""));
                  onlinePlayer.sendMessage(ChatColor.translate("  &fUnlocked at " + color + link));
                  onlinePlayer.sendMessage(
                     ChatColor.translate(
                        color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------"
                     )
                  );
               }

               this.rewardsWon.clear();
               Location centralLocation = this.currentlyOpening.getCentralLocation().clone();
               Hologram hologram = Proton.getInstance()
                  .getHologramHandler()
                  .createHologram()
                  .addLines(ChatColor.translate("&f&ki&4&l TREASURE CHESTS &f&ki"), ChatColor.translate("&f&oComing Soon!"))
                  .at(centralLocation.getBlock().getLocation().add(0.5, 0.4, 0.5))
                  .build();
               hologram.send();
               centralLocation.getBlock().setType(Material.ENDER_CHEST);
               this.currentlyOpening = null;
            },
            100L
         );
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onPickup(PlayerPickupItemEvent event) {
      if (event.getItem().hasMetadata("TREASURE_REWARD")) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteractChest(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getAction() != Action.PHYSICAL) {
         Block block = event.getClickedBlock();
         if (block != null && DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
            if (block.getType() == Material.ENDER_CHEST
               && block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.DAYLIGHT_DETECTOR) {
               if (this.currentlyOpening == null || !this.currentlyOpening.getChests().contains(block.getLocation())) {
                  if (event.getAction().name().contains("RIGHT") || event.getAction().name().contains("LEFT")) {
                     if (this.currentPlayer != null && !this.currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                        player.sendMessage(ChatColor.RED + "The Treasure Chest is already being used by someone!");
                     } else {
                        new TreasureChestMenu().openMenu(player);
                        event.setCancelled(true);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onMove(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      if (event.getFrom().getBlockX() != event.getTo().getBlockX()
         || event.getFrom().getBlockY() != event.getTo().getBlockY()
         || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
         if (this.currentlyOpening != null
            && this.currentlyOpening.getCentralLocation() != null
            && this.currentPlayer != null
            && this.currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            Location location = this.currentlyOpening.getCentralLocation().clone();
            if (!(player.getLocation().distance(location.clone()) <= 3.0)) {
               location.setYaw(player.getLocation().getYaw());
               location.setPitch(player.getLocation().getPitch());
               event.setTo(location);
            }
         }
      }
   }

   public TreasureChest getCurrentlyOpening() {
      return this.currentlyOpening;
   }

   public void setCurrentlyOpening(TreasureChest currentlyOpening) {
      this.currentlyOpening = currentlyOpening;
   }

   public UUID getCurrentPlayer() {
      return this.currentPlayer;
   }

   public void setCurrentPlayer(UUID currentPlayer) {
      this.currentPlayer = currentPlayer;
   }

   public int getChestsOpened() {
      return this.chestsOpened;
   }

   public void setChestsOpened(int chestsOpened) {
      this.chestsOpened = chestsOpened;
   }

   public boolean isEnding() {
      return this.ending;
   }

   public void setEnding(boolean ending) {
      this.ending = ending;
   }

   public List<TreasureChestReward> getRewardsWon() {
      return this.rewardsWon;
   }

   public List<Location> getOpenedChests() {
      return this.openedChests;
   }
}
