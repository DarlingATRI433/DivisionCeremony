package com.qwq.division.item;

import net.minecraft.world.item.Item;

/**
 * 灵魂碎片 - 天域之剑四格合成产物，合成后扣除10%血量上限
 */
public class SoulFragmentItem extends Item {
    public SoulFragmentItem() {
        super(new Item.Properties().stacksTo(64));
    }
}
