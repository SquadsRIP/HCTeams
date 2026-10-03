package net.frozenorb.foxtrot.util;

import java.util.Arrays;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public class ProgressBarBuilder {
   private int blocksToDisplay;
   private char blockChar;
   private String completedColor;
   private String uncompletedColor;

   public ProgressBarBuilder(int blocksToDisplay) {
      this.blocksToDisplay = blocksToDisplay;
   }

   public ProgressBarBuilder() {
      this(10);
      this.blockChar = StringEscapeUtils.unescapeJava("█").charAt(0);
      this.completedColor = ChatColor.GREEN.toString();
      this.uncompletedColor = ChatColor.GRAY.toString();
   }

   public String build(double percentage) {
      Object[] blocks = new String[this.blocksToDisplay];
      Arrays.fill(blocks, this.uncompletedColor + this.blockChar);
      if (percentage > 100.0) {
         percentage = 100.0;
      }

      for (int i = 0; i < percentage / 10.0; i++) {
         blocks[i] = this.completedColor + this.blockChar;
      }

      return StringUtils.join(blocks);
   }

   public static double percentage(int value, int goal) {
      return value > goal ? 100.0 : (double)value / goal * 100.0;
   }

   public int blocksToDisplay() {
      return this.blocksToDisplay;
   }

   public char blockChar() {
      return this.blockChar;
   }

   public String completedColor() {
      return this.completedColor;
   }

   public String uncompletedColor() {
      return this.uncompletedColor;
   }

   public ProgressBarBuilder blocksToDisplay(int blocksToDisplay) {
      this.blocksToDisplay = blocksToDisplay;
      return this;
   }

   public ProgressBarBuilder blockChar(char blockChar) {
      this.blockChar = blockChar;
      return this;
   }

   public ProgressBarBuilder completedColor(String completedColor) {
      this.completedColor = completedColor;
      return this;
   }

   public ProgressBarBuilder uncompletedColor(String uncompletedColor) {
      this.uncompletedColor = uncompletedColor;
      return this;
   }
}
