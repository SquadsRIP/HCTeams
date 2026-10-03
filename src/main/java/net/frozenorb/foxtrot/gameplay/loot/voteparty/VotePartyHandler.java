package net.frozenorb.foxtrot.gameplay.loot.voteparty;

import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.voteparty.listener.VoteListener;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

public class VotePartyHandler {
   private Foxtrot instance;
   private int currentVotes;
   private int votePartyRequirement = 300;

   public VotePartyHandler(Foxtrot instance) {
      this.instance = instance;
      this.instance.getServer().getPluginManager().registerEvents(new VoteListener(), this.instance);
      this.currentVotes = this.instance.getConfig().getInt("voteParty.currentVotes", 0);
   }

   public void saveData() {
      this.instance.getConfig().set("voteParty.currentVotes", this.currentVotes);
   }

   public void addVote() {
      this.currentVotes++;
      if (this.currentVotes >= this.votePartyRequirement) {
         this.start();
      }
   }

   public void start() {
      this.currentVotes = 0;
      final Server server = this.instance.getServer();
      final AtomicInteger seconds = new AtomicInteger(10);
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (seconds.get() <= 0) {
               server.dispatchCommand(server.getConsoleSender(), "cr giveallkey Items 1");
               server.dispatchCommand(server.getConsoleSender(), "cr giveallkey Gold 3");
               this.cancel();
            }
         }
      }).runTaskTimer(this.instance, 20L, 20L);
   }

   public int getCurrentVotes() {
      return this.currentVotes;
   }

   public void setCurrentVotes(int currentVotes) {
      this.currentVotes = currentVotes;
   }

   public int getVotePartyRequirement() {
      return this.votePartyRequirement;
   }

   public void setVotePartyRequirement(int votePartyRequirement) {
      this.votePartyRequirement = votePartyRequirement;
   }
}
