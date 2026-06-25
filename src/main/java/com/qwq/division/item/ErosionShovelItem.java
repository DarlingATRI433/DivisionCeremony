package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.component.Unbreakable;

/**
 * 侵蚀之铲 - 无法破坏，下界合金属性
 */
public class ErosionShovelItem extends ShovelItem {
    public ErosionShovelItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(ShovelItem.createAttributes(DivisionToolMaterials.INSTANCE, 1.5F, -3.0F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}
