package net.frozenorb.foxtrot.server.polls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;

public class Poll {
   private String question;
   private List<String> options;
   private Map<UUID, String> votes = new HashMap<>();
   private boolean active;

   public Poll(String question, List<String> options) {
      this.question = question;
      this.options = options;
      this.active = true;
      Foxtrot.getInstance().getPollHandler().getPolls().add(this);
   }

   public String getQuestion() {
      return this.question;
   }

   public List<String> getOptions() {
      return this.options;
   }

   public Map<UUID, String> getVotes() {
      return this.votes;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }
}
