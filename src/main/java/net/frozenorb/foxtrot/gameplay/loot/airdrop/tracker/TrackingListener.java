package net.frozenorb.foxtrot.gameplay.loot.airdrop.tracker;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDropHandler;
import org.bukkit.event.Listener;

public class TrackingListener implements Listener {
   private Foxtrot instance;
   private AirDropHandler airDropHandler;

   @ConstructorProperties({"instance", "airDropHandler"})
   public TrackingListener(Foxtrot instance, AirDropHandler airDropHandler) {
      this.instance = instance;
      this.airDropHandler = airDropHandler;
   }
}
