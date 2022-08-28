package com.github.thedeathlycow.frostiful.entity;

import com.github.thedeathlycow.frostiful.config.FrostifulConfig;
import com.github.thedeathlycow.frostiful.entity.damage.FrostifulDamageSource;
import com.github.thedeathlycow.frostiful.entity.effect.FrostifulStatusEffects;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class FrostSpellEntity extends ExplosiveProjectileEntity {

    private static final String AMPLIFIER_NBT_KEY = "EffectAmplifier";
    private static final String MAX_DISTANCE_NBT_KEY = "MaxDistance";
    private double maxDistance = Double.POSITIVE_INFINITY;
    @Nullable
    private Vec3d startPosition = null;

    protected FrostSpellEntity(EntityType<? extends FrostSpellEntity> entityType, World world) {
        super(entityType, world);
    }

    public FrostSpellEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        this(world, owner, velocityX, velocityY, velocityZ, Double.POSITIVE_INFINITY);
    }

    public FrostSpellEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, double maxDistance) {
        super(FrostifulEntityTypes.FROST_SPELL, owner, velocityX, velocityY, velocityZ, world);
        this.maxDistance = maxDistance;
    }

    public void tick() {
        super.tick();

        if (!this.world.isClient && this.isAlive()) {
            if (this.startPosition == null) {
                this.startPosition = this.getPos();
            }

            double distTravelledSqd = this.startPosition.squaredDistanceTo(this.getPos());
            if (distTravelledSqd > this.maxDistance * this.maxDistance) {
                this.createFrozenCloud();
            }
        }
    }

    @Override
    public void onEntityHit(EntityHitResult hitResult) {
        super.onEntityHit(hitResult);
        if (!this.world.isClient && this.isAlive()) {
            Entity entityHit = hitResult.getEntity();
            entityHit.damage(FrostifulDamageSource.frozenAttack(this.getOwner()), 3.0f);
            if (entityHit instanceof RootedEntity rootedEntity) {
                rootedEntity.frostiful$root();
            }
            this.createFrozenCloud();
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putDouble(MAX_DISTANCE_NBT_KEY, this.maxDistance);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(MAX_DISTANCE_NBT_KEY, NbtElement.DOUBLE_TYPE)) {
            this.maxDistance = nbt.getDouble(MAX_DISTANCE_NBT_KEY);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            this.createFrozenCloud();
        }
    }

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected float getDrag() {
        return 1.0f;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    private void createFrozenCloud() {

        if (this.isRemoved()) {
            return;
        }

        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 0.01f, Explosion.DestructionType.NONE);

        this.discard();
    }
}
