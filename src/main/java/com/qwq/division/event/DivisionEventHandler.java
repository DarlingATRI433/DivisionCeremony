package com.qwq.division.event;

import com.qwq.division.DivisionCeremony;
import com.qwq.division.item.DivisionDataComponents;
import com.qwq.division.item.DivisionSigilItem;
import com.qwq.division.item.EthericSwordItem;
import com.qwq.division.item.HealingAxeItem;
import com.qwq.division.item.SoulFragmentItem;
import com.qwq.division.item.UnstableIngotItem;
import com.qwq.division.item.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        if (stack.getItem() instanceof EthericSwordItem && event.getTarget() instanceof LivingEntity target) {
            event.setCanceled(true);
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurt(player.level().damageSources().indirectMagic(player, player), damage);
            return;
        }

        if (stack.getItem() instanceof HealingAxeItem && event.getTarget() instanceof LivingEntity target) {
            if (!target.isInvertedHealAndHarm()) {
                event.setCanceled(true);
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
            }
        }
    }

    // ==================== 仪式触发器 ====================

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof DivisionSigilItem)) return;
        if (!(event.getLevel().getBlockState(event.getPos()).getBlock() instanceof BeaconBlock)) return;
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof BeaconBlockEntity)) return;

        BlockPos beaconPos = event.getPos();

        if (RitualManager.getRitual(beaconPos) != null) {
            // Shift+右键解除绑定
            if (player.isShiftKeyDown()) {
                RitualManager.removeRitual(beaconPos);
                player.sendSystemMessage(Component.translatable("ritual.divisionceremony.unbound"));
            } else {
                player.sendSystemMessage(Component.translatable("ritual.divisionceremony.already_active"));
            }
            event.setUseBlock(TriState.FALSE);
            return;
        }

        if (!RitualManager.isRitualTime(event.getLevel())) {
            player.sendSystemMessage(Component.translatable("ritual.divisionceremony.wrong_time"));
            event.setUseBlock(TriState.FALSE);
            return;
        }

        RitualManager.startRitual(beaconPos, player.getUUID(), (ServerLevel) event.getLevel());
        player.sendSystemMessage(Component.translatable("ritual.divisionceremony.ready"));
        event.setUseBlock(TriState.FALSE);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    // ==================== 合成事件 ====================

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (event.getEntity().level().isClientSide) return;

        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getInventory().getItem(i);
            if (stack.getItem() instanceof DivisionSigilItem) {
                int newDamage = stack.getDamageValue() + 1;
                if (newDamage < stack.getMaxDamage()) {
                    ItemStack returned = new ItemStack(ModItems.DIVISION_SIGIL.get());
                    returned.setDamageValue(newDamage);
                    event.getEntity().addItem(returned);
                }
                break;
            }
        }

        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            if (event.getInventory().getItem(i).getItem() == ModItems.PSEUDO_INVERSION_SIGIL.get()) {
                event.getEntity().addItem(new ItemStack(ModItems.PSEUDO_INVERSION_SIGIL.get()));
                break;
            }
        }

        if (result.getItem() == ModItems.UNSTABLE_INGOT.get()) {
            boolean makeStable = false;
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                if (event.getInventory().getItem(i).getItem() == ModItems.PSEUDO_INVERSION_SIGIL.get()) {
                    makeStable = true;
                    break;
                }
            }
            if (!makeStable) {
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
                if (allNuggets && count == 9) makeStable = true;
            }
            if (makeStable) result.remove(timerType());
        }

        if (result.getItem() instanceof SoulFragmentItem) {
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                if (event.getInventory().getItem(i).getItem() instanceof EthericSwordItem) {
                    Player player = event.getEntity();
                    double currentMax = player.getAttributeValue(Attributes.MAX_HEALTH);
                    player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.max(2.0, currentMax * 0.9));
                    break;
                }
            }
        }
    }

    // ==================== 死亡事件 ====================

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Entity entity = event.getEntity();

        if (entity instanceof WitherBoss || entity instanceof EnderDragon) {
            entity.level().addFreshEntity(new ItemEntity(
                    entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(ModItems.DIVISION_SIGIL.get())
            ));
        }

        // 仪式怪物死亡 → 检查波次
        if (entity.getTags().contains("division_ritual")) {
            handleRitualMobDeath(entity);
        }

        // 铁傀儡死亡 → 激活仪式第一波
        if (entity instanceof IronGolem) {
            handleIronGolemRitualTrigger(entity);
        }
    }

    private static void handleIronGolemRitualTrigger(Entity golem) {
        BlockPos pos = golem.blockPosition();
        for (BlockPos beaconPos : BlockPos.betweenClosed(pos.offset(-7, -7, -7), pos.offset(7, 7, 7))) {
            RitualManager.ActiveRitual ritual = RitualManager.getRitual(beaconPos.immutable());
            if (ritual != null && ritual.state == RitualManager.State.AWAITING_GOLEM) {
                ritual.state = RitualManager.State.ACTIVE;
                ServerLevel level = ritual.level;
                Player player = level.getPlayerByUUID(ritual.playerUUID);
                if (player != null) {
                    ritual.bossBar.addPlayer((ServerPlayer) player);
                }
                // 生成第一波
                spawnNextWave(ritual);
                return;
            }
        }
    }

    private static void handleRitualMobDeath(Entity entity) {
        ServerLevel level = (ServerLevel) entity.level();

        Map<BlockPos, RitualManager.ActiveRitual> snapshot = new HashMap<>(RitualManager.RITUALS);
        for (RitualManager.ActiveRitual ritual : snapshot.values()) {
            if (ritual.state != RitualManager.State.ACTIVE) continue;

            // 检查时间限制
            if (RitualManager.isPastDeadline(level)) {
                failRitual(ritual);
                continue;
            }

            if (ritual.spawnedMobs.remove(entity.getUUID())) {
                ritual.updateProgress();

                // 当前波次全部击杀
                if (ritual.spawnedMobs.isEmpty()) {
                    ritual.currentWave++;

                    if (ritual.currentWave >= RitualManager.TOTAL_WAVES) {
                        completeRitual(ritual);
                    } else {
                        spawnNextWave(ritual);
                    }
                }
            }
        }
    }

    private static void spawnNextWave(RitualManager.ActiveRitual ritual) {
        RitualManager.spawnWave(ritual);
        ritual.bossBar.setName(Component.translatable("ritual.divisionceremony.progress")
                .append(" - ")
                .append(Component.translatable("ritual.divisionceremony.wave",
                        ritual.currentWave + 1, RitualManager.TOTAL_WAVES)));
        ritual.updateProgress();
    }

    private static void completeRitual(RitualManager.ActiveRitual ritual) {
        ritual.state = RitualManager.State.COMPLETED;
        ritual.bossBar.setProgress(1.0f);
        ritual.bossBar.setName(Component.translatable("ritual.divisionceremony.complete"));

        ServerLevel level = ritual.level;

        Player player = level.getPlayerByUUID(ritual.playerUUID);
        if (player != null) {
            convertSigil(player);
        }

        level.getServer().execute(() -> RitualManager.removeRitual(ritual.beaconPos));
    }

    private static void failRitual(RitualManager.ActiveRitual ritual) {
        ritual.state = RitualManager.State.FAILED;
        ritual.bossBar.setName(Component.translatable("ritual.divisionceremony.failed"));
        ritual.bossBar.setColor(BossEvent.BossBarColor.WHITE);

        // 清理残留怪物
        for (UUID uuid : ritual.spawnedMobs) {
            Entity mob = ritual.level.getEntity(uuid);
            if (mob != null) mob.discard();
        }
        ritual.spawnedMobs.clear();

        Player player = ritual.level.getPlayerByUUID(ritual.playerUUID);
        if (player != null) {
            player.sendSystemMessage(Component.translatable("ritual.divisionceremony.failed_msg"));
        }

        ritual.level.getServer().execute(() -> RitualManager.removeRitual(ritual.beaconPos));
    }

    private static void convertSigil(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof DivisionSigilItem) {
                player.getInventory().setItem(i, new ItemStack(ModItems.PSEUDO_INVERSION_SIGIL.get()));
                player.sendSystemMessage(Component.translatable("ritual.divisionceremony.sigil_converted"));
                return;
            }
        }
    }

    // ==================== 不稳定锭事件 ====================

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            if (stack.getItem() == ModItems.UNSTABLE_INGOT.get()) {
                Long creationTime = stack.get(timerType());
                if (creationTime != null) {
                    itemEntity.discard();
                    UnstableIngotItem.triggerExplosion(event.getLevel(), itemEntity, stack);
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
