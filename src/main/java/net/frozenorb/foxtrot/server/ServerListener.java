package net.frozenorb.foxtrot.server;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public final class ServerListener implements Listener {
   private static final List<Material> MOB_DROPS = Arrays.asList(
      Material.SPIDER_EYE, Material.BONE, Material.ARROW, Material.ROTTEN_FLESH, Material.BOW, Material.STRING
   );
}
