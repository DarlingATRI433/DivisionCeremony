package com.qwq.division.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 不稳定金属锭 - 合成后10秒内不使用会爆炸（9粒合成版本稳定）
 */
public class UnstableIngotItem extends Item {
    public static final long TICKS_TO_EXPLODE = 200L; // 10秒 = 200刻

    public UnstableIngotItem() {
        super(new Item.Properties());
    }

    private static DataComponentType<Long> timerType() {
        return DivisionDataComponents.UNSTABLE_CREATION_TIME.get();
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        stack.set(timerType(), level.getGameTime());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;

        Long creationTime = stack.get(timerType());
        if (creationTime == null) return;

        long elapsed = level.getGameTime() - creationTime;
        if (elapsed >= TICKS_TO_EXPLODE) {
            triggerExplosion(level, entity, stack);
        }
    }

    /**
     * 触发爆炸：不破坏方块，必定击杀附近所有玩家并清除其背包内不稳定锭
     */
    public static void triggerExplosion(Level level, Entity entity, ItemStack stack) {
        if (level.isClientSide) return;

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        // 无方块破坏的爆炸
        level.explode(null, x, y, z, 3.0F, false, Level.ExplosionInteraction.NONE);

        // 击杀附近所有玩家（无论触发者是玩家还是掉落物）
        AABB area = new AABB(x - 6, y - 6, z - 6, x + 6, y + 6, z + 6);
        for (Player nearby : level.getEntitiesOfClass(Player.class, area)) {
            // 清除该玩家背包中所有不稳定锭
            for (int i = 0; i < nearby.getInventory().getContainerSize(); i++) {
                ItemStack invStack = nearby.getInventory().getItem(i);
                if (invStack.getItem() instanceof UnstableIngotItem) {
                    nearby.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
            // 必定击杀
            nearby.hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
        }

        stack.setCount(0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Long creationTime = stack.get(timerType());
        if (creationTime != null && context.level() != null) {
            long elapsed = context.level().getGameTime() - creationTime;
            long remaining = TICKS_TO_EXPLODE - elapsed;
            Style redStyle = Style.EMPTY.withColor(ChatFormatting.RED);
            if (remaining > 0) {
                tooltipComponents.add(Component.translatable(
                        "item.divisionceremony.unstable_ingot.timer",
                        String.format("%.1f", remaining / 20.0))
                        .withStyle(redStyle));
            } else {
                tooltipComponents.add(Component.translatable(
                        "item.divisionceremony.unstable_ingot.exploding")
                        .withStyle(redStyle));
            }
        }
    }
}
