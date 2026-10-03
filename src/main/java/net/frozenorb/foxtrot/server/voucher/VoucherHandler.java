package net.frozenorb.foxtrot.server.voucher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class VoucherHandler {
   private List<Voucher> cache = new ArrayList<>();
   private EffectiveHandler effectiveHandler;
   private File file = new File(Foxtrot.getInstance().getDataFolder(), "data/vouchers.yml");
   private FileConfiguration data = YamlConfiguration.loadConfiguration(this.file);
   private Foxtrot instance;

   public VoucherHandler(Foxtrot instance) {
      this.instance = instance;
      this.effectiveHandler = new EffectiveHandler();
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.get("vouchers") != null) {
         this.instance
            .getServer()
            .getScheduler()
            .runTaskLater(
               this.instance,
               () -> {
                  for (String path : this.data.getConfigurationSection("vouchers").getKeys(false)) {
                     new Voucher(
                        UUID.fromString(this.data.getString("vouchers." + path + ".uuid")),
                        this.data.getString("vouchers." + path + ".voucherType"),
                        this.data.getString("vouchers." + path + ".addedBy"),
                        this.data.getLong("vouchers." + path + ".addedTime"),
                        this.data.getString("vouchers." + path + ".usedBy"),
                        this.data.getString("vouchers." + path + ".code"),
                        this.data.getLong("vouchers." + path + ".redeemedTime"),
                        this.data.getBoolean("vouchers." + path + ".used"),
                        this.data.getBoolean("vouchers." + path + ".rank"),
                        this.data.getBoolean("vouchers." + path + ".bundle"),
                        this.data.getString("vouchers." + path + ".rankName"),
                        this.data.getString("vouchers." + path + ".rankDuration"),
                        this.data.getString("vouchers." + path + ".bundleID"),
                        this.data.getInt("vouchers." + path + ".amount")
                     );
                  }
               },
               20L
            );
      }
   }

   public void saveData() {
      Map<String, Object> configValues = this.data.getValues(false);

      for (Entry<String, Object> entry : configValues.entrySet()) {
         this.data.set(entry.getKey(), null);
      }

      int i = 0;

      for (Voucher voucher : this.cache) {
         this.data.set("vouchers." + ++i + ".uuid", voucher.getTarget().toString());
         this.data.set("vouchers." + i + ".voucherType", voucher.getVoucher());
         this.data.set("vouchers." + i + ".addedBy", voucher.getAddedBy());
         this.data.set("vouchers." + i + ".addedTime", voucher.getAddedTime());
         this.data.set("vouchers." + i + ".usedBy", voucher.getUsedBy());
         this.data.set("vouchers." + i + ".code", voucher.getCode());
         this.data.set("vouchers." + i + ".usedTime", voucher.getUsedTime());
         this.data.set("vouchers." + i + ".used", voucher.isUsed());
         this.data.set("vouchers." + i + ".rank", voucher.isRank());
         this.data.set("vouchers." + i + ".rankName", voucher.getRankName());
         this.data.set("vouchers." + i + ".rankDuration", voucher.getRankDuration());
         this.data.set("vouchers." + i + ".amount", voucher.getAmount());
         this.data.set("vouchers." + i + ".bundle", voucher.isBundle());
         this.data.set("vouchers." + i + ".bundleID", voucher.getBundleID());
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public List<Voucher> getCache() {
      return this.cache;
   }

   public EffectiveHandler getEffectiveHandler() {
      return this.effectiveHandler;
   }
}
