package com.qwq.division.item;

import com.qwq.division.DivisionCeremony;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 统一注册所有物品和创造模式标签页
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DivisionCeremony.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DivisionCeremony.MODID);

    // ==================== 工具（无法破坏） ====================
    public static final DeferredItem<DestructionPickaxeItem> DESTRUCTION_PICKAXE =
            ITEMS.register("destruction_pickaxe", DestructionPickaxeItem::new);

    public static final DeferredItem<ErosionShovelItem> EROSION_SHOVEL =
            ITEMS.register("erosion_shovel", ErosionShovelItem::new);

    public static final DeferredItem<EthericSwordItem> ETHERIC_SWORD =
            ITEMS.register("etheric_sword", EthericSwordItem::new);

    public static final DeferredItem<HealingAxeItem> HEALING_AXE =
            ITEMS.register("healing_axe", HealingAxeItem::new);

    public static final DeferredItem<ReversingHoeItem> REVERSING_HOE =
            ITEMS.register("reversing_hoe", ReversingHoeItem::new);

    // ==================== 特殊物品 ====================
    public static final DeferredItem<DivisionSigilItem> DIVISION_SIGIL =
            ITEMS.register("division_sigil", DivisionSigilItem::new);

    public static final DeferredItem<UnstableIngotItem> UNSTABLE_INGOT =
            ITEMS.register("unstable_ingot", UnstableIngotItem::new);

    public static final DeferredItem<SimpleDivisionItem> UNSTABLE_NUGGET =
            ITEMS.register("unstable_nugget", SimpleDivisionItem::new);

    public static final DeferredItem<SoulFragmentItem> SOUL_FRAGMENT =
            ITEMS.register("soul_fragment", SoulFragmentItem::new);

    public static final DeferredItem<ReinforcedWateringCanItem> REINFORCED_WATERING_CAN =
            ITEMS.register("reinforced_watering_can", ReinforcedWateringCanItem::new);

    // ==================== 创造模式标签页 ====================

    static {
        CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.divisionceremony"))
                .icon(() -> new ItemStack(DIVISION_SIGIL.get()))
                .displayItems((params, output) -> {
                    output.accept(DESTRUCTION_PICKAXE.get());
                    output.accept(EROSION_SHOVEL.get());
                    output.accept(ETHERIC_SWORD.get());
                    output.accept(HEALING_AXE.get());
                    output.accept(REVERSING_HOE.get());
                    output.accept(DIVISION_SIGIL.get());
                    output.accept(UNSTABLE_INGOT.get());
                    output.accept(UNSTABLE_NUGGET.get());
                    output.accept(SOUL_FRAGMENT.get());
                    output.accept(REINFORCED_WATERING_CAN.get());
                })
                .build());
    }
}
