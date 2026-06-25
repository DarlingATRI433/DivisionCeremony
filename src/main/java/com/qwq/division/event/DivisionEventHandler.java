package com.qwq.division.event;

import com.qwq.division.DivisionCeremony;
import com.qwq.division.item.DivisionDataComponents;
import com.qwq.division.item.EthericSwordItem;
import com.qwq.division.item.HealingAxeItem;
import com.qwq.division.item.SoulFragmentItem;
import com.qwq.division.item.UnstableIngotItem;
import com.qwq.division.item.ModItems;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
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

    // ==================== 物品攻击行为 ====================

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        // 天域之剑 —— 转为魔法伤害，无视护甲
        if (stack.getItem() instanceof EthericSwordItem && event.getTarget() instanceof LivingEntity target) {
            event.setCanceled(true);
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurt(player.level().damageSources().indirectMagic(player, player), damage);
            return;
        }

        // 治愈之斧 —— 非亡灵目标不造成伤害，给予生命恢复
        if (stack.getItem() instanceof HealingAxeItem && event.getTarget() instanceof LivingEntity target) {
            if (!target.isInvertedHealAndHarm()) {
                event.setCanceled(true);
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
            }
        }
    }

    // ==================== 合成事件 ====================

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

        // 灵魂碎片合成（天域之剑 → 灵魂碎片）—— 扣除10%血量上限
        if (result.getItem() instanceof SoulFragmentItem) {
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack stack = event.getInventory().getItem(i);
                if (stack.getItem() instanceof EthericSwordItem) {
                    Player player = event.getEntity();
                    double currentMax = player.getAttributeValue(Attributes.MAX_HEALTH);
                    player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.max(2.0, currentMax * 0.9));
                    break;
                }
            }
        }
    }

    // ==================== 掉落事件 ====================

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
