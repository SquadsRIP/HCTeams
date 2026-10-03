package net.frozenorb.foxtrot.util;

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f",
   d2 = {
         "Lnet/frozenorb/foxtrot/util/MongoUtil;",
         "",
         "()V",
         "RELAXED_WRITE_SETTING",
         "Lorg/bson/json/JsonWriterSettings;",
         "getRELAXED_WRITE_SETTING",
         "()Lorg/bson/json/JsonWriterSettings;",
         "REPLACE_OPTIONS",
         "Lcom/mongodb/client/model/ReplaceOptions;",
         "getREPLACE_OPTIONS",
         "()Lcom/mongodb/client/model/ReplaceOptions;",
         "UPDATE_OPTIONS",
         "Lcom/mongodb/client/model/UpdateOptions;",
         "getUPDATE_OPTIONS",
         "()Lcom/mongodb/client/model/UpdateOptions;",
         "HCTeams"
   }
)
public final class MongoUtil {
   @NotNull
   public static final MongoUtil INSTANCE = new MongoUtil();
   @NotNull
   private static final UpdateOptions UPDATE_OPTIONS;
   @NotNull
   private static final ReplaceOptions REPLACE_OPTIONS;
   @NotNull
   private static final JsonWriterSettings RELAXED_WRITE_SETTING;

   private MongoUtil() {
   }

   @NotNull
   public final UpdateOptions getUPDATE_OPTIONS() {
      return UPDATE_OPTIONS;
   }

   @NotNull
   public final ReplaceOptions getREPLACE_OPTIONS() {
      return REPLACE_OPTIONS;
   }

   @NotNull
   public final JsonWriterSettings getRELAXED_WRITE_SETTING() {
      return RELAXED_WRITE_SETTING;
   }

   static {
      UpdateOptions var0 = new UpdateOptions().upsert(true);
      Intrinsics.checkNotNullExpressionValue(var0, "UpdateOptions().upsert(true)");
      UPDATE_OPTIONS = var0;
      ReplaceOptions var1 = new ReplaceOptions().upsert(true);
      Intrinsics.checkNotNullExpressionValue(var1, "ReplaceOptions().upsert(true)");
      REPLACE_OPTIONS = var1;
      JsonWriterSettings var2 = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
      Intrinsics.checkNotNullExpressionValue(var2, "builder().outputMode(JsonMode.RELAXED).build()");
      RELAXED_WRITE_SETTING = var2;
   }
}
