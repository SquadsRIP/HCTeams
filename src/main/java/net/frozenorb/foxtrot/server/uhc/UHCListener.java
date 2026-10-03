package net.frozenorb.foxtrot.server.uhc;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.PlayerUtils;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerHealthChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class UHCListener implements Listener {
   private static final String BELOW_NAME_OBJECTIVE_NAME = "BelowName";
   private static final char HEART_CHAR = '❤';
   private static final DecimalFormat HEARTS_FORMAT = new DecimalFormat("#.#");
   private static Field aField = null;
   private static Field bField = null;
   private static Field cField = null;
   private static Field dField = null;
   private Map<UUID, Long> deathTime = new HashMap<>();

   public UHCListener() {
      ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE));
      recipe.shape("NNN", "NAN", "NNN");
      recipe.setIngredient('N', Material.GOLD_NUGGET);
      recipe.setIngredient('A', Material.APPLE);
      Bukkit.addRecipe(recipe);
   }

   private void init(Player player) {
      Scoreboard scoreboard = player.getScoreboard();
      Objective objective = scoreboard.registerNewObjective("BelowName", Criterias.HEALTH);
      objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
      objective.setDisplayName(ChatColor.DARK_RED.toString() + '❤');
      Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
         this.updateAllTo(player);
         this.updateToAll(player);
      });
   }

   private void updateToAll(Player player) {
      Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
      if (objective != null) {
         try {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
            aField.set(packet, player.getName());
            bField.set(packet, "BelowName");
            cField.set(packet, getHealth(player));
            dField.set(packet, 0);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
               if (onlinePlayer.getScoreboard() != null && onlinePlayer.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) != null) {
                  ((CraftPlayer)onlinePlayer).getHandle().playerConnection.sendPacket(packet);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void updateAllTo(Player player) {
      Objective belowNameObjective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
      if (belowNameObjective != null) {
         try {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
               if (player.getScoreboard() != null && player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) != null) {
                  PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
                  aField.set(packet, onlinePlayer.getName());
                  bField.set(packet, "BelowName");
                  cField.set(packet, getHealth(onlinePlayer));
                  dField.set(packet, 0);
                  ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventHandler
   public void onCraft(CraftItemEvent event) {
      if (this.isOldCrappleRecipe(event.getRecipe())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPrepare(PrepareItemCraftEvent event) {
      if (this.isOldCrappleRecipe(event.getRecipe())) {
         ItemStack result = ItemBuilder.of(Material.WOOL)
            .data(DyeColor.RED.getWoolData())
            .name("&cThis recipe is disabled.")
            .addToLore("&eUse gold nuggets instead of ingots.")
            .build();
         event.getInventory().setResult(result);
      }
   }

   private boolean isOldCrappleRecipe(Recipe r) {
      if (!(r instanceof ShapedRecipe)) {
         return false;
      }

      ShapedRecipe recipe = (ShapedRecipe)r;
      char[] goldChars = "abcdfghi".toCharArray();
      char appleChar = 'e';

      for (char c : goldChars) {
         if (recipe.getIngredientMap().get(c) == null) {
            return false;
         }

         if (recipe.getIngredientMap().get(c).getType() != Material.GOLD_INGOT) {
            return false;
         }
      }

      return recipe.getIngredientMap().get(appleChar).getType() == Material.APPLE;
   }

   @EventHandler
   public void onEntityRegainHealth(EntityRegainHealthEvent event) {
      if (event.getRegainReason() == RegainReason.SATIATED) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> this.init(event.getPlayer()), 1L);
   }

   @EventHandler
   public void onHealthChange(PlayerHealthChangeEvent event) {
      this.updateToAll(event.getPlayer());
   }

   @EventHandler
   public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
      ItemStack item = event.getItem();
      if (item.getType() == Material.GOLDEN_APPLE) {
         event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
         final Player damaged = (Player)event.getEntity();
         final Player damager = PlayerUtils.getDamageSource(event.getDamager());
         if (damager != null) {
            (new BukkitRunnable() {
                  @Override
                  public void run() {
                     if (!UHCListener.this.deathTime.containsKey(damaged.getUniqueId())
                        || System.currentTimeMillis() - UHCListener.this.deathTime.get(damaged.getUniqueId()) > 200L) {
                        damager.sendMessage(
                           damaged.getDisplayName()
                              + ChatColor.YELLOW
                              + " is now at "
                              + ChatColor.RED
                              + UHCListener.formatHearts(damaged, true)
                              + ChatColor.YELLOW
                              + "."
                        );
                     }
                  }
               })
               .runTask(Foxtrot.getInstance());
         }
      }
   }

   @EventHandler
   public void onRegen(EntityRegainHealthEvent event) {
      if (event.getEntity() instanceof Player && event.getRegainReason() == RegainReason.SATIATED) {
         Player player = (Player)event.getEntity();
         Team team = LandBoard.getInstance().getTeam(player.getLocation());
         if (team == null || !team.isMember(player.getUniqueId()) || SpawnTagHandler.isTagged(player)) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void enderpearlDamage(EntityDamageByEntityEvent event) {
      if (event.getCause() == DamageCause.FALL && event.getDamager() instanceof EnderPearl) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.deathTime.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
      Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
         Long var10000 = this.deathTime.remove(event.getEntity().getUniqueId());
      }, 20L);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onLeafDecay(LeavesDecayEvent event) {
      if (event.getBlock().getType() == Material.LEAVES) {
         if (Math.random() * 100.0 <= 3.0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
         }
      } else if (event.getBlock().getType() == Material.LEAVES_2 && Math.random() * 100.0 <= 3.0) {
         event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onBlockBreak(BlockBreakEvent event) {
      int multiplier = 1;
      if (event.getPlayer().getItemInHand() != null
         && event.getPlayer().getItemInHand().getType() == Material.SHEARS
         && event.getPlayer().getItemInHand().hasItemMeta()
         && event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()
         && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GOLD.toString() + ChatColor.BOLD)) {
         multiplier = 3;
      }

      if (event.getBlock().getType() == Material.LEAVES) {
         if (Math.random() * 100.0 <= 3 * multiplier) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
         }
      } else if (event.getBlock().getType() == Material.LEAVES_2 && Math.random() * 100.0 <= 3 * multiplier) {
         event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
      }
   }

   private static String formatHearts(Player player, boolean heartChar) {
      return HEARTS_FORMAT.format(getHearts(player)) + (heartChar ? '❤' : "");
   }

   private static double getHearts(Player player) {
      return getHearts0(player.getHealth() + ((CraftPlayer)player).getHandle().getAbsorptionHearts());
   }

   private static int getHealth(Player player) {
      return (int)Math.ceil(player.getHealth() + ((CraftPlayer)player).getHandle().getAbsorptionHearts());
   }

   private static double getHearts0(double health) {
      return Math.round(Math.ceil(health) / 2.0 * 2.0) / 2.0;
   }

   static {
      try {
         aField = PacketPlayOutScoreboardScore.class.getDeclaredField("a");
         aField.setAccessible(true);
         bField = PacketPlayOutScoreboardScore.class.getDeclaredField("b");
         bField.setAccessible(true);
         cField = PacketPlayOutScoreboardScore.class.getDeclaredField("c");
         cField.setAccessible(true);
         dField = PacketPlayOutScoreboardScore.class.getDeclaredField("d");
         dField.setAccessible(true);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      }
   }
}
