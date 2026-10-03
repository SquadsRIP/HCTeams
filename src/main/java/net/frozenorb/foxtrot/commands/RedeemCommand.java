package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class RedeemCommand {
   private static FileConfiguration config;
   private static Set<UUID> redeemed = new HashSet<>();

   public static void loadConfig() {
      File file = new File(Foxtrot.getInstance().getDataFolder(), "redeem.yml");
      if (!file.exists()) {
         Foxtrot.getInstance().saveResource("redeem.yml", false);
      }

      config = YamlConfiguration.loadConfiguration(file);
      File dataFile = new File(Foxtrot.getInstance().getDataFolder(), "redeem-data.yml");
      if (dataFile.exists()) {
         YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

         for (String key : data.getKeys(false)) {
            if (data.getBoolean(key, false)) {
               try {
                  redeemed.add(UUID.fromString(key));
               } catch (Exception var6) {
               }
            }
         }
      }
   }

   public static void clearRedeemed() {
      redeemed.clear();
   }

   public static void removeRedeemed(UUID uuid) {
      redeemed.remove(uuid);
   }

   private static void saveRedeemed(UUID uuid) {
      File dataFile = new File(Foxtrot.getInstance().getDataFolder(), "redeem-data.yml");
      YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
      data.set(uuid.toString(), true);

      try {
         data.save(dataFile);
      } catch (Exception e) {
         Foxtrot.getInstance().getLogger().warning("Failed to save redeem-data.yml: " + e.getMessage());
      }
   }

   private static FileConfiguration getConfig() {
      if (config == null) {
         loadConfig();
      }

      return config;
   }

   @Command(names = "redeem", permission = "")
   public static void redeem(Player player) {
      if (!getConfig().getBoolean("redeem.enabled", true)) {
         player.sendMessage(ChatColor.RED + "Redeem is currently disabled.");
      } else if (getConfig().getBoolean("redeem.once-per-map", true) && redeemed.contains(player.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You have already redeemed this map!");
      } else {
         new RedeemCommand.RedeemMenu().openMenu(player);
      }
   }

   private static class RedeemMenu extends Menu {
      private RedeemMenu() {
      }

      @Override
      public String getTitle(Player player) {
         return ChatColor.translateAlternateColorCodes('&', RedeemCommand.getConfig().getString("redeem.gui-title", "&4&lRedeem"));
      }

      @Override
      public int size(Player player) {
         return RedeemCommand.getConfig().getInt("redeem.gui-size", 27);
      }

      @Override
      public Map<Integer, Button> getButtons(Player player) {
         HashMap<Integer, Button> buttons = new HashMap<>();
         ConfigurationSection creatorsSection = RedeemCommand.getConfig().getConfigurationSection("redeem.creators");
         if (creatorsSection == null) {
            return buttons;
         }

         final String broadcastFormat = RedeemCommand.getConfig()
            .getString("redeem.broadcast-format", "&4{player} &fhas redeemed &4&l{creator}&f. Use &4/redeem &fto get free rewards!");
         final List<String> commands = RedeemCommand.getConfig().getStringList("redeem.commands");

         for (String key : creatorsSection.getKeys(false)) {
            String basePath = "redeem.creators." + key;
            int slot = RedeemCommand.getConfig().getInt(basePath + ".slot", 0);
            final String headIgn = RedeemCommand.getConfig().getString(basePath + ".head-ign", "Steve");
            final String name = RedeemCommand.getConfig().getString(basePath + ".name", "&f" + key);
            final List<String> lore = RedeemCommand.getConfig().getStringList(basePath + ".lore");
            final String creatorName = RedeemCommand.getConfig().getString(basePath + ".creator-name", key);
            buttons.put(
               slot,
               new Button() {
                  @Override
                  public String getName(Player p) {
                     return ChatColor.translateAlternateColorCodes('&', name);
                  }

                  @Override
                  public List<String> getDescription(Player p) {
                     return lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
                  }

                  @Override
                  public Material getMaterial(Player p) {
                     return Material.SKULL_ITEM;
                  }

                  @Override
                  public byte getDamageValue(Player p) {
                     return 3;
                  }

                  @Override
                  public ItemStack getButtonItem(Player p) {
                     ItemStack item = super.getButtonItem(p);
                     if (item.getType() == Material.SKULL_ITEM) {
                        SkullMeta meta = (SkullMeta)item.getItemMeta();
                        meta.setOwner(headIgn);
                        item.setItemMeta(meta);
                     }

                     return item;
                  }

                  @Override
                  public void clicked(Player p, int slotx, ClickType clickType) {
                     p.closeInventory();
                     if (RedeemCommand.getConfig().getBoolean("redeem.once-per-map", true) && RedeemCommand.redeemed.contains(p.getUniqueId())) {
                        p.sendMessage(ChatColor.RED + "You have already redeemed this map!");
                     } else {
                        RedeemCommand.redeemed.add(p.getUniqueId());
                        RedeemCommand.saveRedeemed(p.getUniqueId());

                        for (String cmd : commands) {
                           String formatted = cmd.replace("{player}", p.getName());
                           Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted);
                        }

                        String msg = ChatColor.translateAlternateColorCodes(
                           '&', broadcastFormat.replace("{player}", p.getName()).replace("{creator}", creatorName)
                        );
                        Bukkit.broadcastMessage(msg);
                        p.sendMessage(ChatColor.GREEN + "Thank you for supporting " + creatorName + "! You received 1 life.");
                     }
                  }
               }
            );
         }

         return buttons;
      }
   }
}
