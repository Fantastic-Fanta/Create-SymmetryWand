package com.symmetrywand.client;

import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.mirror.EmptyMirror;
import com.symmetrywand.mirror.SymmetryMirror;
import com.symmetrywand.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * World-space mirror pane mesh (ported from Create's {@code SymmetryHandler#onRenderWorld}), using vanilla
 * {@link net.minecraft.client.renderer.block.ModelBlockRenderer#tesselateBlock} instead of NeoForge ModelData.
 */
public final class MirrorPaneWorldRenderer {
	private MirrorPaneWorldRenderer() {
	}

	public static void render(WorldRenderContext context) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.level == null) {
			return;
		}
		MultiBufferSource consumers = context.consumers();
		com.mojang.blaze3d.vertex.PoseStack ms = context.matrixStack();
		if (consumers == null || ms == null) {
			return;
		}

		Vec3 cam = context.camera().getPosition();
		float renderTime = mc.level.getGameTime() + mc.getTimer().getGameTimeDeltaPartialTick(false);
		double speed = 1 / 16d;
		float yShift = Mth.sin((float) (renderTime * speed)) / 5f;

		var modelRenderer = mc.getBlockRenderer().getModelRenderer();
		RandomSource random = RandomSource.create();
		var buffer = consumers.getBuffer(RenderType.solid());

		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (!stack.is(ModItems.SYMMETRY_WAND) || !SymmetryWandItem.isEnabled(stack)) {
				continue;
			}
			SymmetryMirror mirror = SymmetryWandItem.getMirror(stack);
			if (mirror instanceof EmptyMirror) {
				continue;
			}
			var model = MirrorPaneRenderHelper.previewModel(mc, mirror);
			if (model == null) {
				continue;
			}
			BlockPos pos = BlockPos.containing(mirror.getPosition());
			ms.pushPose();
			ms.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
			ms.translate(0.0f, yShift + 0.2f, 0.0f);
			MirrorPaneRenderHelper.applyModelTransform(mirror, ms);
			modelRenderer.tesselateBlock(
				mc.level,
				model,
				Blocks.AIR.defaultBlockState(),
				pos,
				ms,
				buffer,
				true,
				random,
				Mth.getSeed(pos),
				OverlayTexture.NO_OVERLAY
			);
			ms.popPose();
		}
	}
}
