package net.frozenorb.foxtrot.chat.trivia;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

public class TriviaHandler implements Listener {
   private Foxtrot instance;
   private Map<String, String> questions = new HashMap<>();
   private String chosenQuestion = null;
   private String chosenAnswer = null;
   private Integer riggedNumber = null;
   private boolean chatReaction = false;
   private BukkitTask task;

   public TriviaHandler(Foxtrot instance) {
      this.questions.put("In what year was Minecraft released?", "2011");
      this.questions.put("What is the current top rank on Squads?", "Squads");
      this.questions.put("What is the lowest rank on Squads?", "Iron");
      this.questions.put("What is Squads' Discord link?", "Coming Soon!");
      this.questions.put("How many states are there in the United States?", "50");
      this.questions.put("Is tomato a fruit?", "Yes");
      this.questions.put("What country has the 2nd largest GDP?", "China");
      this.questions.put("What country is largest in population?", "India");
      this.questions.put("What country is largest in land size?", "Russia");
      this.questions.put("What's 6x6?", "36");
      this.questions.put("What's 829+219?", "1048");
      this.questions.put("What's 3x3+349?", "358");
      this.questions.put("What's 348+233?", "358");
      this.questions.put("What's the first 5 numbers of PI?", "3.1415");
      this.questions.put("What's 8 squared?", "64");
      this.questions.put("What's 12 squared?", "144");
      this.questions.put("What's 3 squared?", "9");
      this.questions.put("What's 9 cubed?", "729");
      this.questions.put("What's 48+16", "64");
      this.questions.put("How many months have 28 days?", "12");
      this.questions.put("When was the Squads Christmas Update?", "December");
      this.questions.put("How many continents are there?", "7");
      this.questions.put("What's the largest crypto currency?", "Bitcoin");
      this.questions.put("What company invented the iPhone?", "Apple");
      this.questions.put("What company invented the Playstation?", "Sony");
      this.questions.put("What company invented the Xbox?", "Microsoft");
      this.questions.put("Who created Minecraft?", "Notch");
      this.questions.put("What's the name of the Minecraft convention?", "Minecon");
      this.questions.put("What's the name of the Twitch convention?", "Twitchcon");
      this.questions.put("What's the final boss in Minecraft?", "Enderdragon");
      this.questions.put("What version is Minecraft currently on?", "1.17");
      this.questions.put("What is the IP to Squads?", "Coming Soon!");
      this.questions.put("What is the new Minecraft mob that was voted upon?", "Allay");
      this.questions.put("What is the 8th letter of the alphabet?", "H");
      this.questions.put("What is the 12th letter of the alphabet?", "L");
      this.questions.put("What is the 18th letter of the alphabet?", "R");
      this.questions.put("What is the 24th letter of the alphabet?", "W");
      this.questions.put("chatreaction:SimplyTrash", "SimplyTrash");
      this.questions.put("chatreaction:DrePvP", "DrePvP");
      this.questions.put("chatreaction:iMakeMcVids", "iMakeMcVids");
      this.questions.put("chatreaction:Headed", "Headed");
      this.questions.put("chatreaction:Vik", "Vik");
      this.questions.put("chatreaction:Lectors", "Lectors");
      this.questions.put("chatreaction:Frozeado", "Frozeado");
      this.questions.put("chatreaction:SamHCF", "SamHCF");
      this.questions.put("chatreaction:Squads", "Squads");
      this.questions.put("chatreaction:Squads On Top", "Squads On Top");
      this.questions.put("chatreaction:Trojan", "Trojan");
      this.instance = instance;
      this.reloadQuestions();
      this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
      this.scheduleTask();
   }

   public void reloadFromConfig() {
      this.reloadQuestions();
      this.scheduleTask();
   }

   public void reloadQuestions() {
      List<Map<?, ?>> configuredQuestions = this.instance.getConfiguration("trivia.yml").getMapList("trivia.questions");
      if (!configuredQuestions.isEmpty()) {
         HashMap<String, String> loaded = new HashMap<>();

         for (Map entry : configuredQuestions) {
            Object question = entry.get("question");
            Object answer = entry.get("answer");
            if (question != null && answer != null) {
               String key = question.toString();
               Object reaction = entry.get("reaction");
               if (reaction != null && Boolean.parseBoolean(reaction.toString())) {
                  key = "chatreaction:" + key;
               }

               loaded.put(key, answer.toString());
            }
         }

         if (!loaded.isEmpty()) {
            this.questions.clear();
            this.questions.putAll(loaded);
         }
      }
   }

