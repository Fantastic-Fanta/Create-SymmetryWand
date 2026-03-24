package com.symmetrywand.util;

import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Vendored subset of Create's BlockHelper (MIT) — inventory consumption for mirrored placement.
 */
public final class InventoryUtil {
	private static final List<IntegerProperty> COUNT_STATES = List.of(
		BlockStateProperties.EGGS,
		BlockStateProperties.PICKLES,
		BlockStateProperties.CANDLES
	);

	private static final List<BooleanProperty> VINELIKE_STATES = List.of(
		BlockStateProperties.UP,
		BlockStateProperties.NORTH,
		BlockStateProperties.EAST,
		BlockStateProperties.SOUTH,
		BlockStateProperties.WEST,
		BlockStateProperties.DOWN
	);

	private InventoryUtil() {
	}

	public static int findAndRemoveInInventory(BlockState block, Player player, int amount) {
		int amountFound = 0;
		Item required = getRequiredItem(block).getItem();

		boolean needsTwo = block.hasProperty(BlockStateProperties.SLAB_TYPE)
			&& block.getValue(BlockStateProperties.SLAB_TYPE) == net.minecraft.world.level.block.state.properties.SlabType.DOUBLE;

		if (needsTwo) {
			amount *= 2;
		}

		for (IntegerProperty property : COUNT_STATES) {
			if (block.hasProperty(property)) {
				amount *= block.getValue(property);
			}
		}

		if (block.is(Blocks.VINE) || block.is(Blocks.GLOW_LICHEN)) {
			int vineCount = 0;
			for (BooleanProperty vineState : VINELIKE_STATES) {
				if (block.hasProperty(vineState) && block.getValue(vineState)) {
					vineCount++;
				}
			}
			amount += vineCount - 1;
		}

		{
			int preferredSlot = player.getInventory().selected;
			ItemStack itemstack = player.getInventory().getItem(preferredSlot);
			int count = itemstack.getCount();
			if (itemstack.getItem() == required && count > 0) {
				int taken = Math.min(count, amount - amountFound);
				player.getInventory().setItem(preferredSlot, new ItemStack(itemstack.getItem(), count - taken));
				amountFound += taken;
			}
		}

		for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
			if (amountFound == amount) {
				break;
			}

			ItemStack itemstack = player.getInventory().getItem(i);
			int count = itemstack.getCount();
			if (itemstack.getItem() == required && count > 0) {
				int taken = Math.min(count, amount - amountFound);
				player.getInventory().setItem(i, new ItemStack(itemstack.getItem(), count - taken));
				amountFound += taken;
			}
		}

		if (needsTwo) {
			if (amountFound % 2 != 0) {
				player.getInventory().add(new ItemStack(required));
			}
			amountFound /= 2;
		}

		return amountFound;
	}

	public static ItemStack getRequiredItem(BlockState state) {
		ItemStack itemStack = new ItemStack(state.getBlock());
		Item item = itemStack.getItem();
		if (item == Items.FARMLAND || item == Items.DIRT_PATH) {
			itemStack = new ItemStack(Items.DIRT);
		}
		return itemStack;
	}
}
