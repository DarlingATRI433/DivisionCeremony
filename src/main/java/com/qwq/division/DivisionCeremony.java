package com.qwq.division;

import com.qwq.division.item.DivisionDataComponents;
import com.qwq.division.item.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(DivisionCeremony.MODID)
public class DivisionCeremony {
    public static final String MODID = "divisionceremony";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DivisionCeremony(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // 注册物品
        ModItems.ITEMS.register(modEventBus);
        // 注册创造标签页
        ModItems.CREATIVE_TABS.register(modEventBus);
        // 注册自定义数据组件
        DivisionDataComponents.DATA_COMPONENTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
