package com.symmetrywand.registry;

import com.mojang.serialization.Codec;
import com.symmetrywand.SymmetryWandMod;
import com.symmetrywand.mirror.SymmetryMirror;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

public final class ModDataComponents {
	public static final DataComponentType<SymmetryMirror> SYMMETRY_WAND = register("symmetry_wand",
		DataComponentType.<SymmetryMirror>builder()
			.persistent(SymmetryMirror.CODEC)
			.networkSynchronized(SymmetryMirror.STREAM_CODEC)
			.build());

	public static final DataComponentType<Boolean> SYMMETRY_WAND_ENABLE = register("symmetry_wand_enable",
		DataComponentType.<Boolean>builder()
			.persistent(Codec.BOOL)
			.networkSynchronized(ByteBufCodecs.BOOL)
			.build());

	public static final DataComponentType<Boolean> SYMMETRY_WAND_SIMULATE = register("symmetry_wand_simulate",
		DataComponentType.<Boolean>builder()
			.persistent(Codec.BOOL)
			.networkSynchronized(ByteBufCodecs.BOOL)
			.build());

	private ModDataComponents() {
	}

	public static void register() {
	}

	private static <T> DataComponentType<T> register(String id, DataComponentType<T> type) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
			ResourceLocation.fromNamespaceAndPath(SymmetryWandMod.MOD_ID, id), type);
	}
}
