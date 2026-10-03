package net.frozenorb.foxtrot.controlpanel;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import net.frozenorb.foxtrot.Foxtrot;

public class ControlPanelService {
   private static String API_URL = "";
   private static String API_KEY = "";
   private static final ExecutorService executor = Executors.newSingleThreadExecutor();
   private static boolean enabled = false;

   public static void init() {
      API_KEY = Foxtrot.getInstance().getConfig().getString("controlPanel.apiKey", "");
      if (API_KEY == null) API_KEY = "";
      String configUrl = Foxtrot.getInstance().getConfig().getString("controlPanel.url", "");
      if (configUrl != null && !configUrl.isEmpty()) {
         configUrl = configUrl.endsWith("/") ? configUrl.substring(0, configUrl.length() - 1) : configUrl;
         API_URL = configUrl.endsWith("/api") ? configUrl : configUrl + "/api";
      }

      if (API_URL.isEmpty() || API_KEY.isEmpty()) {
         Foxtrot.getInstance()
            .getLogger()
            .info("[ControlPanel] Missing controlPanel.url or controlPanel.apiKey in config.yml — logging disabled.");
         enabled = false;
      } else {
         try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            int code = conn.getResponseCode();
            if (code != 200 && code != 401 && code != 308) {
               Foxtrot.getInstance().getLogger().warning("[ControlPanel] Control panel returned HTTP " + code + " — logging disabled.");
            } else {
               enabled = true;
               Foxtrot.getInstance().getLogger().info("[ControlPanel] Connected to Squads Control Panel at " + API_URL);
            }
         } catch (Exception e) {
            Foxtrot.getInstance().getLogger().info("[ControlPanel] Control panel not reachable at " + API_URL + " — logging disabled.");
            enabled = false;
         }
      }
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static void logChat(String player, String message) {
      if (enabled) {
         postAsync(
            "/log/chat", String.format("{\"player\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}", escape(player), escape(message), System.currentTimeMillis())
         );
      }
   }

   public static void logKill(String killer, String victim, String weapon, double x, double y, double z, String world) {
      if (enabled) {
         postAsync(
            "/log/kill",
            String.format(
               "{\"killer\":\"%s\",\"victim\":\"%s\",\"weapon\":\"%s\",\"locationX\":%.1f,\"locationY\":%.1f,\"locationZ\":%.1f,\"world\":\"%s\",\"timestamp\":%d}",
               escape(killer),
               escape(victim),
               escape(weapon),
               x,
               y,
               z,
               escape(world),
               System.currentTimeMillis()
            )
         );
      }
   }

   public static void logItem(String admin, String target, String item, int amount) {
      if (enabled) {
         postAsync(
            "/log/item",
            String.format(
               "{\"admin\":\"%s\",\"target\":\"%s\",\"item\":\"%s\",\"amount\":%d,\"timestamp\":%d}",
               escape(admin),
               escape(target),
               escape(item),
               amount,
               System.currentTimeMillis()
            )
         );
      }
   }

   public static void logCommand(String player, String command) {
      if (enabled) {
         postAsync(
            "/log/command",
            String.format("{\"player\":\"%s\",\"command\":\"%s\",\"timestamp\":%d}", escape(player), escape(command), System.currentTimeMillis())
         );
      }
   }

   public static void logPlayer(String name, String uuid, long lastSeen, long playtime, int kills, int deaths) {
      if (enabled) {
         postAsync(
            "/log/player",
            String.format(
               "{\"name\":\"%s\",\"uuid\":\"%s\",\"lastSeen\":%d,\"playtime\":%d,\"kills\":%d,\"deaths\":%d}",
               escape(name),
               escape(uuid),
               lastSeen,
               playtime,
               kills,
               deaths
            )
         );
      }
   }

   public static void updateStatus(int onlinePlayers, double tps, long uptime) {
      if (enabled) {
         postAsync("/status", String.format("{\"onlinePlayers\":%d,\"tps\":%.1f,\"uptime\":%d}", onlinePlayers, tps, uptime));
      }
   }

   public static String issueAccessCode() {
      String code = generateCode();

      try {
         URL url = new URL(API_URL + "/auth/issue-code");
         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setRequestProperty("X-API-Key", API_KEY);
         conn.setDoOutput(true);
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(5000);
         conn.setInstanceFollowRedirects(true);
         String body = String.format("{\"code\":\"%s\"}", code);
         OutputStream os = conn.getOutputStream();

         try {
            os.write(body.getBytes(Charset.forName("UTF-8")));
         } catch (Throwable var8) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (os != null) {
            os.close();
         }

         int responseCode = conn.getResponseCode();
         if (responseCode == 200 || responseCode == 201) {
            return code;
         }

         Foxtrot.getInstance().getLogger().warning("[ControlPanel] issueAccessCode got HTTP " + responseCode);
      } catch (Exception e) {
         Foxtrot.getInstance().getLogger().log(Level.WARNING, "[ControlPanel] Failed to issue access code", e);
      }

      return null;
   }

   private static String generateCode() {
      String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < 6; i++) {
         sb.append(chars.charAt((int)(Math.random() * chars.length())));
      }

      return sb.toString();
   }

   private static void postAsync(String path, String jsonBody) {
      executor.submit(() -> {
         try {
            URL url = new URL(API_URL + path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-API-Key", API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            OutputStream os = conn.getOutputStream();

            try {
               os.write(jsonBody.getBytes(Charset.forName("UTF-8")));
            } catch (Throwable t$) {
               if (os != null) {
                  try {
                     os.close();
                  } catch (Throwable x2) {
                     t$.addSuppressed(x2);
                  }
               }

               throw t$;
            }

            if (os != null) {
               os.close();
            }

            conn.getResponseCode();
         } catch (Exception var9) {
         }
      });
   }

   private static String escape(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
   }
}
