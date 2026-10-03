package net.frozenorb.foxtrot.server.keyalls.menu.editor;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditDisplayNameButton;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditEndButton;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditGiveAllButton;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditGivingButton;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditItemsButton;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditRedeemedButton;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.button.EditScoreboardNameButton;
import org.bukkit.entity.Player;

public class EditKeyAllMenu extends Menu {
   private KeyAll keyAll;

   @Override
   public String getTitle(Player player) {
      return "Edit Key-All";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(10, new EditDisplayNameButton(this, this.keyAll));
      toReturn.put(11, new EditScoreboardNameButton(this, this.keyAll));
      toReturn.put(12, new EditGiveAllButton(this, this.keyAll));
      toReturn.put(13, new EditEndButton(this, this.keyAll));
      toReturn.put(14, new EditItemsButton(this.keyAll));
      toReturn.put(15, new EditRedeemedButton(this, this.keyAll));
      toReturn.put(16, new EditGivingButton(this, this.keyAll));
      return toReturn;
   }

   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }

   @Override
   public void onClose(Player player) {
      Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> new EditorMainMenu().openMenu(player), 1L);
   }

   @ConstructorProperties("keyAll")
   public EditKeyAllMenu(KeyAll keyAll) {
      this.keyAll = keyAll;
   }
}
