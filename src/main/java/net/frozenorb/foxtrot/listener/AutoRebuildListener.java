package net.frozenorb.foxtrot.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import net.frozenorb.foxtrot.team.event.TeamRegenerateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoRebuildListener implements Listener {
   @EventHandler
   private void onTeamRaidable(TeamRaidableEvent event) {
      Team team = event.getTeam();
      List<Claim> claims = new ArrayList<>(team.getClaims());
      if (!claims.isEmpty()) {
         Thread thread = new Thread(() -> {
            for (final Claim claim : claims) {
               Location min = claim.getMinimumPoint();
               Location max = claim.getMaximumPoint();
               final World world = min.getWorld();
               final int minX = min.getBlockX();
               final int maxX = max.getBlockX();
               final int minZ = min.getBlockZ();
               final int maxZ = max.getBlockZ();
               System.out.println("Saving claim " + claim.getName());
               File file = new File(Foxtrot.getInstance().getDataFolder(), "claim-snapshots" + File.separator + claim.getName());
               if (file.getParentFile().mkdirs()) {
                  System.out.println("Created parent directory " + file.getParentFile().getName());
               }

               try (
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  final DataOutputStream dos = new DataOutputStream(baos);
               ) {
                  dos.writeUTF(world.getName());
                  dos.writeInt(minX);
                  dos.writeInt(maxX);
                  dos.writeInt(minZ);
                  dos.writeInt(maxZ);
                  final AtomicInteger y = new AtomicInteger();
                  final CountDownLatch latch = new CountDownLatch(1);
                  (new BukkitRunnable() {
                     @Override
                     public void run() {
                        try {
                           for (int x = minX; x < maxX; x++) {
                              for (int z = minZ; z < maxZ; z++) {
                                 Location loc = new Location(world, x, y.get(), z);
                                 Block block = loc.getBlock();
                                 dos.writeInt(block.getTypeId());
                                 dos.writeByte(block.getData());
                              }
                           }

                           if (y.incrementAndGet() == 256) {
                              this.cancel();
                              latch.countDown();
                           }
                        } catch (IOException e) {
                           e.printStackTrace();
                           team.sendMessage(ChatColor.RED + "An error occurred while saving claim '" + claim.getName() + "'");
                           this.cancel();
                           latch.countDown();
                        }
                     }
                  }).runTaskTimer(Foxtrot.getInstance(), 0L, 1L);

                  try {
                     latch.await();
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }

                  try (FileOutputStream fos = new FileOutputStream(file)) {
                     fos.write(baos.toByteArray());
                  }

                  System.out.println("Saved claim " + claim.getName());
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         });
         thread.setName("save-" + team.getName());
         thread.start();
      }
   }

   @EventHandler
   public void onTeamRegenerate(TeamRegenerateEvent event) {
      if (event.isWasRaidable()) {
         Team team = event.getTeam();
         List<Claim> claims = new ArrayList<>(team.getClaims());
         if (!claims.isEmpty()) {
            Thread thread = new Thread(() -> {
               team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your claim has begun regenerating.");

               for (final Claim claim : claims) {
                  File file = new File(Foxtrot.getInstance().getDataFolder(), "claim-snapshots" + File.separator + claim.getName());
                  if (!file.exists()) {
                     team.sendMessage(ChatColor.RED + "No snapshot found for claim: " + claim.getName());
                     return;
                  }

                  System.out.println("Regenerating claim " + claim.getName());
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();

                  try (FileInputStream fis = new FileInputStream(file)) {
                     byte[] buf = new byte[4096];

                     int read;
                     while ((read = fis.read(buf)) != -1) {
                        baos.write(buf, 0, read);
                     }
                  } catch (IOException e) {
                     e.printStackTrace();
                     team.sendMessage(ChatColor.RED + "An error occurred while regenerating claim '" + claim.getName() + "'");
                     return;
                  }

                  try (final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                     final World world = Bukkit.getWorld(dis.readUTF());
                     final int minX = dis.readInt();
                     final int maxX = dis.readInt();
                     final int minZ = dis.readInt();
                     final int maxZ = dis.readInt();
                     final AtomicInteger y = new AtomicInteger();
                     final CountDownLatch latch = new CountDownLatch(1);
                     (new BukkitRunnable() {
                        @Override
                        public void run() {
                           try {
                              for (int x = minX; x < maxX; x++) {
                                 for (int z = minZ; z < maxZ; z++) {
                                    int typeId = dis.readInt();
                                    byte data = dis.readByte();
                                    Location loc = new Location(world, x, y.get(), z);
                                    Block block = loc.getBlock();
                                    if (block.getTypeId() != typeId && typeId != 52) {
                                       block.setTypeId(typeId);
                                    }

                                    if (block.getData() != data) {
                                       block.setData(data);
                                    }
                                 }
                              }

                              if (y.incrementAndGet() == 256) {
                                 this.cancel();
                                 latch.countDown();
                              }
                           } catch (IOException e) {
                              e.printStackTrace();
                              team.sendMessage(ChatColor.RED + "An error occurred while regenerating claim '" + claim.getName() + "'");
                              this.cancel();
                              latch.countDown();
                           }
                        }
                     }).runTaskTimer(Foxtrot.getInstance(), 0L, 1L);

                     try {
                        latch.await();
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }

                     System.out.println("Regenerated claim " + claim.getName());
                     if (file.delete()) {
                        System.out.println("Deleted " + file.getName());
                     }
                  } catch (IOException e) {
                     e.printStackTrace();
                  }
               }

               team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your claim has finished regenerating.");
            });
            thread.setName("regenerate-" + team.getName());
            thread.start();
         }
      }
   }
}
