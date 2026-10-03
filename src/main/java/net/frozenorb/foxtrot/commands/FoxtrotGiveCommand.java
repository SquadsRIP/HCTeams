package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;

public class FoxtrotGiveCommand {
   @Command(names = "fxgive", permission = "op")
   public static void fxgive(
      CommandSender sender,
      @Parameter(name = "player") Player target,
      @Parameter(name = "item") ItemStack item,
      @Parameter(name = "amount") int amount,
      @Parameter(name = "name") String name,
      @Parameter(name = "lore") String lore,
      @Parameter(name = "enchants") String enchants
   ) {
      ItemBuilder builder = ItemBuilder.copyOf(item).amount(amount);
      name = !name.isEmpty() && name.contains(";") && name.split(";").length == 0 ? null : name.replace("%s", " ");
      if (name != null) {
         builder.name(name);
      }

      String[] loreSplit = lore.contains(";") ? lore.split(";") : new String[]{lore};
      if (loreSplit.length > 0) {
         builder.addToLore(loreSplit);
      }

      String[] enchantsSplit = enchants.split(";");

      for (String s : enchantsSplit) {
         String[] parts = s.split(":");
         String enchantment = parts[0];
         String enchantmentId = parts[1];
         Enchantment enchant = findEnchantment(enchantment);
         if (enchant != null) {
            builder.enchant(enchant, Integer.parseInt(enchantmentId));
         } else {
            Suge suge = Suge.getInstance();
            CustomEnchant customEnchant = suge.getEnchantHandler()
               .getCustomEnchants()
               .stream()
               .filter(it -> it.getName().equalsIgnoreCase(enchantment))
               .findFirst()
               .orElse(null);
            if (customEnchant != null) {
               ItemStack build = builder.build();
               ItemStack enchantedItem = build.clone();
               ItemMeta itemMeta = enchantedItem.getItemMeta();
               List<String> enchantLore;
               if (itemMeta.getLore() != null && !itemMeta.getLore().isEmpty()) {
                  enchantLore = itemMeta.getLore();
               } else {
                  enchantLore = new ArrayList<>();
               }

               enchantLore.add(ChatColor.RED + customEnchant.getName() + " " + enchantmentId);
               itemMeta.setLore(enchantLore);
               enchantedItem.setItemMeta(itemMeta);
               builder = ItemBuilder.copyOf(enchantedItem);
            }
         }
      }

      ItemStack build = builder.build();
      InventoryUtils.addAmountToInventory(target.getInventory(), build, amount);
      if (sender instanceof Player) {
         sender.sendMessage(
            CC.GREEN
               + "Gave "
               + CC.DARK_GREEN
               + amount
               + CC.GREEN
               + " of "
               + CC.DARK_GREEN
               + item.getType().toString().toLowerCase()
               + CC.GREEN
               + " to "
               + CC.DARK_GREEN
               + target.getName()
               + CC.GREEN
               + "!"
         );
      }

      Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), target::updateInventory, 2L);
   }

   private static Enchantment findEnchantment(String string) {
      string = string.toLowerCase().replaceAll(" ", "_");
      if (string.matches("\\d+")) {
         int enchantmentId = Integer.parseInt(string);

         for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getId() == enchantmentId) {
               return enchantment;
            }
         }
      } else {
         for (Enchantment value : Enchantment.values()) {
            String name = value.getName();
            if (name != null && string.equalsIgnoreCase(name.toLowerCase())) {
               return value;
            }
         }
      }

      return null;
   }
}
