package com.qwq.division.event;

import com.qwq.division.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仪式管理器：波次刷怪 + 时间限制
 */
public class RitualManager {
    static final Map<BlockPos, ActiveRitual> RITUALS = new ConcurrentHashMap<>();
    public static final long DEADLINE_TIME = 22000;

    // 缓存的实体类型列表
    private static List<EntityType<?>> cachedMobTypes = null;
    private static long lastConfigHash = 0;

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
            int totalWaves = Config.RITUAL_TOTAL_WAVES.get();
            int done = currentWave - (spawnedMobs.isEmpty() ? 0 : 1);
            int perWave = Config.RITUAL_MOBS_PER_WAVE.get();
            int killedInWave = perWave - spawnedMobs.size();
            float waveProgress = (float) killedInWave / perWave;
            bossBar.setProgress((done + waveProgress) / totalWaves);
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
     * 在信标周围地表生成一波怪物（配置驱动）
     */
    public static void spawnWave(ActiveRitual ritual) {
        ServerLevel level = ritual.level;
        BlockPos center = ritual.beaconPos;
        List<EntityType<?>> types = getMobTypes();
        int count = Config.RITUAL_MOBS_PER_WAVE.get();

        for (int i = 0; i < count; i++) {
            EntityType<?> type = types.get(level.random.nextInt(types.size()));
            BlockPos spawnPos = findSurfacePos(level, center, 7, 20);
            Mob mob = (Mob) type.create(level, null, spawnPos, MobSpawnType.EVENT, false, false);
            if (mob != null) {
                mob.addTag("division_ritual");
                mob.setGlowingTag(true);
                level.addFreshEntity(mob);
                ritual.spawnedMobs.add(mob.getUUID());
            }
        }
    }

    /**
     * 从配置加载并缓存实体类型列表
     */
    @SuppressWarnings("unchecked")
    private static List<EntityType<?>> getMobTypes() {
        List<? extends String> configList = Config.RITUAL_MOB_TYPES.get();
        long hash = configList.hashCode();
        if (cachedMobTypes != null && hash == lastConfigHash) {
            return cachedMobTypes;
        }

        List<EntityType<?>> types = new ArrayList<>();
        for (String id : configList) {
            ResourceLocation rl = ResourceLocation.tryParse(id.trim());
            if (rl != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
                if (type != null) {
                    types.add(type);
                }
            }
        }
        if (types.isEmpty()) {
            types.add(EntityType.ZOMBIE);
            types.add(EntityType.SKELETON);
        }
        cachedMobTypes = types;
        lastConfigHash = hash;
        return types;
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

    public static boolean isPastDeadline(Level level) {
        long timeOfDay = level.getDayTime() % 24000;
        return timeOfDay >= DEADLINE_TIME;
    }
}
