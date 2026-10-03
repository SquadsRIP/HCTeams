package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import cc.fyre.proton.util.EntityUtils;
import java.lang.reflect.Type;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class KillEntityChallenge extends Challenge {
   private EntityType entityType;
   private int kills;

   public KillEntityChallenge(String id, String name, int experience, boolean daily, EntityType entityType, int kills) {
      super(id, name, experience, daily);
      this.entityType = entityType;
      this.kills = kills;
   }

   public int getKilled(Player player) {
      return this.getProgress(player).getEntitiesKilled(this.entityType, this.isDaily());
   }

   @Override
   public Type getAbstractType() {
      return KillEntityChallenge.class;
   }

   @Override
   public String getText() {
      return "Kill " + this.kills + " " + this.getEntityName();
   }

   @Override
   public boolean hasStarted(Player player) {
      return this.getKilled(player) > 0;
   }

   @Override
   public boolean meetsCompletionCriteria(Player player) {
      return this.getKilled(player) >= this.kills;
   }

   @Override
   public String getProgressText(Player player) {
      int remaining = this.kills - this.getKilled(player);
      return "You need to kill "
         + Formats.formatNumber(remaining)
         + " more "
         + (remaining == 1 ? this.getEntityName() : this.getEntityNamePlural()).toLowerCase()
         + ".";
   }

   private String getEntityName() {
      return this.entityType == EntityType.ENDERMAN ? "Endermen" : EntityUtils.getName(this.entityType);
   }

   private String getEntityNamePlural() {
      return this.entityType == EntityType.ENDERMAN ? "Endermen" : EntityUtils.getName(this.entityType) + "s";
   }

   public EntityType getEntityType() {
      return this.entityType;
   }

   public int getKills() {
      return this.kills;
   }
}
