package net.frozenorb.foxtrot.tab;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.construct.Tab;
import cc.fyre.proton.tab.construct.TabLayout;
import cc.fyre.proton.tab.provider.LayoutProvider;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.Universe;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamListCommand;
import net.frozenorb.foxtrot.util.PlayerDirection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FoxtrotTabLayoutProvider implements LayoutProvider {
   private static final String TITLE = "&4";
   private static final String INFO = "&7";
   private LinkedHashMap<Team, Integer> cachedTeamList = new LinkedHashMap<>();
   private long cacheLastUpdated;
   private final Method updateTabEntry;
   private final Map<Tab, String[]> previousTeamColumns = new WeakHashMap<>();

   public FoxtrotTabLayoutProvider() {
      Proton proton = Proton.getInstance();
      ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
      protocolManager.addPacketListener(
         new LegacyTabAdapter(
            Foxtrot.getInstance(),
            protocolManager,
            () -> proton.getTabHandler().getLayoutProvider() == this && !proton.getConfig().getBoolean("disableTab", false)
         )
      );

      try {
         this.updateTabEntry = Tab.class.getDeclaredMethod("update", String.class, String.class, String.class, int.class);
         this.updateTabEntry.setAccessible(true);
      } catch (ReflectiveOperationException exception) {
         throw new IllegalStateException("Cannot access Proton's tab entry updater", exception);
      }
   }

   @Override
   public TabLayout provide(Player player) {
      Foxtrot plugin = Foxtrot.getInstance();
      TabLayout layout = TabLayout.create(player);
      Team team = plugin.getTeamHandler().getTeam(player);
      int online = getOnlinePlayersList().size() + Universe.fakePlayers;
      if (layout.is18()) {
         layout.setHeader("\n&4&lSquads\n&7Teams\n");
         layout.setFooter(
            "\n&4Discord: &f"
               + ConfigMessages.link("discord", "Coming Soon!").replaceFirst("^https?://", "")
               + " &7| &4Store: &f"
               + ConfigMessages.link("store", "Coming Soon!").replaceFirst("^https?://", "")
               + "\n&7You are playing with &c"
               + online
               + " &7players.\n"
         );
      } else {
         layout.set(1, 0, LegacyTabText.center("&4&lSquads"));
      }

      layout.set(0, 0, "&4" + (layout.is18() ? "Team Home" : "Home"));
      if (team != null && team.getHQ() != null) {
         layout.set(0, 1, "&f" + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ());
      } else {
         layout.set(0, 1, "&7Not Set");
      }

      layout.set(0, 3, "&4Team Info");
      if (team != null) {
         String dtr = team.getDTRColor() + String.format(Locale.US, "%.2f", team.getDTR()) + team.getDTRSuffix();
         if (plugin.getDTRDisplayMap().isHearts(player.getUniqueId())) {
            dtr = team.getDTRColor().toString() + (int)Math.ceil(team.getDTR()) + "❤" + team.getDTRSuffix();
         }

         layout.set(0, 4, "&7DTR: " + dtr);
         layout.set(0, 5, "&7" + (layout.is18() ? "Balance: &a$" : "Bal: &a$") + (int)team.getBalance());
         layout.set(0, 6, "&7Online: &f" + team.getOnlineMemberAmount() + "/" + team.getMembers().size());
      } else {
         layout.set(0, 4, "&7Create a team");
         layout.set(0, 5, "&f/t create [name]");
      }

      StatsEntry stats = plugin.getMapHandler().getStatsHandler().getStats(player.getUniqueId());
      layout.set(0, 8, "&4Player Info");
      layout.set(0, 9, "&7Kills: &f" + (stats != null ? stats.getKills() : plugin.getKillsMap().getKills(player.getUniqueId())));
      layout.set(0, 10, "&7Deaths: &f" + (stats != null ? stats.getDeaths() : plugin.getDeathsMap().getDeaths(player.getUniqueId())));
      layout.set(0, 11, "&7" + (layout.is18() ? "Balance: &a$" : "Bal: &a$") + (int)plugin.getEconomyHandler().getBalance(player.getUniqueId()));
      layout.set(0, 13, "&4" + (layout.is18() ? "Location Info" : "Location"));
      Location loc = player.getLocation();
      Team ownerTeam = LandBoard.getInstance().getTeam(loc);
      layout.set(0, 14, ownerTeam != null ? ownerTeam.getName(player) : (plugin.getServerHandler().isWarzone(loc) ? "&cWarzone" : "&7Wilderness"));
      String direction = PlayerDirection.getCardinalDirection(player);
      layout.set(0, 15, "&f" + loc.getBlockX() + (layout.is18() ? ", " : ",") + loc.getBlockZ() + (direction != null ? " &7[" + direction + "]" : ""));
      this.renderRoster(layout, player, team);
      this.renderMap(layout, online);
      this.renderTeams(layout, player);
      LegacyTabText.fitLayout(layout);
      if (layout.is18()) {
         this.updateModernTeamColumn(player, layout);
      }

      return layout;
   }

   private void renderRoster(TabLayout layout, Player player, Team team) {
      if (team != null) {
         layout.set(1, 2, "&4" + team.getName());
         List<Player> members = new ArrayList<>(team.getOnlineMembers());
         members.remove(player);
         members.sort((first, second) -> Integer.compare(memberRank(team, first), memberRank(team, second)));
         members.add(0, player);
         int limit = layout.is18() ? 17 : 4;
         int shown = Math.min(members.size(), members.size() > limit ? limit - 1 : limit);

         for (int i = 0; i < shown; i++) {
            Player member = members.get(i);
            int rank = memberRank(team, member);
            layout.set(1, i + 3, ChatColor.GREEN + member.getName() + (rank < 2 ? "&7**" : (rank == 2 ? "&7*" : "")), getPlayerPing(member));
         }

         if (shown < members.size()) {
            layout.set(1, shown + 3, "&7+" + (members.size() - shown) + " more");
         }
      }
   }

   private static int memberRank(Team team, Player player) {
      if (team.isOwner(player.getUniqueId())) {
         return 0;
      } else if (team.isCoLeader(player.getUniqueId())) {
         return 1;
      } else {
         return team.isCaptain(player.getUniqueId()) ? 2 : 3;
      }
   }

   private void renderMap(TabLayout layout, int online) {
      Foxtrot plugin = Foxtrot.getInstance();
      boolean modern = layout.is18();
      int column = modern ? 2 : 1;
      int row = modern ? 0 : 8;
      layout.set(column, row, "&4Map Kit");
      layout.set(
         column,
         row + 1,
         "&f"
            + LegacyTabText.mapKit(
               modern, plugin.getConfig().getInt("enchantmentLimits.protection", 2), plugin.getConfig().getInt("enchantmentLimits.sharpness", 2)
            )
      );
      if (modern) {
         layout.set(column, 3, "&4Border");
         layout.set(column, 4, "&f" + BorderListener.BORDER_SIZE);
         layout.set(column, 6, "&4Players");
         layout.set(column, 7, "&f" + online);
      } else {
         layout.set(column, 11, "&4Border");
         layout.set(column, 12, "&f" + BorderListener.BORDER_SIZE);
         layout.set(column, 14, "&4Players");
         layout.set(column, 15, "&f" + online);
      }

      KOTH activeKoth = null;

      for (Event event : plugin.getEventHandler().getEvents()) {
         if (event instanceof KOTH && ((KOTH)event).isActive() && !((KOTH)event).isHidden()) {
            activeKoth = (KOTH)event;
            break;
         }
      }

      row = modern ? 9 : 17;
      if (activeKoth != null) {
         layout.set(column, row++, "&4Active Event");
         layout.set(column, row++, "&f" + activeKoth.getName());
         layout.set(column, row++, "&f" + TimeUtils.formatIntoHHMMSS(activeKoth.getRemainingCapTime()));
         if (modern) {
            layout.set(column, row, "&7" + activeKoth.getCapLocation().getBlockX() + ", " + activeKoth.getCapLocation().getBlockZ());
         }
      } else {
         Date now = new Date();
         Date nextDate = null;
         String nextName = null;

         for (Entry<EventScheduledTime, String> entry : plugin.getEventHandler().getEventSchedule().entrySet()) {
            Date date = entry.getKey().toDate();
            if (date.after(now) && (nextDate == null || date.before(nextDate))) {
               nextDate = date;
               nextName = entry.getValue();
            }
         }

         layout.set(column, row++, "&4Next Event");
         layout.set(column, row++, nextName == null ? "&7None scheduled" : "&f" + nextName);
         if (nextDate != null) {
            if (modern) {
               layout.set(column, row++, "&4Goes active in");
            }

            long seconds = Math.max(0L, (nextDate.getTime() - System.currentTimeMillis()) / 1000L);
            layout.set(
               column,
               row,
               modern
                  ? "&f" + formatCountdown(seconds)
                  : "&7In &f" + (seconds >= 3600L ? seconds / 3600L + "h " : "") + seconds / 60L % 60L + "m " + seconds % 60L + "s"
            );
         }
      }

      String portals = plugin.getMapHandler().getEndPortalLocation();
      if (portals != null && !portals.isEmpty() && !portals.equals("N/A")) {
         column = modern ? 2 : 0;
         row = modern ? 15 : 17;
         layout.set(column, row, "&4End Portals");
         layout.set(column, row + 1, "&f" + portals);
         layout.set(column, row + 2, "&7" + (modern ? "in each quadrant" : "each quadrant"));
      }
   }

   private void renderTeams(TabLayout layout, Player player) {
      if (System.currentTimeMillis() - this.cacheLastUpdated > 2000L) {
         Map<Team, Integer> counts = new HashMap<>();

         for (Player other : getOnlinePlayersList()) {
            if (!ModHandler.INSTANCE.isInVanish(other.getUniqueId())) {
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(other);
               if (team != null) {
                  counts.put(team, counts.getOrDefault(team, 0) + 1);
               }
            }
         }

         this.cachedTeamList = TeamListCommand.sortByValues(counts);
         this.cacheLastUpdated = System.currentTimeMillis();
      }

      setTeamRow(layout, 0, "&4Teams");
      int row = 1;

      for (Entry<Team, Integer> entry : this.cachedTeamList.entrySet()) {
         if (row >= 20) {
            break;
         }

         Team team = entry.getKey();
         String color = team.isMember(player.getUniqueId()) ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
         String dtr = team.getDTRColor() + String.format(Locale.US, "%.2f", team.getDTR());
         setTeamRow(layout, row++, LegacyTabText.teamEntry(layout.is18(), team.getName(), color, entry.getValue(), dtr));
      }
   }

   private static void setTeamRow(TabLayout layout, int row, String text) {
      if (layout.is18()) {
         layout.getTabNames()[60 + row] = ChatColor.translateAlternateColorCodes('&', text);
         layout.getTabPings()[60 + row] = -1;
      } else {
         layout.set(2, row, text);
      }
   }

   private void updateModernTeamColumn(Player player, TabLayout layout) {
      Tab tab = Proton.getInstance().getTabHandler().getTabs().get(player.getName());
      if (tab != null && tab.isInitiated()) {
         String[] previous = this.previousTeamColumns.computeIfAbsent(tab, key -> new String[20]);
         String[] placeholders = TabLayout.getTAB_LAYOUT_1_8().getTabNames();

         for (int row = 0; row < 20; row++) {
            String text = layout.getTabNames()[60 + row];
            if (!text.equals(previous[row])) {
               String name = placeholders[60 + row];
               String scoreboardTeam = "$" + name.replace(String.valueOf('§'), "");

               try {
                  this.updateTabEntry.invoke(tab, name, scoreboardTeam, text, -1);
                  previous[row] = text;
               } catch (ReflectiveOperationException exception) {
                  throw new IllegalStateException("Cannot update the Teams tab column", exception);
               }
            }
         }
      }
   }

   private static String formatCountdown(long seconds) {
      long hours = seconds / 3600L;
      return (hours > 0L ? hours + "h, " : "") + seconds / 60L % 60L + "m, " + seconds % 60L + "s";
   }

   public static String formatIntoDetailedString(int secs) {
      if (secs <= 60) {
         return "1 minute";
      }

      int days = secs / 86400;
      int hours = secs % 86400 / 3600;
      int minutes = secs % 3600 / 60;
      return ((days > 0 ? days + " day" + (days > 1 ? "s" : "") + " " : "")
            + (hours > 0 ? hours + " hour" + (hours > 1 ? "s" : "") + " " : "")
            + (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") : ""))
         .trim();
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Object result = Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var1) {
      }

      return new ArrayList<>();
   }

   private static int getPlayerPing(Player player) {
      try {
         Object handle = player.getClass().getMethod("getHandle").invoke(player);
         Field ping = handle.getClass().getField("ping");
         return ping.getInt(handle);
      } catch (Exception ignored) {
         return 0;
      }
   }
}
