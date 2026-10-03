package net.frozenorb.foxtrot.gameplay.ability.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.Invisibility;
import net.minecraft.server.v1_7_R4.ItemArmor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class InvisibilityPacketAdapter extends PacketAdapter {
   public InvisibilityPacketAdapter() {
      super(Foxtrot.getInstance(), Server.ENTITY_EQUIPMENT);
   }

   @Override
   public void onPacketSending(PacketEvent event) {
      Integer id = event.getPacket().getIntegers().read(0);
      ItemStack itemStack = event.getPacket().getItemModifier().read(0);
      if (itemStack != null && itemStack.getType() != Material.AIR) {
         if (CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor) {
            Foxtrot.getInstance()
               .getServer()
               .getOnlinePlayers()
               .stream()
               .filter(it -> it.getEntityId() == id)
               .findFirst()
               .ifPresent(
                  it -> {
                     if (!it.getActivePotionEffects()
                        .stream()
                        .noneMatch(
                           effect -> effect.getType().equals(Invisibility.EFFECT.getType()) && effect.getAmplifier() == Invisibility.EFFECT.getAmplifier()
                        )) {
                        event.setCancelled(true);
                     }
                  }
               );
         }
      }
   }
}
