package net.frozenorb.foxtrot.protocol;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

public final class AnvilRenamePacketAdapter extends PacketAdapter {
   public AnvilRenamePacketAdapter(Plugin plugin) {
      super(plugin, ListenerPriority.HIGHEST, Client.CUSTOM_PAYLOAD);
   }

   @Override
   public void onPacketReceiving(PacketEvent event) {
      if (!event.isCancelled()) {
         PacketContainer packet = event.getPacket();
         if ("MC|ItemName".equals(packet.getStrings().read(0))) {
            byte[] data = packet.getByteArrays().read(0);
            if (data == null || data.length <= 0) {
               if (packet.getIntegers().read(0) != 0) {
                  event.setCancelled(true);
               } else {
                  PacketContainer normalized = packet.shallowClone();
                  normalized.getByteArrays().write(0, new byte[]{0});
                  normalized.getIntegers().write(0, 1);
                  event.setPacket(normalized);
               }
            }
         }
      }
   }
}
