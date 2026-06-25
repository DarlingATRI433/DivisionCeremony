package com.qwq.division.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * 分割仪式工具等级 - 数值套用下界合金
 */
public enum DivisionToolMaterials implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 2031; // 与下界合金相同
    }

    @Override
    public float getSpeed() {
        return 9.0F; // 与下界合金相同
    }

    @Override
    public float getAttackDamageBonus() {
        return 4.0F; // 与下界合金相同
    }

    @Override
    public int getEnchantmentValue() {
        return 15; // 与下界合金相同
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY; // 无法破坏，不需要修复材料
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }
}
