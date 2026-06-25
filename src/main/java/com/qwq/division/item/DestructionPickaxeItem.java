package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 毁灭之镐 - 石头5倍速，其他空手速；锻造台升级后获得效率X+时运X并移除限制
 */
public class DestructionPickaxeItem extends PickaxeItem {
    public DestructionPickaxeItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(PickaxeItem.createAttributes(DivisionToolMaterials.INSTANCE, 1.0F, -2.8F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // 莫比乌斯升级后恢复正常速度
        if (stack.get(DivisionDataComponents.UPGRADED) != null) {
            return super.getDestroySpeed(stack, state);
        }
        // 石头类方块：5倍下界合金速度
        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return 45.0F; // 9.0 * 5
        }
        // 其他方块：空手速度
        return 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // 升级后可正常掉落
        if (stack.get(DivisionDataComponents.UPGRADED) != null) {
            return super.isCorrectToolForDrops(stack, state);
        }
        // 未升级：只能正确挖掘石头
        return state.is(BlockTags.BASE_STONE_OVERWORLD);
    }
}
