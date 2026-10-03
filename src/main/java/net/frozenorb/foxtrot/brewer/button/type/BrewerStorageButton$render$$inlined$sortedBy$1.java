package net.frozenorb.foxtrot.brewer.button.type;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;

@Metadata(
   mv = {1, 5, 1},
   k = 3,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u000e\u0010\u0003\u001a\n \u0004*\u0004\u0018\u0001H\u0002H\u00022\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u0001H\u0002H\u0002H\n¢\u0006\u0004\b\u0006\u0010\u0007¨\u0006\b",
   d2 = {
         "<anonymous>",
         "",
         "T",
         "a",
         "kotlin.jvm.PlatformType",
         "b",
         "compare",
         "(Ljava/lang/Object;Ljava/lang/Object;)I",
         "kotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2"
   }
)
public final class BrewerStorageButton$render$$inlined$sortedBy$1<T> implements Comparator<T> {
   @Override
   public final int compare(T a, T b) {
      boolean var3 = false;
      Entry it = (Entry)a;
      int var5/* $VF was: $i$a$-sortedBy-BrewerStorageButton$render$2 */ = 0;
      Comparable var10000 = ((AtomicInteger)it.getValue()).get();
      it = (Entry)b;
      Comparable var6 = var10000;
      var5/* $VF was: $i$a$-sortedBy-BrewerStorageButton$render$2 */ = 0;
      Integer var7 = ((AtomicInteger)it.getValue()).get();
      return ComparisonsKt.compareValues(var6, var7);
   }
}
