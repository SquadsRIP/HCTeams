package net.frozenorb.foxtrot.antixray;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R4.PacketPlayOutMapChunk;
import org.bukkit.plugin.Plugin;

public final class AntiXrayChunkAdapter extends PacketAdapter {
   private static final Field INCLUDED_SECTIONS = field("c");
   private static final Field OBFUSCATION_SECTIONS = field("mask");

   public AntiXrayChunkAdapter(Plugin plugin) {
      super(plugin, ListenerPriority.HIGHEST, Server.MAP_CHUNK);
   }

   @Override
   public void onPacketSending(PacketEvent event) {
      if (!event.isCancelled()) {
         correctSectionMask((PacketPlayOutMapChunk)event.getPacket().getHandle());
      }
   }

   static void correctSectionMask(PacketPlayOutMapChunk packet) {
      try {
         OBFUSCATION_SECTIONS.setInt(packet, INCLUDED_SECTIONS.getInt(packet));
      } catch (IllegalAccessException exception) {
         throw new IllegalStateException("Cannot correct vSpigot anti-xray section mask", exception);
      }
   }

   private static Field field(String name) {
      try {
         Field field = PacketPlayOutMapChunk.class.getDeclaredField(name);
         field.setAccessible(true);
         return field;
      } catch (ReflectiveOperationException exception) {
         throw new IllegalStateException("Anti-xray requires the supplied vSpigot v1_7_R4 build", exception);
      }
   }
}
