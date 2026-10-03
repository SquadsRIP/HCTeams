package net.frozenorb.foxtrot.server.voucher;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;

public enum Bundles {
   SOTW_BUNDLE(
      "SOTW",
      Arrays.asList("mcrate give {player} Seasonal 3", "airdrops give {player} 5", "crates give {player} Seasonal 50", "cr givekey {player} Seasonal 100")
   ),
   THREE(
      "Seasonal",
      Arrays.asList("mcrate give {player} Seasonal 9", "airdrops give {player} 10", "crates give {player} Seasonal 100", "cr givekey {player} Seasonal 150")
   ),
   SUPREME_BUNDLE(
      "Supreme",
      Arrays.asList(
         "airdrops give {player} 15",
         "crates give {player} Seasonal 120",
         "cr givekey {player} Seasonal 250",
         "cr givekey {player} Items 250",
         "randomownerkeys {player} 50"
      )
   );

   String id;
   List<String> commands;

   @ConstructorProperties({"id", "commands"})
   Bundles(String id, List<String> commands) {
      this.id = id;
      this.commands = commands;
   }

   public String getId() {
      return this.id;
   }

   public List<String> getCommands() {
      return this.commands;
   }
}
