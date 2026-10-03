package net.frozenorb.foxtrot.tab;

import cc.fyre.proton.tab.construct.TabLayout;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.bukkit.plugin.Plugin;

final class LegacyTabAdapter extends PacketAdapter {
   private static final int TAB_SIZE = TabLayout.getTAB_LAYOUT_DEFAULT().getTabNames().length;
   private static final Set<String> TAB_ENTRIES = new HashSet<>(Arrays.asList(TabLayout.getTAB_LAYOUT_DEFAULT().getTabNames()));
   private final ProtocolManager protocolManager;
   private final BooleanSupplier active;

   LegacyTabAdapter(Plugin plugin, ProtocolManager protocolManager, BooleanSupplier active) {
      super(plugin, ListenerPriority.HIGHEST, Server.LOGIN, Server.PLAYER_INFO);
      this.protocolManager = protocolManager;
      this.active = active;
   }

   @Override
   public void onPacketSending(PacketEvent event) {
      if (!event.isCancelled() && this.active.getAsBoolean()) {
         int protocol = this.protocolManager.getProtocolVersion(event.getPlayer());
         if (protocol == 4 || protocol == 5) {
            if (event.getPacketType() != Server.PLAYER_INFO) {
               PacketContainer login = event.getPacket().shallowClone();
               login.getIntegers().write(2, TAB_SIZE);
               event.setPacket(login);
            } else {
               PacketContainer packet = event.getPacket();
               String name = packet.getStrings().read(0);
               String entry = name != null && name.startsWith("$") ? name.substring(1) : name;
               if (packet.getIntegers().read(1) != 4 && !TAB_ENTRIES.contains(entry)) {
                  event.setCancelled(true);
               } else if (name != null && name.startsWith("$") && TAB_ENTRIES.contains(entry)) {
                  PacketContainer clean = packet.shallowClone();
                  clean.getStrings().write(0, entry);
                  event.setPacket(clean);
               }
            }
         }
      }
   }
}
