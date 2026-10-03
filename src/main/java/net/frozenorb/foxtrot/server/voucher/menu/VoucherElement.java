package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.Voucher;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class VoucherElement extends Button {
   private Voucher voucher;
   private Menu menu;

   @Override
   public String getName(Player player) {
      return null;
   }

   @Override
   public List<String> getDescription(Player player) {
      return null;
   }

   @Override
   public Material getMaterial(Player player) {
      return null;
   }

   @Override
   public void clicked(final Player player, int i, ClickType clickType) {
      if (this.voucher.isUsed()) {
         if (this.voucher.getRankName() != null) {
            Foxtrot.getInstance()
               .getServer()
               .getScheduler()
               .runTaskAsynchronously(
                  Foxtrot.getInstance(),
                  () -> {
                     if (!this.voucher.getCode().contains("Given to ")) {
                        player.sendMessage(ChatColor.RED + "Doesnt exist!");
                     } else {
                        String playerName = this.voucher.getCode().replace("Given to ", "");
                        String usedBy = UUIDUtils.name(this.voucher.getTarget());
                        Profile profile = Neutron.getInstance().getProfileHandler().fromName(playerName, true, true, true);
                        if (profile == null) {
                           player.sendMessage(ChatColor.RED + "PROFILE NOT FOUND!");
                        } else {
                           for (Grant grant : profile.getGrants()) {
                              if (grant.getExecutedReason().contains("Auto-generated voucher")
                                 && grant.getExecutedReason().contains(usedBy)
                                 && grant.getRank().getName().contains(this.voucher.getRankName())) {
                                 player.sendMessage(ChatColor.GREEN + "Deleted a rank, found " + grant.getRank().getName());
                                 grant.setPardoner(player.getUniqueId());
                                 grant.setPardonedAt(System.currentTimeMillis());
                                 grant.setPardonedReason("DUPED DO NOT GIVE IT BACK");
                              }
                           }

                           profile.recalculateGrants();
                           profile.save();
                           this.voucher.setCode(null);
                           this.voucher.setUsedBy(null);
                           this.voucher.setUsedTime(0L);
                           this.voucher.setUsed(false);
                           Foxtrot.getInstance().getVoucherHandler().getCache().remove(this.voucher);
                        }
                     }
                  }
               );
         }

         player.sendMessage(ChatColor.YELLOW + "This voucher has been set as pending.");
      } else {
         if (!this.voucher.isUsed()) {
            player.closeInventory();
            ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
               .withModality(true)
               .withPrefix(new NullConversationPrefix())
               .withFirstPrompt(new StringPrompt() {
                  @Override
                  public String getPromptText(ConversationContext context) {
                     return "§eType the code of the Voucher that has been applied.";
                  }

                  @Override
                  public Prompt acceptInput(ConversationContext cc, String s) {
                     VoucherElement.this.voucher.setCode(s);
                     VoucherElement.this.voucher.setUsedBy(player.getName());
                     VoucherElement.this.voucher.setUsedTime(System.currentTimeMillis());
                     VoucherElement.this.voucher.setUsed(true);
                     player.sendMessage(ChatColor.GREEN + "This voucher has been set as used.");
                     VoucherElement.this.menu.openMenu(player);
                     return Prompt.END_OF_CONVERSATION;
                  }
               })
               .withLocalEcho(false)
               .withEscapeSequence("/null")
               .withTimeout(100)
               .thatExcludesNonPlayersWithMessage("Go away evil console!");
            Conversation con = factory.buildConversation(player);
            player.beginConversation(con);
         }
      }
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      return this.voucher.isUsed()
         ? ItemBuilder.of(Material.BEDROCK)
            .name(ChatColor.WHITE + this.voucher.getVoucher())
            .setLore(
               Arrays.asList(
                  "",
                  CC.translate("&eTime Added &6» &f" + new Date(this.voucher.getAddedTime()).toLocaleString()),
                  CC.translate("&eAdded By &6» &f" + this.voucher.getAddedBy()),
                  CC.translate("&eStaff who Redeemed &6» &f" + this.voucher.getUsedBy()),
                  CC.translate("&eTime Used &6» &f" + new Date(this.voucher.getUsedTime()).toLocaleString()),
                  CC.translate("&eVoucher Code &6» &f" + this.voucher.getCode()),
                  "",
                  ChatColor.GRAY + "Click to set as pending."
               )
            )
            .build()
         : ItemBuilder.of(Material.BEDROCK)
            .name(ChatColor.WHITE + this.voucher.getVoucher())
            .setLore(
               Arrays.asList(
                  "",
                  CC.translate("&eTime Added &6» &f" + new Date(this.voucher.getAddedTime()).toLocaleString()),
                  CC.translate("&eAdded By &6» &f" + this.voucher.getAddedBy()),
                  "",
                  ChatColor.GRAY + "Click to set as used."
               )
            )
            .build();
   }

   @ConstructorProperties({"voucher", "menu"})
   public VoucherElement(Voucher voucher, Menu menu) {
      this.voucher = voucher;
      this.menu = menu;
   }
}
