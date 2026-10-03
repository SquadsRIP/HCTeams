package net.frozenorb.foxtrot.config;

import org.junit.Test;
import static org.junit.Assert.*;

public class SquadsTextTest {
   @Test
   public void keepsSquadsLinks() {
      assertEquals("Store: store.squads.rip", SquadsText.display("Store: store.squads.rip"));
      assertEquals("Join discord.gg/squadsrip!", SquadsText.display("Join discord.gg/squadsrip!"));
      assertEquals("https://store.squads.rip/category/ranks", SquadsText.display("https://store.squads.rip/category/ranks"));
      assertEquals("&cstore.squads.rip.", SquadsText.display("&cstore.squads.rip."));
      assertEquals("https://squadsrip.tebex.io", SquadsText.display("https://squadsrip.tebex.io"));
   }

   @Test
   public void scrubsForeignLinks() {
      assertEquals("Buy at Coming Soon!", SquadsText.display("Buy at store.cavepvp.org"));
      assertEquals("Join Coming Soon!.", SquadsText.display("Join discord.gg/cavepvp."));
      assertEquals("See Coming Soon!", SquadsText.display("See https://example.com/page"));
   }

   @Test
   public void stillRebrandsCave() {
      assertEquals("Welcome to Squads", SquadsText.display("Welcome to CavePvP"));
   }
}
