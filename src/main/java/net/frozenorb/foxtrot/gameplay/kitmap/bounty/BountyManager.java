package net.frozenorb.foxtrot.gameplay.kitmap.bounty;

import cc.fyre.proton.uuid.UUIDCache;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.bounty.listener.BountyListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BountyManager {
   private UUID autoBounty;
   private final Map<UUID, Bounty> bountyMap = new HashMap<>();

   public BountyManager() {
      final Foxtrot instance = Foxtrot.getInstance();
      instance.getServer().getPluginManager().registerEvents(new BountyListener(instance, this), instance);
      (new BukkitRunnable() {
            @Override
            public void run() {
               if (DisableCommand.bounty) {
                  if (BountyManager.this.autoBounty == null || instance.getServer().getPlayer(BountyManager.this.autoBounty) == null) {
                     List<Player> players = instance.getServer()
                        .getOnlinePlayers()
                        .stream()
                        .filter(it -> BountyManager.this.getBounty(it) == null && instance.getServerHandler().isWarzone(it.getLocation()))
                        .collect(Collectors.toList());
                     if (!players.isEmpty()) {
                        Player randomPlayer = players.get(ThreadLocalRandom.current().nextInt(0, players.size() - 1));

                        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                           onlinePlayer.sendMessage("");
                           onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Bounty");
                           onlinePlayer.sendMessage(
                              ChatColor.translate("&7A random bounty worth &a50 Gems &7has been placed on &f" + randomPlayer.getDisplayName())
                           );
                           onlinePlayer.sendMessage(
                              ChatColor.translate("&cLocation: &f" + randomPlayer.getLocation().getBlockX() + ", " + randomPlayer.getLocation().getBlockZ())
                           );
                           onlinePlayer.sendMessage("");
                        }

                        BountyManager.this.autoBounty = randomPlayer.getUniqueId();
                        BountyManager.this.placeBounty(UUIDCache.CONSOLE_UUID, randomPlayer, 50);
                     }
                  }
               }
            }
         })
         .runTaskTimer(instance, 1200L, 1200L);
   }

   public void save() {
      this.bountyMap.forEach((key, value) -> Foxtrot.getInstance().getGemMap().addGemsSync(value.getPlacedBy(), value.getGems()));
      this.bountyMap.clear();
   }

   public void placeBounty(Player player, Player target, int gems) {
      this.bountyMap.put(target.getUniqueId(), new Bounty(player.getUniqueId(), gems));
   }

   public void placeBounty(UUID sender, Player target, int gems) {
      this.bountyMap.put(target.getUniqueId(), new Bounty(sender, gems));
   }

   public Bounty getBounty(Player player) {
      return this.bountyMap.get(player.getUniqueId());
   }

   public Bounty removeBounty(Player player) {
      return this.bountyMap.remove(player.getUniqueId());
   }

   public UUID getAutoBounty() {
      return this.autoBounty;
   }

   public Map<UUID, Bounty> getBountyMap() {
      return this.bountyMap;
   }
}
