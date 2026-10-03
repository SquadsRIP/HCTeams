package net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.Invisibility;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CasperTheGhost extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "caspertheghost";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.WHITE + ChatColor.BOLD.toString() + "Casper The Ghost";
   }

   @Override
   public int getSlot() {
      return 10;
   }

   @Override
   public Material getDisplayItem() {
      return Material.IRON_INGOT;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.WHITE;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("Ability to see nametags of invisible players.");
      toReturn.add("30% more damage to players with over 6 partner items.");
      toReturn.add("Shift Right Click to go fully invisible for 15 seconds.");
      return toReturn;
   }

   @Override
   public Category getCategory() {
      return Category.DIAMOND;
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         Player player = event.getPlayer();
         if (this.isWearing(player)) {
            if (player.isSneaking()) {
               if (this.cooldown.containsKey(player.getUniqueId()) && this.cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                  long millisLeft = (this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L * 1000L;
                  String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
                  player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
               } else {
                  event.getPlayer().addPotionEffect(Invisibility.EFFECT, true);
                  event.getPlayer().setFireTicks(0);
                  ((CraftPlayer)event.getPlayer()).getHandle().getDataWatcher().watch(9, (byte)0);
                  this.sendRestorePacket(event.getPlayer(), Foxtrot.getInstance().getServer().getOnlinePlayers(), true);
               }
            }
         }
      }
   }

   @EventHandler
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player damager = (Player)event.getDamager();
         Player target = (Player)event.getEntity();
         if (this.isWearing(damager)) {
            int number = 0;

            for (ItemStack content : target.getInventory().getContents()) {
               if (content != null
                  && !Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values().stream().noneMatch(it -> it.isSimilar(content))) {
                  number++;
               }
            }

            if (number >= 6) {
               damager.sendMessage(CC.translate(target.getName() + " &6has over 6 partner items in their inventory therefor you delt 30% more damage!"));
               target.sendMessage(
                  CC.translate(
                     damager.getName()
                        + " &cis in a "
                        + this.getDisplayName()
                        + " &cset and you have over 6 partner items so they delt 30% more damage to you!"
                  )
               );
               event.setDamage(event.getDamage() * 1.3);
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
      return itemBuilder.build();
   }

   @Override
   public void apply(Player player) {
   }

   @Override
   public void unapply(Player player) {
   }

   private void sendRestorePacket(Player player, Collection<? extends Player> players, boolean clear) {
      List<PacketContainer> packets = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         PacketContainer packet = new PacketContainer(Server.ENTITY_EQUIPMENT);
         packet.getIntegers().write(0, player.getEntityId());
         packet.getIntegers().write(1, i + 1);
         packet.getItemModifier().write(0, clear ? new ItemStack(Material.AIR) : player.getInventory().getArmorContents()[i]);
         packets.add(packet);
      }

      players.stream().filter(it -> it.getUniqueId() != player.getUniqueId()).forEach(it -> packets.forEach(packetx -> {
         try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(it, packetx);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }));
   }
}
