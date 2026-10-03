package net.frozenorb.foxtrot.gameplay.boosters;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ClassUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.parameter.BoosterParameter;
import net.frozenorb.foxtrot.gameplay.boosters.service.BoosterService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class NetworkBoosterHandler implements Listener {
   private Foxtrot instance;
   private List<Booster> boostersTypes = new ArrayList<>();
   private Map<UUID, List<Booster>> boostersQueued = new HashMap<>();
   private Map<UUID, Map<Booster, Integer>> boosterBalances = new HashMap<>();
   private File file;
   private FileConfiguration data;

   public NetworkBoosterHandler(Foxtrot instance) {
      this.instance = instance;

      for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.gameplay.boosters.type")) {
         if (Booster.class.isAssignableFrom(clazz)) {
            try {
               this.boostersTypes.add((Booster)clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
               ex.printStackTrace();
            }
         }
      }

      Proton.getInstance().getCommandHandler().registerParameterType(Booster.class, new BoosterParameter());
      this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
      new BoosterService(this.instance, this).runTaskTimer(this.instance, 20L, 20L);
      this.loadBoosters();
   }

   public void loadBoosters() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/boosters.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         if (this.data.get("activeBoosters") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
               for (String boosterName : this.data.getConfigurationSection("activeBoosters").getKeys(false)) {
                  Booster booster = this.findBooster(boosterName);
                  UUID activatedBy = UUID.fromString(this.data.getString("activeBoosters." + boosterName + ".activatedBy"));
                  long activatedAt = this.data.getLong("activeBoosters." + boosterName + ".activatedAt");
                  booster.activate(activatedBy);
                  booster.setActivatedAt(activatedAt);
               }
            }, 5L);
         }

         if (this.data.get("boostersQueued") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
               for (String playerID : this.data.getConfigurationSection("boostersQueued").getKeys(false)) {
                  UUID uuid = UUID.fromString(playerID);
                  List<Booster> boosters = new ArrayList<>();

                  for (String boosterName : this.data.getConfigurationSection("boostersQueued." + playerID).getKeys(false)) {
                     Booster booster = this.findBooster(boosterName);
                     if (booster == null) {
                        System.out.println("***WARNING*** " + uuid + " has an invalid booster. What is " + boosterName + "?");
                     } else {
                        boosters.add(booster);
                     }
                  }

                  this.boostersQueued.put(uuid, boosters);
               }
            }, 5L);
         }

         if (this.data.get("boosterBalance") != null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
               for (String playerID : this.data.getConfigurationSection("boosterBalance").getKeys(false)) {
                  UUID uuid = UUID.fromString(playerID);
                  Map<Booster, Integer> boosterMap = new HashMap<>();

                  for (String boosterName : this.data.getConfigurationSection("boosterBalance." + playerID).getKeys(false)) {
                     Booster booster = this.findBooster(boosterName);
                     if (booster == null) {
                        System.out.println("***WARNING*** " + uuid + " has an invalid booster. What is " + boosterName + "?");
                     } else {
                        boosterMap.put(booster, this.data.getInt("boosterBalance." + playerID + "." + boosterName));
                     }
                  }

                  this.boosterBalances.put(uuid, boosterMap);
               }
            }, 5L);
         }
      }
   }

   public void saveBoosters() {
      this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

      for (Entry<UUID, Map<Booster, Integer>> mainEntry : this.boosterBalances.entrySet()) {
         for (Entry<Booster, Integer> entry : mainEntry.getValue().entrySet()) {
            this.data.set("boosterBalance." + mainEntry.getKey().toString() + "." + entry.getKey().getId(), entry.getValue());
         }
      }

      for (Booster booster : this.getActiveBoosters().keySet()) {
         this.data.set("activeBoosters." + booster.getId() + ".activatedBy", booster.getActivatedBy().toString());
         this.data.set("activeBoosters." + booster.getId() + ".activatedAt", booster.getActivatedAt());
      }

      for (Entry<UUID, List<Booster>> entry : this.boostersQueued.entrySet()) {
         for (Booster booster : entry.getValue()) {
            this.data.set("boostersQueued." + entry.getKey() + "." + booster.getId(), 1);
         }
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public Map<Booster, UUID> getActiveBoosters() {
      Map<Booster, UUID> toReturn = new HashMap<>();

      for (Booster boostersType : this.boostersTypes) {
         if (boostersType.isActive() && boostersType.getActivatedBy() != null) {
            toReturn.put(boostersType, boostersType.getActivatedBy());
         }
      }

      return toReturn;
   }

   public boolean isFrenzy() {
      return this.getActiveBoosters().containsKey(this.findBooster("Frenzy"));
   }

   public boolean isDoublePoints() {
      return this.getActiveBoosters().containsKey(this.findBooster("2xPoints"));
   }

   public boolean isReducedEnderpearl() {
      return this.getActiveBoosters().containsKey(this.findBooster("ReducedEnderpearl"));
   }

   public Booster findBooster(String id) {
      return this.boostersTypes.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   public List<Booster> getBoostersTypes() {
      return this.boostersTypes;
   }

   public Map<UUID, List<Booster>> getBoostersQueued() {
      return this.boostersQueued;
   }

   public void setBoostersQueued(Map<UUID, List<Booster>> boostersQueued) {
      this.boostersQueued = boostersQueued;
   }

   public Map<UUID, Map<Booster, Integer>> getBoosterBalances() {
      return this.boosterBalances;
   }

   public void setBoosterBalances(Map<UUID, Map<Booster, Integer>> boosterBalances) {
      this.boosterBalances = boosterBalances;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }
}
