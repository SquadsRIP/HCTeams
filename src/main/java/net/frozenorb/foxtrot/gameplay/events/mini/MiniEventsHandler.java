package net.frozenorb.foxtrot.gameplay.events.mini;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Mayhem;
import net.frozenorb.foxtrot.gameplay.events.mini.type.MiningFrenzy;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Rage;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Raider;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Traveller;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.King;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.Shooter;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.Stabber;

public class MiniEventsHandler {
   private Foxtrot instance;
   private List<MiniEvent> events = new ArrayList<>();
   private MiniEvent activeEvent;

   public MiniEventsHandler(Foxtrot instance) {
      this.instance = instance;
      if (this.instance.getMapHandler().isKitMap()) {
         this.events.add(new King());
         this.events.add(new Shooter());
         this.events.add(new Stabber());
      } else {
         this.events.add(new MiningFrenzy());
         this.events.add(new Raider());
      }

      this.events.add(new Traveller());
      this.events.add(new Rage());
      this.events.add(new Mayhem());
   }

   public MiniEvent findEvent(String id) {
      return this.events.stream().filter(it -> it.getEventID().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   public void activateRandom() {
      this.activeEvent = this.events.get(ThreadLocalRandom.current().nextInt(this.events.size()));
      this.activeEvent.activate();
   }

   public List<MiniEvent> getEvents() {
      return this.events;
   }

   public MiniEvent getActiveEvent() {
      return this.activeEvent;
   }

   public void setActiveEvent(MiniEvent activeEvent) {
      this.activeEvent = activeEvent;
   }
}
