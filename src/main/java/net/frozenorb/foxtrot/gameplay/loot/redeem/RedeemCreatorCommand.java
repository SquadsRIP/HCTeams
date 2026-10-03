package net.frozenorb.foxtrot.gameplay.loot.redeem;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedeemCreatorCommand {
   private static final List<String> ADDRESSES = new ArrayList<>();

   @Command(names = {"redeem", "redeemcreator", "creatorredeem", "support"}, description = "Redeem a creator", permission = "", async = true)
   public static void execute(Player player, @Parameter(name = "creator") String creator) {
      if (!Foxtrot.getInstance().getServerHandler().isTeams() && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
         String c = Foxtrot.getInstance().getRedeemCreatorHandler().getCreator(creator);
         if (c == null) {
            player.sendMessage(CC.RED + "No creator by the name of " + CC.YELLOW + creator + CC.RED + " exists!");
         } else {
            Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
            if (profile == null) {
               player.sendMessage(ChatColor.RED + "Failed to load your profile! Please contact an admin!");
            } else if (Foxtrot.getInstance().getRedeemCreatorHandler().hasRedeemed(player)) {
               player.sendMessage(CC.RED + "You have already redeemed a creator this map!");
            } else if (ADDRESSES.contains(profile.getIpAddress())) {
               player.sendMessage(ChatColor.RED + "Someone on your IP Address has already redeemed!");
            } else {
               ADDRESSES.add(profile.getIpAddress());
               Foxtrot.getInstance().getRedeemCreatorHandler().redeem(player, c);
               Foxtrot.getInstance()
                  .getServer()
                  .getScheduler()
                  .runTask(
                     Foxtrot.getInstance(),
                     () -> {
                        Server server = Foxtrot.getInstance().getServer();
                        int randomNumber = ThreadLocalRandom.current().nextInt(0, 102);
                        String prize;
                        if (randomNumber <= 61) {
                           prize = ChatColor.translate("&5&l3x Legendary Keys");
                           server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Legendary 3");
                        } else if (randomNumber <= 76) {
                           prize = ChatColor.translate("&e&ki&6&l3x Halloween Keys&e&ki&r");
                           server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Seasonal 3");
                        } else if (randomNumber <= 86) {
                           prize = ChatColor.translate("&6&l1x Fall Lootbox");
                           server.dispatchCommand(server.getConsoleSender(), "crates give " + player.getName() + " Seasonal 1");
                        } else if (randomNumber <= 96) {
                           prize = ChatColor.translate("&6&l1x Reinforce Lootbox");
                           server.dispatchCommand(server.getConsoleSender(), "crates give " + player.getName() + " Reinforce 1");
                        } else {
                           prize = ChatColor.translate("&5&l3x Legendary Keys");
                           server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Legendary 3");
                        }

                        player.sendMessage(
                           ChatColor.GREEN
                              + "Thank you for supporting "
                              + ChatColor.WHITE
                              + c
                              + ChatColor.GREEN
                              + ", you have been given "
                              + prize
                              + ChatColor.GREEN
                              + "."
                        );
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

                        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                           if (Foxtrot.getInstance().getAnnoyingBroadcastMap().isAnnoyingBroadcast(onlinePlayer.getUniqueId())) {
                              onlinePlayer.sendMessage(
                                 ChatColor.translate("&4&lRedeem &8┃ &f" + player.getName() + " &7has received &f" + prize + " &7for using &f/redeem&7!")
                              );
                           }
                        }
                     }
                  );
            }
         }
      } else {
         player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      }
   }

   @Command(names = "redeemlist", permission = "op", async = true)
   public static void list(Player sender, @Parameter(name = "creator") String creator) {
      String c = Foxtrot.getInstance().getRedeemCreatorHandler().getCreator(creator);
      if (c == null) {
         sender.sendMessage(CC.RED + "No creator by the name of " + CC.YELLOW + creator + CC.RED + " exists!");
      } else {
         sender.sendMessage(Team.DARK_GRAY_LINE);
         sender.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + c + "'s Redeem Stats");
         Proton.getInstance().runRedisCommand(jedis -> {
            Set<String> keys = jedis.keys("redeemed_creator:*");
            List<UUID> uuids = new ArrayList<>();

            for (String key : keys) {
               if (jedis.get(key).equals(c)) {
                  uuids.add(UUID.fromString(key.replace("redeemed_creator:", "")));
               }
            }

            int total = 0;
            int uses = 0;
            int teams = 0;
            int kills = 0;
            int deaths = 0;

            for (UUID uuid : uuids) {
               long playTime = Foxtrot.getInstance().getPlaytimeMap().getPlaytime(uuid);
               if (Foxtrot.getInstance().getTeamHandler().getTeam(uuid) != null) {
                  teams++;
               }

               kills += Foxtrot.getInstance().getKillsMap().getKills(uuid);
               deaths += Foxtrot.getInstance().getDeathsMap().getDeaths(uuid);
               total = (int)(total + playTime);
               uses++;
            }

            sender.sendMessage(ChatColor.RED + "Avg Playtime: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString(total / uses));
            sender.sendMessage(ChatColor.RED + "Players in Teams: " + ChatColor.WHITE + teams);
            sender.sendMessage(ChatColor.RED + "Average Kills: " + ChatColor.WHITE + kills / uses);
            sender.sendMessage(ChatColor.RED + "Average Deaths: " + ChatColor.WHITE + deaths / uses);
            sender.sendMessage(Team.DARK_GRAY_LINE);
            return null;
         });
      }
   }

   @Command(names = "redeemclear", description = "Clear a players", permission = "redeem.creator.clear", async = true)
   public static void executeClear(Player sender, @Parameter(name = "target", defaultValue = "self") Player target) {
      ADDRESSES.clear();
      Proton.getInstance().runRedisCommand(jedis -> jedis.del("redeemed_creator:" + target.getUniqueId().toString()));
      sender.sendMessage(CC.GREEN + "Reset");
   }

   @Command(names = {"redeemview", "redeembreakdown"}, description = "Review redeem stats", permission = "redeem.creator.stats", async = true)
   public static void execute(CommandSender sender) {
      sender.sendMessage(Team.DARK_GRAY_LINE);
      sender.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Redeem Breakdown");
      sender.sendMessage("");
      Proton.getInstance()
         .runRedisCommand(
            jedis -> {
               Set<String> keys = jedis.keys("redeemed_creator:*");
               Map<String, Integer> countMap = new HashMap<>();

               for (String key : keys) {
                  String creator = jedis.get(key);
                  Integer value = countMap.computeIfAbsent(creator, $ -> 0);
                  countMap.put(creator, value + 1);
               }

               LinkedHashMap<String, Integer> sorted = countMap.entrySet()
                  .stream()
                  .sorted(Entry.<String, Integer>comparingByValue().reversed())
                  .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
               int count = 0;

               for (Entry<String, Integer> entry : sorted.entrySet()) {
                  sender.sendMessage(ChatColor.RED.toString() + ++count + ". " + ChatColor.GRAY + entry.getKey() + ChatColor.WHITE + " - " + entry.getValue());
               }

               sender.sendMessage(Team.DARK_GRAY_LINE);
               return null;
            }
         );
   }
}
