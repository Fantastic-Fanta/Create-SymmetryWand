package com.symmetrywand.registry;

import com.symmetrywand.SymmetryWandMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroups {
	private ModItemGroups() {
	}

	public static void register() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
			ResourceLocation.fromNamespaceAndPath(SymmetryWandMod.MOD_ID, "general"),
			FabricItemGroup.builder()
				.icon(() -> new ItemStack(ModItems.SYMMETRY_WAND))
				.title(Component.translatable("itemGroup.symmetrywand.general"))
				.displayItems((params, output) -> output.accept(ModItems.SYMMETRY_WAND))
				.build());
	}
}
