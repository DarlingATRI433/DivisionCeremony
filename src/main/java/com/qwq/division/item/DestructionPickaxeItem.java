package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 毁灭之镐 - 石头5倍速，其他空手速；锻造台升级后获得效率X+时运X并恢复全速
 */
public class DestructionPickaxeItem extends PickaxeItem {
    public DestructionPickaxeItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(PickaxeItem.createAttributes(DivisionToolMaterials.INSTANCE, 1.0F, -2.8F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (stack.get(DivisionDataComponents.UPGRADED) != null) {
            return super.getDestroySpeed(stack, state);
        }
        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return 45.0F;
        }
        return 1.0F;
    }
}
