package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PointBreakDownMenu extends Menu {
   private Team team;
   private int kills;
   private int deaths;
   private int kothCaptures;
   private int citadelCaptures;
   private int conquestCaptures;
   private int doublePoints;
   private int addedPoints;
   private int removedPoints;

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(
         0,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.DIAMOND_SWORD)
                  .name(
                     ChatColor.GREEN + ChatColor.BOLD.toString() + "Kills" + ChatColor.GRAY + " [" + PointBreakDownMenu.this.kills + ChatColor.GRAY + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "", CC.translate(PointBreakDownMenu.this.team.getName(player) + " &7has &f" + PointBreakDownMenu.this.kills + " &7kills at this time.")
                     )
                  )
                  .build();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
               player.closeInventory();

               for (UUID uuid : PointBreakDownMenu.this.team.getMembers()) {
                  DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");

                  for (DBObject object : mongoCollection.find(
                     new BasicDBObject("killerUUID", uuid.toString()), new DBCollectionFindOptions().sort(new BasicDBObject("when", -1))
                  )) {
                     StringBuilder message = new StringBuilder();
                     message.append(ChatColor.RED)
                        .append(Proton.getInstance().getUuidCache().name(PointBreakDownMenu.UUIDfromString(object.get("uuid").toString())));
                     if (object.get("killerUUID") != null) {
                        message.append(ChatColor.YELLOW + " was slain by " + ChatColor.RED).append(Proton.getInstance().getUuidCache().name(uuid));
                     } else if (object.get("reason") != null) {
                        message.append(ChatColor.YELLOW + " died to ").append(object.get("reason").toString().toLowerCase()).append(" damage.");
                     } else {
                        message.append(ChatColor.YELLOW + " died to an unknown reason.");
                     }

                     player.sendMessage(message.toString());
                  }
               }
            }
         }
      );
      toReturn.put(
         1,
         new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.SKULL_ITEM)
                  .name(
                     ChatColor.DARK_RED
                        + ChatColor.BOLD.toString()
                        + "Deaths"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.deaths
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(PointBreakDownMenu.this.team.getName(player) + " &7has &f" + PointBreakDownMenu.this.deaths + " &7deaths at this time.")
                     )
                  )
                  .build();
            }

            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
               player.closeInventory();

               for (UUID uuid : PointBreakDownMenu.this.team.getMembers()) {
                  DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");

                  for (DBObject object : mongoCollection.find(
                     new BasicDBObject("uuid", uuid.toString().replace("-", "")), new DBCollectionFindOptions().sort(new BasicDBObject("when", -1))
                  )) {
                     StringBuilder message = new StringBuilder();
                     message.append(ChatColor.RED).append(Proton.getInstance().getUuidCache().name(uuid));
                     if (object.get("killerUUID") != null) {
                        message.append(ChatColor.YELLOW + " was slain by " + ChatColor.RED)
                           .append(Proton.getInstance().getUuidCache().name(UUID.fromString(object.get("killerUUID").toString())));
                     } else if (object.get("reason") != null) {
                        message.append(ChatColor.YELLOW + " died to ").append(object.get("reason").toString().toLowerCase()).append(" damage.");
                     } else {
                        message.append(ChatColor.YELLOW + " died to an unknown reason.");
                     }

                     player.sendMessage(message.toString());
                  }
               }
            }
         }
      );
      toReturn.put(
         2,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.GOLD_NUGGET)
                  .name(
                     ChatColor.GOLD
                        + ChatColor.BOLD.toString()
                        + "KOTH Points"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.kothCaptures
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(
                           PointBreakDownMenu.this.team.getName(player)
                              + " &7has &f"
                              + PointBreakDownMenu.this.team.getKothCaptures()
                              + " &7KOTH captures at this time."
                        )
                     )
                  )
                  .build();
            }
         }
      );
      toReturn.put(
         3,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.EYE_OF_ENDER)
                  .name(
                     ChatColor.DARK_PURPLE
                        + ChatColor.BOLD.toString()
                        + "Citadel Points"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.citadelCaptures
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(
                           PointBreakDownMenu.this.team.getName(player)
                              + " &7has &f"
                              + PointBreakDownMenu.this.team.getCitadelsCapped()
                              + " &7Citadel captures at this time."
                        )
                     )
                  )
                  .build();
            }
         }
      );
      toReturn.put(
         4,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.DIAMOND)
                  .name(
                     ChatColor.BLUE
                        + ChatColor.BOLD.toString()
                        + "Conquest Points"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.conquestCaptures
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(
                           PointBreakDownMenu.this.team.getName(player)
                              + " &7has &f"
                              + PointBreakDownMenu.this.team.getConquestsCapped()
                              + " &7Conquest captures at this time."
                        )
                     )
                  )
                  .build();
            }
         }
      );
      toReturn.put(
         5,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.NETHER_STAR)
                  .name(
                     ChatColor.LIGHT_PURPLE
                        + ChatColor.BOLD.toString()
                        + "Double Points"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.doublePoints
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(
                           PointBreakDownMenu.this.team.getName(player)
                              + " &7has had &f"
                              + PointBreakDownMenu.this.doublePoints
                              + " &7points added on Double Point Thursdays."
                        )
                     )
                  )
                  .build();
            }
         }
      );
      toReturn.put(
         6,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.EMERALD)
                  .name(
                     ChatColor.DARK_GREEN
                        + ChatColor.BOLD.toString()
                        + "Extra Added Points"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.addedPoints
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(
                           PointBreakDownMenu.this.team.getName(player) + " &7has had &f" + PointBreakDownMenu.this.addedPoints + " &7points added by staff."
                        )
                     )
                  )
                  .build();
            }
         }
      );
      toReturn.put(
         7,
         new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.TNT)
                  .name(
                     ChatColor.RED
                        + ChatColor.BOLD.toString()
                        + "Extra Removed Points"
                        + ChatColor.GRAY
                        + " ["
                        + PointBreakDownMenu.this.removedPoints
                        + ChatColor.GRAY
                        + " pts]"
                  )
                  .setLore(
                     Arrays.asList(
                        "",
                        CC.translate(
                           PointBreakDownMenu.this.team.getName(player)
                              + " &7has had &f"
                              + PointBreakDownMenu.this.removedPoints
                              + " &7points removed by staff/raidable."
                        )
                     )
                  )
                  .build();
            }
         }
      );
      return toReturn;
   }

   @Override
   public String getTitle(Player player) {
      return ChatColor.GRAY + "Point Breakdown";
   }

   private static UUID UUIDfromString(String string) {
      return UUID.fromString(string.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
   }

   @ConstructorProperties({"team", "kills", "deaths", "kothCaptures", "citadelCaptures", "conquestCaptures", "doublePoints", "addedPoints", "removedPoints"})
   public PointBreakDownMenu(
      Team team, int kills, int deaths, int kothCaptures, int citadelCaptures, int conquestCaptures, int doublePoints, int addedPoints, int removedPoints
   ) {
      this.team = team;
      this.kills = kills;
      this.deaths = deaths;
      this.kothCaptures = kothCaptures;
      this.citadelCaptures = citadelCaptures;
      this.conquestCaptures = conquestCaptures;
      this.doublePoints = doublePoints;
      this.addedPoints = addedPoints;
      this.removedPoints = removedPoints;
   }
}
