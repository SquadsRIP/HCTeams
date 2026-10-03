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
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpidermanClass extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "spiderman";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.RED + ChatColor.BOLD.toString() + "Spider Man";
   }

   @Override
   public int getSlot() {
      return 14;
   }

   @Override
   public Material getDisplayItem() {
      return Material.WEB;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.RED;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("25% Fall Damage Reduction");
      toReturn.add("Permanent Jump Boost II, Speed II, Fire Resistance I");
      toReturn.add("SHIFT + Click to put the last player you hit in cobwebs!");
      return toReturn;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageEvent event) {
      if (event.getCause().equals(DamageCause.FALL)) {
         if (event.getEntity() instanceof Player) {
            Player damager = (Player)event.getEntity();
            if (this.isWearing(damager)) {
               if (ArmorClassHandler.isAllowed(damager.getLocation())) {
                  event.setDamage(event.getDamage() * 0.75);
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
            if (ArmorClassHandler.isAllowed(player.getLocation())) {
               if (player.isSneaking()) {
                  if (this.cooldown.containsKey(player.getUniqueId()) && this.cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                     long millisLeft = (this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L * 1000L;
                     String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
                     player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                  } else {
                     player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
                     Block blockAt = player.getLocation().getBlock();
                     this.setCobweb(blockAt);
                     this.setCobweb(blockAt.getRelative(BlockFace.UP));
                     this.setCobweb(blockAt.getRelative(BlockFace.EAST));
                     this.setCobweb(blockAt.getRelative(BlockFace.EAST).getRelative(BlockFace.UP));
                     this.setCobweb(blockAt.getRelative(BlockFace.NORTH));
                     this.setCobweb(blockAt.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP));
                     this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
                  }
               }
            }
         }
      }
   }

   public void setCobweb(Block block) {
      if (block.getType().equals(Material.AIR)) {
         block.setType(Material.WEB);
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> block.setType(Material.AIR), 200L);
      }
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
   }

   @Override
   public void unapply(Player player) {
      player.removePotionEffect(PotionEffectType.JUMP);
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
