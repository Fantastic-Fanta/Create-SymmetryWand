package com.symmetrywand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the base wand model plus Create's extra element layers (bits, core, glow), using vanilla block/item textures.
 */
public class SymmetryWandItemRenderer implements BuiltinItemRenderer {
	private static final ResourceLocation BITS = ResourceLocation.fromNamespaceAndPath("symmetrywand", "item/wand_of_symmetry/bits");
	private static final ResourceLocation CORE = ResourceLocation.fromNamespaceAndPath("symmetrywand", "item/wand_of_symmetry/core");
	private static final ResourceLocation CORE_GLOW = ResourceLocation.fromNamespaceAndPath("symmetrywand", "item/wand_of_symmetry/core_glow");

	private static ModelResourceLocation modelLoc(ResourceLocation id) {
		return new ModelResourceLocation(id, "inventory");
	}

	@Override
	public void render(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		var itemRenderer = mc.getItemRenderer();
		BakedModel original = itemRenderer.getModel(stack, mc.level, null, 0);
		itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, buffer, light, overlay, original);

		int maxLight = 0xF000F0;
		BakedModel coreModel = mc.getModelManager().getModel(modelLoc(CORE));
		BakedModel glowModel = mc.getModelManager().getModel(modelLoc(CORE_GLOW));
		BakedModel bitsModel = mc.getModelManager().getModel(modelLoc(BITS));

		itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, buffer, maxLight, overlay, coreModel);
		itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, buffer, maxLight, overlay, glowModel);

		float worldTime = (mc.level == null ? 0f : mc.level.getGameTime()) / 20f;
		float floating = Mth.sin(worldTime) * .05f;
		float angle = worldTime * -10 % 360;
		poseStack.pushPose();
		poseStack.translate(0, floating, 0);
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
		itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, buffer, maxLight, overlay, bitsModel);
		poseStack.popPose();
	}
}
