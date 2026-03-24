package com.symmetrywand.mirror;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CrossPlaneMirror extends SymmetryMirror {
	public enum Align implements StringRepresentable {
		Y("y"),
		D("d");

		private final String name;

		Align(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public CrossPlaneMirror(Vec3 pos) {
		super(pos);
		this.orientation = Align.Y;
	}

	@Override
	protected void setOrientation() {
		if (orientationIndex < 0) {
			orientationIndex += Align.values().length;
		}
		if (orientationIndex >= Align.values().length) {
			orientationIndex -= Align.values().length;
		}
		this.orientation = Align.values()[orientationIndex];
	}

	@Override
	public void setOrientation(int index) {
		this.orientation = Align.values()[index];
		this.orientationIndex = index;
	}

	@Override
	public Map<BlockPos, BlockState> process(BlockPos position, BlockState block) {
		Map<BlockPos, BlockState> result = new HashMap<>();
		switch ((Align) orientation) {
			case D -> {
				result.put(flipD1(position), flipD1(block));
				result.put(flipD2(position), flipD2(block));
				result.put(flipD1(flipD2(position)), flipD1(flipD2(block)));
			}
			case Y -> {
				result.put(flipX(position), flipX(block));
				result.put(flipZ(position), flipZ(block));
				result.put(flipX(flipZ(position)), flipX(flipZ(block)));
			}
		}
		return result;
	}

	@Override
	public String typeName() {
		return CROSS_PLANE;
	}

	@Override
	public List<Component> alignToolTips() {
		return List.of(
			Component.translatable("symmetrywand.orientation.orthogonal"),
			Component.translatable("symmetrywand.orientation.diagonal")
		);
	}
}
