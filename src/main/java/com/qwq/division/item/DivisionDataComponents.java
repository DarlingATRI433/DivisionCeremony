package com.qwq.division.item;

import com.mojang.serialization.Codec;
import com.qwq.division.DivisionCeremony;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 自定义数据组件注册
 */
public class DivisionDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, DivisionCeremony.MODID);

    /** 不稳定金属锭的创建时间（游戏刻），用于爆炸倒计时 */
    public static final Supplier<DataComponentType<Long>> UNSTABLE_CREATION_TIME =
            DATA_COMPONENTS.register("unstable_creation_time", () ->
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(StreamCodec.of(
                                    FriendlyByteBuf::writeVarLong,
                                    FriendlyByteBuf::readVarLong
                            ))
                            .build()
            );

    /** 莫比乌斯升级标记（锻造台升级后获得） */
    public static final Supplier<DataComponentType<Boolean>> UPGRADED =
            DATA_COMPONENTS.register("upgraded", () ->
                    DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(StreamCodec.of(
                                    FriendlyByteBuf::writeBoolean,
                                    FriendlyByteBuf::readBoolean
                            ))
                            .build()
            );
}
