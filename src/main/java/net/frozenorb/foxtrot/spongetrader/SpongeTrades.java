package net.frozenorb.foxtrot.spongetrader;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public final class SpongeTrades {
   private SpongeTrades() {
   }

   public static List<SpongeTrades.Trade> create() {
      List<SpongeTrades.Trade> trades = new ArrayList<>();
      trades.add(new SpongeTrades.Trade(1, new ItemStack(Material.ENDER_PORTAL_FRAME, 3)));
      trades.add(new SpongeTrades.Trade(1, new ItemStack(Material.EYE_OF_ENDER, 12)));
      trades.add(new SpongeTrades.Trade(1, new ItemStack(Material.MONSTER_EGG, 1, EntityType.COW.getTypeId())));
      trades.add(new SpongeTrades.Trade(2, InventoryUtils.SKELETON_SPAWNER));
      trades.add(new SpongeTrades.Trade(2, InventoryUtils.SPIDER_SPAWNER));
      trades.add(new SpongeTrades.Trade(2, InventoryUtils.CAVE_SPIDER_SPAWNER));
      trades.add(new SpongeTrades.Trade(2, InventoryUtils.ZOMBIE_SPAWNER));
      return trades;
   }

   public static final class Trade {
      private final int cost;
      private final ItemStack reward;

      private Trade(int cost, ItemStack reward) {
         this.cost = cost;
         this.reward = reward.clone();
      }

      public ItemStack getCost() {
         return new ItemStack(Material.SPONGE, this.cost);
      }

      public ItemStack getReward() {
         return this.reward.clone();
      }
   }
}
