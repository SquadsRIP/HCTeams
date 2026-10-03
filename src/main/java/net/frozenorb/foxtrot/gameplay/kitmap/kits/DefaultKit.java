package net.frozenorb.foxtrot.gameplay.kitmap.kits;

import cc.fyre.proton.command.param.ParameterType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.util.com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DefaultKit extends Kit {
   private ItemStack icon;
   private String description;
   private int order = 1;
   private List<ItemStack> editorItems = new ArrayList<>();
   private String displayName = "";

   public DefaultKit(String kitName) {
      super(kitName);
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public void setIcon(ItemStack icon) {
      this.icon = icon;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public List<ItemStack> getEditorItems() {
      return this.editorItems;
   }

   public void setEditorItems(List<ItemStack> editorItems) {
      this.editorItems = editorItems;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public static class Type implements ParameterType<DefaultKit> {
      public DefaultKit transform(CommandSender sender, String source) {
         DefaultKit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(source);
         if (kit == null) {
            sender.sendMessage(ChatColor.YELLOW + "Default kit '" + source + "' not found.");
            return null;
         } else {
            return kit;
         }
      }

      @Override
      public List<String> tabComplete(Player player, Set<String> flags, String source) {
         ArrayList completions = Lists.newArrayList();

         for (DefaultKit kit : Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKits()) {
            if (StringUtils.startsWith(kit.getName(), source)) {
               completions.add(kit.getName());
            }
         }

         return completions;
      }
   }
}
