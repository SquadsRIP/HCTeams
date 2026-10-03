package net.frozenorb.foxtrot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonClass;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Request.Builder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.cavepvp.entity.type.npc.NPC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonClass(generateAdapter = true)
@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \r2\u00020\u0001:\u0001\rB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u000b\u001a\u00020\fH\u0016R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\u000e",
   d2 = {
         "Lnet/frozenorb/foxtrot/PartnerKeyNPC;",
         "Lorg/cavepvp/entity/type/npc/NPC;",
         "location",
         "Lorg/bukkit/Location;",
         "(Lorg/bukkit/Location;)V",
         "index",
         "",
         "getIndex",
         "()I",
         "setIndex",
         "(I)V",
         "init",
         "",
         "Companion",
         "HCTeams"
   }
)
public final class PartnerKeyNPC extends NPC {
   @NotNull
   public static final PartnerKeyNPC.Companion Companion = new PartnerKeyNPC.Companion();
   private transient int index;
   @NotNull
   public static final String NAME = "PartnerKey";
   @NotNull
   private static final OkHttpClient client = new OkHttpClient();
   @NotNull
   private static final ArrayList<Pair<String, String>> skinCache = new ArrayList<>();

   public PartnerKeyNPC(@NotNull Location location) {
      super("PartnerKey", checkedLocation(location));
   }

   private static Location checkedLocation(Location location) {
      Intrinsics.checkNotNullParameter(location, "location");
      return location;
   }

   public final int getIndex() {
      return this.index;
   }

   public final void setIndex(int var1) {
      this.index = var1/* $VF was: <set-?> */;
   }

   public void init() {
      super.init();
      Object var2 = skinCache.get(this.index);
      Intrinsics.checkNotNullExpressionValue(var2, "skinCache[index]");
      Pair skin = (Pair)var2;
      this.setSkin("Hey", (String)skin.getFirst(), (String)skin.getSecond());
      Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Foxtrot.instance, () -> init$lambda_0(this), 300L, 300L);
   }

   private static final void init$lambda_0/* $VF was: init$lambda-0 */(PartnerKeyNPC this$0) {
      Intrinsics.checkNotNullParameter(this$0, "this$0");
      if (this$0.getIndex() == skinCache.size() - 1) {
         this$0.setIndex(0);
      } else {
         PartnerKeyNPC next = this$0;
         int var2 = next.getIndex();
         next.setIndex(var2 + 1);
      }

      Object var4 = skinCache.get(this$0.getIndex());
      Intrinsics.checkNotNullExpressionValue(var4, "skinCache[index]");
      Pair next = (Pair)var4;
      this$0.setSkin("Hey", (String)next.getFirst(), (String)next.getSecond());
   }

   static {
      boolean var0 = false;
   }

   @Metadata(
      mv = {1, 5, 1},
      k = 1,
      xi = 48,
      d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001c\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u000b2\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0013R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR9\u0010\t\u001a*\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u000b0\nj\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u000b`\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u0014",
      d2 = {
            "Lnet/frozenorb/foxtrot/PartnerKeyNPC$Companion;",
            "",
            "()V",
            "NAME",
            "",
            "client",
            "Lokhttp3/OkHttpClient;",
            "getClient",
            "()Lokhttp3/OkHttpClient;",
            "skinCache",
            "Ljava/util/ArrayList;",
            "Lkotlin/Pair;",
            "Lkotlin/collections/ArrayList;",
            "getSkinCache",
            "()Ljava/util/ArrayList;",
            "getSkin",
            "uuid",
            "Ljava/util/UUID;",
            "loadSkins",
            "",
            "HCTeams"
      }
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final OkHttpClient getClient() {
         return PartnerKeyNPC.client;
      }

      @NotNull
      public final ArrayList<Pair<String, String>> getSkinCache() {
         return PartnerKeyNPC.skinCache;
      }

      public final void loadSkins() {
         PartnerType[] var1 = PartnerType.values();
         int var2 = 0;
         int var3 = var1.length;

         while (var2 < var3) {
            PartnerType value = var1[var2];
            var2++;
            UUID uuid = value.uuid;
            Pair skin = null;

            try {
               Intrinsics.checkNotNullExpressionValue(uuid, "uuid");
               skin = this.getSkin(uuid);
            } catch (IOException e) {
               e.printStackTrace();
            }

            if (skin == null) {
               Bukkit.getLogger().info("Failed to load skin for " + value.crateName + " (Mojang API).");
            } else {
               this.getSkinCache().add(skin);
            }
         }
      }

      @Nullable
      public final Pair<String, String> getSkin(@NotNull UUID uuid) throws IOException {
         Intrinsics.checkNotNullParameter(uuid, "uuid");
         Request request = new Builder().url("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").build();
         Response execute = this.getClient().newCall(request).execute();
         PartnerKeyNPC.Companion var4 = this;
         boolean var5 = false;
         boolean var6 = false;
         PartnerKeyNPC.Companion $this$getSkin_u24lambda_u2d0 = var4;
         int var8/* $VF was: $i$a$-run-PartnerKeyNPC$Companion$getSkin$1 */ = 0;
         JsonParser var10000 = new JsonParser();
         ResponseBody var10001 = execute.body();
         Intrinsics.checkNotNull(var10001);
         JsonObject properties = var10000.parse(var10001.string()).getAsJsonObject();
         Intrinsics.checkNotNullExpressionValue(properties, "JsonParser().parse(execute.body!!.string()).getAsJsonObject()");
         JsonObject json = properties;
         Pair var12;
         if (!json.has("properties")) {
            var12 = (Pair)null;
         } else {
            properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
            var12 = new Pair<>(properties.get("value").getAsString(), properties.get("signature").getAsString());
         }

         return var12;
      }

   }
}
