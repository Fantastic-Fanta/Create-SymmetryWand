package com.symmetrywand.network;

import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.registry.ModItems;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ModNetworking {
	private ModNetworking() {
	}

	/** Registers payload types (both sides) and server receivers. */
	public static void register() {
		PayloadTypeRegistry.playC2S().register(ConfigureWandPayload.TYPE, ConfigureWandPayload.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(SymmetryEffectPayload.TYPE, SymmetryEffectPayload.STREAM_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ConfigureWandPayload.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayer player = context.player();
				ItemStack stack = player.getItemInHand(payload.hand());
				if (stack.is(ModItems.SYMMETRY_WAND)) {
					SymmetryWandItem.configureSettings(stack, payload.mirror());
				}
			});
		});
	}

	public static void sendSymmetryEffect(ServerPlayer player, BlockPos mirror, List<BlockPos> targets) {
		ServerPlayNetworking.send(player, new SymmetryEffectPayload(mirror, targets));
	}
}
