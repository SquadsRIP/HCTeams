package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ResetForceInvitesCommand {
   @Command(names = "resetforceinvites all", permission = "op")
   public static void resetforceinvites_all(Player sender) {
      ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
         .withModality(true)
         .withPrefix(new NullConversationPrefix())
         .withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
               return ChatColor.GREEN + "Are you sure you want to reset all force invites? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
               if (!s.equalsIgnoreCase("yes")) {
                  if (s.equalsIgnoreCase("no")) {
                     cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Resetting cancelled.");
                     return Prompt.END_OF_CONVERSATION;
                  } else {
                     cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                     return Prompt.END_OF_CONVERSATION;
                  }
               } else {
                  for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                     team.setForceInvites(5);
                  }

                  Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "All force invites have been reset!");
                  cc.getForWhom().sendRawMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "All force invites have been reset!");
                  return Prompt.END_OF_CONVERSATION;
               }
            }
         })
         .withEscapeSequence("/no")
         .withLocalEcho(false)
         .withTimeout(10)
         .thatExcludesNonPlayersWithMessage("Go away evil console!");
      Conversation con = factory.buildConversation(sender);
      sender.beginConversation(con);
   }

   @Command(names = "resetforceinvites", permission = "op")
   public static void resetforceinvites(Player sender, @Parameter(name = "team") Team team) {
      team.setForceInvites(5);
      sender.sendMessage(ChatColor.GREEN + team.getName() + " now has " + team.getForceInvites() + " force invites.");
   }
}
