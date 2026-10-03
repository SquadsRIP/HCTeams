package net.frozenorb.foxtrot.gameplay.events.region.glowmtn;

import java.io.File;
import java.io.IOException;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.listeners.GlowListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GlowHandler {
   private static File file;
   private static final String glowTeamName = "Glowstone";
   private GlowMountain glowMountain;
   private long lastReset = System.currentTimeMillis();

   public GlowHandler() {
      try {
         file = new File(Foxtrot.getInstance().getDataFolder(), "data/glowmtn.json");
         if (!file.exists()) {
            this.glowMountain = null;
            if (file.createNewFile()) {
               Foxtrot.getInstance().getLogger().warning("Created a new glow mountain json file.");
            }
         } else {
            this.glowMountain = Foxtrot.GSON.fromJson(FileUtils.readFileToString(file), GlowMountain.class);
            Foxtrot.getInstance().getLogger().info("Successfully loaded the glow mountain from file");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      (new BukkitRunnable() {
         @Override
         public void run() {
            GlowHandler.this.ensureGlowMountain();
         }
      }).runTaskLater(Foxtrot.getInstance(), 100L);
      int intervalSeconds = this.getBroadcastInterval();
      long intervalTicks = intervalSeconds * 20L;
      Foxtrot.getInstance()
         .getServer()
         .getScheduler()
         .runTaskTimer(
            Foxtrot.getInstance(),
            () -> {
               if (!this.hasGlowMountain()) {
                  this.ensureGlowMountain();
               }

               if (!this.hasGlowMountain()) {
                  System.out.println("[Glowstone Mountain] No glowstone mountain found — skipping auto-reset.");
               } else {
                  int restored = this.getGlowMountain().reset();
                  this.lastReset = System.currentTimeMillis();
                  Bukkit.broadcastMessage("");
                  Bukkit.broadcastMessage(
                     ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.GREEN + " All glowstone has been reset! (" + restored + " blocks restored)"
                  );
                  Bukkit.broadcastMessage("");
               }
            },
            intervalTicks,
            intervalTicks
         );
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), Foxtrot.getInstance());
   }

   public void ensureGlowMountain() {
      if (this.glowMountain == null) {
         Team glowTeam = Foxtrot.getInstance().getTeamHandler().getTeam("Glowstone");
         if (glowTeam == null) {
            Foxtrot.getInstance().getLogger().warning("[Glowstone Mountain] No team named 'Glowstone' found — create & claim it, then run /glow scan.");
         } else if (glowTeam.getClaims().isEmpty()) {
            Foxtrot.getInstance().getLogger().warning("[Glowstone Mountain] Team 'Glowstone' has no claim — claim land for it, then run /glow scan.");
         } else {
            try {
               this.glowMountain = new GlowMountain();
               this.glowMountain.scan();
               this.save();
               Foxtrot.getInstance()
                  .getLogger()
                  .info("[Glowstone Mountain] Auto-scanned " + this.glowMountain.getGlowstone().size() + " glowstone blocks on startup.");
            } catch (Throwable t) {
               Foxtrot.getInstance().getLogger().warning("[Glowstone Mountain] Auto-scan failed: " + t.getMessage());
               this.glowMountain = null;
            }
         }
      }
   }

   public void save() {
      try {
         FileUtils.write(file, Foxtrot.GSON.toJson(this.glowMountain));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public boolean hasGlowMountain() {
      return this.glowMountain != null;
   }

   public static Claim getClaim() {
      return Foxtrot.getInstance().getTeamHandler().getTeam("Glowstone").getClaims().get(0);
   }

   private int getBroadcastInterval() {
      return Foxtrot.getInstance().getServerHandler().isTeams() ? 3600 : 600;
   }

   public long getResetTime() {
      long elapsed = System.currentTimeMillis() - this.lastReset;
      long intervalMillis = this.getBroadcastInterval() * 1000L;
      long remaining = intervalMillis - elapsed;
      return Math.max(0L, remaining);
   }

   public static String getGlowTeamName() {
      return "Glowstone";
   }

   public GlowMountain getGlowMountain() {
      return this.glowMountain;
   }

   public void setGlowMountain(GlowMountain glowMountain) {
      this.glowMountain = glowMountain;
   }

   public long getLastReset() {
      return this.lastReset;
   }

   public void setLastReset(long lastReset) {
      this.lastReset = lastReset;
   }
}
