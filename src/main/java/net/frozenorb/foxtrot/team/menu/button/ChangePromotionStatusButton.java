package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.ConfirmMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ChangePromotionStatusButton extends Button {
   @NonNull
   private UUID uuid;
   @NonNull
   private Team team;
   @NonNull
   private boolean promote;

   @Override
   public String getName(Player player) {
      return this.promote ? "§aPromote §e" + UUIDUtils.name(this.uuid) : "§cDemote §e" + UUIDUtils.name(this.uuid);
   }

   @Override
   public List<String> getDescription(Player player) {
      ArrayList<String> lore = new ArrayList<>();
      lore.add(
         this.promote
            ? "§eClick to promote §b" + UUIDUtils.name(this.uuid) + "§e to captain"
            : "§eClick to demote §b" + UUIDUtils.name(this.uuid) + "§e to member"
      );
      return lore;
   }

   @Override
   public byte getDamageValue(Player player) {
      return 3;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.SKULL_ITEM;
   }

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      if (this.promote) {
         String newRank;
         if (this.team.isCaptain(this.uuid)) {
            newRank = "co-leader";
         } else {
            newRank = "captain";
         }

         new ConfirmMenu("Make " + UUIDUtils.name(this.uuid) + " " + newRank + "?", b -> {
            if (b) {
               if (this.team.isCaptain(this.uuid)) {
                  this.team.removeCaptain(this.uuid);
                  this.team.addCoLeader(this.uuid);
               } else {
                  this.team.addCaptain(this.uuid);
               }

               Player bukkitPlayer = Bukkit.getPlayer(this.uuid);
               if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                  bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you a §a" + newRank + " §eof your faction.");
               }

               player.sendMessage(ChatColor.YELLOW + UUIDUtils.name(this.uuid) + " has been made a " + newRank + " of " + this.team.getName() + ".");
            }
         }).openMenu(player);
      } else {
         new ConfirmMenu("Make " + UUIDUtils.name(this.uuid) + " member?", b -> {
            if (b) {
               this.team.removeCaptain(this.uuid);
               this.team.removeCoLeader(this.uuid);
               Player bukkitPlayer = Bukkit.getPlayer(this.uuid);
               if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                  bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you a §bmember §eof your team.");
               }

               player.sendMessage(ChatColor.YELLOW + UUIDUtils.name(this.uuid) + " has been made a member of " + this.team.getName() + ".");
            }
         }).openMenu(player);
      }
   }

   @ConstructorProperties({"uuid", "team", "promote"})
   public ChangePromotionStatusButton(@NonNull UUID uuid, @NonNull Team team, @NonNull boolean promote) {
      if (uuid == null) {
         throw new NullPointerException("uuid");
      }

      if (team == null) {
         throw new NullPointerException("team");
      }

      this.uuid = uuid;
      this.team = team;
      this.promote = promote;
   }
}
