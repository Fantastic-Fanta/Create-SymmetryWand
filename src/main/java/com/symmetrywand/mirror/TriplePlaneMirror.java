package com.symmetrywand.mirror;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TriplePlaneMirror extends SymmetryMirror {
	public TriplePlaneMirror(Vec3 pos) {
		super(pos);
		this.orientationIndex = 0;
	}

	@Override
	public Map<BlockPos, BlockState> process(BlockPos position, BlockState block) {
		Map<BlockPos, BlockState> result = new HashMap<>();
		result.put(flipX(position), flipX(block));
		result.put(flipZ(position), flipZ(block));
		result.put(flipX(flipZ(position)), flipX(flipZ(block)));
		result.put(flipD1(position), flipD1(block));
		result.put(flipD1(flipX(position)), flipD1(flipX(block)));
		result.put(flipD1(flipZ(position)), flipD1(flipZ(block)));
		result.put(flipD1(flipX(flipZ(position))), flipD1(flipX(flipZ(block))));
		return result;
	}

	@Override
	public String typeName() {
		return TRIPLE_PLANE;
	}

	@Override
	public void rotate(boolean forward) {
		// Single alignment mode; orientation UI is a no-op (matches Create behaviour).
	}

	@Override
	protected void setOrientation() {
	}

	@Override
	public void setOrientation(int index) {
	}

	@Override
	public StringRepresentable getOrientation() {
		return CrossPlaneMirror.Align.Y;
	}

	@Override
	public List<Component> alignToolTips() {
		return List.of(Component.translatable("symmetrywand.orientation.horizontal"));
	}
}
