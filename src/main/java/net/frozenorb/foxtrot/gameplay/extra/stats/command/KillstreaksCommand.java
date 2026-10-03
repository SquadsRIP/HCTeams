package net.frozenorb.foxtrot.gameplay.extra.stats.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillstreaksCommand {
   @Command(names = {"killstreaks", "ks", "killstreak"}, permission = "")
   public static void killstreaks(Player sender) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
      } else {
         final List<Object> streaks = Lists.newArrayList(Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getKillstreaks());
         streaks.addAll(Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks());
         streaks.sort((first, second) -> {
            int firstNumber = first instanceof Killstreak ? ((Killstreak)first).getKills()[0] : ((PersistentKillstreak)first).getKillsRequired();
            int secondNumber = second instanceof Killstreak ? ((Killstreak)second).getKills()[0] : ((PersistentKillstreak)second).getKillsRequired();
            return firstNumber < secondNumber ? -1 : 1;
         });
         (new Menu() {
               @Override
               public String getTitle(Player player) {
                  return "Killstreaks";
               }

               @Override
               public Map<Integer, Button> getButtons(Player player) {
                  Map<Integer, Button> toReturn = new HashMap<>();

                  for (Object object : streaks) {
                     final String name = object instanceof Killstreak ? ((Killstreak)object).getName() : ((PersistentKillstreak)object).getName();
                     final int kills = object instanceof Killstreak ? ((Killstreak)object).getKills()[0] : ((PersistentKillstreak)object).getKillsRequired();
                     final List<String> description = object instanceof Killstreak
                        ? ((Killstreak)object).getDescription()
                        : ((PersistentKillstreak)object).getDescription();
                     final Material material = object instanceof Killstreak ? ((Killstreak)object).getMaterial() : ((PersistentKillstreak)object).getMaterial();
                     toReturn.put(toReturn.size(), new Button() {
                        @Override
                        public String getName(Player player) {
                           return ChatColor.DARK_RED + ChatColor.BOLD.toString() + name;
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                           List<String> toReturnx = new ArrayList<>();

                           for (String s : description) {
                              toReturnx.add(ChatColor.GRAY + s);
                           }

                           toReturnx.add("");
                           toReturnx.add(ChatColor.translate("&4❙ &fRequired Kills: &c" + kills));
                           return toReturnx;
                        }

                        @Override
                        public Material getMaterial(Player player) {
                           return material;
                        }
                     });
                  }

                  return toReturn;
               }
            })
            .openMenu(sender);

         for (Object ks : streaks) {
            String name = ks instanceof Killstreak ? ((Killstreak)ks).getName() : ((PersistentKillstreak)ks).getName();
            int kills = ks instanceof Killstreak ? ((Killstreak)ks).getKills()[0] : ((PersistentKillstreak)ks).getKillsRequired();
            if (name != null) {
               sender.sendMessage(ChatColor.YELLOW + name + ": " + ChatColor.RED + kills);
            }
         }

         sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
      }
   }

   @Command(names = {"killstreaks active", "ks active", "killstreak active"}, permission = "")
   public static void killstreaksActive(CommandSender sender) {
      Map<Player, Integer> sorted = getSortedPlayers();
      if (sorted.isEmpty()) {
         sender.sendMessage(ChatColor.GRAY + "There is no data to display.");
      } else {
         sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
         int index = 0;

         for (Entry<Player, Integer> entry : getSortedPlayers().entrySet()) {
            if (index > 10) {
               break;
            }

            index++;
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(entry.getKey());
            FancyMessage playerMessage = new FancyMessage();
            playerMessage.text(index + ". ").color(ChatColor.GRAY).then();
            playerMessage.text(entry.getKey().getName())
               .color(sender instanceof Player && team != null && team.isMember(((Player)sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
               .then();
            playerMessage.text(" - ").color(ChatColor.YELLOW).then();
            playerMessage.text(entry.getValue() + " killstreak").color(ChatColor.GRAY);
            playerMessage.send(sender);
         }

         sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
      }
   }

   public static LinkedHashMap<Player, Integer> getSortedPlayers() {
      Map<Player, Integer> playerKillstreaks = new HashMap<>();

      for (Player player : Bukkit.getOnlinePlayers()) {
         StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());
         if (stats.getKillstreak() > 0) {
            playerKillstreaks.put(player, stats.getKillstreak());
         }
      }

      return sortByValues(playerKillstreaks);
   }

   public static LinkedHashMap<Player, Integer> sortByValues(Map<Player, Integer> map) {
      LinkedList<Entry<Player, Integer>> list = new LinkedList<>(map.entrySet());
      Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      LinkedHashMap<Player, Integer> sortedHashMap = new LinkedHashMap<>();

      for (Entry<Player, Integer> entry : list) {
         sortedHashMap.put(entry.getKey(), entry.getValue());
      }

      return sortedHashMap;
   }
}
