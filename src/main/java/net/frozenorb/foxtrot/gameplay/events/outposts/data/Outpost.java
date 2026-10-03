package net.frozenorb.foxtrot.gameplay.events.outposts.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.ProgressBarUtil;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.cavepvp.entity.EntityHandler;
import org.cavepvp.entity.type.hologram.Hologram;

public abstract class Outpost {
   private ObjectId control = null;
   private Map<Team, Integer> pointCache = new HashMap<>();
   private List<UUID> players = new ArrayList<>();
   private List<ObjectId> attacking = new ArrayList<>();
   private AtomicDouble percentage = new AtomicDouble(0.0);
   private Location hologramLocation = null;
   private long lastMessage = 0L;
   private long timeCaptured = 0L;

   public abstract String getId();

   public abstract ChatColor getDisplayColor();

   public abstract Material getMaterial();

   public abstract int getSlot();

   public abstract List<String> getBenefits();

   public String getDisplayName() {
      return this.getDisplayColor() + ChatColor.BOLD.toString() + this.getId();
   }

   public String getFactionName() {
      return this.getId() + "Outpost";
   }

   public KOTH getKoth() {
      return (KOTH)Foxtrot.getInstance().getEventHandler().getEvent(this.getId() + "-Outpost");
   }

   public List<Team> findAttackers() {
      return this.attacking.stream().map(it -> Foxtrot.getInstance().getTeamHandler().getTeam(it)).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public Team findTeam() {
      return Foxtrot.getInstance().getTeamHandler().getTeam(this.getFactionName());
   }

   public Team findController() {
      return Foxtrot.getInstance().getTeamHandler().getTeam(this.control);
   }

   public OutpostStatus getStatus() {
      if (this.findController() != null) {
         boolean challenging = this.isChallenging();
         if (!this.attacking.isEmpty() && challenging) {
            return OutpostStatus.CONTESTED;
         } else if (this.attacking.isEmpty() && challenging) {
            return this.percentage.get() > 0.0 ? OutpostStatus.CONTROLLED : OutpostStatus.CONTROLLING;
         } else {
            return !this.attacking.isEmpty() ? OutpostStatus.NEUTRALIZING : OutpostStatus.CONTROLLED;
         }
      } else {
         return this.attacking.isEmpty()
            ? OutpostStatus.NEUTRAL
            : (this.attacking.size() == 1 ? (this.percentage.get() >= 0.0 ? OutpostStatus.CONTROLLING : OutpostStatus.NEUTRALIZING) : OutpostStatus.CONTESTED);
      }
   }

   public void updatePercentage() {
      OutpostStatus status = this.getStatus();
      switch (status) {
         case CONTROLLED:
         case CONTROLLING:
            if (this.percentage.get() < 100.0 && this.percentage.addAndGet(0.1) > 100.0) {
               this.percentage.set(100.0);
            }

            if (this.percentage.get() > 0.0 && this.percentage.get() % 20.0 == 0.0 && this.percentage.get() < 100.0) {
               for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                  onlinePlayer.sendMessage(
                     "&5[Outposts] &eThe &f" + this.getDisplayName() + " &ehas reached &a" + String.format("%.2f", this.percentage.get()) + "%&e!"
                  );
               }

               this.lastMessage = System.currentTimeMillis();
            }
            break;
         case NEUTRAL:
         case NEUTRALIZING:
            if (this.percentage.get() > 0.0 && this.percentage.addAndGet(-0.1) < 0.0) {
               this.percentage.set(0.0);
            }

            if (this.percentage.get() > 0.0 && this.percentage.get() % 20.0 == 0.0 && this.percentage.get() < 100.0) {
               if (System.currentTimeMillis() - this.lastMessage < 12000L) {
                  return;
               }

               for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                  onlinePlayer.sendMessage(
                     ChatColor.translate(
                        "&5[Outposts] &eThe &f"
                           + this.getDisplayName()
                           + " &eis being neutralized and is now at &a"
                           + String.format("%.2f", this.percentage.get())
                           + "%&e!"
                     )
                  );
               }

               this.lastMessage = System.currentTimeMillis();
            }
      }
   }

   public void updateController() {
      if (this.findAttackers().size() == 1) {
         this.setController(new ArrayList<>(this.findAttackers()).get(0));
         if (this.findController() != null) {
            this.removeAttacker(this.findController());
         }
      }
   }

   public void setController(Team controller) {
      this.setController(controller, false);
   }

   public void setController(Team controller, boolean onlySet) {
      if (!onlySet) {
         if (controller != null) {
            String[] messages = new String[]{
               ChatColor.GRAY + "███████",
               ChatColor.GRAY + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█ " + ChatColor.GOLD + "[Outpost]",
               ChatColor.GRAY
                  + "█"
                  + ChatColor.GOLD
                  + "█"
                  + ChatColor.GRAY
                  + "███"
                  + ChatColor.GOLD
                  + "█"
                  + ChatColor.GRAY
                  + "█ "
                  + ChatColor.YELLOW
                  + controller.getName()
                  + " has",
               ChatColor.GRAY
                  + "█"
                  + ChatColor.GOLD
                  + "█"
                  + ChatColor.GRAY
                  + "███"
                  + ChatColor.GOLD
                  + "█"
                  + ChatColor.GRAY
                  + "█"
                  + ChatColor.YELLOW
                  + " gained control of",
               ChatColor.GRAY
                  + "█"
                  + ChatColor.GOLD
                  + "█"
                  + ChatColor.GRAY
                  + "███"
                  + ChatColor.GOLD
                  + "█"
                  + ChatColor.GRAY
                  + "█ "
                  + ChatColor.translate(this.getDisplayName() + " Outpost"),
               ChatColor.GRAY + "█" + ChatColor.GOLD + "█████" + ChatColor.GRAY + "█",
               ChatColor.GRAY + "███████"
            };

            for (String message : messages) {
               Foxtrot.getInstance().getServer().broadcastMessage(message);
            }

            this.timeCaptured = System.currentTimeMillis();
         } else if (this.control != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
               player.sendMessage(" ");
               player.sendMessage(
                  ChatColor.translate(
                     "&6[Outpost] &f" + this.findController().getName(player) + " &ehas lost control of &f" + this.getDisplayName() + " Outpost&e!"
                  )
               );
               player.sendMessage(" ");
            }
         }
      }

      if (controller != null) {
         this.control = controller.getUniqueId();
      } else {
         this.control = null;
      }
   }

   public long getTimeSinceCapture() {
      return this.timeCaptured == 0L ? 0L : System.currentTimeMillis() - this.timeCaptured;
   }

   public List<Player> findPlayers() {
      List<Player> toReturn = new ArrayList<>();

      for (UUID uuid : new ArrayList<>(this.players)) {
         Player player = Foxtrot.getInstance().getServer().getPlayer(uuid);
         if (player == null || !player.isOnline()) {
            this.players.remove(uuid);
         } else if (!this.onCapzone(player.getLocation())) {
            this.players.remove(uuid);
         } else {
            toReturn.add(player);
         }
      }

      return toReturn;
   }

   public void updateAttackers() {
      this.attacking.removeIf(attacker -> {
         Team teamx = Foxtrot.getInstance().getTeamHandler().getTeam(attacker);
         return this.players.stream().noneMatch(teamx::isMember);
      });

      for (Player player : this.findPlayers()) {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         if (this.findController() == null || !this.findController().equals(team)) {
            this.addAttacker(team);
         }
      }
   }

   public void checkOld() {
      this.findPlayers().removeIf(uuid -> Foxtrot.getInstance().getOutpostHandler().findOutpost(uuid.getLocation()) != this);
      this.findAttackers().removeIf(attacker -> this.findPlayers().stream().noneMatch(player -> attacker.isMember(player.getUniqueId())));
   }

   public boolean onCapzone(Location location) {
      return this.getKoth() == null ? false : this.getKoth().onCap(location);
   }

   public void updateHologram() {
      Hologram hologram = (Hologram)EntityHandler.INSTANCE.getEntityByName(this.getId() + "-Hologram");
      if (hologram != null) {
         hologram.setText(0, ChatColor.translate(this.getDisplayName() + " Outpost"));
         hologram.setText(1, ChatColor.translate("&8&m------------------------------"));
         hologram.setText(2, ChatColor.translate("&7Status: &f" + this.getStatus().getDisplayName()));
         hologram.setText(3, ChatColor.translate("&7Controller: &c" + (this.findController() != null ? this.findController().getName() : "None")));
         if (this.findAttackers().isEmpty()) {
            hologram.setText(4, ChatColor.translate("&7Attackers: &cNone"));
         } else {
            hologram.setText(
               4,
               ChatColor.translate(
                  "&7Attacker"
                     + (this.attacking.size() == 1 ? "" : "s")
                     + ": "
                     + this.findAttackers().stream().map(faction -> "&c" + faction.getName()).collect(Collectors.joining("&f,"))
               )
            );
         }

         hologram.setText(
            5,
            ChatColor.translate(
               "&7Percentage: &f" + (this.percentage.get() == 0.0 ? ChatColor.RED : ChatColor.GREEN) + String.format("%.2f", this.percentage.get()) + "%"
            )
         );
         hologram.setText(
            6,
            ChatColor.translate(
               "&7[" + ProgressBarUtil.getProgressBar(Math.min(this.percentage.intValue(), 100), 100, 24, "■", ChatColor.GREEN, ChatColor.RED) + " &7]"
            )
         );
         hologram.setText(7, ChatColor.translate("&8&m------------------------------"));
      }
   }

   public boolean addAttacker(Team faction) {
      return this.attacking.contains(faction.getUniqueId()) ? false : this.attacking.add(faction.getUniqueId());
   }

   public boolean removeAttacker(Team faction) {
      return this.attacking.remove(faction.getUniqueId());
   }

   public boolean addPlayer(Player player) {
      return this.players.contains(player.getUniqueId()) ? false : this.players.add(player.getUniqueId());
   }

   public boolean removePlayer(Player player) {
      return this.players.remove(player.getUniqueId());
   }

   public boolean hasPlayer(Player player) {
      return this.players.contains(player.getUniqueId());
   }

   public boolean isChallenging() {
      return this.findController() == null
         ? false
         : this.players
            .stream()
            .anyMatch(
               player -> Foxtrot.getInstance().getTeamHandler().getTeam(player) != null
                  && Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(this.findController())
            );
   }

   public ObjectId getControl() {
      return this.control;
   }

   public void setControl(ObjectId control) {
      this.control = control;
   }

   public Map<Team, Integer> getPointCache() {
      return this.pointCache;
   }

   public List<UUID> getPlayers() {
      return this.players;
   }

   public void setPlayers(List<UUID> players) {
      this.players = players;
   }

   public List<ObjectId> getAttacking() {
      return this.attacking;
   }

   public void setAttacking(List<ObjectId> attacking) {
      this.attacking = attacking;
   }

   public AtomicDouble getPercentage() {
      return this.percentage;
   }

   public void setPercentage(AtomicDouble percentage) {
      this.percentage = percentage;
   }

   public Location getHologramLocation() {
      return this.hologramLocation;
   }

   public void setHologramLocation(Location hologramLocation) {
      this.hologramLocation = hologramLocation;
   }

   public long getLastMessage() {
      return this.lastMessage;
   }

   public void setLastMessage(long lastMessage) {
      this.lastMessage = lastMessage;
   }

   public long getTimeCaptured() {
      return this.timeCaptured;
   }

   public void setTimeCaptured(long timeCaptured) {
      this.timeCaptured = timeCaptured;
   }
}
