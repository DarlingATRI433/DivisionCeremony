package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.component.Unbreakable;

/**
 * 回溯之锄 - 无法破坏，下界合金属性
 */
public class ReversingHoeItem extends HoeItem {
    public ReversingHoeItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(HoeItem.createAttributes(DivisionToolMaterials.INSTANCE, -4.0F, 0.0F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}
