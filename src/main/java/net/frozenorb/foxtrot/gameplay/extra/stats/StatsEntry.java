package net.frozenorb.foxtrot.gameplay.extra.stats;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.command.StatsTopCommand;

public class StatsEntry {
   private final UUID owner;
   private int kills;
   private int deaths;
   private int killstreak;
   private int kothCaptures;
   private int caveSaysCompleted;
   private int highestKillstreak;

   public StatsEntry(UUID owner) {
      this.owner = owner;
   }

   public void addKill() {
      this.kills++;
      this.killstreak++;
      if (this.highestKillstreak < this.killstreak) {
         this.highestKillstreak = this.killstreak;
      }
   }

   public void addDeath() {
      this.deaths++;
      this.killstreak = 0;
      Foxtrot.getInstance().getKillstreakMap().setKillstreak(this.owner, 0);
   }

   public void addKothCapture() {
      this.kothCaptures++;
   }

   public void addCaveSaysCompleted() {
      this.caveSaysCompleted++;
   }

   public void clear() {
      this.kills = 0;
      this.deaths = 0;
      this.killstreak = 0;
      this.highestKillstreak = 0;
      this.kothCaptures = 0;
   }

   public double getKD() {
      return this.getDeaths() == 0 ? 0.0 : (double)this.getKills() / this.getDeaths();
   }

   public Number get(StatsTopCommand.StatsObjective objective) {
      switch (objective) {
         case KILLS:
            return this.getKills();
         case DEATHS:
            return this.getDeaths();
         case KD:
            return this.getKD();
         case HIGHEST_KILLSTREAK:
            return this.getHighestKillstreak();
         case KOTH_CAPTURES:
            return this.getKothCaptures();
         case CAVE_SAYS_COMPLETED:
            return this.getCaveSaysCompleted();
         default:
            return 0;
      }
   }

   public UUID getOwner() {
      return this.owner;
   }

   public int getKills() {
      return this.kills;
   }

   public void setKills(int kills) {
      this.kills = kills;
   }

   public int getDeaths() {
      return this.deaths;
   }

   public void setDeaths(int deaths) {
      this.deaths = deaths;
   }

   public int getKillstreak() {
      return this.killstreak;
   }

   public void setKillstreak(int killstreak) {
      this.killstreak = killstreak;
   }

   public int getKothCaptures() {
      return this.kothCaptures;
   }

   public void setKothCaptures(int kothCaptures) {
      this.kothCaptures = kothCaptures;
   }

   public int getCaveSaysCompleted() {
      return this.caveSaysCompleted;
   }

   public void setCaveSaysCompleted(int caveSaysCompleted) {
      this.caveSaysCompleted = caveSaysCompleted;
   }

   public int getHighestKillstreak() {
      return this.highestKillstreak;
   }
}
