package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonBow extends Ability {
   private static final int USES = 50;
   private static final int BASE = 334;

   public PoisonBow() {
      this.hassanStack = ItemBuilder.copyOf(this.hassanStack).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.ARROW_DAMAGE, 3).build();
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.BOW;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Poison Bow";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fShoot an enemy and they will be"));
      toReturn.add(ChatColor.translate("&6❙ &fpoisoned for &e&l5 seconds&f!"));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &d&l??? &fCrate!"));
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
      return 30000L;
   }

   @EventHandler
   public void onLaunch(ProjectileLaunchEvent event) {
      if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
         Player player = (Player)event.getEntity().getShooter();
         ItemStack itemStack = player.getItemInHand();
         if (this.isSimilar(itemStack)) {
            short durability = itemStack.getDurability();
            if (durability <= 334) {
               itemStack.setDurability((short)334);
            } else {
               itemStack.setDurability((short)(durability + 1));
            }

            event.getEntity().setMetadata("POISON_BOW", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
            this.applyCooldown(player);
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.hasItem() && this.isSimilar(event.getPlayer().getItemInHand())) {
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(CC.translate("&cYou may not use " + this.getDisplayName() + " &cwhile your &a&lPvP Timer &cis active!"));
            event.getPlayer().updateInventory();
            event.setCancelled(true);
         } else if (this.hasCooldown(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
         } else if (!this.isAllowedAtLocation(event.getPlayer().getLocation())) {
            Team ownerTeam = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
            String teamName;
            if (ownerTeam != null) {
               teamName = ownerTeam.getName(event.getPlayer());
            } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(event.getPlayer().getLocation())) {
               teamName = ChatColor.GRAY + "The Wilderness";
            } else {
               teamName = ChatColor.DARK_RED + "WarZone";
            }

            event.getPlayer()
               .sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
            event.getPlayer().updateInventory();
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && event.getDamager().hasMetadata("POISON_BOW")) {
         Player shooter = Foxtrot.getInstance().getServer().getPlayer(UUID.fromString(event.getDamager().getMetadata("POISON_BOW").get(0).asString()));
         Player target = (Player)event.getEntity();
         if (shooter != null) {
            if (target.getLocation().getWorld().getEnvironment() == Environment.THE_END
               || target.getLocation().getWorld().getEnvironment() == Environment.NETHER) {
               shooter.sendMessage(ChatColor.RED + "You may not use this item in The End/Nether!");
               shooter.updateInventory();
            } else if (!this.isAllowedAtLocation(target.getLocation())) {
               Team ownerTeam = LandBoard.getInstance().getTeam(target.getLocation());
               String location;
               if (ownerTeam != null) {
                  location = ownerTeam.getName(shooter);
               } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(target.getLocation())) {
                  location = ChatColor.GRAY + "The Wilderness";
               } else {
                  location = ChatColor.DARK_RED + "WarZone";
               }

               shooter.updateInventory();
               shooter.sendMessage(ChatColor.RED + "You may use this on " + target.getDisplayName() + " who is in " + location + ChatColor.RED + ".");
            } else if (PvPClassHandler.getPvPClass(target) != null) {
               shooter.sendMessage(
                  ChatColor.RED
                     + "You may not use a "
                     + this.getDisplayName()
                     + ChatColor.RED
                     + " on a "
                     + ChatColor.WHITE
                     + Objects.requireNonNull(PvPClassHandler.getPvPClass(target)).getName()
                     + ChatColor.RED
                     + "."
               );
            } else {
               this.fullDescription = "You have given " + target.getName() + " Poison for 5 seconds!";
               target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
            }
         }
      }
   }

   @Override
   public Category getCategory() {
      return Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "";
   }
}
