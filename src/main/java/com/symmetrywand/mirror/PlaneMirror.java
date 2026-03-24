package com.symmetrywand.mirror;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PlaneMirror extends SymmetryMirror {
	public enum Align implements StringRepresentable {
		XY("xy"),
		YZ("yz");

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

	public PlaneMirror(Vec3 pos) {
		super(pos);
		this.orientation = Align.XY;
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
			case XY -> result.put(flipZ(position), flipZ(block));
			case YZ -> result.put(flipX(position), flipX(block));
		}
		return result;
	}

	@Override
	public String typeName() {
		return PLANE;
	}

	@Override
	public List<Component> alignToolTips() {
		return List.of(
			Component.translatable("symmetrywand.orientation.along_z"),
			Component.translatable("symmetrywand.orientation.along_x")
		);
	}
}
