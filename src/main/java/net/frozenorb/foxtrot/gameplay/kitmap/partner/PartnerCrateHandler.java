package net.frozenorb.foxtrot.gameplay.kitmap.partner;

import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.util.FileConfig;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public final class PartnerCrateHandler implements Listener {
   private final FileConfig fileConfig = new FileConfig(Foxtrot.getInstance(), "data/partner_packages.yml");
   private ItemStack crateItem;
   private final List<String> commands = new ArrayList<>();

   public PartnerCrateHandler() {
      Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
      this.loadConfig();
   }

   private void loadConfig() {
      FileConfiguration config = this.fileConfig.getConfig();
      config.addDefault("package-crate-name", "&6&lPartner &f&lPackage &e&lCrate");
      config.addDefault("package-crate-lore", Collections.singletonList("&fThe best partner packages"));
      config.addDefault("package-commands", Arrays.asList("/raw %player% is raw", "/raw %player% is raw af"));
      config.options().copyDefaults(true);
      this.fileConfig.save();
      this.reloadConfig();
   }

   public void reloadConfig() {
      FileConfiguration config = this.fileConfig.getConfig();

      try {
         config.load(this.fileConfig.getFile());
      } catch (Exception exception) {
         throw new IllegalStateException("Unable to load the partner crate configuration", exception);
      }

      this.crateItem = ItemBuilder.of(Material.ENDER_CHEST)
         .name(config.getString("package-crate-name"))
         .addToLore(config.getStringList("package-crate-lore").toArray(new String[0]))
         .build();
      this.commands.clear();
      this.commands.addAll(config.getStringList("package-commands"));
   }

   @EventHandler
   private void onPlayerInteractEvent(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      ItemStack item = event.getItem();
      if (item != null) {
         if (item.getType() == Material.ENDER_CHEST) {
            if (item.hasItemMeta()) {
               if (this.crateItem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                  if (this.crateItem.getItemMeta().getLore().equals(item.getItemMeta().getLore())) {
                     InventoryUtils.removeAmountFromInventory(player.getInventory(), item, 1);
                     List hassan = Foxtrot.getInstance()
                        .getMapHandler()
                        .getAbilityHandler()
                        .getAbilities()
                        .values()
                        .stream()
                        .filter(Ability::inPartnerPackage)
                        .collect(Collectors.toList());
                     Ability ability = (Ability)hassan.get(ThreadLocalRandom.current().nextInt(hassan.size()));
                     ItemStack itemStack = ability.hassanStack.clone();
                     Firework fireWork = (Firework)player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                     FireworkMeta fwMeta = fireWork.getFireworkMeta();
                     fwMeta.addEffect(
                        FireworkEffect.builder()
                           .flicker(false)
                           .trail(true)
                           .with(Type.BALL)
                           .with(Type.BALL_LARGE)
                           .with(Type.STAR)
                           .withColor(Color.ORANGE)
                           .withColor(Color.YELLOW)
                           .withFade(Color.PURPLE)
                           .withFade(Color.RED)
                           .build()
                     );
                     fireWork.setFireworkMeta(fwMeta);
                     Bukkit.getServer()
                        .dispatchCommand(
                           Bukkit.getConsoleSender(),
                           this.commands.get(ThreadLocalRandom.current().nextInt(this.commands.size())).replace("%player%", player.getName()).substring(1)
                        );
                     InventoryUtils.addAmountToInventory(player.getInventory(), itemStack, 4);
                     Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> player.updateInventory(), 2L);
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   public ItemStack getCrateItem() {
      return this.crateItem;
   }
}
