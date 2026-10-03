package net.frozenorb.foxtrot.server.polls;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PollHandler {
   private List<Poll> polls = new ArrayList<>();

   public List<Poll> getActivePolls() {
      return this.polls.stream().filter(Poll::isActive).collect(Collectors.toList());
   }

   public Optional<Poll> findByQuestion(String question) {
      return this.polls.stream().filter(it -> it.getQuestion().equalsIgnoreCase(question)).findFirst();
   }

   public List<Poll> getPolls() {
      return this.polls;
   }
}
