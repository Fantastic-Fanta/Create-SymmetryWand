package com.symmetrywand.client;

import com.symmetrywand.network.SymmetryEffectPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientNetworking {
	private ClientNetworking() {
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(SymmetryEffectPayload.TYPE, (payload, context) ->
			context.client().execute(() -> SymmetryEffectHandler.handle(payload)));
	}
}
