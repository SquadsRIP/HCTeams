package net.frozenorb.foxtrot.gameplay.events.outposts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.EndOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.NetherOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.RoadOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.kitmap.KitmapOutpost;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class OutpostHandler {
   private Foxtrot instance;
   private File file;
   private FileConfiguration data;
   private List<Outpost> outposts = new ArrayList<>();

   public OutpostHandler(Foxtrot instance) {
      this.instance = instance;
   }

   public void loadData() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/outpost.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.getConfigurationSection("outposts") != null) {
         for (String key : this.data.getConfigurationSection("outposts").getKeys(false)) {
            Outpost outpost = this.findOutpost(key);
            if (outpost != null) {
               if (this.data.contains("outposts." + key + ".controller")) {
                  ObjectId controlId = new ObjectId(this.data.getString("outposts." + key + ".controller"));
                  Team team = Foxtrot.getInstance().getTeamHandler().getTeam(controlId);
                  if (team != null) {
                     outpost.setControl(controlId);
                  }
               }

               outpost.setPercentage(new AtomicDouble(this.data.getDouble("outposts." + key + ".percentage")));
            }
         }
      }
   }

   public void saveData() {
      if (this.data != null && this.file != null) {
         this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

         for (Outpost outpost : this.outposts) {
            if (outpost.getControl() != null) {
               this.data.set("outposts." + outpost.getId() + ".controller", outpost.getControl().toString());
            }

            this.data.set("outposts." + outpost.getId() + ".percentage", outpost.getPercentage().get());
         }

         try {
            this.data.save(this.file);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void loadOutposts() {
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         this.outposts.add(new KitmapOutpost());
      } else {
         this.outposts.add(new NetherOutpost());
         this.outposts.add(new EndOutpost());
      }
   }

   public Outpost findOutpost(String id) {
      return this.outposts.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   public Outpost findRoadOutpost() {
      // Keep the original public return descriptor for binary plugin compatibility.
      return this.findTypedRoadOutpost();
   }

   public RoadOutpost findTypedRoadOutpost() {
      // Do not register Road Outpost here: the supplied server does not load it.
      // Look up only an explicitly registered instance, preserving that default.
      return this.outposts.stream().filter(RoadOutpost.class::isInstance).map(RoadOutpost.class::cast).findFirst().orElse(null);
   }

   public Outpost findKitsOutpost() {
      return null;
   }

   public List<Outpost> findControllingOutposts(Team team) {
      return this.findControllingOutpost(team.getUniqueId());
   }

   public List<Outpost> findControllingOutpost(ObjectId objectId) {
      return this.getOutposts()
         .stream()
         .filter(outpost -> outpost.getControl() != null && outpost.getControl().toString().equals(objectId.toString()))
         .collect(Collectors.toList());
   }

   public Outpost findOutpost(Location location) {
      return this.outposts.stream().filter(it -> it.onCapzone(location)).findFirst().orElse(null);
   }

   public Foxtrot getInstance() {
      return this.instance;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }

   public List<Outpost> getOutposts() {
      return this.outposts;
   }
}
