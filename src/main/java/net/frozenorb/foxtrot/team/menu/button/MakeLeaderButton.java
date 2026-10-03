package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.ForceLeaderCommand;
import net.frozenorb.foxtrot.team.menu.ConfirmMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class MakeLeaderButton extends Button {
   @NonNull
   private UUID uuid;
   @NonNull
   private Team team;

   @Override
   public String getName(Player player) {
      return (this.team.isOwner(this.uuid) ? "§a§l" : "§7") + UUIDUtils.name(this.uuid);
   }

   @Override
   public List<String> getDescription(Player player) {
      ArrayList<String> lore = new ArrayList<>();
      if (this.team.isOwner(this.uuid)) {
         lore.add("§aThis player is already the leader!");
      } else {
         lore.add("§eClick to change §b" + this.team.getName() + "§b's§e leader");
         lore.add("§eto §6" + UUIDUtils.name(this.uuid));
      }

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
      if (this.team.isOwner(this.uuid)) {
         player.sendMessage(ChatColor.RED + "That player is already the leader!");
      } else {
         new ConfirmMenu("Make " + UUIDUtils.name(this.uuid) + " leader?", b -> {
            if (b) {
               ForceLeaderCommand.forceLeader(player, this.uuid);
            }
         }).openMenu(player);
      }
   }

   @ConstructorProperties({"uuid", "team"})
   public MakeLeaderButton(@NonNull UUID uuid, @NonNull Team team) {
      if (uuid == null) {
         throw new NullPointerException("uuid");
      }

      if (team == null) {
         throw new NullPointerException("team");
      }

      this.uuid = uuid;
      this.team = team;
   }
}
