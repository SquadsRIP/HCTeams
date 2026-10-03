package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.Voucher;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoucherUsedMenu extends PaginatedMenu {
   private UUID target;

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @Override
   public int getMaxItemsPerPage(Player player) {
      return 18;
   }

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      Foxtrot.getInstance()
         .getVoucherHandler()
         .getCache()
         .stream()
         .filter(Voucher::isUsed)
         .filter(it -> it.getTarget().toString().equals(this.target.toString()))
         .forEach(it -> {
            Button cfr_ignored_0 = toReturn.put(toReturn.size(), new VoucherElement(it, this));
         });
      return toReturn;
   }

   @Override
   public String getPrePaginatedTitle(Player player) {
      return ChatColor.GREEN + "Used Vouchers";
   }

   @ConstructorProperties("target")
   public VoucherUsedMenu(UUID target) {
      this.target = target;
   }
}
