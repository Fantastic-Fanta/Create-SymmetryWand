package com.symmetrywand.client;

import com.symmetrywand.network.SymmetryEffectPayload;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class SymmetryEffectHandler {
	private SymmetryEffectHandler() {
	}

	public static void handle(SymmetryEffectPayload payload) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		if (player.position().distanceTo(Vec3.atLowerCornerOf(payload.mirror())) > 100) {
			return;
		}
		for (BlockPos to : payload.positions()) {
			drawEffect(payload.mirror(), to);
		}
	}

	private static void drawEffect(BlockPos from, BlockPos to) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}
		double density = 0.8f;
		Vec3 start = Vec3.atLowerCornerOf(from).add(0.5, 0.5, 0.5);
		Vec3 end = Vec3.atLowerCornerOf(to).add(0.5, 0.5, 0.5);
		Vec3 diff = end.subtract(start);
		Vec3 step = diff.normalize().scale(density);
		int steps = (int) (diff.length() / step.length());
		Random r = new Random();
		for (int i = 3; i < steps - 1; i++) {
			Vec3 pos = start.add(step.scale(i));
			Vec3 speed = new Vec3(0, r.nextDouble() * -40f, 0);
			mc.level.addParticle(new DustParticleOptions(new Vector3f(1, 1, 1), 1), pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
		}
		Vec3 speed = new Vec3(0, r.nextDouble() * 1 / 32f, 0);
		Vec3 pos = start.add(step.scale(2));
		mc.level.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
		speed = new Vec3(0, r.nextDouble() * 1 / 32f, 0);
		pos = start.add(step.scale(steps));
		mc.level.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
	}
}
