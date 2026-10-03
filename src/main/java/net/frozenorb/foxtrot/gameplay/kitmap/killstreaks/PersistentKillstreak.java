package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks;

import java.beans.ConstructorProperties;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PersistentKillstreak {
   private String name;
   private int killsRequired;
   private Material material;
   private List<String> description;

   public boolean matchesExactly(int kills) {
      return kills == this.killsRequired;
   }

   public boolean check(int count) {
      return this.killsRequired <= count;
   }

   public void apply(Player player) {
   }

   @ConstructorProperties({"name", "killsRequired", "material", "description"})
   public PersistentKillstreak(String name, int killsRequired, Material material, List<String> description) {
      this.name = name;
      this.killsRequired = killsRequired;
      this.material = material;
      this.description = description;
   }

   public String getName() {
      return this.name;
   }

   public int getKillsRequired() {
      return this.killsRequired;
   }

   public Material getMaterial() {
      return this.material;
   }

   public List<String> getDescription() {
      return this.description;
   }
}
