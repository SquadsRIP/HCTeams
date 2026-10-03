package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.AttackZombies;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.Debuffs;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.Gapple;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.GemBoost;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.GoldenApples;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.Invis;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.Nuke;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.PermSpeed2;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.PotionRefillToken;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes.Strength;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class KillstreakHandler implements Listener {
   private final List<Killstreak> killstreaks = Lists.newArrayList();
   private final List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

   public KillstreakHandler() {
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
      Proton.getInstance().getCommandHandler().registerClass(this.getClass());
      this.killstreaks.add(new Debuffs());
      this.killstreaks.add(new Gapple());
      this.killstreaks.add(new GoldenApples());
      this.killstreaks.add(new PotionRefillToken());
      this.killstreaks.add(new GemBoost());
      this.killstreaks.add(new AttackZombies());
      this.killstreaks.add(new Nuke());
      this.persistentKillstreaks.add(new Invis());
      this.persistentKillstreaks.add(new PermSpeed2());
      this.persistentKillstreaks.add(new Strength());
      this.killstreaks.sort((first, second) -> {
         int firstNumber = first.getKills()[0];
         return firstNumber < second.getKills()[0] ? -1 : 1;
      });
      this.persistentKillstreaks.sort((first, second) -> {
         int firstNumber = first.getKillsRequired();
         return firstNumber < second.getKillsRequired() ? -1 : 1;
      });
   }

   public Killstreak check(int kills) {
      for (Killstreak killstreak : this.killstreaks) {
         for (int kill : killstreak.getKills()) {
            if (kills == kill) {
               return killstreak;
            }
         }
      }

      return null;
   }

   public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
      return this.persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
   }

   @Command(names = "setks", permission = "hcteams.setkillstreak")
   public static void setKillstreak(Player player, @Parameter(name = "killstreak") int killstreak) {
      StatsEntry statsEntry = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player);
      statsEntry.setKillstreak(killstreak);
      player.sendMessage(ChatColor.GREEN + "You set your killstreak to: " + killstreak);
   }

   public List<Killstreak> getKillstreaks() {
      return this.killstreaks;
   }

   public List<PersistentKillstreak> getPersistentKillstreaks() {
      return this.persistentKillstreaks;
   }
}
