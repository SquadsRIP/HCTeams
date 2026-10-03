package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GemFlipWager {
   private final long amount;
   private final Player creator;
   private final Player opponent;
   private final GemFlipSide creatorSide;
   private final GemFlipSide opponentSide;
   private AtomicInteger countdown;
   private GemFlipSide current = GemFlipSide.HEADS;

   public void start() {
      this.countdown = new AtomicInteger(5);
      final Menu menuOne = this.openMenuFor(this.creator);
      Menu menuTwo = this.openMenuFor(this.opponent);
      (new BukkitRunnable() {
         private int ticks = 0;
         private int countdownTicks = 0;

         @Override
         public void run() {
            if (GemFlipWager.this.countdown.get() > 0) {
               if (++this.countdownTicks % 10 == 0) {
                  GemFlipWager.this.countdown.getAndDecrement();
                  menuOne.openMenu(GemFlipWager.this.creator);
                  menuOne.openMenu(GemFlipWager.this.opponent);
                  float volume = MathUtil.scaleBetween(GemFlipWager.this.countdown.get(), 1.0F, 3.0F, 1.0F, 5.0F);
                  float pitch = MathUtil.scaleBetween(GemFlipWager.this.countdown.get(), 1.0F, 2.0F, 1.0F, 5.0F);
                  GemFlipWager.this.creator.playSound(GemFlipWager.this.creator.getLocation(), Sound.NOTE_PLING, volume, pitch);
                  GemFlipWager.this.opponent.playSound(GemFlipWager.this.opponent.getLocation(), Sound.NOTE_PLING, volume, pitch);
               }
            } else if (++this.ticks >= 70) {
               GemFlipWager.this.chooseWinner();
               this.cancel();
               GemFlipWager.this.creator.playSound(GemFlipWager.this.creator.getLocation(), Sound.NOTE_PLING, 3.0F, 2.0F);
               GemFlipWager.this.opponent.playSound(GemFlipWager.this.opponent.getLocation(), Sound.NOTE_PLING, 3.0F, 2.0F);
               Foxtrot.getInstance().getGemFlipHandler().getActiveWagers().remove(GemFlipWager.this);
               GemFlipWager.this.openMenuFor(GemFlipWager.this.creator);
               GemFlipWager.this.openMenuFor(GemFlipWager.this.opponent);
            } else {
               boolean switchCurrent = false;
               if (this.ticks > 30 || this.ticks % 2 != 0 && this.ticks % 3 != 0) {
                  if (this.ticks > 50 || this.ticks % 3 != 0 && this.ticks % 5 != 0) {
                     if (this.ticks % 4 == 0) {
                        switchCurrent = true;
                     }
                  } else {
                     switchCurrent = true;
                  }
               } else {
                  switchCurrent = true;
               }

               if (switchCurrent) {
                  GemFlipWager.this.current = GemFlipWager.this.current.getOpposite();
                  GemFlipWager.this.creator.playSound(GemFlipWager.this.creator.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                  GemFlipWager.this.opponent.playSound(GemFlipWager.this.opponent.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
               }
            }
         }
      }).runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 2L);
   }

   private Menu openMenuFor(Player player) {
      final GemFlipSide currentSide = this.isCreator(player) ? this.creatorSide : this.opponentSide;
      final Player other = this.isCreator(player) ? this.opponent : this.creator;
      final GemFlipSide otherSide = currentSide.getOpposite();
      Menu menu = new Menu() {
         @Override
         public String getTitle(Player player) {
            return "Wager for " + GemFlipWager.this.getFinalAmount();
         }

         @Override
         public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> toReturn = new HashMap<>();
            toReturn.put(
               0,
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return player.getName();
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     return null;
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return null;
                  }

                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return ItemBuilder.of(Material.SKULL_ITEM)
                        .data((byte)SkullType.PLAYER.ordinal())
                        .skull(GemFlipWager.this.creator.getName())
                        .addToLore(" ", "&2&lWager Position", " &a" + currentSide.getFriendlyName(), " ")
                        .build();
                  }
               }
            );
            toReturn.put(1, new Button() {
               @Override
               public String getName(Player player) {
                  return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + currentSide.getFriendlyName();
               }

               @Override
               public List<String> getDescription(Player player) {
                  return null;
               }

               @Override
               public Material getMaterial(Player player) {
                  return currentSide.getMaterial();
               }
            });
            toReturn.put(7, new Button() {
               @Override
               public String getName(Player player) {
                  return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + otherSide.getFriendlyName();
               }

               @Override
               public List<String> getDescription(Player player) {
                  return null;
               }

               @Override
               public Material getMaterial(Player player) {
                  return otherSide.getMaterial();
               }
            });
            toReturn.put(
               8,
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return player.getName();
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     return null;
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return null;
                  }

                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return ItemBuilder.of(Material.SKULL_ITEM)
                        .data((byte)SkullType.PLAYER.ordinal())
                        .skull(other.getName())
                        .addToLore(" ", "&2&lWager Position", " &a" + otherSide.getFriendlyName(), " ")
                        .build();
                  }
               }
            );
            toReturn.put(
               4,
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return player.getName();
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     return null;
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return null;
                  }

                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return GemFlipWager.this.countdown.get() > 0
                        ? ItemBuilder.of(Material.STAINED_GLASS_PANE)
                           .data(DyeColor.GREEN.getWoolData())
                           .amount(GemFlipWager.this.countdown.get())
                           .name("&a&lStarting in " + GemFlipWager.this.getCountdown())
                           .build()
                        : ItemBuilder.of(GemFlipWager.this.current.getMaterial()).name("&2&l" + GemFlipWager.this.current.getFriendlyName()).build();
                  }
               }
            );
            return toReturn;
         }

         @Override
         public boolean isAutoUpdate() {
            return true;
         }
      };
      menu.openMenu(player);
      return menu;
   }

   private void chooseWinner() {
      this.current = ThreadLocalRandom.current().nextBoolean() ? GemFlipSide.HEADS : GemFlipSide.TAILS;
      Player winner = this.current == this.creatorSide ? this.creator : this.opponent;
      Player loser = this.isCreator(winner) ? this.opponent : this.creator;
      if (this.creator.hasMetadata("RIG") && this.creator.isOp()) {
         winner = this.creator;
         loser = this.opponent;
         this.creator.removeMetadata("RIG", Foxtrot.getInstance());
         this.creator.sendMessage(ChatColor.GREEN + ".");
      } else if (this.opponent.hasMetadata("RIG")) {
         winner = this.opponent;
         loser = this.creator;
         this.opponent.removeMetadata("RIG", Foxtrot.getInstance());
         this.opponent.sendMessage(ChatColor.GREEN + ".");
      }

      System.out.println(winner.getName() + " has won the gem flip against " + loser.getName() + " for " + this.getFinalAmount());
      Foxtrot.getInstance().getGemMap().addGems(winner.getUniqueId(), this.getFinalAmount(), true);

      for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         onlinePlayer.sendMessage("");
         onlinePlayer.sendMessage(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Gem Flips");
         onlinePlayer.sendMessage(
            ChatColor.translate(
               winner.getDisplayName()
                  + "("
                  + this.current.getFriendlyName()
                  + ") &ahas defeated &f"
                  + loser.getDisplayName()
                  + "("
                  + (winner == this.creator ? this.opponentSide : this.creatorSide).getFriendlyName()
                  + ") &ain a Gem Flip for &f"
                  + this.getFinalAmount()
                  + " Gems&a!"
            )
         );
         onlinePlayer.sendMessage("");
      }

      Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
         this.creator.closeInventory();
         this.opponent.closeInventory();
      }, 40L);
   }

   private boolean isCreator(Player player) {
      return this.creator.getUniqueId().equals(player.getUniqueId());
   }

   public long getFinalAmount() {
      return this.amount;
   }

   public long getAmount() {
      return this.amount;
   }

   public Player getCreator() {
      return this.creator;
   }

   public Player getOpponent() {
      return this.opponent;
   }

   public GemFlipSide getCreatorSide() {
      return this.creatorSide;
   }

   public GemFlipSide getOpponentSide() {
      return this.opponentSide;
   }

   public AtomicInteger getCountdown() {
      return this.countdown;
   }

   public GemFlipSide getCurrent() {
      return this.current;
   }

   public void setCountdown(AtomicInteger countdown) {
      this.countdown = countdown;
   }

   public void setCurrent(GemFlipSide current) {
      this.current = current;
   }

   @ConstructorProperties({"amount", "creator", "opponent", "creatorSide", "opponentSide"})
   public GemFlipWager(long amount, Player creator, Player opponent, GemFlipSide creatorSide, GemFlipSide opponentSide) {
      this.amount = amount;
      this.creator = creator;
      this.opponent = opponent;
      this.creatorSide = creatorSide;
      this.opponentSide = opponentSide;
   }
}
