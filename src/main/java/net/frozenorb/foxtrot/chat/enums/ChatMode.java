package net.frozenorb.foxtrot.chat.enums;

import java.beans.ConstructorProperties;
import java.util.Set;
import net.minecraft.util.com.google.common.collect.ImmutableSet;

public enum ChatMode {
   PUBLIC(ImmutableSet.of('!')),
   ALLIANCE(ImmutableSet.of('#')),
   TEAM(ImmutableSet.of('@')),
   OFFICER(ImmutableSet.of('^'));

   Set<Character> forcedPrefixes;

   public static ChatMode findFromForcedPrefix(char forcedPrefix) {
      for (ChatMode chatMode : values()) {
         for (char chatModePrefix : chatMode.forcedPrefixes) {
            if (chatModePrefix == forcedPrefix) {
               return chatMode;
            }
         }
      }

      return null;
   }

   @ConstructorProperties("forcedPrefixes")
   ChatMode(Set<Character> forcedPrefixes) {
      this.forcedPrefixes = forcedPrefixes;
   }
}
