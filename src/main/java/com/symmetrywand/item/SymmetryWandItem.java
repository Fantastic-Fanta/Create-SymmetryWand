package com.symmetrywand.item;

import com.symmetrywand.mirror.CrossPlaneMirror;
import com.symmetrywand.mirror.EmptyMirror;
import com.symmetrywand.mirror.PlaneMirror;
import com.symmetrywand.mirror.SymmetryMirror;
import com.symmetrywand.registry.ModDataComponents;
import com.symmetrywand.registry.ModItems;
import com.symmetrywand.util.InventoryUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SymmetryWandItem extends Item {
	public static final int MAX_RANGE = 128;

	public static volatile OpenGui OPEN_GUI = (player, stack, hand) -> {
	};

	@FunctionalInterface
	public interface OpenGui {
		void open(Player player, ItemStack stack, InteractionHand hand);
	}

	public SymmetryWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		if (player == null) {
			return InteractionResult.PASS;
		}
		player.getCooldowns().addCooldown(this, 5);
		ItemStack wand = player.getItemInHand(context.getHand());
		checkComponents(wand);

		if (player.isShiftKeyDown()) {
			if (player.level().isClientSide()) {
				OPEN_GUI.open(player, wand, context.getHand());
				player.getCooldowns().addCooldown(this, 5);
			}
			return InteractionResult.SUCCESS;
		}

		if (context.getLevel().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		// Create wiki: right-click a block to create or move the mirror; right-click air (use) clears it.
		pos = pos.relative(context.getClickedFace());
		SymmetryMirror previousElement = wand.get(ModDataComponents.SYMMETRY_WAND);

		wand.set(ModDataComponents.SYMMETRY_WAND_ENABLE, true);
		Vec3 pos3d = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		SymmetryMirror newElement = new PlaneMirror(pos3d);

		if (previousElement instanceof EmptyMirror) {
			newElement.setOrientation(
				(player.getDirection() == Direction.NORTH || player.getDirection() == Direction.SOUTH)
					? PlaneMirror.Align.XY.ordinal()
					: PlaneMirror.Align.YZ.ordinal());
			newElement.enable = true;
			wand.set(ModDataComponents.SYMMETRY_WAND_ENABLE, true);
		} else {
			// New instance so ItemStack applies the component change (in-place mutation is skipped).
			SymmetryMirror moved = previousElement.withPosition(pos3d);

			if (moved instanceof PlaneMirror) {
				moved.setOrientation(
					(player.getDirection() == Direction.NORTH || player.getDirection() == Direction.SOUTH)
						? PlaneMirror.Align.XY.ordinal()
						: PlaneMirror.Align.YZ.ordinal());
			}

			if (moved instanceof CrossPlaneMirror) {
				float rotation = player.getYHeadRot();
				float abs = Math.abs(rotation % 90);
				boolean diagonal = abs > 22 && abs < 45 + 22;
				moved.setOrientation(diagonal ? CrossPlaneMirror.Align.D.ordinal() : CrossPlaneMirror.Align.Y.ordinal());
			}

			newElement = moved;
		}

		wand.set(ModDataComponents.SYMMETRY_WAND, newElement);
		player.setItemInHand(context.getHand(), wand);
		return InteractionResult.SUCCESS;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack wand = playerIn.getItemInHand(handIn);
		checkComponents(wand);

		if (playerIn.isShiftKeyDown()) {
			if (worldIn.isClientSide()) {
				OPEN_GUI.open(playerIn, wand, handIn);
				playerIn.getCooldowns().addCooldown(this, 5);
			}
			return InteractionResultHolder.sidedSuccess(wand, worldIn.isClientSide());
		}

		wand.set(ModDataComponents.SYMMETRY_WAND_ENABLE, false);
		return InteractionResultHolder.sidedSuccess(wand, worldIn.isClientSide());
	}

	private static void checkComponents(ItemStack wand) {
		if (!wand.has(ModDataComponents.SYMMETRY_WAND)) {
			wand.set(ModDataComponents.SYMMETRY_WAND, new EmptyMirror(Vec3.ZERO));
			wand.set(ModDataComponents.SYMMETRY_WAND_ENABLE, false);
		}
	}

	public static boolean isEnabled(ItemStack stack) {
		checkComponents(stack);
		return Boolean.TRUE.equals(stack.get(ModDataComponents.SYMMETRY_WAND_ENABLE))
			&& !Boolean.TRUE.equals(stack.get(ModDataComponents.SYMMETRY_WAND_SIMULATE));
	}

	public static SymmetryMirror getMirror(ItemStack stack) {
		checkComponents(stack);
		return stack.get(ModDataComponents.SYMMETRY_WAND);
	}

	public static void configureSettings(ItemStack stack, SymmetryMirror mirror) {
		checkComponents(stack);
		stack.set(ModDataComponents.SYMMETRY_WAND, mirror);
	}

	public static void apply(Level world, ItemStack wand, Player player, BlockPos pos, BlockState block) {
		checkComponents(wand);
		if (!isEnabled(wand)) {
			return;
		}
		if (!BlockItem.BY_BLOCK.containsKey(block.getBlock())) {
			return;
		}

		Map<BlockPos, BlockState> blockSet = new HashMap<>();
		blockSet.put(pos, block);
		SymmetryMirror symmetry = wand.get(ModDataComponents.SYMMETRY_WAND);

		Vec3 mirrorPos = symmetry.getPosition();
		if (mirrorPos.distanceTo(Vec3.atLowerCornerOf(pos)) > MAX_RANGE) {
			return;
		}
		if (!player.isCreative() && isHoldingBlock(player, block)
			&& InventoryUtil.findAndRemoveInInventory(block, player, 1) == 0) {
			return;
		}

		symmetry.process(blockSet);
		BlockPos to = BlockPos.containing(mirrorPos);
		List<BlockPos> targets = new ArrayList<>();
		targets.add(pos);

		for (BlockPos position : blockSet.keySet()) {
			if (position.equals(pos)) {
				continue;
			}

			if (world.isUnobstructed(block, position, CollisionContext.of(player))) {
				BlockState blockState = blockSet.get(position);
				boolean skipNeighborShapeUpdates = blockState.hasProperty(BlockStateProperties.SLAB_TYPE)
					&& blockState.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE;
				if (!skipNeighborShapeUpdates) {
					for (Direction face : Direction.values()) {
						blockState = blockState.updateShape(face, world.getBlockState(position.relative(face)), world,
							position, position.relative(face));
					}
				}

				if (player.isCreative()) {
					world.setBlockAndUpdate(position, blockState);
					targets.add(position);
					continue;
				}

				BlockState toReplace = world.getBlockState(position);
				if (!toReplace.canBeReplaced()) {
					continue;
				}
				if (toReplace.getDestroySpeed(world, position) == -1) {
					continue;
				}

				if (InventoryUtil.findAndRemoveInInventory(blockState, player, 1) == 0) {
					continue;
				}

				FluidState fluidState = world.getFluidState(position);
				world.setBlock(position, fluidState.createLegacyBlock(), Block.UPDATE_KNOWN_SHAPE);
				world.setBlockAndUpdate(position, blockState);
				targets.add(position);
			}
		}

		if (player instanceof ServerPlayer sp) {
			com.symmetrywand.network.ModNetworking.sendSymmetryEffect(sp, to, targets);
		}
	}

	private static boolean isHoldingBlock(Player player, BlockState block) {
		ItemStack itemBlock = InventoryUtil.getRequiredItem(block);
		return player.isHolding(itemBlock.getItem());
	}

	public static void remove(Level world, ItemStack wand, Player player, BlockPos pos) {
		BlockState air = Blocks.AIR.defaultBlockState();
		BlockState ogBlock = world.getBlockState(pos);
		checkComponents(wand);
		if (!isEnabled(wand)) {
			return;
		}

		Map<BlockPos, BlockState> blockSet = new HashMap<>();
		blockSet.put(pos, air);
		SymmetryMirror symmetry = wand.get(ModDataComponents.SYMMETRY_WAND);

		Vec3 mirrorPos = symmetry.getPosition();
		if (mirrorPos.distanceTo(Vec3.atLowerCornerOf(pos)) > MAX_RANGE) {
			return;
		}

		symmetry.process(blockSet);

		BlockPos to = BlockPos.containing(mirrorPos);
		List<BlockPos> targets = new ArrayList<>();
		targets.add(pos);

		for (BlockPos position : blockSet.keySet()) {
			if (!player.isCreative() && ogBlock.getBlock() != world.getBlockState(position).getBlock()) {
				continue;
			}
			if (position.equals(pos)) {
				continue;
			}

			BlockState blockstate = world.getBlockState(position);
			if (!blockstate.isAir()) {
				targets.add(position);
				world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, position, Block.getId(blockstate));
				world.setBlock(position, air, Block.UPDATE_ALL);

				if (!player.isCreative()) {
					if (!player.getMainHandItem().isEmpty()) {
						player.getMainHandItem().mineBlock(world, blockstate, position, player);
					}
					BlockEntity blockEntity = blockstate.hasBlockEntity() ? world.getBlockEntity(position) : null;
					Block.dropResources(blockstate, world, pos, blockEntity, player, player.getMainHandItem());
				}
			}
		}

		if (player instanceof ServerPlayer sp) {
			com.symmetrywand.network.ModNetworking.sendSymmetryEffect(sp, to, targets);
		}
	}

	public static boolean presentInHotbar(Player player) {
		Inventory inv = player.getInventory();
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			if (inv.getItem(i).is(ModItems.SYMMETRY_WAND)) {
				return true;
			}
		}
		return false;
	}
}
