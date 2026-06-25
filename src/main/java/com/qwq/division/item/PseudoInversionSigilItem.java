package com.qwq.division.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 伪逆徽章 - 附魔光效，无耐久，合成稳定不稳定锭
 */
public class PseudoInversionSigilItem extends Item {
    public PseudoInversionSigilItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // 自带附魔光效
    }
}
