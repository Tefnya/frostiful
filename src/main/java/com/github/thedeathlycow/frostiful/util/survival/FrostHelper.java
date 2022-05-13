package com.github.thedeathlycow.frostiful.util.survival;

import com.github.thedeathlycow.frostiful.attributes.FrostifulEntityAttributes;
import com.github.thedeathlycow.frostiful.config.AttributeConfig;
import com.github.thedeathlycow.simple.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

public class FrostHelper {

    public static void addLivingFrost(LivingEntity entity, int amount) {
        addLivingFrost(entity, amount, true);
    }

    public static void addLivingFrost(LivingEntity entity, int amount, boolean applyFrostResistance) {
        double frostModifier = 0.0D;

        if (applyFrostResistance) {
            final Config config = AttributeConfig.CONFIG;
            double frostResistance = entity.getAttributeValue(FrostifulEntityAttributes.FROST_RESISTANCE);
            frostModifier = frostResistance * config.get(AttributeConfig.PERCENT_FROST_REDUCTION_PER_FROST_RESISTANCE);
            frostModifier /= 100.0D;
        }

        int toAdd = (int) ((1 - frostModifier) * amount);

        int current = entity.getFrozenTicks();
        setLivingFrost(entity, current + toAdd);
    }

    public static void removeLivingFrost(LivingEntity entity, int amount) {
        int current = entity.getFrozenTicks();
        setLivingFrost(entity, current - amount);
    }

    public static void setLivingFrost(LivingEntity entity, int amount) {
        setFrost(entity, amount);
        applyEffects(entity);
    }

    public static void addFrost(Entity entity, int amount) {
        int current = entity.getFrozenTicks();
        setFrost(entity, current + amount);
    }

    public static void removeFrost(Entity entity, int amount) {
        int current = entity.getFrozenTicks();
        setFrost(entity, current - amount);
    }

    public static void setFrost(Entity entity, int amount) {
        amount = MathHelper.clamp(amount, 0, entity.getMinFreezeDamageTicks());
        entity.setFrozenTicks(amount);
    }

    private static void applyEffects(LivingEntity entity) {
        double progress = getFrostProgress(entity);
        for (FrostStatusEffect effect : FrostStatusEffect.getPassiveFreezingEffects()) {
            StatusEffectInstance effectInstance = entity.getStatusEffect(effect.effect());
            boolean shouldApplyEffect = progress >= effect.progressThreshold()
                    && (effectInstance == null || effectInstance.getAmplifier() < effect.amplifier());
            if (shouldApplyEffect) {
                entity.addStatusEffect(
                        new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier(), true, true),
                        null
                );
            }
        }
    }

    public static double getFrostProgress(Entity entity) {
        return ((double) entity.getFrozenTicks()) / entity.getMinFreezeDamageTicks();
    }

}
