package net.frozenorb.foxtrot.gameplay.kitmap.kits;

import cc.fyre.proton.Proton;
import cc.fyre.proton.serialization.PlayerInventorySerializer.PlayerInventoryWrapper;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.SortedList;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.collect.Maps;
import net.minecraft.util.com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitManager {
   private static File DEFAULT_KITS_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/default-kits.json");
   private static Type DEFAULT_KITS_TYPE = (new TypeToken<List<DefaultKit>>() {}).getType();
   private static File USER_KITS_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/user-kits.json");
   private static Type USER_KITS_TYPE = (new TypeToken<Map<UUID, List<Kit>>>() {}).getType();
   private List<DefaultKit> defaultKits = new SortedList<>(Comparator.comparingInt(DefaultKit::getOrder));
   private Map<UUID, List<Kit>> userKits = Maps.newHashMap();
   private final Map<UUID, PlayerInventoryWrapper> storedStates = new HashMap<>();

   public KitManager() {
      this.loadDefaultKits();
      this.loadUserKits();
      Foxtrot.getInstance().getLogger().info("- Kit Manager - Loaded " + this.defaultKits.size() + " default kits!");
      Foxtrot.getInstance().getLogger().info("- Kit Manager - Loaded " + this.userKits.size() + " user kits!");
      Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
         Proton.getInstance().getCommandHandler().registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.gameplay.kitmap.kits.command");
         Proton.getInstance().getCommandHandler().registerParameterType(DefaultKit.class, new DefaultKit.Type());
      }, 5L);
      Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
         this.saveDefaultKits();
         this.saveUserKits();
      }, 2400L, 2400L);
      Bukkit.getPluginManager().registerEvents(new KitListener(), Foxtrot.getInstance());
   }

   private void loadDefaultKits() {
      if (DEFAULT_KITS_FILE.exists()) {
         try {
            BufferedReader reader = Files.newReader(DEFAULT_KITS_FILE, Charsets.UTF_8);

            try {
               this.defaultKits = Proton.PLAIN_GSON.fromJson(reader, DEFAULT_KITS_TYPE);
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
            e.printStackTrace();
            Foxtrot.getInstance().getLogger().severe(ChatColor.YELLOW + "Failed to import default-kits.json!");
         }
      }
   }

   public void saveDefaultKits() {
      try {
         Files.write(Proton.PLAIN_GSON.toJson(this.defaultKits, DEFAULT_KITS_TYPE), DEFAULT_KITS_FILE, Charsets.UTF_8);
      } catch (IOException e) {
         e.printStackTrace();
         Foxtrot.getInstance().getLogger().severe(ChatColor.YELLOW + "Failed to export default-kits.json!");
      }
   }

   private void loadUserKits() {
      if (USER_KITS_FILE.exists()) {
         try {
            BufferedReader reader = Files.newReader(USER_KITS_FILE, Charsets.UTF_8);

            try {
               this.userKits = Proton.PLAIN_GSON.fromJson(reader, USER_KITS_TYPE);
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
            e.printStackTrace();
            Foxtrot.getInstance().getLogger().severe(ChatColor.YELLOW + "Failed to import user-kits.json!");
         }
      }
   }

   private void saveUserKits() {
      try {
         Files.write(Proton.PLAIN_GSON.toJson(this.userKits, USER_KITS_TYPE), USER_KITS_FILE, Charsets.UTF_8);
      } catch (IOException e) {
         e.printStackTrace();
         Foxtrot.getInstance().getLogger().severe(ChatColor.YELLOW + "Failed to export user-kits.json!");
      }
   }

   public Kit getUserKit(UUID player, Kit kit) {
      if (kit == null) {
         return null;
      }

      if (this.userKits.containsKey(player)) {
         for (Kit userKit : this.userKits.get(player)) {
            if (userKit.getOriginal() == kit) {
               return userKit;
            }
         }
      }

      return null;
   }

   public void trackUserKit(UUID player, Kit kit) {
      if (kit != null) {
         if (!this.userKits.containsKey(player)) {
            this.userKits.put(player, new ArrayList<>());
         }

         this.userKits.get(player).add(kit);
      }
   }

   public void deleteUserKit(UUID player, Kit kit) {
      if (kit != null) {
         if (this.userKits.containsKey(player)) {
            this.userKits.get(player).remove(kit);
         }
      }
   }

   public DefaultKit getDefaultKit(String name) {
      for (DefaultKit kit : this.defaultKits) {
         if (kit.getName().equalsIgnoreCase(name)) {
            return kit;
         }
      }

      return null;
   }

   public Kit getOrCreateDefaultKit(String name) {
      for (Kit kit : this.defaultKits) {
         if (kit.getName().equalsIgnoreCase(name)) {
            return kit;
         }
      }

      DefaultKit kit = new DefaultKit(name);
      this.defaultKits.add(kit);
      return kit;
   }

   public void deleteDefaultKit(DefaultKit kit) {
      this.defaultKits.remove(kit);
   }

   public boolean awaitingRestore(Player player) {
      return this.storedStates.containsKey(player.getUniqueId());
   }

   public void saveState(Player player) {
      this.storedStates.put(player.getUniqueId(), new PlayerInventoryWrapper(player));
   }

   public void restoreState(Player player) {
      if (this.storedStates.containsKey(player.getUniqueId())) {
         this.storedStates.get(player.getUniqueId()).apply(player);
         this.storedStates.remove(player.getUniqueId());
      }
   }

   public List<DefaultKit> getDefaultKits() {
      return this.defaultKits;
   }

   public Map<UUID, PlayerInventoryWrapper> getStoredStates() {
      return this.storedStates;
   }
}
