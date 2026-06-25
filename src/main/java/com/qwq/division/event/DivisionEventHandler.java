package com.qwq.division.event;

import com.qwq.division.DivisionCeremony;
import com.qwq.division.item.DivisionDataComponents;
import com.qwq.division.item.UnstableIngotItem;
import com.qwq.division.item.ModItems;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 模组事件处理器
 */
@EventBusSubscriber(modid = DivisionCeremony.MODID)
public class DivisionEventHandler {

    private static DataComponentType<Long> timerType() {
        return DivisionDataComponents.UNSTABLE_CREATION_TIME.get();
    }

    /**
     * 分割徽章合成后保留（无限使用）
     * 9粒合成的不稳定锭移除计时器（稳定版）
     */
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (event.getEntity().level().isClientSide) return;

        // 分割徽章合成保留
        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getInventory().getItem(i);
            if (stack.getItem() == ModItems.DIVISION_SIGIL.get()) {
                event.getEntity().addItem(new ItemStack(ModItems.DIVISION_SIGIL.get()));
                break;
            }
        }

        // 9粒压缩合成的不稳定锭 → 移除计时器（不爆炸）
        if (result.getItem() == ModItems.UNSTABLE_INGOT.get()) {
            boolean allNuggets = true;
            int count = 0;
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack stack = event.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    count++;
                    if (stack.getItem() != ModItems.UNSTABLE_NUGGET.get()) {
                        allNuggets = false;
                        break;
                    }
                }
            }
            if (allNuggets && count == 9) {
                result.remove(timerType());
            }
        }
    }

    /**
     * 凋灵和末影龙死亡时掉落分割徽章
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof WitherBoss || event.getEntity() instanceof EnderDragon) {
            if (!event.getEntity().level().isClientSide) {
                event.getEntity().level().addFreshEntity(new ItemEntity(
                        event.getEntity().level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        new ItemStack(ModItems.DIVISION_SIGIL.get())
                ));
            }
        }
    }

    /**
     * 不稳定金属锭掉落即爆炸
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            if (stack.getItem() == ModItems.UNSTABLE_INGOT.get()) {
                Long creationTime = stack.get(timerType());
                if (creationTime != null) {
                    itemEntity.discard();
                    UnstableIngotItem.triggerExplosion(
                            event.getLevel(), itemEntity, stack);
                }
            }
        }
    }

    /**
     * 关闭容器时检查不稳定金属锭 —— 加速倒计时归零
     */
    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == ModItems.UNSTABLE_INGOT.get()) {
                Long creationTime = stack.get(timerType());
                if (creationTime != null) {
                    stack.set(timerType(), 0L);
                }
            }
        }
    }
}
