package net.frozenorb.foxtrot.server.deaths.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import java.beans.ConstructorProperties;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.serialization.LocationSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DeathBreakDownMenu extends Menu {
   private BasicDBObject object;
   private OfflinePlayer target;
   private DateFormat FORMAT;

   @Override
   public String getTitle(Player player) {
      return "Death Breakdown";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 27; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
      }

      toReturn.put(
         10,
         new Button() {
            final String victimName = UUIDUtils.name(DeathBreakDownMenu.UUIDfromString(DeathBreakDownMenu.this.object.getString("uuid")));

            @Override
            public String getName(Player player) {
               return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Victim Information";
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturnx = new ArrayList<>();
               toReturnx.add(ChatColor.translate("&4&l┃ &fVictim: &c" + this.victimName));
               int ping = DeathBreakDownMenu.this.object.getInt("ping");
               toReturnx.add(ChatColor.translate("&4&l┃ &fConnection: &c" + DeathBreakDownMenu.this.getPingStatus(ping) + " (" + ping + " ms)"));
               toReturnx.add(ChatColor.translate("&4&l┃ &fFaction upon Death: &c" + DeathBreakDownMenu.this.object.getString("playerTeam")));
               Location location = LocationSerializer.deserialize((BasicDBObject)DeathBreakDownMenu.this.object.get("playerLocation"));
               toReturnx.add(
                  ChatColor.translate(
                     "&4&l┃ &fLocation upon Death: &c" + location.getBlockX() + ", " + (location.getBlockY() + 1) + ", " + location.getBlockZ()
                  )
               );
               if (!DeathBreakDownMenu.this.object.getString("playerTeam").startsWith("None Found")) {
                  toReturnx.add(ChatColor.translate("&4&l┃ &fBefore DTR: &f" + DeathBreakDownMenu.this.object.getString("beforeDTR")));
                  toReturnx.add(
                     ChatColor.translate("&4&l┃ &fAfter DTR: &f" + DeathBreakDownMenu.this.formatDTR(DeathBreakDownMenu.this.object.getDouble("afterDTR")))
                  );
               }

               if (DeathBreakDownMenu.this.object.containsKey("lastUsedLong") && DeathBreakDownMenu.this.object.containsKey("lastUsedAbility")) {
                  long systemTime = DeathBreakDownMenu.this.object.getLong("systemTime");
                  long lastUsedAbility = DeathBreakDownMenu.this.object.getLong("lastUsedLong");
                  long difference = systemTime - lastUsedAbility;
                  toReturnx.add(
                     ChatColor.translate(
                        "&4&l┃ &fLast Used Ability: &f"
                           + DeathBreakDownMenu.this.object.getString("lastUsedAbility")
                           + " ("
                           + TimeUtils.formatIntoDetailedString((int)difference / 1000)
                           + " ago)"
                     )
                  );
               }

               toReturnx.add("");
               toReturnx.add(ChatColor.RED + "Click to view the victim's inventory upon death");
               return toReturnx;
            }

            @Override
            public byte getDamageValue(Player player) {
               return 3;
            }

            @Override
            public Material getMaterial(Player player) {
               return Material.SKULL_ITEM;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               ItemStack itemStack = super.getButtonItem(player);
               SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
               skullMeta.setOwner(this.victimName);
               itemStack.setItemMeta(skullMeta);
               return itemStack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               new RestoreInventoryMenu(DeathBreakDownMenu.this.object, DeathBreakDownMenu.this.target, false).openMenu(player);
            }
         }
      );
      toReturn.put(13, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Generic Death Information";
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturn = new ArrayList<>();
            double tps = DeathBreakDownMenu.this.object.getDouble("tps");
            toReturn.add(ChatColor.translate("&4&l┃ &fDate: &c" + DeathBreakDownMenu.this.object.getDate("when").toLocaleString()));
            toReturn.add(ChatColor.translate("&4&l┃ &fServer Lag: &c" + DeathBreakDownMenu.this.getServerLag(tps) + " (" + tps + " TPS)"));
            toReturn.add(ChatColor.translate("&4&l┃ &fDeath Cause: &c" + DeathBreakDownMenu.this.object.getString("reason")));
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.COMMAND;
         }
      });
      toReturn.put(
         16,
         new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Killer Information";
            }

            @Override
            public byte getDamageValue(Player player) {
               return 3;
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturn = new ArrayList<>();
               if (!DeathBreakDownMenu.this.object.containsKey("killerUUID")) {
                  toReturn.add(ChatColor.YELLOW + "No killer found for this death");
                  toReturn.add(ChatColor.GRAY + "Player most likely died to natural causes (rip bozo)");
                  return toReturn;
               }

               toReturn.add(
                  ChatColor.translate(
                     "&4&l┃ &fKiller: &c" + UUIDUtils.name(DeathBreakDownMenu.UUIDfromString(DeathBreakDownMenu.this.object.getString("killerUUID")))
                  )
               );
               int ping = DeathBreakDownMenu.this.object.getInt("killerPing");
               toReturn.add(ChatColor.translate("&4&l┃ &fConnection: &f" + DeathBreakDownMenu.this.getPingStatus(ping) + " (" + ping + " ms)"));
               toReturn.add(ChatColor.translate("&4&l┃ &fHearts Left: &c" + DeathBreakDownMenu.this.object.getInt("healthLeft") / 2 + " hearts"));
               toReturn.add(ChatColor.translate("&4&l┃ &fFaction upon Kill: &c" + DeathBreakDownMenu.this.object.getString("killerTeam")));
               toReturn.add(ChatColor.translate("&4&l┃ &fFaction DTR upon Kill: &c" + DeathBreakDownMenu.this.object.getString("killerDTR")));
               Location location = LocationSerializer.deserialize((BasicDBObject)DeathBreakDownMenu.this.object.get("killerLocation"));
               toReturn.add(
                  ChatColor.translate("&4&l┃ &fLocation upon Kill: &c" + location.getBlockX() + ", " + location.getBlockY() + 1 + ", " + location.getBlockZ())
               );
               if (DeathBreakDownMenu.this.object.containsKey("lastUsedLong") && DeathBreakDownMenu.this.object.containsKey("killerLastUsedAbility")) {
                  long systemTime = DeathBreakDownMenu.this.object.getLong("systemTime");
                  long lastUsedAbility = DeathBreakDownMenu.this.object.getLong("killerLastUsedLong");
                  long difference = systemTime - lastUsedAbility;
                  toReturn.add(
                     ChatColor.translate(
                        "&4&l┃ &fLast Used Ability: &c"
                           + DeathBreakDownMenu.this.object.getString("killerLastUsedAbility")
                           + " ("
                           + TimeUtils.formatIntoDetailedString((int)difference / 1000)
                           + " ago)"
                     )
                  );
               }

               toReturn.add("");
               toReturn.add(ChatColor.RED + "Click to view the killer's inventory upon death");
               return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
               return Material.SKULL_ITEM;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               ItemStack itemStack = super.getButtonItem(player);
               if (!DeathBreakDownMenu.this.object.containsKey("killerUUID")) {
                  return itemStack;
               }

               SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
               skullMeta.setOwner(UUIDUtils.name(DeathBreakDownMenu.UUIDfromString(DeathBreakDownMenu.this.object.getString("killerUUID"))));
               itemStack.setItemMeta(skullMeta);
               return itemStack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               new RestoreInventoryMenu(DeathBreakDownMenu.this.object, DeathBreakDownMenu.this.target, true).openMenu(player);
            }
         }
      );
      return toReturn;
   }

   public String getServerLag(double tps) {
      if (tps >= 19.0) {
         return ChatColor.DARK_GREEN + "None";
      } else if (tps >= 18.0) {
         return ChatColor.YELLOW + "Slight";
      } else if (tps >= 15.0) {
         return ChatColor.GOLD + "Moderate (Revivable)";
      } else {
         return tps >= 10.0 ? ChatColor.RED + "Bad (Revivable)" : ChatColor.DARK_RED + "Very Bad (Revivable)";
      }
   }

   public String getPingStatus(int ping) {
      if (ping <= 30) {
         return ChatColor.DARK_GREEN + "Very Good";
      } else if (ping <= 60) {
         return ChatColor.GREEN + "Good";
      } else if (ping <= 150) {
         return ChatColor.GOLD + "Moderate";
      } else {
         return ping <= 200 ? ChatColor.RED + "Bad" : ChatColor.DARK_RED + "Very Bad";
      }
   }

   public String formatDTR(double DTR) {
      ChatColor dtrColor = ChatColor.GREEN;
      if (DTR / 5.0 <= 0.25) {
         if (DTR <= 0.0) {
            dtrColor = ChatColor.DARK_RED;
         } else {
            dtrColor = ChatColor.YELLOW;
         }
      }

      return dtrColor + "" + DTR;
   }

   private static UUID UUIDfromString(String string) {
      return UUID.fromString(string.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
   }

   @ConstructorProperties({"object", "target", "FORMAT"})
   public DeathBreakDownMenu(BasicDBObject object, OfflinePlayer target, DateFormat FORMAT) {
      this.object = object;
      this.target = target;
      this.FORMAT = FORMAT;
   }
}
