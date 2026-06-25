package com.qwq.division;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// 客户端类，不会在专用服务器上加载。从这里访问客户端代码是安全的。
@Mod(value = DivisionCeremony.MODID, dist = Dist.CLIENT)
// 使用EventBusSubscriber自动注册类中所有带@SubscribeEvent注解的静态方法
@EventBusSubscriber(modid = DivisionCeremony.MODID, value = Dist.CLIENT)
public class DivisionCeremonyClient {
    public DivisionCeremonyClient(ModContainer container) {
        // 允许NeoForge为此模组的配置创建配置屏幕。
        // 配置屏幕通过转到模组屏幕 > 点击你的模组 > 点击配置来访问。
        // 不要忘记将配置选项的翻译添加到en_us.json文件中。
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端设置代码
        DivisionCeremony.LOGGER.info("客户端设置完成");
    }
}
