package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import java.util.Arrays;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Invis extends PersistentKillstreak {
   public Invis() {
      super("Invis", 27, Material.INK_SACK, Arrays.asList("Reach this killstreak to", "receive a Invisibility potion"));
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 12000, 1));
   }
}
