package com.qwq.division.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仪式管理器：波次刷怪 + 时间限制
 */
public class RitualManager {
    static final Map<BlockPos, ActiveRitual> RITUALS = new ConcurrentHashMap<>();

    public static final int TOTAL_WAVES = 5;
    public static final int ZOMBIES_PER_WAVE = 5;
    public static final int SKELETONS_PER_WAVE = 3;
    public static final long DEADLINE_TIME = 22000; // 凌晨4:00

    public enum State {
        AWAITING_GOLEM,
        ACTIVE,
        COMPLETED,
        FAILED
    }

    public static class ActiveRitual {
        public final BlockPos beaconPos;
        public final UUID playerUUID;
        public final ServerBossEvent bossBar;
        public final Set<UUID> spawnedMobs = ConcurrentHashMap.newKeySet();
        public State state;
        public ServerLevel level;
        public int currentWave;

        public ActiveRitual(BlockPos beaconPos, UUID playerUUID, ServerLevel level) {
            this.beaconPos = beaconPos;
            this.playerUUID = playerUUID;
            this.level = level;
            this.currentWave = 0;
            this.state = State.AWAITING_GOLEM;

            this.bossBar = new ServerBossEvent(
                    Component.translatable("ritual.divisionceremony.progress"),
                    BossEvent.BossBarColor.RED,
                    BossEvent.BossBarOverlay.PROGRESS
            );
            this.bossBar.setProgress(0.0f);
        }

        public void updateProgress() {
            int done = currentWave - (spawnedMobs.isEmpty() ? 0 : 1);
            int killedInWave = ZOMBIES_PER_WAVE + SKELETONS_PER_WAVE - spawnedMobs.size();
            float waveProgress = (float) killedInWave / (ZOMBIES_PER_WAVE + SKELETONS_PER_WAVE);
            bossBar.setProgress((done + waveProgress) / TOTAL_WAVES);
        }
    }

    public static ActiveRitual getRitual(BlockPos beaconPos) {
        return RITUALS.get(beaconPos);
    }

    public static void startRitual(BlockPos beaconPos, UUID playerUUID, ServerLevel level) {
        ActiveRitual ritual = new ActiveRitual(beaconPos, playerUUID, level);
        RITUALS.put(beaconPos, ritual);
    }

    public static void removeRitual(BlockPos beaconPos) {
        ActiveRitual ritual = RITUALS.remove(beaconPos);
        if (ritual != null) {
            ritual.bossBar.removeAllPlayers();
        }
    }

    /**
     * 在信标周围地表生成一波怪物
     */
    public static void spawnWave(ActiveRitual ritual) {
        ServerLevel level = ritual.level;
        BlockPos center = ritual.beaconPos;

        for (int i = 0; i < ZOMBIES_PER_WAVE; i++) {
            BlockPos spawnPos = findSurfacePos(level, center, 7, 20);
            Zombie zombie = EntityType.ZOMBIE.create(level, null, spawnPos, MobSpawnType.EVENT, false, false);
            if (zombie != null) {
                zombie.addTag("division_ritual");
                zombie.setGlowingTag(true);
                level.addFreshEntity(zombie);
                ritual.spawnedMobs.add(zombie.getUUID());
            }
        }

        for (int i = 0; i < SKELETONS_PER_WAVE; i++) {
            BlockPos spawnPos = findSurfacePos(level, center, 7, 20);
            Skeleton skeleton = EntityType.SKELETON.create(level, null, spawnPos, MobSpawnType.EVENT, false, false);
            if (skeleton != null) {
                skeleton.addTag("division_ritual");
                skeleton.setGlowingTag(true);
                level.addFreshEntity(skeleton);
                ritual.spawnedMobs.add(skeleton.getUUID());
            }
        }
    }

    private static BlockPos findSurfacePos(ServerLevel level, BlockPos center, int minRadius, int maxRadius) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int dx = level.random.nextIntBetweenInclusive(-maxRadius, maxRadius);
            int dz = level.random.nextIntBetweenInclusive(-maxRadius, maxRadius);
            if (Math.abs(dx) < minRadius && Math.abs(dz) < minRadius) continue;

            BlockPos pos = center.offset(dx, 0, dz);
            int surfaceY = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY();
            return new BlockPos(pos.getX(), surfaceY + 1, pos.getZ());
        }
        return center.above(2);
    }

    public static boolean isRitualTime(Level level) {
        long timeOfDay = level.getDayTime() % 24000;
        return timeOfDay >= 17500 && timeOfDay <= 18500;
    }

    /**
     * 检查是否超过时限（凌晨4:00）
     */
    public static boolean isPastDeadline(Level level) {
        long timeOfDay = level.getDayTime() % 24000;
        return timeOfDay >= DEADLINE_TIME;
    }
}
