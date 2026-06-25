package com.qwq.division;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.ArrayList;
import java.util.List;

@Mod(value = DivisionCeremony.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DivisionCeremony.MODID, value = Dist.CLIENT)
public class DivisionCeremonyClient {
    public DivisionCeremonyClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> createConfigScreen(parent));
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        DivisionCeremony.LOGGER.info("客户端设置完成");
    }

    @SuppressWarnings("unchecked")
    private static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.divisionceremony.title"));

        ConfigEntryBuilder eb = builder.entryBuilder();

        // 不稳定金属锭
        ConfigCategory cat1 = builder.getOrCreateCategory(
                Component.translatable("config.divisionceremony.category.unstable_ingot"));
        cat1.addEntry(eb.startIntSlider(
                        Component.translatable("config.divisionceremony.unstable_ingot.timer"),
                        Config.UNSTABLE_INGOT_TIMER.get(), 1, 60)
                .setDefaultValue(8)
                .setSaveConsumer(v -> Config.UNSTABLE_INGOT_TIMER.set(v))
                .setTextGetter(v -> Component.literal(v + " ")
                        .append(Component.translatable("config.divisionceremony.seconds")))
                .build());

        // 分割仪式
        ConfigCategory cat2 = builder.getOrCreateCategory(
                Component.translatable("config.divisionceremony.category.ritual"));
        cat2.addEntry(eb.startIntSlider(
                        Component.translatable("config.divisionceremony.ritual.waves"),
                        Config.RITUAL_TOTAL_WAVES.get(), 1, 20)
                .setDefaultValue(5)
                .setSaveConsumer(v -> Config.RITUAL_TOTAL_WAVES.set(v))
                .setTextGetter(v -> Component.literal(v + " ")
                        .append(Component.translatable("config.divisionceremony.waves_unit")))
                .build());
        cat2.addEntry(eb.startIntSlider(
                        Component.translatable("config.divisionceremony.ritual.mobs_per_wave"),
                        Config.RITUAL_MOBS_PER_WAVE.get(), 1, 8)
                .setDefaultValue(8)
                .setSaveConsumer(v -> Config.RITUAL_MOBS_PER_WAVE.set(v))
                .setTextGetter(v -> Component.literal(String.valueOf(v)))
                .build());
        cat2.addEntry(eb.startStrList(
                        Component.translatable("config.divisionceremony.ritual.mob_types"),
                        new ArrayList<>((List<String>) (Object) Config.RITUAL_MOB_TYPES.get()))
                .setDefaultValue(() -> List.of("minecraft:zombie", "minecraft:skeleton"))
                .setSaveConsumer(list -> Config.RITUAL_MOB_TYPES.set((List<String>) (Object) list))
                .build());

        builder.setSavingRunnable(() -> Config.SPEC.save());
        return builder.build();
    }
}
