package net.frozenorb.foxtrot.util;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class Minecraft {
   public static final Minecraft.Version VERSION = Minecraft.Version.getVersion();

   public static String getVersion() {
      return VERSION.name() + ".";
   }

   public static Object getHandle(Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getHandle")).invoke(object);
   }

   public static Entity getBukkitEntity(Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return (Entity)AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getBukkitEntity")).invoke(object);
   }

   public static Object getHandleSilent(Object object) {
      try {
         return getHandle(object);
      } catch (Exception e) {
         return null;
      }
   }

   static {
      System.out.println("[ReflectionHelper] Version is " + VERSION);
   }

   public enum Version {
      UNKNOWN(-1) {
         @Override
         public boolean matchesPackageName(String packageName) {
            return false;
         }
      },
      v1_7_R1(10701),
      v1_7_R2(10702),
      v1_7_R3(10703),
      v1_7_R4(10704),
      v1_8_R1(10801),
      v1_8_R2(10802),
      v1_8_R3(10803),
      v1_8_R4(10804),
      v1_9_R1(109001);

      private int version;

      Version(int version) {
         this.version = version;
      }

      public int version() {
         return this.version;
      }

      public boolean olderThan(Minecraft.Version version) {
         return this.version() < version.version();
      }

      public boolean newerThan(Minecraft.Version version) {
         return this.version() >= version.version();
      }

      public boolean inRange(Minecraft.Version oldVersion, Minecraft.Version newVersion) {
         return this.newerThan(oldVersion) && this.olderThan(newVersion);
      }

      public boolean matchesPackageName(String packageName) {
         return packageName.toLowerCase().contains(this.name().toLowerCase());
      }

      public static Minecraft.Version getVersion() {
         String name = Bukkit.getServer().getClass().getPackage().getName();
         String versionPackage = name.substring(name.lastIndexOf(46) + 1) + ".";

         for (Minecraft.Version version : values()) {
            if (version.matchesPackageName(versionPackage)) {
               return version;
            }
         }

         System.err.println("[ReflectionHelper] Failed to find version enum for '" + name + "'/'" + versionPackage + "'");
         return UNKNOWN;
      }

      @Override
      public String toString() {
         return this.name() + " (" + this.version() + ")";
      }
   }
}
