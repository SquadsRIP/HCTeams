package net.frozenorb.foxtrot.team;

import org.bukkit.World.Environment;

// $VF: synthetic class
class Team$2 {
   // $VF: synthetic field
   static final int[] $SwitchMap$org$bukkit$World$Environment = new int[Environment.values().length];

   static {
      try {
         $SwitchMap$org$bukkit$World$Environment[Environment.NETHER.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         $SwitchMap$org$bukkit$World$Environment[Environment.THE_END.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
