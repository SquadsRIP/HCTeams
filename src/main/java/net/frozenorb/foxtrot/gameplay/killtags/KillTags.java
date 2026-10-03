package net.frozenorb.foxtrot.gameplay.killtags;

import java.beans.ConstructorProperties;

public enum KillTags {
   NONE("", " was slain by ", ""),
   RATIO("Ratio'd", " has been ratio'd by ", "The most phrase in mankind"),
   BEAMED("Beamed", " was beamed by ", "Show your enemies that they've been scammed"),
   SMOKED("Smoked", " was smoked by ", "Smoke their pack bro, just smoke it"),
   BLACKLISTED("Blacklisted", " was blacklisted by ", "A classic SimplyTrash move"),
   PACKED("Packed", " was packed by ", "Raise the heat, show em who's boss"),
   COMBOED("Combo'd", " was combo'd by ", "Just be honest, did you actually?"),
   PISS("Piss", " was pissed on by ", "Claim territory, show players who's boss");

   String displayName;
   String deathTag;
   String description;

   @ConstructorProperties({"displayName", "deathTag", "description"})
   KillTags(String displayName, String deathTag, String description) {
      this.displayName = displayName;
      this.deathTag = deathTag;
      this.description = description;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getDeathTag() {
      return this.deathTag;
   }

   public String getDescription() {
      return this.description;
   }
}
