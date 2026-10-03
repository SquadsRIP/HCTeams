package net.frozenorb.foxtrot.util;

public class ClassResolver extends ResolverAbstract<Class> {
   public Class resolveSilent(String... names) {
      try {
         return this.resolve(names);
      } catch (Exception e) {
         return null;
      }
   }

   public Class resolve(String... names) throws ClassNotFoundException {
      ResolverQuery.Builder builder = ResolverQuery.builder();

      for (String name : names) {
         builder.with(name);
      }

      try {
         return (Class)super.resolve(builder.build());
      } catch (ReflectiveOperationException e) {
         throw (ClassNotFoundException)e;
      }
   }

   protected Class resolveObject(ResolverQuery query) throws ReflectiveOperationException {
      return Class.forName(query.getName());
   }

   protected ClassNotFoundException notFoundException(String joinedNames) {
      return new ClassNotFoundException("Could not resolve class for " + joinedNames);
   }
}
