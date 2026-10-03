package net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class UndeadWarriorClass extends ArmorClass {
   private Map<UUID, Long> cooldown = new HashMap<>();

   @Override
   public String getId() {
      return "undeadwarrior";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Undead Warrior";
   }

   @Override
   public int getSlot() {
      return 16;
   }

   @Override
   public Material getDisplayItem() {
      return Material.BONE;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.DARK_PURPLE;
   }

   @Override
   public List<String> getPerks() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("Piercing Shot doesn't work on this class.");
      toReturn.add("Allies within a 20-block radius of you take 25%");
      toReturn.add("less fall damage and take 0.5 fewer hearts from an archer tag.");
      return toReturn;
   }

   @EventHandler
   private void onDamage(EntityDamageEvent event) {
      if (event.getCause().equals(DamageCause.FALL)) {
         if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            boolean reduce = false;
            if (team != null) {
               for (Player onlineMember : team.getOnlineMembers()) {
                  if (onlineMember.hasMetadata("UNDEAD_WARRIOR")
                     && onlineMember.getLocation().getWorld().equals(player.getWorld())
                     && onlineMember.getLocation().distance(player.getLocation()) <= 20.0) {
                     reduce = true;
                  }
               }

               if (reduce) {
                  event.setDamage(event.getDamage() * 0.75);
               }
            }
         }
      }
   }

   @Override
   public Category getCategory() {
      return Category.DIAMOND;
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
      player.setMetadata("UNDEAD_WARRIOR", new FixedMetadataValue(Foxtrot.getInstance(), true));
   }

   @Override
   public void unapply(Player player) {
      player.removeMetadata("UNDEAD_WARRIOR", Foxtrot.getInstance());
   }
}
