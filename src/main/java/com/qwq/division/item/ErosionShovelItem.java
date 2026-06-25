package com.qwq.division.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 侵蚀之铲 - 软方块速掘，上方沙砾消除(无掉落)；锻造台升级后获得效率X并正常掉落
 */
public class ErosionShovelItem extends ShovelItem {
    public ErosionShovelItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(ShovelItem.createAttributes(DivisionToolMaterials.INSTANCE, 1.5F, -3.0F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (stack.get(DivisionDataComponents.UPGRADED) != null) {
            return super.getDestroySpeed(stack, state);
        }
        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return 27.0F;
        }
        return 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && stack.get(DivisionDataComponents.UPGRADED) == null) {
            BlockPos above = pos.above();
            while (above.getY() < level.getMaxBuildHeight()) {
                BlockState aboveState = level.getBlockState(above);
                if (aboveState.is(Blocks.SAND) || aboveState.is(Blocks.GRAVEL)
                        || aboveState.is(Blocks.RED_SAND)) {
                    level.destroyBlock(above, false);
                    above = above.above();
                } else {
                    break;
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (stack.get(DivisionDataComponents.UPGRADED) != null) {
            return super.isCorrectToolForDrops(stack, state);
        }
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }
}
