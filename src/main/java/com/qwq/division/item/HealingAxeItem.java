package com.qwq.division.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;

/**
 * 治愈之斧 - 手持（主手/副手）每30秒回复1饱食度+2饱和度，攻击非亡灵给予生命恢复
 */
public class HealingAxeItem extends AxeItem {
    private static final int FEED_INTERVAL = 600; // 30秒 = 600刻

    public HealingAxeItem() {
        super(DivisionToolMaterials.INSTANCE, new Properties()
                .attributes(AxeItem.createAttributes(DivisionToolMaterials.INSTANCE, 5.0F, -3.0F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        // 主手或副手持有时均生效
        boolean isHeld = selected || ItemStack.isSameItem(player.getOffhandItem(), stack);
        if (!isHeld) return;

        if (level.getGameTime() % FEED_INTERVAL == 0) {
            player.getFoodData().eat(1, 1.0F);
        }
    }
}
