package net.frozenorb.foxtrot.gameplay.events;

public interface Event {
   String getName();

   boolean isActive();

   void tick();

   void setActive(boolean var1);

   boolean isHidden();

   void setHidden(boolean var1);

   boolean activate();

   boolean deactivate();

   EventType getType();
}
