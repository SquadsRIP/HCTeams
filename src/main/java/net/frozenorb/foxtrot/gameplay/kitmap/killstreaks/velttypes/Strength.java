package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import java.util.Arrays;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Strength extends PersistentKillstreak {
   public Strength() {
      super("Strength", 18, Material.BLAZE_POWDER, Arrays.asList("Reach this killstreak to", "receive Strength II for 45 seconds!"));
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 900, 1));
   }
}
