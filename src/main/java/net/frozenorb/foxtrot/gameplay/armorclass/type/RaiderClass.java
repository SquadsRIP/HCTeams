package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class RaiderClass extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "raider";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Raider";
   }

   @Override
   public int getSlot() {
      return 13;
   }

   @Override
   public Material getDisplayItem() {
      return Material.DIAMOND_SWORD;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.DARK_RED;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("Deal 5% more damage");
      toReturn.add("10% Reduced Partner Item cooldown");
      toReturn.add("SHIFT + Click to activate Midas Touch for 10 seconds");
      return toReturn;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Player damager = (Player)event.getDamager();
         if (this.isWearing(damager)) {
            if (ArmorClassHandler.isAllowed(damager.getLocation())) {
               event.setDamage(event.getDamage() * 1.1);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         Player player = event.getPlayer();
         if (this.isWearing(player)) {
            if (player.isSneaking()) {
               if (ArmorClassHandler.isAllowed(player.getLocation())) {
                  if (this.cooldown.containsKey(player.getUniqueId()) && this.cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                     long millisLeft = (this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L * 1000L;
                     String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
                     player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                  } else {
                     player.setMetadata("ANTI_TRAP", new FixedMetadataValue(Foxtrot.getInstance(), true));
                     Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        if (player.isOnline()) {
                           player.removeMetadata("ANTI_TRAP", Foxtrot.getInstance());
                        }
                     }, 300L);
                     this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
                  }
               }
            }
         }
      }
   }

   @Override
   public ItemStack createPiece(ArmorPiece armorPiece) {
      ItemBuilder itemBuilder = ItemBuilder.of(armorPiece.getDefaultMaterial())
         .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
         .enchant(Enchantment.DURABILITY, 3)
         .name(this.getDisplayName() + " " + this.getPieceName(armorPiece))
         .addToLore("", this.getChatColor() + "Armor Class: &f" + ChatColor.stripColor(this.getDisplayName()), this.getChatColor() + "Perks:");

      for (String perk : this.getPerks()) {
         itemBuilder.addToLore(this.getChatColor() + "❙ &f" + perk);
      }

      itemBuilder.addToLore("");
      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_CHESTPLATE)) {
         itemBuilder.addToLore("&cFireResistance I");
      }

      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_BOOTS)) {
         itemBuilder.enchant(Enchantment.PROTECTION_FALL, 4);
         itemBuilder.addToLore("&cSpeed II");
      }

      return itemBuilder.build();
   }
}
