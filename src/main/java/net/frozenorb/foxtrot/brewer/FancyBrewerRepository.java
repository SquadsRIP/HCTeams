package net.frozenorb.foxtrot.brewer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.MongoUtil;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nJ\u0006\u0010\f\u001a\u00020\bR2\u0010\u0003\u001a&\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u0005 \u0006*\u0012\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u0005\u0018\u00010\u00040\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewerRepository;",
         "",
         "()V",
         "collection",
         "Lcom/mongodb/client/MongoCollection;",
         "Lorg/bson/Document;",
         "kotlin.jvm.PlatformType",
         "deleteAll",
         "",
         "findAll",
         "",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "saveAll",
         "HCTeams"
   }
)
public final class FancyBrewerRepository {
   @NotNull
   public static final FancyBrewerRepository INSTANCE = new FancyBrewerRepository();
   private static final MongoCollection<Document> collection = Foxtrot.instance.mongoPool.getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("brewers");

   private FancyBrewerRepository() {
   }

   @NotNull
   public final List<FancyBrewer> findAll() {
      Iterable $this$mapNotNull$iv = collection.find();
      Intrinsics.checkNotNullExpressionValue($this$mapNotNull$iv, "collection.find()");
      $this$mapNotNull$iv = $this$mapNotNull$iv;
      int $i$f$mapNotNull = 0;
      Iterable $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
      ArrayList destination$iv$iv = new ArrayList();
      int $i$f$mapNotNullTo = 0;
      Iterable $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
      int $i$f$forEach = 0;

      for (Object element$iv$iv$iv : $this$forEach$iv$iv$iv) {
         Object element$iv$iv = element$iv$iv$iv;
         int var11/* $VF was: $i$a$-forEach-CollectionsKt___CollectionsKt$mapNotNullTo$1$iv$iv */ = 0;
         Document it = (Document)element$iv$iv;
         int var13/* $VF was: $i$a$-mapNotNull-FancyBrewerRepository$findAll$1 */ = 0;
         FancyBrewer var10000 = Foxtrot.GSON.fromJson(it.toJson(MongoUtil.INSTANCE.getRELAXED_WRITE_SETTING()), FancyBrewer.class);
         if (var10000 != null) {
            FancyBrewer var14 = var10000;
            boolean var15 = false;
            boolean var16 = false;
            Object it$iv$iv = var14;
            int var18/* $VF was: $i$a$-let-CollectionsKt___CollectionsKt$mapNotNullTo$1$1$iv$iv */ = 0;
            destination$iv$iv.add(it$iv$iv);
         }
      }

      return (List<FancyBrewer>)destination$iv$iv;
   }

   public final void saveAll() {
      Iterator bulkWrite = FancyBrewerHandler.INSTANCE.getAllUpdates().iterator();
      Intrinsics.checkNotNullExpressionValue(bulkWrite, "FancyBrewerHandler.getAllUpdates().iterator()");
      Iterator iterator = bulkWrite;
      boolean brewer = false;
      List bulkWritex = new ArrayList();

      while (iterator.hasNext()) {
         FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
         Object var4 = iterator.next();
         Intrinsics.checkNotNullExpressionValue(var4, "iterator.next()");
         FancyBrewer brewerx = var10000.getBrewerById((UUID)var4);
         if (brewerx != null) {
            bulkWritex.add(
               new ReplaceOneModel<>(
                  Filters.eq("_id", brewerx.getId().toString()), Document.parse(Foxtrot.GSON.toJson(brewerx)), MongoUtil.INSTANCE.getREPLACE_OPTIONS()
               )
            );
         }

         iterator.remove();
      }

      if (!bulkWritex.isEmpty()) {
         collection.bulkWrite(bulkWritex);
      }
   }

   public final void deleteAll() {
      Iterator bulkWrite = FancyBrewerHandler.INSTANCE.getAllDeletes().iterator();
      Intrinsics.checkNotNullExpressionValue(bulkWrite, "FancyBrewerHandler.getAllDeletes().iterator()");
      Iterator iterator = bulkWrite;
      boolean var3 = false;
      List bulkWritex = new ArrayList();

      while (iterator.hasNext()) {
         bulkWritex.add(new DeleteOneModel(Filters.eq("_id", ((UUID)iterator.next()).toString())));
         iterator.remove();
      }

      if (!bulkWritex.isEmpty()) {
         collection.bulkWrite(bulkWritex);
      }
   }
}
