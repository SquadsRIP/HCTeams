package net.frozenorb.foxtrot.gameplay.loot.treasurecove;

import cc.fyre.proton.serialization.LocationSerializer;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.integration.buycraft.LegacyBuycraftBridge;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.listeners.TreasureCoveListener;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.tasks.TreasureCoveSaveTask;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.InventorySerialization;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class TreasureCoveHandler {
   public static final String PREFIX = ChatColor.GOLD + "[Treasure Cove]";
   private File treasureInfo;
   private String nextVoucher;
   private int nextVoucherAmount = 5;
   private Location centralChest;
   private Set<Location> treasureChests = new HashSet<>();
   private List<ItemStack> treasureLoot = new ArrayList<>();

   public TreasureCoveHandler() {
      this.treasureInfo = new File(Foxtrot.getInstance().getDataFolder(), "data/treasureInfo.json");
      this.loadTreasureInfo();
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new TreasureCoveListener(Foxtrot.getInstance()), Foxtrot.getInstance());
      new TreasureCoveSaveTask().runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 6000L);
      this.nextVoucherAmount = ThreadLocalRandom.current().nextInt(5, 30);
   }

   public boolean generateCentralChest() {
      if (this.centralChest == null) {
         System.out.println();
         System.out.println("NO CENTRAL CHEST FOUND!!");
         System.out.println();
         return false;
      } else {
         final LegacyBuycraftBridge buycraft;
         try {
            buycraft = LegacyBuycraftBridge.fromPlugin(Foxtrot.getInstance().getBuycraftPlugin());
            buycraft.requireGiftCardSupport();
         } catch (IOException e) {
            Foxtrot.getInstance().getLogger().warning("Cannot generate Treasure Cove gift card: " + e.getMessage());
            return false;
         }
         final int voucherAmount = this.nextVoucherAmount;
         Block block = this.centralChest.getBlock();
         block.setType(Material.CHEST);
         Foxtrot.getInstance()
            .getServer()
            .getScheduler()
            .runTaskAsynchronously(
               Foxtrot.getInstance(),
               () -> {
                  final String code;
                  try {
                     code = buycraft.createGiftCard(BigDecimal.valueOf(voucherAmount),
                        "For Treasure Cove on " + new Date(System.currentTimeMillis()).toLocaleString());
                  } catch (IOException e) {
                     Foxtrot.getInstance().getLogger().warning("Treasure Cove gift-card creation failed: " + e.getMessage());
                     return;
                  }
                  Foxtrot.getInstance().getServer().getScheduler().runTask(Foxtrot.getInstance(), () -> {
                     if (!(block.getState() instanceof Chest)) {
                        Foxtrot.getInstance().getLogger().warning("Treasure Cove chest was removed before its gift card could be delivered.");
                        return;
                     }
                     Chest chest = (Chest)block.getState();
                     ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                     BookMeta bookMeta = (BookMeta)book.getItemMeta();
                     bookMeta.addPage(
                        ChatColor.BLACK
                           + "Enjoy your "
                           + ChatColor.DARK_GREEN
                           + "$"
                           + ChatColor.GREEN
                           + voucherAmount
                           + ChatColor.BLACK
                           + " Gift Card. The code is on the next page."
                     );
                     bookMeta.addPage(code);
                     bookMeta.setDisplayName(ChatColor.translate("&2&l$&a&l" + voucherAmount + " &6Gift Card"));
                     bookMeta.setAuthor("Treasure Cove");
                     bookMeta.setTitle("Gift Card");
                     book.setItemMeta(bookMeta);
                     chest.getInventory().setItem(13, book.clone());
                  });
               }
            );
         return true;
      }
   }

   public void loadTreasureInfo() {
      try {
         if (!this.treasureInfo.exists() && this.treasureInfo.createNewFile()) {
            BasicDBObject dbo = new BasicDBObject();
            dbo.put("chests", new BasicDBList());
            dbo.put("loot", new BasicDBList());
            FileUtils.write(this.treasureInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
         }

         BasicDBObject dbo = (BasicDBObject)JSON.parse(FileUtils.readFileToString(this.treasureInfo));
         if (dbo != null) {
            this.centralChest = LocationSerializer.deserialize((BasicDBObject)dbo.get("centralChest"));
            BasicDBList chests = (BasicDBList)dbo.get("chests");
            BasicDBList loot = (BasicDBList)dbo.get("loot");

            for (Object chestObj : chests) {
               BasicDBObject chest = (BasicDBObject)chestObj;
               this.treasureChests.add(LocationSerializer.deserialize((BasicDBObject)chest.get("location")));
            }

            for (Object lootObj : loot) {
               this.treasureLoot.add(InventorySerialization.deserialize((BasicDBObject)lootObj));
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void saveTreasureInfo() {
      try {
         BasicDBObject dbo = new BasicDBObject();
         BasicDBList chests = new BasicDBList();
         BasicDBList loot = new BasicDBList();

         for (Location treasureChest : this.treasureChests) {
            BasicDBObject chest = new BasicDBObject();
            chest.put("location", LocationSerializer.serialize(treasureChest));
            chests.add(chest);
         }

         for (ItemStack lootItem : this.treasureLoot) {
            loot.add(InventorySerialization.serialize(lootItem));
         }

         dbo.put("chests", chests);
         dbo.put("loot", loot);
         dbo.put("centralChest", LocationSerializer.serialize(this.centralChest));
         this.treasureInfo.delete();
         FileUtils.write(this.treasureInfo, Foxtrot.GSON.toJson(new JsonParser().parse(dbo.toString())));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void scanLoot() {
      this.treasureChests.clear();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         if (team.getOwner() == null && team.hasDTRBitmask(DTRBitmask.MOUNTAIN) && team.getName().equalsIgnoreCase("TreasureCove")) {
            for (Claim claim : team.getClaims()) {
               for (Location location : new CuboidRegion("TreasureCove", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                  if (location.getBlock().getType() == Material.CHEST) {
                     this.treasureChests.add(location);
                  }
               }
            }
         }
      }
   }

   public int respawnTreasureChests() {
      int respawned = 0;

      for (Location chest : this.treasureChests) {
         if (this.respawnTreasureChest(chest)) {
            respawned++;
         }
      }

      return respawned;
   }

   public boolean respawnTreasureChest(Location location) {
      BlockState blockState = location.getBlock().getState();
      if (blockState instanceof Chest) {
         Chest chest = (Chest)blockState;
         chest.getBlockInventory().clear();
         chest.getBlockInventory().addItem(this.treasureLoot.get(ThreadLocalRandom.current().nextInt(this.treasureLoot.size())));
         return true;
      } else {
         Foxtrot.getInstance()
            .getLogger()
            .warning("Citadel chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
         return false;
      }
   }

   public long getResetsIn() {
      return 0L;
   }

   public String getNextVoucher() {
      return this.nextVoucher;
   }

   public int getNextVoucherAmount() {
      return this.nextVoucherAmount;
   }

   public Location getCentralChest() {
      return this.centralChest;
   }

   public void setCentralChest(Location centralChest) {
      this.centralChest = centralChest;
   }

   public Set<Location> getTreasureChests() {
      return this.treasureChests;
   }

   public List<ItemStack> getTreasureLoot() {
      return this.treasureLoot;
   }
}
