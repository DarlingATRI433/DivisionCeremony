package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.component.Unbreakable;

/**
 * 治愈之斧 - 无法破坏，下界合金属性
 */
public class HealingAxeItem extends AxeItem {
    public HealingAxeItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(AxeItem.createAttributes(DivisionToolMaterials.INSTANCE, 5.0F, -3.0F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}
