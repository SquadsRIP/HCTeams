package net.frozenorb.foxtrot.util;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0006X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0013",
   d2 = {
         "Lnet/frozenorb/foxtrot/util/UnicodeUtil;",
         "",
         "()V",
         "BLOCK",
         "",
         "BULLETIN_POINT",
         "",
         "CAUTION",
         "CHECK_MARK",
         "CROSSED_MARK",
         "DOT_SYMBOL",
         "DOUBLE_ARROW_LEFT",
         "DOUBLE_ARROW_RIGHT",
         "EXP_SYMBOL",
         "HEART",
         "SMALL_ARROW",
         "SMALL_BLOCK",
         "THICK_VERTICAL_LINE",
         "VERTICAL_LINE",
         "HCTeams"
   }
)
public final class UnicodeUtil {
   @NotNull
   public static final UnicodeUtil INSTANCE = new UnicodeUtil();
   public static final char BLOCK = '⬛';
   public static final char SMALL_BLOCK = '▌';
   @NotNull
   public static final String HEART = "❤";
   @NotNull
   public static final String CAUTION = "⚠";
   @NotNull
   public static final String EXP_SYMBOL = "✪";
   @NotNull
   public static final String SMALL_ARROW = "►";
   @NotNull
   public static final String DOT_SYMBOL = "●";
   @NotNull
   public static final String BULLETIN_POINT = "•";
   @NotNull
   public static final String DOUBLE_ARROW_LEFT = "«";
   @NotNull
   public static final String DOUBLE_ARROW_RIGHT = "»";
   @NotNull
   public static final String VERTICAL_LINE = "┃";
   @NotNull
   public static final String THICK_VERTICAL_LINE = "❙";
   @NotNull
   public static final String CHECK_MARK = "✔";
   @NotNull
   public static final String CROSSED_MARK = "❌";

   private UnicodeUtil() {
   }
}
