package com.symmetrywand.mirror;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EmptyMirror extends SymmetryMirror {
	public enum Align implements StringRepresentable {
		NONE("none");

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

	public EmptyMirror(Vec3 pos) {
		super(pos);
		this.orientation = Align.NONE;
	}

	@Override
	protected void setOrientation() {
	}

	@Override
	public void setOrientation(int index) {
		this.orientation = Align.values()[index];
		this.orientationIndex = index;
	}

	@Override
	public Map<BlockPos, BlockState> process(BlockPos position, BlockState block) {
		return new HashMap<>();
	}

	@Override
	public String typeName() {
		return EMPTY;
	}

	@Override
	public List<Component> alignToolTips() {
		return List.of();
	}
}
