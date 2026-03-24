package com.symmetrywand.registry;

import com.symmetrywand.SymmetryWandMod;
import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.mirror.EmptyMirror;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public final class ModItems {
	public static final Item SYMMETRY_WAND = register("symmetry_wand",
		new SymmetryWandItem(new Item.Properties()
			.stacksTo(1)
			.component(ModDataComponents.SYMMETRY_WAND, new EmptyMirror(Vec3.ZERO))
			.component(ModDataComponents.SYMMETRY_WAND_ENABLE, false)
			.component(ModDataComponents.SYMMETRY_WAND_SIMULATE, false)));

	private ModItems() {
	}

	public static void register() {
	}

	private static Item register(String id, Item item) {
		return Registry.register(BuiltInRegistries.ITEM,
			ResourceLocation.fromNamespaceAndPath(SymmetryWandMod.MOD_ID, id), item);
	}
}
