package net.frozenorb.foxtrot.gameplay.loot.crate;

import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Crate {
   private String id;
   private String displayName;
   private Material material;
   private Color fireworkColor;
   private List<String> lore;
   private List<CrateItem> items;

   public ItemStack getItemStack() {
      List<String> newLore = new ArrayList<>();
      this.lore.forEach(it -> newLore.add(ChatColor.translate(it)));
      return ItemBuilder.of(this.material).name(ChatColor.translate(this.displayName)).setLore(newLore).build();
   }

   public void saveCrate(File file, FileConfiguration data, Crate crate) {
      Map<String, Object> configValues = data.getValues(false);

      for (Entry<String, Object> entry : configValues.entrySet()) {
         data.set(entry.getKey(), null);
      }

      data.set("id", crate.getId());
      data.set("displayName", crate.getDisplayName());
      data.set("material", crate.getMaterial().name());
      data.set("fireworkColor", crate.getFireworkColor().asRGB());
      data.set("lore", crate.getLore());
      int i = 0;

      for (CrateItem crateItem : crate.getItems()) {
         data.set("items." + crateItem.getItemStack().getType().name() + "_" + ++i + ".itemStack", crateItem.getItemStack());
         data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".chance", crateItem.getChance());
         data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".command", crateItem.getCommand());
         data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".giveItem", crateItem.isGiveItem());
         data.set("items." + crateItem.getItemStack().getType().name() + "_" + i + ".broadcast", crateItem.isBroadcast());
      }

      try {
         data.save(file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @ConstructorProperties({"id", "displayName", "material", "fireworkColor", "lore", "items"})
   public Crate(String id, String displayName, Material material, Color fireworkColor, List<String> lore, List<CrateItem> items) {
      this.id = id;
      this.displayName = displayName;
      this.material = material;
      this.fireworkColor = fireworkColor;
      this.lore = lore;
      this.items = items;
   }

   public String getId() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public Material getMaterial() {
      return this.material;
   }

   public Color getFireworkColor() {
      return this.fireworkColor;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public void setLore(List<String> lore) {
      this.lore = lore;
   }

   public List<CrateItem> getItems() {
      return this.items;
   }
}
