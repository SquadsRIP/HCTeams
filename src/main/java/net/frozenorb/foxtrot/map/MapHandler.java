package net.frozenorb.foxtrot.map;

import cc.fyre.proton.Proton;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventHandler;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.DuelHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.KillstreakHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.KitManager;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.KitUpgradesHandler;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.nametag.FoxtrotNametagProvider;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreboardConfiguration;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.deathban.Deathban;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class MapHandler {
   private transient File mapInfo;
   private boolean kitMap;
   private boolean halloween;
   private int allyLimit;
   private int teamSize;
   private long regenTimeDeath;
   private long regenTimeRaidable;
   private String scoreboardTitle;
   private String mapStartedString;
   private double baseLootingMultiplier;
   private double level1LootingMultiplier;
   private double level2LootingMultiplier;
   private double level3LootingMultiplier;
   private boolean craftingGopple;
   private boolean craftingReducedMelon;
   private int goppleCooldown;
   private int minForceInviteMembers = 10;
   private String endPortalLocation;
   private boolean fastSmeltEnabled;
   private AbilityHandler abilityHandler;
   private int netherBuffer;
   private GameHandler gameHandler;
   private DuelHandler duelHandler;
   private int worldBuffer;
   private float dtrIncrementMultiplier;
   private StatsHandler statsHandler;
   private KillstreakHandler killstreakHandler;
   private KitManager kitManager;
   private KitUpgradesHandler kitUpgradesHandler;

   public void load() {
      this.reloadConfig();
      Proton.getInstance().getNameTagHandler().registerProvider(new FoxtrotNametagProvider());
      Proton.getInstance().getScoreboardHandler().setConfiguration(FoxtrotScoreboardConfiguration.create());
      if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
         this.abilityHandler = new AbilityHandler(Foxtrot.getInstance());
      }

      Iterator recipeIterator = Foxtrot.getInstance().getServer().recipeIterator();

      while (recipeIterator.hasNext()) {
         Recipe recipe = (Recipe)recipeIterator.next();
         if (!this.craftingGopple && recipe.getResult().getDurability() == 1 && recipe.getResult().getType() == Material.GOLDEN_APPLE) {
            recipeIterator.remove();
         }

         if (this.craftingReducedMelon && recipe.getResult().getType() == Material.SPECKLED_MELON) {
            recipeIterator.remove();
         }

         if (Foxtrot.getInstance().getConfig().getBoolean("rodPrevention") && recipe.getResult().getType() == Material.FISHING_ROD) {
            recipeIterator.remove();
         }
      }

      if (this.craftingReducedMelon) {
         Foxtrot.getInstance()
            .getServer()
            .addRecipe(new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON)).addIngredient(Material.MELON).addIngredient(Material.GOLD_NUGGET));
      }

      ShapedRecipe nametagRecipe = new ShapedRecipe(new ItemStack(Material.NAME_TAG));
      ShapedRecipe saddleRecipe = new ShapedRecipe(new ItemStack(Material.SADDLE));
      ShapedRecipe horseArmorRecipe = new ShapedRecipe(new ItemStack(Material.DIAMOND_BARDING));
      nametagRecipe.shape(" I ", " P ", " S ");
      nametagRecipe.setIngredient('I', Material.INK_SACK);
      nametagRecipe.setIngredient('P', Material.PAPER);
      nametagRecipe.setIngredient('S', Material.STRING);
      saddleRecipe.shape("  L", "LLL", "B B");
      saddleRecipe.setIngredient('L', Material.LEATHER);
      saddleRecipe.setIngredient('B', Material.LEASH);
      horseArmorRecipe.shape(" SD", "BBL", "LL ");
      horseArmorRecipe.setIngredient('S', Material.SADDLE);
      horseArmorRecipe.setIngredient('D', Material.DIAMOND);
      horseArmorRecipe.setIngredient('B', Material.DIAMOND_BLOCK);
      horseArmorRecipe.setIngredient('L', Material.LEATHER);
      Foxtrot.getInstance().getServer().addRecipe(nametagRecipe);
      Foxtrot.getInstance().getServer().addRecipe(saddleRecipe);
      Foxtrot.getInstance().getServer().addRecipe(horseArmorRecipe);
      this.statsHandler = new StatsHandler();
      if (this.isKitMap()) {
         this.killstreakHandler = new KillstreakHandler();
         this.kitManager = new KitManager();
         this.gameHandler = new GameHandler();
         this.duelHandler = new DuelHandler();
         this.kitUpgradesHandler = new KitUpgradesHandler();
         Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            EventHandler eventHandler = Foxtrot.getInstance().getEventHandler();
            ArrayList<Event> localEvents = new ArrayList<>(eventHandler.getEvents());
            if (!localEvents.isEmpty()) {
               if (Foxtrot.getInstance().getServer().getOnlinePlayers().size() < 10) {
                  System.out.println();
                  System.out.println("[HCTeams] Couldn't start a valid event as there was less than 10 players online!");
                  System.out.println();
               } else {
                  ArrayList<KOTH> koths = new ArrayList<>();

                  for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
                     if (otherKoth.isActive()) {
                        return;
                     }

                     if (!otherKoth.getName().contains("Citadel") && !otherKoth.getName().contains("Outpost") && otherKoth.getType() == EventType.KOTH) {
                        koths.add((KOTH)otherKoth);
                     }
                  }

                  KOTH selected = koths.get(ThreadLocalRandom.current().nextInt(koths.size()));
                  selected.activate();
               }
            }
         }, 18000L, 18000L);
         TeamActionTracker.setDatabaseLogEnabled(false);
      }
   }

   public void reloadConfig() {
      try {
         this.mapInfo = new File(Foxtrot.getInstance().getDataFolder(), "mapInfo.json");
         if (!this.mapInfo.exists()) {
            this.mapInfo.createNewFile();
            BasicDBObject dbObject = this.getDefaults();
            FileUtils.write(this.mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString())));
         } else {
            BasicDBObject file = (BasicDBObject)JSON.parse(FileUtils.readFileToString(this.mapInfo));
            BasicDBObject defaults = this.getDefaults();
            defaults.keySet().stream().filter(key -> !file.containsKey(key)).forEach(key -> file.put((String)key, defaults.get(key)));
            FileUtils.write(this.mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(file.toString())));
         }

         BasicDBObject dbObject = (BasicDBObject)JSON.parse(FileUtils.readFileToString(this.mapInfo));
         if (dbObject != null) {
            FileConfiguration config = Foxtrot.getInstance().getConfiguration("map.yml");
            this.kitMap = config.getBoolean("map.kit-map", dbObject.getBoolean("kitMap", false));
            this.halloween = config.getBoolean("map.halloween", dbObject.getBoolean("halloween", false));
            this.allyLimit = config.getInt("map.ally-limit", dbObject.getInt("allyLimit", 0));
            this.teamSize = config.getInt("map.team-size", dbObject.getInt("teamSize", 30));
            this.regenTimeDeath = TimeUnit.MINUTES.toMillis(config.getInt("map.dtr-regen.death-minutes", dbObject.getInt("regenTimeDeath", 60)));
            this.regenTimeRaidable = TimeUnit.MINUTES.toMillis(config.getInt("map.dtr-regen.raidable-minutes", dbObject.getInt("regenTimeRaidable", 60)));
            this.scoreboardTitle = ChatColor.translateAlternateColorCodes(
               '&', Foxtrot.getInstance().getConfiguration("branding.yml").getString("scoreboard.title", "&e&lTeams")
            );
            this.mapStartedString = config.getString("map.started-message", dbObject.getString("mapStartedString"));
            ServerHandler.WARZONE_RADIUS = config.getInt("map.warzone-radius", dbObject.getInt("warzone", 1000));
            BorderListener.BORDER_SIZE = config.getInt("map.border-size", dbObject.getInt("border", 3000));
            this.goppleCooldown = config.getInt("map.gopple-cooldown-minutes", dbObject.getInt("goppleCooldown"));
            this.netherBuffer = config.getInt("map.nether-buffer", dbObject.getInt("netherBuffer"));
            this.worldBuffer = config.getInt("map.world-buffer", dbObject.getInt("worldBuffer"));
            this.endPortalLocation = config.getString("map.end-portal-location", dbObject.getString("endPortalLocation"));
            this.fastSmeltEnabled = config.getBoolean("map.fast-smelt", dbObject.getBoolean("fastSmeltEnabled", true));
            BasicDBObject looting = (BasicDBObject)dbObject.get("looting");
            this.baseLootingMultiplier = config.getDouble("map.looting.base", looting.getDouble("base"));
            this.level1LootingMultiplier = config.getDouble("map.looting.level-1", looting.getDouble("level1"));
            this.level2LootingMultiplier = config.getDouble("map.looting.level-2", looting.getDouble("level2"));
            this.level3LootingMultiplier = config.getDouble("map.looting.level-3", looting.getDouble("level3"));
            BasicDBObject crafting = (BasicDBObject)dbObject.get("crafting");
            this.craftingGopple = config.getBoolean("map.crafting.gopple", crafting.getBoolean("gopple"));
            this.craftingReducedMelon = config.getBoolean("map.crafting.reduced-melon", crafting.getBoolean("reducedMelon"));
            if (dbObject.containsKey("deathban")) {
               BasicDBObject deathban = (BasicDBObject)dbObject.get("deathban");
               ConfigurationSection configuredDeathbans = config.getConfigurationSection("map.deathban-minutes");
               if (configuredDeathbans != null) {
                  for (String rank : configuredDeathbans.getKeys(false)) {
                     deathban.put(rank.toUpperCase(), configuredDeathbans.getInt(rank));
                  }
               }

               Deathban.load(deathban);
            }

            this.minForceInviteMembers = config.getInt("map.minimum-force-invite-members", dbObject.getInt("minForceInviteMembers", 10));
            this.dtrIncrementMultiplier = (float)config.getDouble("map.dtr-increment-multiplier", dbObject.getDouble("dtrIncrementMultiplier", 4.5));
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private BasicDBObject getDefaults() {
      BasicDBObject dbObject = new BasicDBObject();
      BasicDBObject looting = new BasicDBObject();
      BasicDBObject crafting = new BasicDBObject();
      BasicDBObject deathban = new BasicDBObject();
      dbObject.put("kitMap", false);
      dbObject.put("allyLimit", 0);
      dbObject.put("teamSize", 30);
      dbObject.put("regenTimeDeath", 60);
      dbObject.put("regenTimeRaidable", 60);
      dbObject.put("scoreboardTitle", "&e&lTeams");
      dbObject.put("mapStartedString", "Map 3 - Started January 31, 2015");
      dbObject.put("warzone", 1000);
      dbObject.put("netherBuffer", 150);
      dbObject.put("worldBuffer", 300);
      dbObject.put("endPortalLocation", "2500, 2500");
      dbObject.put("border", 3000);
      dbObject.put("goppleCooldown", TimeUnit.HOURS.toMinutes(4L));
      dbObject.put("fastSmeltEnabled", true);
      looting.put("base", 1.0);
      looting.put("level1", 1.2);
      looting.put("level2", 1.4);
      looting.put("level3", 2.0);
      dbObject.put("looting", looting);
      crafting.put("gopple", true);
      crafting.put("reducedMelon", true);
      dbObject.put("crafting", crafting);
      deathban.put("HIGHROLLER", 45);
      deathban.put("EPIC", 45);
      deathban.put("PRO", 90);
      deathban.put("VIP", 120);
      deathban.put("DEFAULT", 240);
      dbObject.put("deathban", deathban);
      dbObject.put("minForceInviteMembers", 10);
      dbObject.put("dtrIncrementMultiplier", 4.5F);
      return dbObject;
   }

   public void reloadRuntimeSettings() {
      boolean previousKitMap = this.kitMap;
      boolean previousCraftingGopple = this.craftingGopple;
      boolean previousCraftingReducedMelon = this.craftingReducedMelon;
      this.reloadConfig();
      if (this.kitMap != previousKitMap) {
         this.kitMap = previousKitMap;
         Foxtrot.getInstance().getLogger().warning("map.kit-map is startup-only; keeping the current value until restart.");
      }

      if (this.craftingGopple != previousCraftingGopple || this.craftingReducedMelon != previousCraftingReducedMelon) {
         this.craftingGopple = previousCraftingGopple;
         this.craftingReducedMelon = previousCraftingReducedMelon;
         Foxtrot.getInstance().getLogger().warning("Map crafting recipe changes are startup-only; keeping current recipes until restart.");
      }
   }

   public void saveBorder() {
      try {
         BasicDBObject dbObject = (BasicDBObject)JSON.parse(FileUtils.readFileToString(this.mapInfo));
         if (dbObject != null) {
            dbObject.put("border", BorderListener.BORDER_SIZE);
            FileUtils.write(this.mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            Foxtrot.getInstance().getConfiguration("map.yml").set("map.border-size", BorderListener.BORDER_SIZE);
            Foxtrot.getInstance().saveConfiguration("map.yml");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void saveNetherBuffer() {
      try {
         BasicDBObject dbObject = (BasicDBObject)JSON.parse(FileUtils.readFileToString(this.mapInfo));
         if (dbObject != null) {
            dbObject.put("netherBuffer", Foxtrot.getInstance().getMapHandler().getNetherBuffer());
            FileUtils.write(this.mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            Foxtrot.getInstance().getConfiguration("map.yml").set("map.nether-buffer", Foxtrot.getInstance().getMapHandler().getNetherBuffer());
            Foxtrot.getInstance().saveConfiguration("map.yml");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void saveWorldBuffer() {
      try {
         BasicDBObject dbObject = (BasicDBObject)JSON.parse(FileUtils.readFileToString(this.mapInfo));
         if (dbObject != null) {
            dbObject.put("worldBuffer", Foxtrot.getInstance().getMapHandler().getWorldBuffer());
            FileUtils.write(this.mapInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            Foxtrot.getInstance().getConfiguration("map.yml").set("map.world-buffer", Foxtrot.getInstance().getMapHandler().getWorldBuffer());
            Foxtrot.getInstance().saveConfiguration("map.yml");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public boolean isKitMap() {
      return this.kitMap;
   }

   public boolean isHalloween() {
      return this.halloween;
   }

   public int getAllyLimit() {
      return 0;
   }

   public int getTeamSize() {
      return 5;
   }

   public void setTeamSize(int teamSize) {
      this.teamSize = teamSize;
   }

   public long getRegenTimeDeath() {
      return this.regenTimeDeath;
   }

   public long getRegenTimeRaidable() {
      return this.regenTimeRaidable;
   }

   public String getScoreboardTitle() {
      return this.scoreboardTitle;
   }

   public String getMapStartedString() {
      return this.mapStartedString;
   }

   public double getBaseLootingMultiplier() {
      return this.baseLootingMultiplier;
   }

   public double getLevel1LootingMultiplier() {
      return this.level1LootingMultiplier;
   }

   public double getLevel2LootingMultiplier() {
      return this.level2LootingMultiplier;
   }

   public double getLevel3LootingMultiplier() {
      return this.level3LootingMultiplier;
   }

   public boolean isCraftingGopple() {
      return this.craftingGopple;
   }

   public boolean isCraftingReducedMelon() {
      return this.craftingReducedMelon;
   }

   public int getGoppleCooldown() {
      return this.goppleCooldown;
   }

   public int getMinForceInviteMembers() {
      return this.minForceInviteMembers;
   }

   public String getEndPortalLocation() {
      return this.endPortalLocation;
   }

   public boolean isFastSmeltEnabled() {
      return this.fastSmeltEnabled;
   }

   public AbilityHandler getAbilityHandler() {
      return this.abilityHandler;
   }

   public int getNetherBuffer() {
      return this.netherBuffer;
   }

   public void setNetherBuffer(int netherBuffer) {
      this.netherBuffer = netherBuffer;
   }

   public GameHandler getGameHandler() {
      return this.gameHandler;
   }

   public DuelHandler getDuelHandler() {
      return this.duelHandler;
   }

   public int getWorldBuffer() {
      return this.worldBuffer;
   }

   public void setWorldBuffer(int worldBuffer) {
      this.worldBuffer = worldBuffer;
   }

   public float getDtrIncrementMultiplier() {
      return this.dtrIncrementMultiplier;
   }

   public StatsHandler getStatsHandler() {
      return this.statsHandler;
   }

   public KillstreakHandler getKillstreakHandler() {
      return this.killstreakHandler;
   }

   public KitManager getKitManager() {
      return this.kitManager;
   }

   public KitUpgradesHandler getKitUpgradesHandler() {
      return this.kitUpgradesHandler;
   }
}
