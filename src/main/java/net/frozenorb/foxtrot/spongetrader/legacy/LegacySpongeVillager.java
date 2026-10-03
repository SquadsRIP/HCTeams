package net.frozenorb.foxtrot.spongetrader.legacy;

import java.lang.reflect.Field;
import java.util.Map;
import net.frozenorb.foxtrot.spongetrader.SpongeTrades;
import net.minecraft.server.v1_7_R4.ContainerMerchant;
import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityAgeable;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.EntityTypes;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.server.v1_7_R4.IMerchant;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.MerchantRecipe;
import net.minecraft.server.v1_7_R4.MerchantRecipeList;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NPC;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftAgeable;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public final class LegacySpongeVillager extends EntityAgeable implements IMerchant, NPC {
   private static Map<Class<?>, Integer> entityIds;
   private final MerchantRecipeList offers = new LegacySpongeVillager.FixedOffers();
   private double anchorX;
   private double anchorY;
   private double anchorZ;
   private float anchorYaw;
   private EntityHuman customer;

   public LegacySpongeVillager(World world) {
      super(world);
      this.a(0.6F, 1.8F);
      this.persistent = true;
      this.fireProof = true;
      this.canPickUpLoot = false;
      this.setAge(0);
      this.ageLocked = true;

      for (SpongeTrades.Trade trade : SpongeTrades.create()) {
         MerchantRecipe recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(trade.getCost()), CraftItemStack.asNMSCopy(trade.getReward()));
         recipe.maxUses = Integer.MAX_VALUE;
         this.offers.add(recipe);
      }
   }

   protected void c() {
      super.c();
      this.datawatcher.a(16, 0);
   }

   public EntityAgeable createChild(EntityAgeable parent) {
      return null;
   }

   protected boolean isTypeNotPersistent() {
      return false;
   }

   public void a_(EntityHuman customer) {
      this.customer = customer;
   }

   public EntityHuman b() {
      return this.customer;
   }

   public boolean cc() {
      return this.customer != null;
   }

   public CraftEntity getBukkitEntity() {
      if (this.bukkitEntity == null) {
         this.bukkitEntity = new LegacySpongeVillager.BukkitVillager((CraftServer)Bukkit.getServer(), this);
      }

      return this.bukkitEntity;
   }

   public static void registerType() throws ReflectiveOperationException {
      Field field = EntityTypes.class.getDeclaredField("f");
      field.setAccessible(true);
      entityIds = (Map<Class<?>, Integer>)field.get(null);
      Integer villagerId = entityIds.get(EntityVillager.class);
      if (villagerId != null && villagerId == 120) {
         entityIds.put(LegacySpongeVillager.class, villagerId);
      } else {
         throw new IllegalStateException("Expected v1_7_R4 villager entity ID 120");
      }
   }

   public static void unregisterType() {
      if (entityIds != null) {
         entityIds.remove(LegacySpongeVillager.class);
      }

      entityIds = null;
   }

   public static LegacySpongeVillager spawn(Location location, String name) {
      LegacySpongeVillager villager = new LegacySpongeVillager(((CraftWorld)location.getWorld()).getHandle());
      villager.anchorX = location.getX();
      villager.anchorY = location.getY();
      villager.anchorZ = location.getZ();
      villager.anchorYaw = location.getYaw();
      villager.restoreAnchor();
      villager.setCustomName(name);
      villager.setCustomNameVisible(true);
      if (!villager.world.addEntity(villager, SpawnReason.CUSTOM)) {
         villager.die();
         return null;
      } else {
         return villager;
      }
   }

   public MerchantRecipeList getOffers(EntityHuman customer) {
      return this.offers;
   }

   public void a(MerchantRecipe recipe) {
      recipe.uses = 0;
   }

   public void a_(ItemStack result) {
   }

   public boolean a(EntityHuman customer) {
      if (this.isAlive() && !this.cc() && !this.world.isStatic) {
         this.a_(customer);
         customer.openTrade(this, this.getCustomName());
         if (!(customer.activeContainer instanceof ContainerMerchant)) {
            this.a_((EntityHuman)null);
         }
      }

      return true;
   }

   public void h() {
      this.motX = this.motY = this.motZ = 0.0;
      this.fallDistance = 0.0F;
      this.restoreAnchor();
      if (this.b() != null && (!this.b().isAlive() || this.b().world != this.world || this.e(this.b()) > 64.0F)) {
         this.a_((EntityHuman)null);
      }
   }

   private void restoreAnchor() {
      this.setPositionRotation(this.anchorX, this.anchorY, this.anchorZ, this.anchorYaw, 0.0F);
      this.aM = this.aN = this.aO = this.aP = this.anchorYaw;
   }

   public void inactiveTick() {
      this.h();
   }

   public void move(double x, double y, double z) {
   }

   public void collide(Entity entity) {
   }

   public void mount(Entity entity) {
   }

   public boolean damageEntity(DamageSource source, float damage) {
      return false;
   }

   public boolean isInvulnerable() {
      return true;
   }

   public void a(EntityLightning lightning) {
   }

   public boolean c(NBTTagCompound tag) {
      return false;
   }

   public boolean d(NBTTagCompound tag) {
      return false;
   }

   public void removeTrader() {
      if (this.b() != null) {
         this.b().getBukkitEntity().closeInventory();
      }

      this.a_((EntityHuman)null);
      this.getBukkitEntity().remove();
   }

   private static final class BukkitVillager extends CraftAgeable implements Villager {
      private BukkitVillager(CraftServer server, LegacySpongeVillager entity) {
         super(server, entity);
      }

      @Override
      public EntityType getType() {
         return EntityType.VILLAGER;
      }

      @Override
      public Profession getProfession() {
         return Profession.FARMER;
      }

      @Override
      public void setProfession(Profession profession) {
      }

      @Override
      public boolean canBreed() {
         return false;
      }
   }

   private static final class FixedOffers extends MerchantRecipeList {
      private FixedOffers() {
      }

      public MerchantRecipe a(ItemStack first, ItemStack second, int selected) {
         if (selected >= 0 && selected < this.size()) {
            MerchantRecipeList selectedOffer = new MerchantRecipeList();
            selectedOffer.add(this.get(selected));
            return selectedOffer.a(first, second, 0);
         } else {
            return null;
         }
      }
   }
}
