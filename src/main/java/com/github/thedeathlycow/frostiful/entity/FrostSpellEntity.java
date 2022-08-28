package com.github.thedeathlycow.frostiful.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.List;

public class FrostSpellEntity extends SpellEntity {

    private static final Vec3f EXPLOSION_COLOUR = new Vec3f(Vec3d.unpackRgb(0x61BBE8));

    public FrostSpellEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super(world, owner, velocityX, velocityY, velocityZ);
    }

    public FrostSpellEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, double maxDistance) {
        super(world, owner, velocityX, velocityY, velocityZ, maxDistance);
    }

    protected FrostSpellEntity(EntityType<? extends SpellEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void applyEffectCloud() {
        if (this.isRemoved() || this.world.isClient) {
            return;
        }

        Box box = this.getBoundingBox().expand(4.0, 4.0, 4.0);
        List<LivingEntity> targets = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        for (var target : targets) {
            this.applySingleTargetEffect(target);
        }
        this.world.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.AMBIENT,
                2.0f, 1.0f
        );

        ServerWorld serverWorld = (ServerWorld) this.world;

        serverWorld.spawnParticles(
                ParticleTypes.EXPLOSION,
                this.getX(), this.getY(), this.getZ(),
                10,
                2.0, 2.0, 2.0,
                0.3
        );

        this.discard();
    }

    @Override
    protected void applySingleTargetEffect(Entity target) {
        if (!target.world.isClient && target instanceof RootedEntity rootedEntity) {
            rootedEntity.frostiful$root();
        }
    }
}
