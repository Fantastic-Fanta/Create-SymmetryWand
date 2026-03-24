package com.symmetrywand.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

/**
 * Ensures symmetry-effect block models are baked even though no block/item references them.
 * Orphan {@code models/item/symmetry_preview_*.json} files are never loaded by vanilla.
 */
public final class SymmetryMirrorModelLoading {
	static final ResourceLocation PLANE = ResourceLocation.fromNamespaceAndPath("symmetrywand", "block/symmetry_effect/plane");
	static final ResourceLocation CROSSPLANE = ResourceLocation.fromNamespaceAndPath("symmetrywand", "block/symmetry_effect/crossplane");
	static final ResourceLocation TRIPLEPLANE = ResourceLocation.fromNamespaceAndPath("symmetrywand", "block/symmetry_effect/tripleplane");

	private SymmetryMirrorModelLoading() {
	}

	public static void register() {
		ModelLoadingPlugin.register(ctx -> ctx.addModels(PLANE, CROSSPLANE, TRIPLEPLANE));
	}
}
