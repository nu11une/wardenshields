package net.nu11une.wardenshields.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.nu11une.wardenshields.WardenShields;
import net.nu11une.wardenshields.register.WSItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow protected int despawnCounter;

    @Shadow protected abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Shadow public float limbDistance;

    @Shadow private long lastDamageTime;

    @Shadow protected float lastDamageTaken;

    @Shadow protected abstract void applyDamage(DamageSource source, float amount);

    @Shadow public int maxHurtTime;

    @Shadow public int hurtTime;

    @Shadow public float knockbackVelocity;

    @Shadow public abstract void setAttacker(@Nullable LivingEntity attacker);

    @Shadow public abstract void takeKnockback(double strength, double x, double z);

    @Shadow public abstract boolean isDead();

    @Shadow protected abstract boolean tryUseTotem(DamageSource source);

    @Shadow @Nullable protected abstract SoundEvent getDeathSound();

    @Shadow protected abstract float getSoundVolume();

    @Shadow public abstract float getSoundPitch();

    @Shadow public abstract void onDeath(DamageSource damageSource);

    @Shadow protected abstract void playHurtSound(DamageSource source);

    @Shadow @Nullable private DamageSource lastDamageSource;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damageCallback(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        this.despawnCounter = 0;
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if (amount > 0.0F && blockable(source, thisEntity)) {
            float originalAmount = amount;
            this.damageShield(amount);
            amount *= 0.4F;
            if (!source.isProjectile()) {
                Entity entity = source.getSource();
                if (entity instanceof LivingEntity) {
                    this.takeShieldHit((LivingEntity)entity);
                }
            }

            this.limbDistance = 1.5F;
            boolean bl2 = true;
            if ((float)this.timeUntilRegen > 10.0F) {
                if (amount <= this.lastDamageTime) {
                    cir.setReturnValue(false);
                }

                this.applyDamage(source, amount - this.lastDamageTaken);
                this.lastDamageTaken = amount;
                bl2 = false;
            } else {
                this.lastDamageTaken = amount;
                this.timeUntilRegen = 20;
                this.applyDamage(source, amount);
                this.maxHurtTime = 10;
                this.hurtTime = this.maxHurtTime;
            }

            this.knockbackVelocity = 0.0F;
            Entity attacker = source.getAttacker();
            if (!source.isNeutral()) {
                this.setAttacker((LivingEntity)attacker);
            }

            if (bl2) {
                this.world.sendEntityStatus(this, (byte)29);
                this.scheduleVelocityUpdate();

                double d = attacker.getX() - this.getX();

                double e;
                for(e = attacker.getZ() - this.getZ(); d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
                    d = (Math.random() - Math.random()) * 0.01;
                }

                this.knockbackVelocity = (float)(MathHelper.atan2(e, d) * 57.2957763671875 - (double)this.getYaw());
                this.takeKnockback(0.4000000059604645, d, e);
            }

            if (this.isDead()) {
                if (!this.tryUseTotem(source)) {
                    SoundEvent soundEvent = this.getDeathSound();
                    if (bl2 && soundEvent != null) {
                        this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
                    }

                    this.onDeath(source);
                }
            } else if (bl2) {
                this.playHurtSound(source);
            }

            this.lastDamageSource = source;
            this.lastDamageTime = this.world.getTime();

            if (thisEntity instanceof ServerPlayerEntity) {
                Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)thisEntity, source, originalAmount, amount, true);
                if (originalAmount < 3.4028235E37F) {
                    ((ServerPlayerEntity)thisEntity).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(originalAmount * 10.0F));
                }
            }

            if (attacker instanceof ServerPlayerEntity) {
                Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)attacker, this, source, originalAmount, amount, true);
            }

            cir.setReturnValue(true);
        }
    }

    private static boolean blockable(DamageSource source, LivingEntity livingEntity){
        if(source.getName().equals("sonic_boom") && livingEntity.getActiveItem().isOf(WSItems.SCULK_SHIELD)){
            Vec3d vec3d = source.getPosition();
            if (vec3d != null) {
                Vec3d vec3d2 = livingEntity.getRotationVec(1.0F);
                Vec3d vec3d3 = vec3d.relativize(livingEntity.getPos()).normalize();
                vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
                if (vec3d3.dotProduct(vec3d2) < 0.0) {
                    return true;
                }
            }
        }
        return false;
    }
}
