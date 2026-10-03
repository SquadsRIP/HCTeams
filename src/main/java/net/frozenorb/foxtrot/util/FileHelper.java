package net.frozenorb.foxtrot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class FileHelper {
   public static String readFile(File file) {
      StringBuilder builder = new StringBuilder();

      try {
         BufferedReader reader = new BufferedReader(new FileReader(file));

         String line;
         try {
            while ((line = reader.readLine()) != null) {
               builder.append(line).append("\n");
            }
         } catch (Throwable var6) {
            try {
               reader.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         reader.close();
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      return builder.toString();
   }

   public static void writeFile(File file, String content) {
      try {
         FileWriter writer = new FileWriter(file);

         try {
            writer.write(content);
         } catch (Throwable var6) {
            try {
               writer.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         writer.close();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private FileHelper() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
