package net.frozenorb.foxtrot.config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

public final class SquadsCommandPolicy implements Runnable {
   private static final Set<String> BLOCKED = new HashSet<>(
      Arrays.asList(
         "tokens",
         "cave",
         "cavepvp",
         "cavesay",
         "cavesays",
         "cavenight",
         "cavenite",
         "squadsay",
         "squadsays",
         "squadssay",
         "squadssays",
         "squadsnight",
         "squadsnite",
         "custom",
         "tutorial",
         "treasurecove",
         "treasure",
         "tokenmerchants",
         "givealltoken",
         "ce",
         "customenchants",
         "crackshot",
         "gemflip",
         "gemflips",
         "coinflip",
         "coinflips",
         "gf",
         "cf",
         "totem",
         "totems",
         "shopcoin",
         "coinshop",
         "coinshopcategory",
         "cs",
         "buildcomp",
         "basebuild",
         "basebuildcompetition",
         "ratio"
      )
   );
   private final Object plugin;
   private final Object server;
   private final Logger logger;

   private SquadsCommandPolicy(Object var1, Object var2, Logger var3) {
      this.plugin = var1;
      this.server = var2;
      this.logger = var3;
   }

   public static String label(String var0) {
      if (var0 == null) {
         return "";
      }

      var0 = var0.trim();
      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      var0 = var0.split("\\s+", 2)[0];
      return var0.substring(var0.lastIndexOf(58) + 1).toLowerCase(Locale.ROOT);
   }

   public static String path(String var0) {
      if (var0 == null) {
         return "";
      }

      var0 = var0.trim().toLowerCase(Locale.ROOT);
      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      String[] var1 = var0.split("\\s+");
      var1[0] = var1[0].substring(var1[0].lastIndexOf(58) + 1);
      return String.join(" ", var1);
   }

   public static boolean blocked(String var0) {
      String var1 = path(var0);
      if (BLOCKED.contains(label(var0))) {
         return true;
      } else if (!var1.equals("coin shop") && !var1.startsWith("coin shop ")) {
         String[] var2 = var1.split(" ");
         int var3 = Arrays.asList("f", "t", "team", "faction", "fac").contains(var2[0]) ? 1 : 0;
         return var2.length >= var3 + 2 && (var2[var3].equals("base") || var2[var3].equals("falltrap")) && var2[var3 + 1].equals("wand");
      } else {
         return true;
      }
   }

   private static Object call(Object var0, String var1, Class<?>[] var2, Object... var3) throws ReflectiveOperationException {
      return var0.getClass().getMethod(var1, var2).invoke(var0, var3);
   }

   private static Object call(Object var0, String var1) throws ReflectiveOperationException {
      return call(var0, var1, new Class[0]);
   }

   private static Field field(Class<?> var0, String var1) throws NoSuchFieldException {
      while (var0 != null) {
         try {
            Field var2 = var0.getDeclaredField(var1);
            var2.setAccessible(true);
            return var2;
         } catch (NoSuchFieldException var3) {
            var0 = var0.getSuperclass();
         }
      }

      throw new NoSuchFieldException(var1);
   }

   public static void install(Object var0) {
      try {
         Object var1 = call(var0, "getServer");
         Logger var2 = (Logger)call(var0, "getLogger");
         SquadsCommandPolicy var3 = new SquadsCommandPolicy(var0, var1, var2);
         var3.register();
         var3.run();
         Object var4 = call(var1, "getScheduler");
         Class var5 = Class.forName("org.bukkit.plugin.Plugin");
         call(var4, "scheduleSyncDelayedTask", new Class[]{var5, Runnable.class, long.class}, var0, var3, 1L);
      } catch (Exception var6) {
         throw new IllegalStateException("Could not install disabled-command policy", var6);
      }
   }

   private void register() throws Exception {
      ClassLoader var1 = this.plugin.getClass().getClassLoader();
      Class var2 = Class.forName("org.bukkit.event.Listener");
      Class var3 = Class.forName("org.bukkit.plugin.EventExecutor");
      Class var4 = Class.forName("org.bukkit.event.EventPriority");
      Class var5 = Class.forName("org.bukkit.plugin.Plugin");
      Object var6 = Proxy.newProxyInstance(var1, new Class[]{var2}, new InvocationHandler() {
         @Override
         public Object invoke(Object var1, Method var2x, Object[] var3x) {
            if (var2x.getName().equals("hashCode")) {
               return System.identityHashCode(var1);
            } else {
               return var2x.getName().equals("equals") ? var1 == var3x[0] : "Squads disabled-command listener";
            }
         }
      });
      Object var7 = Proxy.newProxyInstance(var1, new Class[]{var3}, new InvocationHandler() {
         @Override
         public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
            if (var2.getName().equals("execute")) {
               SquadsCommandPolicy.this.handle(var3[1]);
               return null;
            } else if (var2.getName().equals("hashCode")) {
               return System.identityHashCode(var1);
            } else {
               return var2.getName().equals("equals") ? var1 == var3[0] : "Squads disabled-command executor";
            }
         }
      });
      Object var8 = call(this.server, "getPluginManager");
      Enum var9 = Enum.valueOf(var4, "HIGHEST");
      Method var10 = var8.getClass().getMethod("registerEvent", Class.class, var2, var4, var3, var5);

