package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import java.util.Arrays;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PermSpeed2 extends PersistentKillstreak {
   public PermSpeed2() {
      super("Permanent Speed 2", 30, Material.SUGAR, Arrays.asList("Reach this killstreak to", "receive permanent Speed II!"));
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
   }
}
