package com.symmetrywand.client;

import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class SymmetryWandClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SymmetryMirrorModelLoading.register();

		SymmetryWandItem.OPEN_GUI = (player, stack, hand) ->
			net.minecraft.client.Minecraft.getInstance().setScreen(new SymmetryWandScreen(stack, hand));

		ClientNetworking.register();
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SYMMETRY_WAND, new SymmetryWandItemRenderer());
		SymmetryWorldPreview.register();
	}
}
