package com.qwq.division.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 灵魂碎片 - 天域之剑合成产物，右键消耗恢复10%血量上限
 */
public class SoulFragmentItem extends Item {
    public SoulFragmentItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            double currentMax = player.getAttributeValue(Attributes.MAX_HEALTH);
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.min(40.0, currentMax * 1.1));
            stack.shrink(1);
        }
        return InteractionResultHolder.consume(stack);
    }
}
