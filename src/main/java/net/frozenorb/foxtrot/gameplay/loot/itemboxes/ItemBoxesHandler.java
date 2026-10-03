package net.frozenorb.foxtrot.gameplay.loot.itemboxes;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ClassUtils;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.parameter.ItemBoxParameter;
import org.bukkit.event.Listener;

public class ItemBoxesHandler implements Listener {
   private Foxtrot instance;
   private List<ItemBox> itemBoxes = new ArrayList<>();
   private ItemBox activeItemBox;

   public ItemBoxesHandler(Foxtrot instance) {
      this.instance = instance;

      for (Class clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.gameplay.loot.itemboxes.type")) {
         if (ItemBox.class.isAssignableFrom(clazz)) {
            try {
               this.itemBoxes.add((ItemBox)clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException ex) {
               ex.printStackTrace();
            }
         }
      }

      Proton.getInstance().getCommandHandler().registerParameterType(ItemBox.class, new ItemBoxParameter());
      this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
   }

   public ItemBox findItemBox(String id) {
      return this.itemBoxes.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   public List<ItemBox> getItemBoxes() {
      return this.itemBoxes;
   }

   public ItemBox getActiveItemBox() {
      return this.activeItemBox;
   }

   public void setActiveItemBox(ItemBox activeItemBox) {
      this.activeItemBox = activeItemBox;
   }
}
