package net.frozenorb.foxtrot.gameplay.kitmap.game;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import org.bukkit.Location;

public class GameHandlerConfig {
   private Location lobbySpawn;
   private final List<GameArena> arenas = new ArrayList<>();

   public GameArena getArenaByName(String name) {
      for (GameArena arena : this.arenas) {
         if (arena.getName().equalsIgnoreCase(name)) {
            return arena;
         }
      }

      return null;
   }

   public void setLobbySpawnLocation(Location location) {
      this.lobbySpawn = location;
      Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
   }

   public void trackArena(GameArena arena) {
      this.arenas.add(arena);
      Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
   }

   public void forgetArena(GameArena arena) {
      this.arenas.remove(arena);
      Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
   }

   public Location getLobbySpawn() {
      return this.lobbySpawn;
   }

   public List<GameArena> getArenas() {
      return this.arenas;
   }
}
