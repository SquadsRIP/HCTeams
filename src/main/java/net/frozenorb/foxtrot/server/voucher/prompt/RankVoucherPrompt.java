package net.frozenorb.foxtrot.server.voucher.prompt;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.universe.UniverseAPI;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.voucher.Voucher;
import net.frozenorb.foxtrot.server.voucher.menu.VoucherRedeemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class RankVoucherPrompt extends StringPrompt {
   private Player player;
   private Voucher voucher;

   @Override
   public String getPromptText(ConversationContext conversationContext) {
      return "";
   }

   @Override
   public Prompt acceptInput(ConversationContext conversationContext, String string) {
      String playerName = string;
      if (string.equalsIgnoreCase("self")) {
         playerName = this.player.getName();
      }

      Profile profile = Neutron.getInstance().getProfileHandler().fromName(playerName, true, true, true);
      if (profile == null) {
         conversationContext.getForWhom().sendRawMessage(ChatColor.RED + playerName + " has never logged onto the server.");
         return END_OF_CONVERSATION;
      }

      if (playerName.equalsIgnoreCase("cancel")) {
         conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Cancelled rank granting process.");
         new VoucherRedeemMenu().openMenu(this.player);
         return END_OF_CONVERSATION;
      }

      this.voucher.setCode("Given to " + playerName);
      this.voucher.setUsedBy(this.player.getName());
      this.voucher.setUsedTime(System.currentTimeMillis());
      this.voucher.setUsed(true);
      this.player.playSound(this.player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      Server server = Foxtrot.getInstance().getServer();
      Rank activeRank = profile.getActiveRank();
      Rank wonRank = Neutron.getInstance().getRankHandler().fromName(this.voucher.getRankName());
      if (activeRank != null && wonRank != null && activeRank.getWeight().get() <= wonRank.getWeight().get()) {
         server.dispatchCommand(server.getConsoleSender(), "reclaimreset " + profile.getName());
      }

      server.dispatchCommand(
         server.getConsoleSender(),
         "grant "
            + profile.getName()
            + " "
            + this.voucher.getRankName()
            + " "
            + this.voucher.getRankDuration()
            + " Auto-generated voucher on "
            + UniverseAPI.getServerName()
            + ", won by "
            + this.player.getName()
      );
      conversationContext.getForWhom()
         .sendRawMessage(ChatColor.translate("&6Successfully gave " + this.voucher.getVoucher() + " &6to &f" + profile.getFancyName() + "&6."));
      return Prompt.END_OF_CONVERSATION;
   }

   @ConstructorProperties({"player", "voucher"})
   public RankVoucherPrompt(Player player, Voucher voucher) {
      this.player = player;
      this.voucher = voucher;
   }
}