      for (String var12 : Arrays.asList(
         "org.bukkit.event.player.PlayerCommandPreprocessEvent",
         "org.bukkit.event.server.ServerCommandEvent",
         "org.bukkit.event.server.RemoteServerCommandEvent",
         "org.bukkit.event.server.TabCompleteEvent",
         "org.bukkit.event.player.PlayerChatTabCompleteEvent",
         "org.bukkit.event.player.PlayerCommandSendEvent"
      )) {
         Class var13;
         try {
            var13 = Class.forName(var12);
         } catch (ClassNotFoundException var15) {
            continue;
         }

         var10.invoke(var8, var13, var6, var9, var7, this.plugin);
      }
   }

   public void handle(Object var1) throws Exception {
      String var2 = var1.getClass().getSimpleName();
      if (!var2.equals("PlayerCommandPreprocessEvent") && !var2.endsWith("ServerCommandEvent")) {
         String var13 = var2.equals("PlayerCommandSendEvent")
            ? "getCommands"
            : (var2.equals("PlayerChatTabCompleteEvent") ? "getTabCompletions" : "getCompletions");
         String var14 = null;
         if (var2.equals("PlayerChatTabCompleteEvent")) {
            var14 = (String)call(var1, "getChatMessage");
            if (!var14.startsWith("/")) {
               return;
            }
         }

         Object var15 = call(var1, var13);
         if (var15 instanceof Collection) {
            Collection var16 = (Collection)var15;
            Iterator var7 = var16.iterator();

            while (var7.hasNext()) {
               if (blocked(String.valueOf(var7.next()))) {
                  var7.remove();
               }
            }

            if (var2.equals("TabCompleteEvent") || var2.equals("PlayerChatTabCompleteEvent")) {
               String var17 = var14 != null ? var14 : (String)call(var1, "getBuffer");
               if (blocked(var17)) {
                  var16.clear();
               } else {
                  int var8 = Math.max(var17.lastIndexOf(32), var17.lastIndexOf(9));
                  String var9 = var8 < 0 ? "" : var17.substring(0, var8 + 1);
                  Iterator var10 = var16.iterator();

                  while (var10.hasNext()) {
                     if (blocked(var9 + String.valueOf(var10.next()))) {
                        var10.remove();
                     }
                  }
               }
            }
         }
      } else {
         boolean var3 = var2.equals("PlayerCommandPreprocessEvent");
         String var4 = (String)call(var1, var3 ? "getMessage" : "getCommand");
         if (blocked(var4)) {
            boolean var5 = false;

            try {
               var5 = (Boolean)call(var1, "isCancelled");
            } catch (NoSuchMethodException var11) {
            }

            try {
               call(var1, "setCancelled", new Class[]{boolean.class}, true);
            } catch (NoSuchMethodException var12) {
               if (var3) {
                  throw var12;
               }
            }

            if (!var3) {
               call(var1, "setCommand", new Class[]{String.class}, "");
            }

            Object var6 = call(var1, var3 ? "getPlayer" : "getSender");
            if (!var5) {
               call(var6, "sendMessage", new Class[]{String.class}, "Unknown command. Type \"/help\" for help.");
            }
         }
      }
   }

   @Override
   public void run() {
      try {
         Object var1;
         try {
            var1 = call(this.server, "getCommandMap");
         } catch (NoSuchMethodException var11) {
            var1 = field(this.server.getClass(), "commandMap").get(this.server);
         }

         Map var2 = (Map)field(var1.getClass(), "knownCommands").get(var1);
         Set var3 = Collections.newSetFromMap(new IdentityHashMap());

         for (Object var5 : var2.entrySet()) {
            Entry var6 = (Entry)var5;
            Object var7 = var6.getValue();
            if (BLOCKED.contains(label(String.valueOf(var6.getKey()))) || BLOCKED.contains(label(String.valueOf(call(var7, "getName"))))) {
               var3.add(var7);
            }

            Object var8 = call(var7, "getAliases");
            if (var8 instanceof Iterable) {
               for (Object var10 : (Iterable)var8) {
                  if (BLOCKED.contains(label(String.valueOf(var10)))) {
                     var3.add(var7);
                  }
               }
            }
         }

         HashSet var13 = new HashSet();
         Iterator var14 = var2.entrySet().iterator();

         while (var14.hasNext()) {
            Entry var16 = (Entry)var14.next();
            if (var3.contains(var16.getValue()) || blocked(String.valueOf(var16.getKey()))) {
               var13.add(String.valueOf(var16.getKey()).toLowerCase(Locale.ROOT));
               if (var3.contains(var16.getValue()) && !path(String.valueOf(var16.getKey())).contains(" ")) {
                  BLOCKED.add(label(String.valueOf(var16.getKey())));
               }

               var14.remove();
            }
         }

         Object var15 = call(this.server, "getHelpMap");
         Collection var17 = (Collection)call(var15, "getHelpTopics");
         this.pruneTopics(var17, var13, Collections.newSetFromMap(new IdentityHashMap<>()));
      } catch (Exception var12) {
         this.logger
            .warning("Squads could not remove disabled command registrations: " + var12.getClass().getSimpleName() + "; execution guards remain active.");
      }
   }

   private void pruneTopics(Collection var1, Set<String> var2, Set<Object> var3) throws Exception {
      if (var3.add(var1)) {
         Iterator var4 = var1.iterator();

         while (var4.hasNext()) {
            Object var5 = var4.next();
            String var6 = String.valueOf(call(var5, "getName"));
            String var7 = path(var6);
            if (!blocked(var6) && !var2.contains(label(var6)) && !var2.contains(var7)) {
               try {
                  Object var8 = field(var5.getClass(), "allTopics").get(var5);
                  if (var8 instanceof Collection) {
                     this.pruneTopics((Collection)var8, var2, var3);
                  }
               } catch (NoSuchFieldException var9) {
               }
            } else {
               var4.remove();
            }
         }
      }
   }
}
