package com.symmetrywand.client;

import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.mirror.EmptyMirror;
import com.symmetrywand.mirror.SymmetryMirror;
import com.symmetrywand.registry.ModItems;
import java.util.Random;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Client-side mirror marker particles and world-space mirror mesh preview (from Create's {@code SymmetryHandler}).
 */
public final class SymmetryWorldPreview {
	private static int tickCounter;

	private SymmetryWorldPreview() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(SymmetryWorldPreview::clientTick);
		WorldRenderEvents.AFTER_ENTITIES.register(MirrorPaneWorldRenderer::render);
	}

	private static void clientTick(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null || mc.level == null || mc.isPaused()) {
			return;
		}
		tickCounter++;
		if (tickCounter % 10 != 0) {
			return;
		}
		Random r = new Random();
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stackInSlot = player.getInventory().getItem(i);
			if (!stackInSlot.is(ModItems.SYMMETRY_WAND) || !SymmetryWandItem.isEnabled(stackInSlot)) {
				continue;
			}
			SymmetryMirror mirror = SymmetryWandItem.getMirror(stackInSlot);
			if (mirror instanceof EmptyMirror) {
				continue;
			}
			double offsetX = (r.nextDouble() - 0.5) * 0.3;
			double offsetZ = (r.nextDouble() - 0.5) * 0.3;
			Vec3 pos = mirror.getPosition().add(0.5 + offsetX, 1 / 4d, 0.5 + offsetZ);
			Vec3 speed = new Vec3(0, r.nextDouble() * 1 / 8f, 0);
			mc.level.addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
		}
	}
}
