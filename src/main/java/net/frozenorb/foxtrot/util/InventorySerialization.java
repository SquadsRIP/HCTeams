package net.frozenorb.foxtrot.util;

import cc.fyre.proton.Proton;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventorySerialization {
   private static final Type TYPE = (new TypeToken<ItemStack[]>() {}).getType();
   public static final BasicDBObject AIR = new BasicDBObject();

   public static BasicDBObject serialize(ItemStack[] armor, ItemStack[] inventory) {
      BasicDBList armorDBObject = serialize(armor);
      BasicDBList inventoryDBObject = serialize(inventory);
      BasicDBObject dbObject = new BasicDBObject();
      dbObject.put("ArmorContents", armorDBObject);
      dbObject.put("InventoryContents", inventoryDBObject);
      return dbObject;
   }

   public static BasicDBList serialize(ItemStack[] items) {
      List<ItemStack> kits = new ArrayList<>(Arrays.asList(items));
      kits.removeIf(Objects::isNull);
      return (BasicDBList)JSON.parse(Proton.PLAIN_GSON.toJson(kits.toArray(new ItemStack[0])));
   }

   public static ItemStack[] deserialize(BasicDBList dbList) {
      return Proton.PLAIN_GSON.fromJson(Proton.PLAIN_GSON.toJson(dbList), TYPE);
   }

   public static BasicDBObject serialize(ItemStack itemStack) {
      if (itemStack != null && itemStack.getType() != Material.AIR) {
         BasicDBObject item = new BasicDBObject("type", itemStack.getType().toString())
            .append("amount", itemStack.getAmount())
            .append("data", itemStack.getDurability());
         BasicDBList enchants = new BasicDBList();

         for (Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchants.add(new BasicDBObject("enchantment", entry.getKey().getName()).append("level", entry.getValue()));
         }

         if (itemStack.getEnchantments().size() > 0) {
            item.append("enchants", enchants);
         }

         if (itemStack.hasItemMeta()) {
            ItemMeta m = itemStack.getItemMeta();
            BasicDBObject meta = new BasicDBObject("displayName", m.getDisplayName());
            if (m.getLore() != null) {
               meta.append("lore", m.getLore());
            }

            item.append("meta", meta);
         }

         return item;
      } else {
         return AIR;
      }
   }

   public static ItemStack deserialize(BasicDBObject dbObject) {
      if (dbObject != null && !dbObject.isEmpty()) {
         Material type = Material.valueOf(dbObject.getString("type"));
         ItemStack item = new ItemStack(type, dbObject.getInt("amount"));
         item.setDurability(Short.parseShort(dbObject.getString("data")));
         if (dbObject.containsField("meta")) {
            BasicDBObject meta = (BasicDBObject)dbObject.get("meta");
            ItemMeta m = item.getItemMeta();
            if (meta.containsField("displayName")) {
               m.setDisplayName(meta.getString("displayName"));
            }

            if (meta.containsField("lore")) {
               m.setLore((List<String>)meta.get("lore"));
            }

            item.setItemMeta(m);
         }

         if (dbObject.containsField("enchants")) {
            for (Object o : (BasicDBList)dbObject.get("enchants")) {
               BasicDBObject enchant = (BasicDBObject)o;
               String enchantment = enchant.getString("enchantment");
               item.addUnsafeEnchantment(Enchantment.getByName(enchantment), enchant.getInt("level"));
            }
         }

         return item;
      } else {
         return new ItemStack(Material.AIR);
      }
   }

   static {
      AIR.put("type", "AIR");
      AIR.put("amount", 1);
      AIR.put("data", 0);
   }
}
