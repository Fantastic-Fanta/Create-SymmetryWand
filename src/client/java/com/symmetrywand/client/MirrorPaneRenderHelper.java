package com.symmetrywand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.symmetrywand.mirror.CrossPlaneMirror;
import com.symmetrywand.mirror.PlaneMirror;
import com.symmetrywand.mirror.SymmetryMirror;
import com.symmetrywand.mirror.TriplePlaneMirror;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;

/**
 * Maps mirror types to Create's baked symmetry-effect models and applies the same Y-rotations as Create's mirror classes.
 */
public final class MirrorPaneRenderHelper {
	private MirrorPaneRenderHelper() {
	}

	public static BakedModel previewModel(Minecraft mc, SymmetryMirror mirror) {
		var models = (FabricBakedModelManager) mc.getModelManager();
		if (mirror instanceof TriplePlaneMirror) {
			return models.getModel(SymmetryMirrorModelLoading.TRIPLEPLANE);
		}
		if (mirror instanceof CrossPlaneMirror) {
			return models.getModel(SymmetryMirrorModelLoading.CROSSPLANE);
		}
		if (mirror instanceof PlaneMirror) {
			return models.getModel(SymmetryMirrorModelLoading.PLANE);
		}
		return null;
	}

	public static void applyModelTransform(SymmetryMirror mirror, PoseStack ms) {
		ms.translate(0.5f, 0.5f, 0.5f);
		if (mirror instanceof PlaneMirror pm) {
			float yDeg = ((PlaneMirror.Align) pm.getOrientation()) == PlaneMirror.Align.XY ? 0f : 90f;
			ms.mulPose(Axis.YP.rotationDegrees(yDeg));
		} else if (mirror instanceof CrossPlaneMirror cpm) {
			float yDeg = ((CrossPlaneMirror.Align) cpm.getOrientation()) == CrossPlaneMirror.Align.Y ? 0f : 45f;
			ms.mulPose(Axis.YP.rotationDegrees(yDeg));
		}
		ms.translate(-0.5f, -0.5f, -0.5f);
	}
}
