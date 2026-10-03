package net.frozenorb.foxtrot.brewer;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.brewer.listener.FancyBrewerListener;
import net.frozenorb.foxtrot.brewer.listener.FancyBrewerResourceListener;
import net.frozenorb.foxtrot.brewer.service.FancyBrewerTickService;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\u000b\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\f",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewerModule;",
         "",
         "()V",
         "getListeners",
         "",
         "Lorg/bukkit/event/Listener;",
         "init",
         "",
         "core",
         "Lnet/frozenorb/foxtrot/Foxtrot;",
         "onSave",
         "shutdown",
         "HCTeams"
   }
)
public final class FancyBrewerModule {
   public final void init(@NotNull Foxtrot core) {
      Intrinsics.checkNotNullParameter(core, "core");
      core.getServer().getScheduler().runTaskLater(Foxtrot.instance, () -> init$lambda_1(core), 20L);

      for (Listener listener : this.getListeners()) {
         core.getServer().getPluginManager().registerEvents(listener, core);
      }
   }

   public final void onSave(@NotNull Foxtrot core) {
      Intrinsics.checkNotNullParameter(core, "core");
      FancyBrewerRepository.INSTANCE.deleteAll();
      FancyBrewerRepository.INSTANCE.saveAll();
   }

   public final void shutdown(@NotNull Foxtrot core) {
      Intrinsics.checkNotNullParameter(core, "core");
      FancyBrewerRepository.INSTANCE.deleteAll();
      FancyBrewerRepository.INSTANCE.saveAll();
   }

   @NotNull
   public final List<Listener> getListeners() {
      Listener[] var1 = new Listener[]{FancyBrewerListener.INSTANCE, FancyBrewerResourceListener.INSTANCE};
      return CollectionsKt.listOf(var1);
   }

   private static final void init$lambda_1/* $VF was: init$lambda-1 */(Foxtrot $core) {
      Intrinsics.checkNotNullParameter($core, "$core");
      Iterable $this$forEach$iv = FancyBrewerRepository.INSTANCE.findAll();
      int $i$f$forEach = 0;

      for (Object element$iv : $this$forEach$iv) {
         FancyBrewer it = (FancyBrewer)element$iv;
         int var6/* $VF was: $i$a$-forEach-FancyBrewerModule$init$1$1 */ = 0;
         FancyBrewerHandler.INSTANCE.addBrewer(it);
      }

      $core.getServer().getScheduler().runTaskTimerAsynchronously($core, FancyBrewerTickService.INSTANCE, 20L, 20L);
   }
}
