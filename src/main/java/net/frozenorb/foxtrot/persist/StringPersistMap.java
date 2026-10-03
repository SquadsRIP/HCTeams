package net.frozenorb.foxtrot.persist;

import cc.fyre.proton.Proton;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class StringPersistMap<T> {
   protected Map<String, T> wrappedMap = new ConcurrentHashMap<>();
   private String keyPrefix;
   private String mongoName;

   public StringPersistMap(String keyPrefix, String mongoName) {
      this.keyPrefix = keyPrefix;
      this.mongoName = mongoName;
      this.loadFromRedis();
   }

   public void loadFromRedis() {
      Proton.getInstance().runRedisCommand(redis -> {
         Map<String, String> results = redis.hgetAll(this.keyPrefix);

         for (Entry<String, String> resultEntry : results.entrySet()) {
            T object = this.getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());
            if (object != null) {
               this.wrappedMap.put(resultEntry.getKey(), object);
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

   protected void updateValueSync(String key, T value) {
      this.wrappedMap.put(key, value);
      Proton.getInstance().runRedisCommand(redis -> {
         redis.hset(this.keyPrefix, key, this.getRedisValue(this.getValue(key)));
         DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
         BasicDBObject player = new BasicDBObject("_id", key.replace("-", ""));
         playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(this.mongoName, this.getMongoValue(this.getValue(key)))), true, false);
         return null;
      });
   }

   protected void updateValueAsync(final String key, T value) {
      this.wrappedMap.put(key, value);
      (new BukkitRunnable() {
            @Override
            public void run() {
               Proton.getInstance()
                  .runRedisCommand(
                     redis -> {
                        redis.hset(StringPersistMap.this.keyPrefix, key, StringPersistMap.this.getRedisValue(StringPersistMap.this.getValue(key)));
                        DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                        BasicDBObject player = new BasicDBObject("_id", key.replace("-", ""));
                        playersCollection.update(
                           player,
                           new BasicDBObject(
                              "$set",
                              new BasicDBObject(StringPersistMap.this.mongoName, StringPersistMap.this.getMongoValue(StringPersistMap.this.getValue(key)))
                           ),
                           true,
                           false
                        );
                        return null;
                     }
                  );
            }
         })
         .runTaskAsynchronously(Foxtrot.getInstance());
   }

   protected T getValue(String key) {
      return this.wrappedMap.get(key);
   }

   protected boolean contains(String key) {
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
}
