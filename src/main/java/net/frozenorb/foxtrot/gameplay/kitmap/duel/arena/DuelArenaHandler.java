package net.frozenorb.foxtrot.gameplay.kitmap.duel.arena;

import cc.fyre.proton.Proton;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.io.Files;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

public class DuelArenaHandler {
   private List<DuelArena> arenas = new ArrayList<>();
   private static File ARENAS_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/kitmap-duel-arenas.json");

   public DuelArenaHandler() {
      Proton.getInstance().getCommandHandler().registerParameterType(DuelArena.class, new DuelArena.Type());
      if (ARENAS_FILE.exists()) {
         try {
            BufferedReader reader = Files.newReader(ARENAS_FILE, Charsets.UTF_8);

            try {
               Type arenaListType = (new TypeToken<List<DuelArena>>() {}).getType();
               this.arenas = Proton.PLAIN_GSON.fromJson(reader, arenaListType);
            } catch (Throwable var5) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (IOException e) {
            Foxtrot.getInstance().getLogger().severe("Failed to load duel arenas!");
            e.printStackTrace();
         }
      }
   }

   public void saveArenas() {
      try {
         Files.write(Proton.PLAIN_GSON.toJson(this.arenas), ARENAS_FILE, Charsets.UTF_8);
      } catch (IOException e) {
         Foxtrot.getInstance().getLogger().severe("Failed to save duel arenas!");
         e.printStackTrace();
      }
   }

   public DuelArena getArenaByName(String name) {
      return this.arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   public DuelArena getRandomArena() {
      return this.arenas.isEmpty() ? null : this.arenas.get(ThreadLocalRandom.current().nextInt(this.arenas.size()));
   }

   public void addArena(DuelArena arena) {
      this.arenas.add(arena);
      this.saveArenas();
   }

   public void removeArena(DuelArena arena) {
      this.arenas.remove(arena);
      this.saveArenas();
   }

   public List<DuelArena> getArenas() {
      return this.arenas;
   }
}
