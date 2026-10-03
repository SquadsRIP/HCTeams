package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

import cc.fyre.modsuite.mod.ModHandler;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.KillTheKingCommand;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TacticalNuke extends Ability {
   public static ItemStack itemStack;

   @Override
   public Category getCategory() {
      return Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "Tactical Nuke has been summoned!";
   }

   public TacticalNuke() {
      itemStack = this.hassanStack;
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.TNT;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Tactical Nuke";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fRight Click to activate the tactical nuke!"));
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
      return 0L;
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onInteract(PlayerInteractEvent event) {
      final Player player = event.getPlayer();
      if (this.isSimilar(player.getItemInHand()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
         if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Wrong server buddy");
         } else {
            AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
            if (!abilityUseEvent.isCancelled()) {
               event.setCancelled(true);
               if (player.getItemInHand().getAmount() == 1) {
                  player.setItemInHand(null);
               } else {
                  player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
               }

               player.updateInventory();

               for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                  onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
                  onlinePlayer.sendMessage(ChatColor.translate("&4&lTactical Nuke incoming!"));
               }

               final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
               (new BukkitRunnable() {
                     int seconds = 6;

                     @Override
                     public void run() {
                        this.seconds--;
                        if (this.seconds != 0) {
                           for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                              onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                              onlinePlayer.sendMessage(ChatColor.translate("&4&l" + this.seconds + "..."));
                           }
                        } else {
                           this.cancel();
                           int amount = 0;

                           for (Entity entity : player.getNearbyEntities(30.0, 30.0, 30.0)) {
                              if (entity instanceof Player) {
                                 Player target = (Player)entity;
                                 if ((team == null || !team.isMember(target.getUniqueId()))
                                    && (
                                       KillTheKingCommand.king == null || !KillTheKingCommand.king.toString().equalsIgnoreCase(target.getUniqueId().toString())
                                    )
                                    && !DTRBitmask.SAFE_ZONE.appliesAt(target.getLocation())
                                    && !ModHandler.INSTANCE.isInModMode(target.getUniqueId())) {
                                    target.sendMessage(ChatColor.translate("&4&lYou have been killed by " + player.getName() + "'s nuke!"));
                                    target.damage(1.0, player);
                                    target.setHealth(0.0);
                                    amount++;
                                 }
                              }
                           }

                           for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                              onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
                           }

                           Foxtrot.getInstance()
                              .getServer()
                              .broadcastMessage(ChatColor.translate("&c&l" + player.getName() + " has killed " + amount + " players with the Tactical Nuke!"));
                        }
                     }
                  })
                  .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
            }
         }
      }
   }
}
