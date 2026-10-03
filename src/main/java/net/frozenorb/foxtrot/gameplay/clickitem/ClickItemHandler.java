package net.frozenorb.foxtrot.gameplay.clickitem;

import cc.fyre.proton.Proton;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.clickitem.parameter.ClickItemParameter;
import net.frozenorb.foxtrot.gameplay.clickitem.type.RandomAbilityItem;
import net.frozenorb.foxtrot.gameplay.clickitem.type.RandomArmorClass;
import org.bukkit.event.Listener;

public class ClickItemHandler implements Listener {
   private Foxtrot instance;
   private List<ClickItem> clickItems = new ArrayList<>();

   public ClickItemHandler(Foxtrot instance) {
      this.instance = instance;
      this.clickItems.add(new RandomAbilityItem());
      this.clickItems.add(new RandomArmorClass());
      Proton.getInstance().getCommandHandler().registerParameterType(ClickItem.class, new ClickItemParameter());
      this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
   }

   public ClickItem findClickItem(String id) {
      return this.clickItems.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   public List<ClickItem> getClickItems() {
      return this.clickItems;
   }
}
