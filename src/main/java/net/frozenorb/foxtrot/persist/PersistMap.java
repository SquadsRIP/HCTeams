package net.frozenorb.foxtrot.persist;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class PersistMap<T> {
   protected Map<UUID, T> wrappedMap = new ConcurrentHashMap<>();
   private String keyPrefix;
   private String mongoName;
   private boolean useMongo;

   public PersistMap(String keyPrefix, String mongoName) {
      this(keyPrefix, mongoName, true);
   }

   public PersistMap(String keyPrefix, String mongoName, boolean useMongo) {
      this.keyPrefix = keyPrefix;
      this.mongoName = mongoName;
      this.useMongo = useMongo;
      this.loadFromRedis();
   }

   public void loadFromRedis() {
      Proton.getInstance().runRedisCommand(redis -> {
         Map<String, String> results = redis.hgetAll(this.keyPrefix);

         for (Entry<String, String> resultEntry : results.entrySet()) {
            T object = this.getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());
            if (object != null) {
               this.wrappedMap.put(UUID.fromString(resultEntry.getKey()), object);
            }
         }

         return null;
      });
   }

   protected void wipeValues() {
      this.wrappedMap.clear();
      Proton.getInstance().runRedisCommand(redis -> {
         redis.del(this.keyPrefix);
         return null;
      });
   }

   protected void updateValueSync(UUID key, T value) {
      this.wrappedMap.put(key, value);
      Proton.getInstance().runRedisCommand(redis -> {
         redis.hset(this.keyPrefix, key.toString(), this.getRedisValue(this.getValue(key)));
         boolean succeeded = false;
         int tries = 0;

         while (this.useMongo && !succeeded && tries++ < 5) {
            try {
               DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
               BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));
               BasicDBObject toSet = new BasicDBObject(this.mongoName, this.getMongoValue(this.getValue(key)));
               toSet.put("lastUsername", UUIDUtils.name(key));
               playersCollection.update(player, new BasicDBObject("$set", toSet), true, false);
               succeeded = true;
            } catch (Exception e) {
               succeeded = false;
            }
         }

         if (this.useMongo && !succeeded) {
            throw new RuntimeException("Mongo insert failed after five retries! User: " + key + ".");
         } else {
            return null;
         }
      });
   }

   protected void updateValueAsync(final UUID key, T value) {
      this.wrappedMap.put(key, value);
      (new BukkitRunnable() {
         @Override
         public void run() {
            Proton.getInstance().runRedisCommand(redis -> {
               redis.hset(PersistMap.this.keyPrefix, key.toString(), PersistMap.this.getRedisValue(PersistMap.this.getValue(key)));
               boolean succeeded = false;
               int tries = 0;

               while (PersistMap.this.useMongo && !succeeded && tries++ < 5) {
                  try {
                     DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                     BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));
                     BasicDBObject toSet = new BasicDBObject(PersistMap.this.mongoName, PersistMap.this.getMongoValue(PersistMap.this.getValue(key)));
                     toSet.put("lastUsername", UUIDUtils.name(key));
                     playersCollection.update(player, new BasicDBObject("$set", toSet), true, false);
                     succeeded = true;
                  } catch (Exception e) {
                     succeeded = false;
                  }
               }

               if (PersistMap.this.useMongo && !succeeded) {
                  throw new RuntimeException("Mongo update failed after five retries! User: " + key + ".");
               } else {
                  return null;
               }
            });
         }
      }).runTaskAsynchronously(Foxtrot.getInstance());
   }

   protected T getValue(UUID key) {
      return this.wrappedMap.get(key);
   }

   protected boolean contains(UUID key) {
      return this.wrappedMap.containsKey(key);
   }

   public abstract String getRedisValue(T var1);

   public abstract Object getMongoValue(T var1);

   public T getJavaObjectSafe(String key, String redisValue) {
      try {
         return this.getJavaObject(redisValue);
      } catch (Exception e) {
         System.out.println("Error parsing Redis result.");
         System.out.println(" - Prefix: " + this.keyPrefix);
         System.out.println(" - Key: " + key);
         System.out.println(" - Value: " + redisValue);
         return null;
      }
   }

   public abstract T getJavaObject(String var1);

   public Map<UUID, T> getWrappedMap() {
      return this.wrappedMap;
   }
}
