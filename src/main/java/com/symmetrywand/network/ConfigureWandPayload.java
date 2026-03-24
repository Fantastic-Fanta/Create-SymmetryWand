package com.symmetrywand.network;

import com.symmetrywand.SymmetryWandMod;
import com.symmetrywand.mirror.SymmetryMirror;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public record ConfigureWandPayload(InteractionHand hand, SymmetryMirror mirror) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ConfigureWandPayload> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SymmetryWandMod.MOD_ID, "configure_wand"));

	private static final StreamCodec<ByteBuf, InteractionHand> HAND_CODEC = ByteBufCodecs.BYTE.map(
		b -> InteractionHand.values()[b],
		h -> (byte) h.ordinal()
	);

	public static final StreamCodec<ByteBuf, ConfigureWandPayload> STREAM_CODEC = StreamCodec.composite(
		HAND_CODEC, ConfigureWandPayload::hand,
		SymmetryMirror.STREAM_CODEC, ConfigureWandPayload::mirror,
		ConfigureWandPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
