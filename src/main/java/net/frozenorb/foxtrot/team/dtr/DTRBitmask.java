package net.frozenorb.foxtrot.team.dtr;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Location;

public enum DTRBitmask {
   SAFE_ZONE(1, "Safe-Zone", "Determines if a region is considered completely safe"),
   DENY_REENTRY(2, "Deny-Reentry", "Determines if a region can be reentered"),
   FIFTEEN_MINUTE_DEATHBAN(4, "15m-Deathban", "Determines if a region has a 15m deathban"),
   FIVE_MINUTE_DEATHBAN(8, "5m-Deathban", "Determines if a region has a 5m deathban"),
   THIRTY_SECOND_ENDERPEARL_COOLDOWN(16, "30s-Enderpearl-Cooldown", "Determines if a region has a 30s enderpearl cooldown"),
   CITADEL(32, "Citadel", "Determines if a region is part of Citadel"),
   KOTH(64, "KOTH", "Determines if a region is a KOTH"),
   REDUCED_DTR_LOSS(128, "Reduced-DTR-Loss", "Determines if a region takes away reduced DTR upon death"),
   NO_ENDERPEARL(256, "No-Enderpearl", "Determines if a region cannot be pearled into"),
   QUARTER_DTR_LOSS(512, "1/4-DTR-Loss", "Determines if a region takes away 1/4th DTR loss."),
   ROAD(1024, "Road", "Determines if a region is a road."),
   CONQUEST(2048, "Conquest", "Determines if a region is part of Conquest."),
   DUEL(4096, "Duel", "Determines if a region is part of a duel"),
   DTC(8192, "DTC", "Determines if a region is part of DTC"),
   FURY(47, "Fury", "Determines if a region is part of Fury"),
   MOUNTAIN(16384, "Mountain", "Determines if a region is part of Mountain"),
   OUTPOST(32768, "Outpost", "Determines if a region is part of Outpost");

   private int bitmask;
   private String name;
   private String description;

   DTRBitmask(int bitmask, String name, String description) {
      this.bitmask = bitmask;
      this.name = name;
      this.description = description;
   }

   public boolean appliesAt(Location location) {
      Team ownerTo = LandBoard.getInstance().getTeam(location);
      return ownerTo != null && ownerTo.getOwner() == null && ownerTo.hasDTRBitmask(this);
   }

   public int getBitmask() {
      return this.bitmask;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }
}
