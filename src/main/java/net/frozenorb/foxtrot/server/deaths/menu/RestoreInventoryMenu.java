package net.frozenorb.foxtrot.server.deaths.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RestoreInventoryMenu extends Menu {
   private BasicDBObject basicDBObject;
   private OfflinePlayer target;
   private boolean killer;

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      ItemStack[] contents = Proton.GSON
         .fromJson(
            JSON.serialize(((BasicDBObject)this.basicDBObject.get(this.killer ? "killerInventory" : "playerInventory")).get("contents")), ItemStack[].class
         );
      ItemStack[] armorContents = Proton.GSON
         .fromJson(JSON.serialize(((BasicDBObject)this.basicDBObject.get(this.killer ? "killerInventory" : "playerInventory")).get("armor")), ItemStack[].class);
      int i = 0;

      for (final ItemStack itemStack : contents) {
         if (itemStack != null && itemStack.getType() != null) {
            toReturn.put(i, new Button() {
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
                  return itemStack;
               }
            });
            i++;
         }
      }

      int j = 36;

      for (final ItemStack itemStack : armorContents) {
         if (itemStack != null && itemStack.getType() != null) {
            toReturn.put(j, new Button() {
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
                  return itemStack;
               }
            });
            j++;
         }
      }

      for (int k = 40; k < 53; k++) {
         toReturn.put(k, new Button() {
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
               return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
            }
         });
      }

      if (this.killer) {
         return toReturn;
      }

      final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(this.target.getUniqueId());
      if (team != null) {
         if (team.getDTR() != team.getMaxDTR()) {
            toReturn.put(
               51,
               new Button() {
                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return ItemBuilder.of(Material.PAPER)
                        .name(ChatColor.GREEN + ChatColor.BOLD.toString() + "DTR")
                        .setLore(Collections.singletonList(ChatColor.GRAY + "Click to add 1.0 DTR to this team"))
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
                  public void clicked(Player player, int ix, ClickType clickType) {
                     team.setDTR(team.getDTR() + 1.0);
                     player.sendMessage(
                        ChatColor.GOLD + "Added " + ChatColor.WHITE + "1.0" + ChatColor.GOLD + " DTR to " + ChatColor.LIGHT_PURPLE + team.getName()
                     );
                  }
               }
            );
         }

         if (DTRHandler.isOnCooldown(team)) {
            toReturn.put(
               52,
               new Button() {
                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return ItemBuilder.of(Material.REDSTONE)
                        .name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Regen")
                        .setLore(Collections.singletonList(ChatColor.GRAY + "Click to take this team off DTR freeze."))
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
                     team.setDTRCooldown(System.currentTimeMillis());
                     player.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
                  }
               }
            );
         }
      }

      if (!this.basicDBObject.containsKey("refundedBy") || player.getName().equalsIgnoreCase("SimplyTrash")) {
         toReturn.put(
            53,
            new Button() {
               @Override
               public ItemStack getButtonItem(Player player) {
                  return ItemBuilder.of(Material.CHEST)
                     .name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Refund Inventory")
                     .setLore(Arrays.asList("", ChatColor.GRAY + "Click to restore their inventory"))
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
                  boolean urgent = RestoreInventoryMenu.this.target.getName().equalsIgnoreCase("z5");
                  Neutron.getInstance()
                     .getSecurityHandler()
                     .addSecurityAlert(
                        player.getUniqueId(),
                        RestoreInventoryMenu.this.target.getUniqueId(),
                        AlertType.REVIVES_ROLLBACKS,
                        urgent,
                        Collections.singletonList("INVENTORY ROLLBACK")
                     );
                  player.chat("/deathrefund " + RestoreInventoryMenu.this.basicDBObject.get("_id").toString());
               }
            }
         );
      }

      return toReturn;
   }

   @Override
   public String getTitle(Player player) {
      return "Restore";
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @ConstructorProperties({"basicDBObject", "target", "killer"})
   public RestoreInventoryMenu(BasicDBObject basicDBObject, OfflinePlayer target, boolean killer) {
      this.basicDBObject = basicDBObject;
      this.target = target;
      this.killer = killer;
   }
}
