package net.frozenorb.foxtrot.gameplay.pvpclasses.archer;

import cc.fyre.neutron.util.ColorUtil;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum ArcherColor {
   DARK_GREEN(
      "Medusa",
      ChatColor.DARK_GREEN,
      player -> {
         player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
         return false;
      },
      ChatColor.DARK_GREEN + "Medusa " + ChatColor.GOLD + "has applied " + ChatColor.DARK_GREEN + "Poison" + ChatColor.GOLD + " for 5 seconds",
      Arrays.asList(ChatColor.GRAY + "Each shot has a chance", "of giving the player Poison II for 5 seconds.")
   ),
   WHITE(
      "Phantom",
      ChatColor.WHITE,
      player -> {
         player.setSprinting(false);
         player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
         return false;
      },
      ChatColor.WHITE + "Phantom " + ChatColor.GOLD + "has applied " + ChatColor.WHITE + "Blindness" + ChatColor.GOLD + " for 5 seconds",
      Arrays.asList(ChatColor.GRAY + "Each shot has chance of disabling", "their sprint and giving Blindness II for 5 seconds.")
   ),
   BLACK(
      "Venom",
      ChatColor.DARK_PURPLE,
      player -> {
         player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 1));
         return false;
      },
      ChatColor.DARK_PURPLE + "Venom " + ChatColor.GOLD + "has applied " + ChatColor.DARK_PURPLE + "Wither II" + ChatColor.GOLD + " for 5 seconds",
      Arrays.asList(ChatColor.GRAY + "Each shot has a chance", "of giving the player Wither II for 5 seconds.")
   ),
   RED(
      "Carnage",
      ChatColor.YELLOW,
      player -> {
         player.getActivePotionEffects().stream().filter(it -> it.getType().equals(PotionEffectType.FIRE_RESISTANCE)).findFirst().ifPresent(it -> {
            player.removePotionEffect(it.getType());
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
               if (player.isOnline()) {
                  player.addPotionEffect(it);
               }
            }, 105L);
         });
         player.setFireTicks(100);
         return false;
      },
      ChatColor.YELLOW + "Carnage " + ChatColor.GOLD + "has applied " + ChatColor.YELLOW + "Fire" + ChatColor.GOLD + " for 5 seconds",
      Arrays.asList(ChatColor.GRAY + "Each shot has a chance of putting them", "on fire and disabling fire resistance for 5 seconds.")
   );

   private final String name;
   private final ChatColor chatColor;
   private final Predicate<Player> predicate;
   private final String message;
   private final List<String> lore;

   public Color getColor() {
      return ColorUtil.COLOR_MAP.get(this.chatColor).getColor();
   }

   public boolean isWearingArmor(Player player) {
      if (player.getInventory().getHelmet() != null
         && player.getInventory().getChestplate() != null
         && player.getInventory().getLeggings() != null
         && player.getInventory().getBoots() != null) {
         return Arrays.stream(player.getInventory().getArmorContents()).anyMatch(it -> !(it.getItemMeta() instanceof LeatherArmorMeta))
            ? false
            : ((LeatherArmorMeta)player.getInventory().getHelmet().getItemMeta()).getColor().equals(this.getColor())
               && ((LeatherArmorMeta)player.getInventory().getChestplate().getItemMeta()).getColor().equals(this.getColor())
               && ((LeatherArmorMeta)player.getInventory().getLeggings().getItemMeta()).getColor().equals(this.getColor())
               && ((LeatherArmorMeta)player.getInventory().getBoots().getItemMeta()).getColor().equals(this.getColor());
      } else {
         return false;
      }
   }

   public static Optional<ArcherColor> findByPlayer(Player player) {
      return Arrays.stream(values()).filter(it -> it.isWearingArmor(player)).findFirst();
   }

   public static ArcherColor findByName(String name) {
      return Arrays.stream(values()).filter(it -> it.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   @ConstructorProperties({"name", "chatColor", "predicate", "message", "lore"})
   ArcherColor(String name, ChatColor chatColor, Predicate<Player> predicate, String message, List<String> lore) {
      this.name = name;
      this.chatColor = chatColor;
      this.predicate = predicate;
      this.message = message;
      this.lore = lore;
   }

   public String getName() {
      return this.name;
   }

   public ChatColor getChatColor() {
      return this.chatColor;
   }

   public Predicate<Player> getPredicate() {
      return this.predicate;
   }

   public String getMessage() {
      return this.message;
   }

   public List<String> getLore() {
      return this.lore;
   }
}
