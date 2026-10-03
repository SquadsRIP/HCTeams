package net.frozenorb.foxtrot.util;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResolverAbstract<T> {
   protected final Map<ResolverQuery, T> resolvedObjects = new ConcurrentHashMap<>();

   protected T resolveSilent(ResolverQuery... queries) {
      try {
         return this.resolve(queries);
      } catch (Exception e) {
         return null;
      }
   }

   protected T resolve(ResolverQuery... queries) throws ReflectiveOperationException {
      if (queries != null && queries.length > 0) {
         for (ResolverQuery query : queries) {
            if (this.resolvedObjects.containsKey(query)) {
               return this.resolvedObjects.get(query);
            }

            try {
               T resolved = this.resolveObject(query);
               this.resolvedObjects.put(query, resolved);
               return resolved;
            } catch (ReflectiveOperationException e) {
            }
         }

         throw this.notFoundException(Arrays.asList(queries).toString());
      } else {
         throw new IllegalArgumentException("Given possibilities are empty");
      }
   }

   protected abstract T resolveObject(ResolverQuery var1) throws ReflectiveOperationException;

   protected ReflectiveOperationException notFoundException(String joinedNames) {
      return new ReflectiveOperationException("Objects could not be resolved: " + joinedNames);
   }
}
