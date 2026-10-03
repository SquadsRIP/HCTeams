package net.frozenorb.foxtrot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResolverQuery {
   private String name;
   private Class<?>[] types;

   public ResolverQuery(String name, Class<?>... types) {
      this.name = name;
      this.types = types;
   }

   public ResolverQuery(String name) {
      this.name = name;
      this.types = new Class[0];
   }

   public ResolverQuery(Class<?>... types) {
      this.types = types;
   }

   public String getName() {
      return this.name;
   }

   public Class<?>[] getTypes() {
      return this.types;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ResolverQuery that = (ResolverQuery)o;
         return (this.name != null ? !this.name.equals(that.name) : that.name != null) ? false : Arrays.equals(this.types, that.types);
      } else {
         return false;
      }
   }

   public static ResolverQuery.Builder builder() {
      return new ResolverQuery.Builder();
   }

   public static class Builder {
      private List<ResolverQuery> queryList = new ArrayList<>();

      private Builder() {
      }

      public ResolverQuery.Builder with(String name, Class<?>[] types) {
         this.queryList.add(new ResolverQuery(name, types));
         return this;
      }

      public ResolverQuery.Builder with(String name) {
         this.queryList.add(new ResolverQuery(name));
         return this;
      }

      public ResolverQuery.Builder with(Class<?>[] types) {
         this.queryList.add(new ResolverQuery(types));
         return this;
      }

      public ResolverQuery[] build() {
         return this.queryList.toArray(new ResolverQuery[this.queryList.size()]);
      }
   }
}