   private void scheduleTask() {
      if (this.task != null) {
         this.task.cancel();
         this.task = null;
      }

      if (this.instance.getConfiguration("trivia.yml").getBoolean("trivia.enabled", true)) {
         long initialDelay = Math.max(1L, this.instance.getConfiguration("trivia.yml").getLong("trivia.initial-delay-seconds", 5404L)) * 20L;
         long interval = Math.max(1L, this.instance.getConfiguration("trivia.yml").getLong("trivia.interval-seconds", 5404L)) * 20L;
         this.task = this.instance.getServer().getScheduler().runTaskTimer(this.instance, this::start, initialDelay, interval);
      }
   }

   public void start() {
      Server server = this.instance.getServer();
      ArrayList<String> questionList = new ArrayList<>(this.questions.keySet());
      if (!questionList.isEmpty()) {
         String question = questionList.get(ThreadLocalRandom.current().nextInt(questionList.size()));
         String answer = this.questions.get(question);
         this.chosenQuestion = question;
         this.chosenAnswer = answer;
         if (question.startsWith("chatreaction:")) {
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.translate("&6&lChat Reaction"));
            server.broadcastMessage(answer);
            server.broadcastMessage(ChatColor.translate("&eFirst person to type the message above will receive &e&la random prize&7!"));
            server.broadcastMessage("");
         } else {
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.translate("&6&lTrivia"));
            server.broadcastMessage(ChatColor.translate(question));
            server.broadcastMessage(
               ChatColor.GRAY
                  + "First person to get the answer right will get a "
                  + ChatColor.YELLOW
                  + ChatColor.BOLD.toString()
                  + "random prize"
                  + ChatColor.GRAY
                  + "!"
            );
            server.broadcastMessage("");
         }

         server.getScheduler().runTaskLater(this.instance, () -> {
            if (this.chosenAnswer.equalsIgnoreCase(answer)) {
               server.broadcastMessage("");
               server.broadcastMessage(ChatColor.YELLOW + "Nobody got the answer in time!");
               server.broadcastMessage("");
               this.chosenQuestion = null;
               this.chosenAnswer = null;
               this.riggedNumber = null;
            }
         }, 600L);
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   private void onChat(PlayerChatEvent event) {
      String message = event.getMessage().replace("!", "");
      if (!event.getMessage().equalsIgnoreCase("null")
         && this.chosenQuestion != null
         && this.chosenAnswer != null
         && this.chosenAnswer.equalsIgnoreCase(message)) {
         Player player = event.getPlayer();
         Server server = this.instance.getServer();
         int randomNumber = this.riggedNumber == null ? ThreadLocalRandom.current().nextInt(0, 100) : this.riggedNumber;
         String prize;
         if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            prize = ChatColor.translate("&a&l100x Gems");
            server.dispatchCommand(server.getConsoleSender(), "gems add " + player.getName() + " 100");
         } else if (randomNumber == -1) {
            prize = ChatColor.translate("&7an &b&lAirdrop");
            server.dispatchCommand(server.getConsoleSender(), "airdrops give " + player.getName() + " 1");
         } else if (randomNumber < 3) {
            prize = ChatColor.translate("&7a &e&ki&6&lHalloween Key&e&ki&r");
            server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Seasonal 1");
         } else if (randomNumber < 6) {
            prize = ChatColor.translate("&7a &6&lFall Lootbox");
            server.dispatchCommand(server.getConsoleSender(), "crates give " + player.getName() + " Seasonal 1");
         } else {
            prize = ChatColor.translate("&5&l3x Legendary Keys");
            server.dispatchCommand(server.getConsoleSender(), "cr givekey " + player.getName() + " Legendary 3");
         }

         server.broadcastMessage(
            ChatColor.translate("&6&lTrivia &8┃ &f" + player.getName() + " &7got the answer &f'" + this.chosenAnswer + "' &7and received " + prize + "&7!")
         );
         this.chosenAnswer = null;
         this.chosenQuestion = null;
         this.riggedNumber = null;
      }
   }

   @Command(names = "trivia start", permission = "op")
   public static void execute(Player player, @Parameter(name = "number", defaultValue = "-5") int number) {
      Foxtrot.getInstance().getTriviaHandler().start();
      if (number != -5) {
         Foxtrot.getInstance().getTriviaHandler().setRiggedNumber(number);
      } else {
         Foxtrot.getInstance().getTriviaHandler().setRiggedNumber(null);
      }
   }

   public Map<String, String> getQuestions() {
      return this.questions;
   }

   public String getChosenQuestion() {
      return this.chosenQuestion;
   }

   public String getChosenAnswer() {
      return this.chosenAnswer;
   }

   public Integer getRiggedNumber() {
      return this.riggedNumber;
   }

   public void setRiggedNumber(Integer riggedNumber) {
      this.riggedNumber = riggedNumber;
   }

   public boolean isChatReaction() {
      return this.chatReaction;
   }

   public void setChatReaction(boolean chatReaction) {
      this.chatReaction = chatReaction;
   }
}
