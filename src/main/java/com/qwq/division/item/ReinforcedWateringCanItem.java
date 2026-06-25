package com.qwq.division.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 强化洒水壶 - 右键长按催熟9x9范围农作物和树苗，带水滴粒子
 */
public class ReinforcedWateringCanItem extends Item {
    private static final int RADIUS = 4; // 9x9 = 半径4
    private static final int USE_DURATION = 72000;

    public ReinforcedWateringCanItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (level.isClientSide || !(entity instanceof Player player)) return;
        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos center = player.blockPosition();

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                // 检查多个Y层：脚下、同层、耕地上层、高植株
                for (int dy = -1; dy <= 2; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);

                    // 排除草方块
                    if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.SHORT_GRASS)
                            || state.is(Blocks.TALL_GRASS)) continue;

                    // 水滴粒子
                    if (level.random.nextFloat() < 0.2f) {
                        serverLevel.sendParticles(
                                ParticleTypes.FALLING_WATER,
                                pos.getX() + level.random.nextFloat(),
                                pos.getY() + 0.8 + level.random.nextFloat() * 0.5,
                                pos.getZ() + level.random.nextFloat(),
                                1, 0.1, 0.05, 0.1, 0.02);
                    }

                    // 催熟
                    if (state.getBlock() instanceof BonemealableBlock bonemealable) {
                        if (bonemealable.isValidBonemealTarget(level, pos, state)) {
                            if (level.random.nextFloat() < 0.06f) {
                                bonemealable.performBonemeal(serverLevel, level.random, pos, state);
                                serverLevel.sendParticles(
                                        ParticleTypes.HAPPY_VILLAGER,
                                        pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
                                        2, 0.2, 0.1, 0.2, 0.5);
                            }
                        }
                    }
                }
            }
        }
    }
}
