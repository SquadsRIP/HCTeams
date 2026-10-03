package net.frozenorb.foxtrot.brewer.service;

import java.util.List;
import kotlin.Metadata;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.FancyBrewerHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005",
   d2 = {"Lnet/frozenorb/foxtrot/brewer/service/FancyBrewerTickService;", "Ljava/lang/Runnable;", "()V", "run", "", "HCTeams"}
)
public final class FancyBrewerTickService implements Runnable {
   @NotNull
   public static final FancyBrewerTickService INSTANCE = new FancyBrewerTickService();

   private FancyBrewerTickService() {
   }

   @Override
   public void run() {
      List<FancyBrewer> $this$forEach$iv = FancyBrewerHandler.INSTANCE.getAllBrewers();
      boolean $i$f$forEach = false;

      for (FancyBrewer it : $this$forEach$iv) {
         boolean bl = false;
         it.tick();
      }
   }
}
