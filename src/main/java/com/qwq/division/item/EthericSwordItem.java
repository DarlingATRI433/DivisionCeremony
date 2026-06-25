package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.Unbreakable;

/**
 * 天域之剑 - 无法破坏，下界合金属性
 */
public class EthericSwordItem extends SwordItem {
    public EthericSwordItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(SwordItem.createAttributes(DivisionToolMaterials.INSTANCE, 3, -2.4F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}
