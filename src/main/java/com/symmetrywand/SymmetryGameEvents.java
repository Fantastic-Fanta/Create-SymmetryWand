package com.symmetrywand;

import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.registry.ModItems;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class SymmetryGameEvents {
	private SymmetryGameEvents() {
	}

	public static void register() {
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
				return true;
			}
			forEachHotbarWand(serverPlayer, stack ->
				SymmetryWandItem.remove(world, stack, serverPlayer, pos));
			return true;
		});
	}

	public static void onBlockPlaced(ServerPlayer player, net.minecraft.core.BlockPos placedPos, net.minecraft.world.level.block.state.BlockState placedState) {
		forEachHotbarWand(player, stack ->
			SymmetryWandItem.apply(player.level(), stack, player, placedPos, placedState));
	}

	private static void forEachHotbarWand(Player player, java.util.function.Consumer<ItemStack> action) {
		Inventory inv = player.getInventory();
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.is(ModItems.SYMMETRY_WAND)) {
				action.accept(stack);
			}
		}
	}
}
