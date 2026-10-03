package net.frozenorb.foxtrot.server.customTimer.task;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomTimerTask extends BukkitRunnable {
   private List<String> queue = new ArrayList<>();

   @Override
   public void run() {
      if (!CustomTimerCreateCommand.isSOTWTimer()) {
         for (String timer : CustomTimerCreateCommand.getCustomTimers().keySet()) {
            if (!timer.equalsIgnoreCase("&a&lSOTW")
               && !timer.equalsIgnoreCase("&d&l2x Points")
               && !this.queue.contains(timer)
               && CustomTimerCreateCommand.getCustomTimers().get(timer) >= System.currentTimeMillis()) {
               this.queue.add(timer);
            }
         }

         for (String timer : this.queue) {
            if (CustomTimerCreateCommand.getCustomTimers().containsKey(timer)
               && CustomTimerCreateCommand.getCustomTimers().get(timer) <= System.currentTimeMillis()) {
               this.queue.remove(timer);
            }
         }

         CustomTimerCreateCommand.getCustomTimers()
            .keySet()
            .stream()
            .filter(it -> !this.queue.contains(it) && !it.equalsIgnoreCase("&a&lSOTW") && !it.equalsIgnoreCase("&d&l2x Points"))
            .forEach(it -> this.queue.add(it));
         this.queue.stream().filter(it -> !CustomTimerCreateCommand.getCustomTimers().containsKey(it)).forEach(it -> this.queue.remove(it));
         if (!this.queue.isEmpty()) {
            String chosenTimer = this.queue.remove(0);
            Foxtrot.getInstance().getCustomTimerHandler().setActiveTimer(chosenTimer);
         }
      }
   }
}
