package com.symmetrywand.mixin;

import com.symmetrywand.SymmetryGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
	@Shadow
	protected abstract BlockState getPlacementState(BlockPlaceContext context);

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
		BlockItem self = (BlockItem) (Object) this;
		BlockPlaceContext adjusted = self.updatePlacementContext(context);
		if (adjusted == null) {
			return;
		}
		BlockState placedState = getPlacementState(adjusted);
		if (placedState == null) {
			return;
		}
		BlockPos placed = adjusted.getClickedPos();
		SymmetryGameEvents.onBlockPlaced(serverPlayer, placed, placedState);
	}
}
