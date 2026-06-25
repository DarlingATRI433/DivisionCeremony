package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.Unbreakable;

/**
 * 毁灭之镐 - 无法破坏，下界合金属性
 */
public class DestructionPickaxeItem extends PickaxeItem {
    public DestructionPickaxeItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(PickaxeItem.createAttributes(DivisionToolMaterials.INSTANCE, 1.0F, -2.8F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }
}
