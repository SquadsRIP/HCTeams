package net.frozenorb.foxtrot.gameplay.cavesays;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.cavesays.type.Chat;
import net.frozenorb.foxtrot.gameplay.cavesays.type.EnchantItem;
import net.frozenorb.foxtrot.gameplay.cavesays.type.GoEnd;
import net.frozenorb.foxtrot.gameplay.cavesays.type.GoKoth;
import net.frozenorb.foxtrot.gameplay.cavesays.type.GoNether;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillCows;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillCreepers;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillEndermen;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillMagmaCube;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillPlayer;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillSkeletons;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillSpiders;
import net.frozenorb.foxtrot.gameplay.cavesays.type.KillZombies;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MakeAFactionRaidable;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineCoal;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineDiamonds;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineGlowstone;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineIron;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineLapis;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineQuartz;
import net.frozenorb.foxtrot.gameplay.cavesays.type.MineRedstone;
import net.frozenorb.foxtrot.gameplay.cavesays.type.SpawnACow;
import net.frozenorb.foxtrot.gameplay.cavesays.type.TakeFallDamage;
import net.frozenorb.foxtrot.gameplay.cavesays.type.Vote;
import org.bukkit.event.Listener;

public class CaveSaysHandler implements Listener {
   /*
    * The supplied binary retained the task implementations and their callers but
    * removed the handler methods. Keep this registry passive: no task is started,
    * listener registered, or repeating event scheduled when the handler is built.
    * Factories give each event fresh progress and task-specific state.
    */
   private static final List<Supplier<Task>> TASK_FACTORIES = Collections.unmodifiableList(Arrays.asList(
      Chat::new,
      EnchantItem::new,
      GoEnd::new,
      GoKoth::new,
      GoNether::new,
      KillCows::new,
      KillCreepers::new,
      KillEndermen::new,
      KillMagmaCube::new,
      KillPlayer::new,
      KillSkeletons::new,
      KillSpiders::new,
      KillZombies::new,
      MakeAFactionRaidable::new,
      MineCoal::new,
      MineDiamonds::new,
      MineGlowstone::new,
      MineIron::new,
      MineLapis::new,
      MineQuartz::new,
      MineRedstone::new,
      SpawnACow::new,
      TakeFallDamage::new,
      Vote::new
   ));

   private Foxtrot instance;
   private Task activeTask;

   public CaveSaysHandler(Foxtrot instance) {
      this.instance = instance;
   }

   public Task findTask(String taskId) {
      if (taskId == null) {
         return null;
      }

      for (Supplier<Task> factory : TASK_FACTORIES) {
         Task task = factory.get();
         if (task.getTaskID().equalsIgnoreCase(taskId)) {
            return task;
         }
      }

      return null;
   }

   public void activateRandom() {
      if (this.activeTask != null) {
         throw new IllegalStateException("A Squads Says task is already active.");
      }

      Task task = TASK_FACTORIES.get(ThreadLocalRandom.current().nextInt(TASK_FACTORIES.size())).get();
      this.setActiveTask(task);
      task.activate();
   }

   public Task getActiveTask() {
      return this.activeTask;
   }

   public void setActiveTask(Task activeTask) {
      this.activeTask = activeTask;
   }
}
