package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment.Type;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.model.DBCollectionFindOptions;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffBreakdownCommand {
   @Command(names = "staffbreakdown", permission = "op", async = true)
   public static void execute(CommandSender sender, @Parameter(name = "target") UUID uuid) {
      String name = UUIDUtils.name(uuid);
      sender.sendMessage(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7L) + " <--");
      sender.sendMessage(ChatColor.GREEN + "Displaying staff breakdown for the last week for " + name + "...");
      Player player = Foxtrot.getInstance().getServer().getPlayer(uuid);
      Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid, true);
      if (profile == null) {
         player.sendMessage(ChatColor.RED + "Unable to request profile belonging to " + name + "!");
      } else {
         DBCollection dbCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");
         int refunds = dbCollection.find(new BasicDBObject("refundedBy", uuid.toString().replace("-", "")), new DBCollectionFindOptions()).count();
         int playtimeTime = (int)Foxtrot.getInstance().getPlaytimeMap().getPlaytime(uuid);
         Map<UUID, Punishment> executedPunishments = new HashMap<>();
         Map<UUID, RemoveAblePunishment> executedRemoveAblePunishments = new HashMap<>();
         Map<UUID, RemoveAblePunishment> pardonedRemoveAblePunishments = new HashMap<>();

         for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {
            Profile targetProfile = new Profile(document);
            if (!targetProfile.getPunishments().isEmpty()) {
               for (IPunishment punishment : targetProfile.getPunishments()) {
                  if (punishment.getExecutedAt() >= System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7L)) {
                     if (punishment.getExecutor().equals(uuid)) {
                        if (punishment.getIType() == Type.NORMAL) {
                           executedPunishments.put(targetProfile.getUuid(), (Punishment)punishment);
                        } else if (punishment.getIType() == Type.REMOVE_ABLE) {
                           executedRemoveAblePunishments.put(targetProfile.getUuid(), (RemoveAblePunishment)punishment);
                        }
                     }

                     if (punishment instanceof RemoveAblePunishment
                        && ((RemoveAblePunishment)punishment).isPardoned()
                        && ((RemoveAblePunishment)punishment).getPardoner().equals(uuid)) {
                        pardonedRemoveAblePunishments.put(targetProfile.getUuid(), (RemoveAblePunishment)punishment);
                     }
                  }
               }
            }
         }

         if (player != null) {
            playtimeTime = (int)(playtimeTime + Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId()) / 1000L);
         }

         sender.sendMessage("");
         sender.sendMessage(name + ChatColor.GOLD + " Breakdown");
         sender.sendMessage("");
         sender.sendMessage(ChatColor.translate("&6&lGeneral Info"));
         sender.sendMessage(ChatColor.translate("&ePlaytime: &f" + TimeUtils.formatIntoDetailedString(playtimeTime)));
         sender.sendMessage(ChatColor.translate("&eRestored Inventories: &f" + refunds));
         sender.sendMessage("");
         sender.sendMessage(ChatColor.translate("&6&lPunishments"));
         sender.sendMessage(
            ChatColor.translate(
               "&eTotal Bans (Last 7 days): &f"
                  + executedRemoveAblePunishments.values()
                     .stream()
                     .filter(it -> it.getType() == cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment.Type.BAN)
                     .count()
            )
         );
         sender.sendMessage(
            ChatColor.translate(
               "&eTotal Mutes (Last 7 days): &f"
                  + executedRemoveAblePunishments.values()
                     .stream()
                     .filter(it -> it.getType() == cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment.Type.MUTE)
                     .count()
            )
         );
         sender.sendMessage(
            ChatColor.translate(
               "&eTotal Unbans (Last 7 days): &f"
                  + pardonedRemoveAblePunishments.values()
                     .stream()
                     .filter(it -> it.getType() == cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment.Type.BAN)
                     .count()
            )
         );
         sender.sendMessage(
            ChatColor.translate(
               "&eTotal Unmutes (Last 7 days): &f"
                  + pardonedRemoveAblePunishments.values()
                     .stream()
                     .filter(it -> it.getType() == cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment.Type.MUTE)
                     .count()
            )
         );
         sender.sendMessage(
            ChatColor.translate(
               "&eTotal Warns (Last 7 days): &f"
                  + executedPunishments.values()
                     .stream()
                     .filter(it -> it.getType() == cc.fyre.neutron.profile.attributes.punishment.impl.Punishment.Type.WARN)
                     .count()
            )
         );
         sender.sendMessage(
            ChatColor.translate(
               "&eTotal Kicks (Last 7 days): &f"
                  + executedPunishments.values()
                     .stream()
                     .filter(it -> it.getType() == cc.fyre.neutron.profile.attributes.punishment.impl.Punishment.Type.KICK)
                     .count()
            )
         );
         sender.sendMessage("");
      }
   }
}
