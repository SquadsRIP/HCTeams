package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.ForceKickCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class KickPlayerButton extends Button {
   @NonNull
   private UUID uuid;
   @NonNull
   private Team team;

   @Override
   public String getName(Player player) {
      return "§cKick §e" + UUIDUtils.name(this.uuid);
   }

   @Override
   public List<String> getDescription(Player player) {
      ArrayList<String> lore = new ArrayList<>();
      if (this.team.isOwner(this.uuid)) {
         lore.add("§e§lLeader");
      } else if (this.team.isCoLeader(this.uuid)) {
         lore.add("§e§lCo-Leader");
      } else if (this.team.isCaptain(this.uuid)) {
         lore.add("§aCaptain");
      } else {
         lore.add("§7Member");
      }

      lore.add("");
      lore.add("§eClick to kick §b" + UUIDUtils.name(this.uuid) + "§e from team.");
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
      ForceKickCommand.forceKick(player, this.uuid);
   }

   @ConstructorProperties({"uuid", "team"})
   public KickPlayerButton(@NonNull UUID uuid, @NonNull Team team) {
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
