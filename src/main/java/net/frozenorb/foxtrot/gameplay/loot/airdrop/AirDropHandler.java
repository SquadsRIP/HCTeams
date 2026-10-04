package net.frozenorb.foxtrot.gameplay.loot.airdrop;

import cc.fyre.proton.util.ItemBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.listener.AirDropListener;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AirDropHandler {
   private Foxtrot instance;
   private List<UUID> variable = new ArrayList<>();
   private List<AirDropReward> cache = new ArrayList<>();
   private File file;
   private FileConfiguration data;
   private ItemStack itemStack;

   public AirDropHandler(Foxtrot instance) {
      this.instance = instance;
      this.instance.getServer().getPluginManager().registerEvents(new AirDropListener(this.instance), this.instance);
      this.itemStack = ItemBuilder.of(Material.DROPPER)
         .name(ChatColor.AQUA + ChatColor.BOLD.toString() + "Air Drop")
         .setLore(
            Arrays.asList(CC.translate("&7Obtainable at &fComing Soon!&7."), "", ChatColor.YELLOW + "Place this down to designate a location for the drop!")
         )
         .build();
      this.loadLootTable();
   }

   public boolean isEnabled() {
      return this.instance.getConfig().getBoolean("airdropsEnabled", false);
   }

   public void loadLootTable() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/airdrops.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.get("rewards") != null) {
         this.instance
            .getServer()
            .getScheduler()
            .runTaskLater(
               this.instance,
               () -> this.data
                  .getConfigurationSection("rewards")
                  .getKeys(false)
                  .forEach(
                     it -> this.cache
                        .add(
                           new AirDropReward(
                              this.data.getItemStack("rewards." + it + ".itemStack"),
                              this.data.getDouble("rewards." + it + ".chance"),
                              this.data.getString("rewards." + it + ".command"),
                              this.data.getBoolean("rewards." + it + ".giveItem")
                           )
                        )
                  ),
               5L
            );
      }
   }

   public void saveLootTable() {
      this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));
      int i = 0;

      for (AirDropReward airDropReward : this.cache) {
         this.data.set("rewards.reward_" + ++i + ".itemStack", airDropReward.getItemStack());
         this.data.set("rewards.reward_" + i + ".chance", airDropReward.getChance());
         this.data.set("rewards.reward_" + i + ".command", airDropReward.getCommand());
         this.data.set("rewards.reward_" + i + ".giveItem", airDropReward.isGrantItem());
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void setContents(Block block, Player player, boolean airdropAll) {
      if (block.getState() instanceof Dropper) {
         Dropper dropper = (Dropper)block.getState();

         for (int i = 0; i < 9; i++) {
            dropper.getInventory().setItem(i, this.findReward(player, airdropAll));
         }

         if (this.variable != null && this.variable.contains(player.getUniqueId())) {
            dropper.getInventory().setItem(5, null);
            String prize = ChatColor.translate("&2&l$&a&l25 &9&lPAY&3&lPAL");
            this.instance.getServer().broadcastMessage("");
            this.instance
               .getServer()
               .broadcastMessage(
                  CC.translate("&f{player} &chas just won {item} &cfrom an &b&lAirdrop&c.").replace("{player}", player.getName()).replace("{item}", prize)
               );
            this.instance
               .getServer()
               .broadcastMessage(ChatColor.GRAY + "Purchase airdrops on our store at " + ChatColor.WHITE + "Coming Soon!" + ChatColor.GRAY + ".");
            this.instance.getServer().broadcastMessage("");
            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), "vouchers add " + player.getName() + " $25 PAYPAL");
            this.variable.clear();
         }
      }
   }

   private ItemStack findReward(Player player, boolean airdropAll) {
      List<AirDropReward> rewards = new ArrayList<>(this.cache);
      double sumNumber = rewards.stream().mapToDouble(AirDropReward::getChance).sum();
      double random = Math.random() * sumNumber;
      AirDropReward choice = new AirDropReward(null, 0.0, "", false);

      for (AirDropReward airDropReward : rewards) {
         double chance = airDropReward.getChance();
         if (chance > 0.0 && airdropAll && !airDropReward.getCommand().equalsIgnoreCase("none") && !airDropReward.getCommand().equalsIgnoreCase("")) {
            chance /= 2.5;
         }

         choice = airDropReward;
         random -= chance;
         if (random < 0.0) {
            break;
         }
      }

      String command = choice.getCommand();
      if (!command.equalsIgnoreCase("none") && !command.equalsIgnoreCase("")) {
         String item = choice.getItemStack().getItemMeta().hasDisplayName()
            ? choice.getItemStack().getItemMeta().getDisplayName()
            : choice.getItemStack().getType().name();

         for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(CC.translate("&f" + player.getName() + " &6has just won a " + item + " &6from a &b&lAirdrop&6."));
            onlinePlayer.sendMessage(ChatColor.GRAY + "Purchase an Airdrop at " + ChatColor.WHITE + "Coming Soon!");
            onlinePlayer.sendMessage("");
         }

         this.instance
            .getServer()
            .dispatchCommand(this.instance.getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{displayName}", item));
      }

      if (this.itemStack.getAmount() <= 0) {
         this.itemStack.setAmount(1);
      }

      return !choice.isGrantItem()
         ? new ItemStack(Material.AIR)
         : ItemBuilder.copyOf(choice.getItemStack().clone()).amount(this.itemStack.getAmount() <= 0 ? 1 : choice.getItemStack().getAmount()).build();
   }

   public List<UUID> getVariable() {
      return this.variable;
   }

   public void setVariable(List<UUID> variable) {
      this.variable = variable;
   }

   public List<AirDropReward> getCache() {
      return this.cache;
   }

   public void setCache(List<AirDropReward> cache) {
      this.cache = cache;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }
}
