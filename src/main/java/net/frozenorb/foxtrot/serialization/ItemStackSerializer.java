package net.frozenorb.foxtrot.serialization;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import java.util.ArrayList;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackSerializer {
   public static BasicDBObject AIR;

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
         if (dbObject.containsField("enchants")) {
            for (Object o : (BasicDBList)dbObject.get("enchants")) {
               BasicDBObject enchant = (BasicDBObject)o;
               item.addUnsafeEnchantment(Enchantment.getByName(enchant.getString("enchantment")), enchant.getInt("level"));
            }
         }

         if (dbObject.containsField("meta")) {
            BasicDBObject meta = (BasicDBObject)dbObject.get("meta");
            ItemMeta m = item.getItemMeta();
            if (meta.containsField("displayName")) {
               m.setDisplayName(meta.getString("displayName"));
            }

            if (meta.containsField("lore")) {
               m.setLore(new ArrayList<String>() {
                  private static final long serialVersionUID = -765088419932829612L;
               });
            }

            item.setItemMeta(m);
         }

         return item;
      } else {
         return new ItemStack(Material.AIR);
      }
   }

   static {
      (AIR = new BasicDBObject()).put("type", "AIR");
      AIR.put("amount", 1);
      AIR.put("data", 0);
   }
}
