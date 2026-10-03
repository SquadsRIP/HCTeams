package net.frozenorb.foxtrot.chat.tips.task;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.tips.TipsHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TipTask extends BukkitRunnable {
   private Foxtrot instance;
   private TipsHandler tipsHandler;
   private List<String> queue = new ArrayList<>();

   public TipTask(Foxtrot instance, TipsHandler tipsHandler) {
      this.instance = instance;
      this.tipsHandler = tipsHandler;
      this.queue.addAll(this.tipsHandler.getTips().values());
   }

   @Override
   public void run() {
      if (this.queue.isEmpty()) {
         this.queue.addAll(this.tipsHandler.getTips().values());
      }

      if (this.tipsHandler.getTips().isEmpty()) {
         System.out.println("-------------------");
         System.out.println("Tried to send a tip out, however none were found.");
         System.out.println("-------------------");
      } else {
         String chosenTip = this.queue.remove(0);

         for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
            if (this.instance.getTipsMap().isTips(onlinePlayer.getUniqueId())) {
               onlinePlayer.sendMessage("");
               onlinePlayer.sendMessage(ChatColor.translate(TipsHandler.PREFIX + chosenTip));
               onlinePlayer.sendMessage("");
            }
         }
      }
   }

   public List<String> getQueue() {
      return this.queue;
   }
}
