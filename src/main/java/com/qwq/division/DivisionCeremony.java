package com.qwq.division;

import com.qwq.division.item.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DivisionCeremony.MODID)
public class DivisionCeremony {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "divisionceremony";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DivisionCeremony(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // 注册物品到事件总线
        ModItems.ITEMS.register(modEventBus);
        // 注册创造标签页到事件总线
        ModItems.CREATIVE_TABS.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
