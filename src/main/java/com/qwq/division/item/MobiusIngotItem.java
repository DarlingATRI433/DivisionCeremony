package com.qwq.division.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 莫比乌斯"稳定/不稳定"锭 - 附魔光效
 */
public class MobiusIngotItem extends Item {
    public MobiusIngotItem() {
        super(new Item.Properties());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
