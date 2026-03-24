package com.symmetrywand.network;

import com.symmetrywand.SymmetryWandMod;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SymmetryEffectPayload(BlockPos mirror, List<BlockPos> positions) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SymmetryEffectPayload> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SymmetryWandMod.MOD_ID, "symmetry_effect"));

	public static final StreamCodec<ByteBuf, SymmetryEffectPayload> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SymmetryEffectPayload::mirror,
		ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC), SymmetryEffectPayload::positions,
		SymmetryEffectPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
