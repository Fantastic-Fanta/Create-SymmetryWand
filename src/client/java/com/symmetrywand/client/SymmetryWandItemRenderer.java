package com.symmetrywand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Renders the base wand plus partial layers like Create's {@code PartialItemModelRenderer}: translucent / “glowing”
 * geometry must use {@link ItemRenderer#getFoilBufferDirect} with {@link Sheets#translucentCullBlockSheet()}, not a
 * second full {@link ItemRenderer#render} pass (that binds the wrong buffer for glass quads, so they disappear).
 */
public final class SymmetryWandItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private static final ResourceLocation BITS = ResourceLocation.fromNamespaceAndPath("symmetrywand", "item/wand_of_symmetry/bits");
	private static final ResourceLocation CORE = ResourceLocation.fromNamespaceAndPath("symmetrywand", "item/wand_of_symmetry/core");
	private static final ResourceLocation CORE_GLOW = ResourceLocation.fromNamespaceAndPath("symmetrywand", "item/wand_of_symmetry/core_glow");

	private static ModelResourceLocation modelLoc(ResourceLocation id) {
		return ModelResourceLocation.inventory(id);
	}

	private static boolean isLeftHand(ItemDisplayContext mode) {
		return mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

	/**
	 * Same transform block as inside {@link ItemRenderer#render} after the outer push (model space centering).
	 */
	private static void applyItemTransform(BakedModel transformSource, ItemDisplayContext mode, boolean leftHand, PoseStack poseStack) {
		ItemTransform transform = transformSource.getTransforms().getTransform(mode);
		transform.apply(leftHand, poseStack);
		poseStack.translate(-0.5f, -0.5f, -0.5f);
	}

	private static void renderPartialModel(
		ItemStack stack,
		PoseStack poseStack,
		MultiBufferSource buffer,
		int light,
		int overlay,
		BakedModel geometry,
		RenderType sheet
	) {
		var consumer = ItemRenderer.getFoilBufferDirect(buffer, sheet, true, stack.hasFoil());
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
			poseStack.last(),
			consumer,
			Blocks.AIR.defaultBlockState(),
			geometry,
			1.0f,
			1.0f,
			1.0f,
			light,
			overlay
		);
	}

	@Override
	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		var itemRenderer = mc.getItemRenderer();
		boolean leftHand = isLeftHand(mode);

		BakedModel baseForTransform = itemRenderer.getModel(stack, mc.level, null, 0);
		itemRenderer.render(stack, mode, leftHand, poseStack, buffer, light, overlay, baseForTransform);

		int maxLight = LightTexture.FULL_BRIGHT;
		BakedModel coreModel = mc.getModelManager().getModel(modelLoc(CORE));
		BakedModel glowModel = mc.getModelManager().getModel(modelLoc(CORE_GLOW));
		BakedModel bitsModel = mc.getModelManager().getModel(modelLoc(BITS));

		poseStack.pushPose();
		applyItemTransform(baseForTransform, mode, leftHand, poseStack);
		renderPartialModel(stack, poseStack, buffer, maxLight, overlay, coreModel, Sheets.solidBlockSheet());
		poseStack.popPose();

		poseStack.pushPose();
		applyItemTransform(baseForTransform, mode, leftHand, poseStack);
		renderPartialModel(stack, poseStack, buffer, maxLight, overlay, glowModel, Sheets.translucentCullBlockSheet());
		poseStack.popPose();

		float worldTime = (mc.level == null ? 0f : mc.level.getGameTime() + mc.getTimer().getGameTimeDeltaPartialTick(false)) / 20f;
		float floating = Mth.sin(worldTime) * 0.05f;
		float angle = worldTime * -10 % 360;
		poseStack.pushPose();
		applyItemTransform(baseForTransform, mode, leftHand, poseStack);
		poseStack.translate(0.0f, floating, 0.0f);
		poseStack.mulPose(Axis.YP.rotationDegrees(angle));
		renderPartialModel(stack, poseStack, buffer, maxLight, overlay, bitsModel, Sheets.translucentCullBlockSheet());
		poseStack.popPose();
	}
}
