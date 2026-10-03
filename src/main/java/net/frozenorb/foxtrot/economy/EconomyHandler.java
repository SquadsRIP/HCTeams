package net.frozenorb.foxtrot.economy;

import cc.fyre.proton.Proton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EconomyHandler {
   private Foxtrot instance;
   private Map<UUID, Double> balances = new HashMap<>();

   public EconomyHandler(final Foxtrot instance) {
      this.instance = instance;
      instance.getServer().getPluginManager().registerEvents(new Listener() {
         @EventHandler
         public void onPlayerQuit(PlayerQuitEvent event) {
            instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> EconomyHandler.this.save(event.getPlayer().getUniqueId()));
         }
      }, instance);
      Proton.getInstance().runRedisCommand(redis -> {
         redis.keys("balance.*").forEach(it -> {
            UUID uuid = UUID.fromString(it.substring(8));
            this.balances.put(uuid, Double.parseDouble(redis.get(it)));
         });
         return null;
      });
   }

   public void setBalance(UUID uuid, double balance) {
      this.balances.put(uuid, balance);
      this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () -> this.save(uuid));
   }

   public double getBalance(UUID uuid) {
      if (!this.balances.containsKey(uuid)) {
         this.load(uuid);
      }

      return this.balances.get(uuid);
   }

   public void withdraw(UUID uuid, double amount) {
      this.setBalance(uuid, this.getBalance(uuid) - amount);
      this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () -> this.save(uuid));
   }

   public void deposit(UUID uuid, double amount) {
      this.setBalance(uuid, this.getBalance(uuid) + amount);
      this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () -> this.save(uuid));
   }

   private double load(UUID uuid) {
      return CompletableFuture.<Double>supplyAsync(() -> Proton.getInstance().runRedisCommand(redis -> {
         double balance = 0.0;
         if (redis.exists("balance." + uuid.toString())) {
            balance = Double.parseDouble(redis.get("balance." + uuid.toString()));
         }

         this.balances.put(uuid, balance);
         return balance;
      })).join();
   }

   private void save(UUID uuid) {
      Proton.getInstance().runRedisCommand(redis -> redis.set("balance." + uuid.toString(), String.valueOf(this.getBalance(uuid))));
   }

   public void save() {
      Proton.getInstance().runRedisCommand(redis -> {
         for (Entry<UUID, Double> entry : this.balances.entrySet()) {
            redis.set("balance." + entry.getKey().toString(), String.valueOf(entry.getValue()));
         }

         return null;
      });
   }

   public Map<UUID, Double> getBalances() {
      return this.balances;
   }
}
