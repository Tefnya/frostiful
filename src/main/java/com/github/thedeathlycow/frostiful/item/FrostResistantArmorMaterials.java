package com.github.thedeathlycow.frostiful.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public enum FrostResistantArmorMaterials implements FrostResistantArmorMaterial {

    FUR_ARMOR(ArmorMaterials.LEATHER, new double[]{0.5D, 1.0D, 2.0D, 1.5D}),
    FUR_LINED_CHAIN(ArmorMaterials.CHAIN, new double[]{0.5D, 1.0D, 2.0D, 1.5D}),
    FUR_LINED_GOLD(ArmorMaterials.GOLD, new double[]{0.5D, 1.0D, 2.0D, 1.5D}),
    FUR_LINED_IRON(ArmorMaterials.IRON, new double[]{0.5D, 1.0D, 2.0D, 1.5D}),
    FUR_LINED_DIAMOND(ArmorMaterials.DIAMOND, new double[]{0.5D, 1.0D, 2.0D, 1.5D}),
    FUR_LINED_NETHERITE(ArmorMaterials.NETHERITE, new double[]{0.75D, 1.5D, 2.5D, 1.75D});

    private final ArmorMaterial parentMaterial;
    private final double[] frostResistanceAmounts;

    FrostResistantArmorMaterials(ArmorMaterial parentMaterial, double[] frostResistanceAmounts) {
        this.parentMaterial = parentMaterial;
        this.frostResistanceAmounts = frostResistanceAmounts;
    }

    @Override
    public ArmorMaterial getParentMaterial() {
        return this.parentMaterial;
    }

    @Override
    public double getFrostResistance(EquipmentSlot slot) {
        return this.frostResistanceAmounts[slot.getEntitySlotId()];
    }
}
