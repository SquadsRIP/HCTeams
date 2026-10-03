package net.frozenorb.foxtrot.gameplay.loot.treasurechest;

import cc.fyre.proton.Proton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.command.parameterType.TreasureChestParameterProvider;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.listener.TreasureChestListener;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class TreasureChestHandler {
   private Foxtrot instance;
   private List<TreasureChest> treasureChests = new ArrayList<>();
   private TreasureChestListener treasureChestListener;
   private File folder;

   public TreasureChestHandler(Foxtrot instance) {
      this.instance = instance;
      this.treasureChestListener = new TreasureChestListener(this.instance);
      Proton.getInstance().getCommandHandler().registerParameterType(TreasureChest.class, new TreasureChestParameterProvider());
      instance.getServer().getPluginManager().registerEvents(this.treasureChestListener, this.instance);
      instance.getServer().getScheduler().runTaskLater(instance, () -> this.loadData(false), 5L);
   }

   public void loadData(boolean rewards) {
      this.folder = new File(this.instance.getDataFolder(), "data/treasurechests");
      if (!this.folder.exists()) {
         this.folder.mkdir();
      }

      for (File file : Objects.requireNonNull(this.folder.listFiles())) {
         TreasureChest treasureChest = new TreasureChest(file.getName().replace(".yml", ""));
         treasureChest.loadRewards();
         if (!rewards) {
            treasureChest.loadLocations();
         }

         this.treasureChests.add(treasureChest);
      }
   }

   public void saveData() {
      this.treasureChests.forEach(it -> {
         File file = new File(this.instance.getDataFolder(), "data/treasurechests/" + it.getId() + ".yml");
         if (!file.exists()) {
            try {
               file.createNewFile();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

         it.saveCrate(file, YamlConfiguration.loadConfiguration(file));
      });
   }

   public TreasureChestReward openChest(TreasureChest treasureChest, Chest chest, Player player) {
      TreasureChestReward treasureChestReward = this.findReward(treasureChest, chest, player);
      ItemStack itemStack = treasureChestReward.getItemStack().clone();
      if (itemStack.getAmount() <= 0) {
         itemStack.setAmount(1);
      }

      ItemStack displayItem = itemStack.clone();
      if (displayItem.getItemMeta() != null) {
         ItemMeta itemMeta = displayItem.getItemMeta();
         itemMeta.setDisplayName("#" + ThreadLocalRandom.current().nextInt(50000, 1000000));
         displayItem.setItemMeta(itemMeta);
      }

      VoucherCommand.spawnFireworks(chest.getLocation().add(0.5, -1.0, 0.5), 1, 0, Color.RED, Type.BURST);
      Item item = chest.getWorld().dropItem(chest.getLocation().getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5), displayItem);
      item.setVelocity(new Vector(0, 0, 0));
      item.setMetadata("TREASURE_REWARD", new FixedMetadataValue(Foxtrot.getInstance(), true));
      if (treasureChestReward.isGrantItem()) {
         player.getInventory().addItem(itemStack.clone());
      }

      return treasureChestReward;
   }

   private TreasureChestReward findReward(TreasureChest treasureChest, Chest chest, Player player) {
      List<TreasureChestReward> rewards = new ArrayList<>(treasureChest.getRewards());
      double sumNumber = rewards.stream().mapToDouble(TreasureChestReward::getChance).sum();
      double random = Math.random() * sumNumber;
      TreasureChestReward choice = new TreasureChestReward(null, 0.0, "", false, false);

      for (TreasureChestReward treasureChestReward : rewards) {
         double chance = treasureChestReward.getChance();
         choice = treasureChestReward;
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

         for (int i = 0; i < 8; i++) {
            this.instance.getServer().broadcastMessage(ChatColor.translate(player.getName() + " &ahas just won a " + item + "&a!"));
         }

         this.instance
            .getServer()
            .dispatchCommand(this.instance.getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{displayName}", item));
         VoucherCommand.spawnFireworks(chest.getLocation(), 3, 2, Color.RED, Type.BALL_LARGE);
      }

      return choice;
   }

   public List<TreasureChest> getTreasureChests() {
      return this.treasureChests;
   }

   public TreasureChestListener getTreasureChestListener() {
      return this.treasureChestListener;
   }

   public File getFolder() {
      return this.folder;
   }
}
