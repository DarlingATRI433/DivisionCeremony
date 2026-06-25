package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Unbreakable;

/**
 * 普通分割仪式物品 - 自带无法破坏属性
 */
public class SimpleDivisionItem extends Item {
    public SimpleDivisionItem() {
        super(new Item.Properties()
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}
