package com.symmetrywand.mixin;

import com.symmetrywand.SymmetryGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
	@Inject(method = "place", at = @At("RETURN"))
	private void symmetrywand_afterPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
		if (!cir.getReturnValue().consumesAction()) {
			return;
		}
		Level level = context.getLevel();
		if (level.isClientSide()) {
			return;
		}
		Player player = context.getPlayer();
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		BlockPos placed = placementPos(context);
		SymmetryGameEvents.onBlockPlaced(serverPlayer, placed, level.getBlockState(placed));
	}

	private static BlockPos placementPos(BlockPlaceContext ctx) {
		BlockPos click = ctx.getClickedPos();
		if (ctx.getLevel().getBlockState(click).canBeReplaced(ctx)) {
			return click;
		}
		return click.relative(ctx.getClickedFace());
	}
}
