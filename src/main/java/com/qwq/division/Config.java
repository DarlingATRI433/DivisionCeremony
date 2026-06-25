package com.qwq.division;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

/**
 * 模组配置文件
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 不稳定金属锭计时（秒）
    public static final ModConfigSpec.IntValue UNSTABLE_INGOT_TIMER;
    // 仪式总波数
    public static final ModConfigSpec.IntValue RITUAL_TOTAL_WAVES;
    // 每波生成实体数量
    public static final ModConfigSpec.IntValue RITUAL_MOBS_PER_WAVE;
    // 仪式可生成实体类型列表
    public static final ModConfigSpec.ConfigValue<List<? extends String>> RITUAL_MOB_TYPES;

    static {
        BUILDER.push("unstable_ingot");
        UNSTABLE_INGOT_TIMER = BUILDER
                .comment("Time in seconds before the unstable ingot explodes")
                .defineInRange("explosion_timer_seconds", 8, 1, 60);
        BUILDER.pop();

        BUILDER.push("ritual");
        RITUAL_TOTAL_WAVES = BUILDER
                .comment("Total waves for the division ritual")
                .defineInRange("total_waves", 5, 1, 20);
        RITUAL_MOBS_PER_WAVE = BUILDER
                .comment("Number of mobs spawned per wave (max 8)")
                .defineInRange("mobs_per_wave", 8, 1, 8);
        RITUAL_MOB_TYPES = BUILDER
                .comment("Entity types to randomly spawn during ritual waves (comma-separated, max 8)")
                .defineList("mob_types",
                        () -> Arrays.asList("minecraft:zombie", "minecraft:skeleton"),
                        s -> s instanceof String && !((String) s).isEmpty());
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
