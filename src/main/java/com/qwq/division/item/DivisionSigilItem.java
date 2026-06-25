package com.qwq.division.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 分割徽章 - 耐久50，合成消耗耐久，凋灵/末影龙掉落
 */
public class DivisionSigilItem extends Item {
    public DivisionSigilItem() {
        super(new Item.Properties().stacksTo(1).durability(50));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.divisionceremony.division_sigil.drop_hint"));
    }
}
