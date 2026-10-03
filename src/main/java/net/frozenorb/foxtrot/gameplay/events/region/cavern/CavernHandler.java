package net.frozenorb.foxtrot.gameplay.events.region.cavern;

import cc.fyre.proton.Proton;
import java.io.File;
import java.io.IOException;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class CavernHandler {
   private static File file;
   private static final String cavernTeamName = "Cavern";
   private Cavern cavern;

   public void save() {
      try {
         FileUtils.write(file, Proton.GSON.toJson(this.cavern));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public boolean hasCavern() {
      return this.cavern != null;
   }

   public static Claim getClaim() {
      return Foxtrot.getInstance().getTeamHandler().getTeam("Cavern").getClaims().get(0);
   }

   public static String getCavernTeamName() {
      return "Cavern";
   }

   public Cavern getCavern() {
      return this.cavern;
   }

   public void setCavern(Cavern cavern) {
      this.cavern = cavern;
   }
}
