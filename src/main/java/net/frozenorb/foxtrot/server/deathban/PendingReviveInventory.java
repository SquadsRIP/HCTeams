package net.frozenorb.foxtrot.server.deathban;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;

public final class PendingReviveInventory {
   private PendingReviveInventory() {
   }

   private static Path marker(UUID var0) {
      return Foxtrot.getInstance().getDataFolder().toPath().resolve("pending-revive-inventory").resolve(var0.toString());
   }

   public static void beforeRevive(UUID var0) {
      Foxtrot var1 = Foxtrot.getInstance();
      if (!var1.getMapHandler().isKitMap()) {
         if (var1.getServer().getPlayer(var0) == null && var1.getDeathbanMap().isDeathbanned(var0)) {
            Path var2 = marker(var0);

            try {
               Files.createDirectories(var2.getParent());

               try {
                  Files.createFile(var2);
               } catch (FileAlreadyExistsException var4) {
                  if (!Files.isRegularFile(var2)) {
                     throw var4;
                  }
               }
            } catch (IOException var5) {
               throw new IllegalStateException("Cannot record pending revive inventory cleanup for " + var0, var5);
            }
         }
      }
   }

   public static void onJoin(Player var0) {
      UUID var1 = var0.getUniqueId();
      if (Files.isRegularFile(marker(var1))) {
         Foxtrot var2 = Foxtrot.getInstance();
         var2.getServer()
            .getScheduler()
            .runTask(
               var2,
               () -> {
                  if (var0.isOnline()
                     && var2.getServer().getPlayer(var1) == var0
                     && Files.isRegularFile(marker(var1))
                     && !var2.getDeathbanMap().isDeathbanned(var1)) {
                     var2.getDeathbanArenaHandler().revive(var1);
                  }
               }
            );
      }
   }

   public static void complete(Player var0) {
      if (var0.isOnline() && Foxtrot.getInstance().getServer().getPlayer(var0.getUniqueId()) == var0) {
         Path var1 = marker(var0.getUniqueId());
         if (Files.isRegularFile(var1)) {
            Path var2 = var1.resolveSibling(var1.getFileName().toString() + ".completed");

            try {
               var0.saveData();
               Files.move(var1, var2, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException | RuntimeException var5) {
               var0.kickPlayer("Your revive could not be saved safely. Please contact staff before rejoining.");
               throw new IllegalStateException("Cannot consume pending revive inventory cleanup for " + var0.getUniqueId(), var5);
            }

            try {
               Files.deleteIfExists(var2);
            } catch (IOException var4) {
            }
         }
      }
   }
}
