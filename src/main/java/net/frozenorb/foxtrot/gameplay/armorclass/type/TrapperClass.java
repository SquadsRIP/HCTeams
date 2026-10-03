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
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.ability.type.NinjaStar;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrapperClass extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "trapper";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Trapper";
   }

   @Override
   public int getSlot() {
      return 15;
   }

   @Override
   public Material getDisplayItem() {
      return Material.COBBLESTONE;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.DARK_PURPLE;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("Take 10% less damage");
      toReturn.add("Med Kits and Portable Resistance last 25% longer");
      toReturn.add("SHIFT + Click to put the last player you hit on Ability Item cooldown");
      return toReturn;
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.isWearing(player)) {
            if (ArmorClassHandler.isAllowed(player.getLocation())) {
               if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
                  event.setDamage(event.getDamage() * 0.9);
               }
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
                     long difference = TimeUnit.SECONDS.toMillis(30L);
                     if (NinjaStar.cache.containsKey(player.getUniqueId())
                        && System.currentTimeMillis() - NinjaStar.cache.get(player.getUniqueId()).getTime() <= difference) {
                        NinjaStar.LastDamageEntry lastDamageEntry = NinjaStar.cache.get(player.getUniqueId());
                        Player target = Foxtrot.getInstance().getServer().getPlayer(lastDamageEntry.getUuid());
                        AbilityHandler abilityHandler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();
                        abilityHandler.getGlobalCooldowns().put(target.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L));
                        this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
                     } else {
                        player.sendMessage(ChatColor.RED + "No player has hit you within the last 30 seconds.");
                     }
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
