package com.symmetrywand.client;

import com.symmetrywand.item.SymmetryWandItem;
import com.symmetrywand.mirror.CrossPlaneMirror;
import com.symmetrywand.mirror.EmptyMirror;
import com.symmetrywand.mirror.PlaneMirror;
import com.symmetrywand.mirror.SymmetryMirror;
import com.symmetrywand.mirror.TriplePlaneMirror;
import com.symmetrywand.network.ConfigureWandPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class SymmetryWandScreen extends Screen {
	private final ItemStack wand;
	private final InteractionHand hand;
	private SymmetryMirror currentElement;
	private Button typeButton;
	private Button alignButton;

	public SymmetryWandScreen(ItemStack wand, InteractionHand hand) {
		super(Component.translatable("symmetrywand.screen.title"));
		this.wand = wand;
		this.hand = hand;
		this.currentElement = SymmetryWandItem.getMirror(wand);
		if (this.currentElement instanceof EmptyMirror) {
			this.currentElement = new PlaneMirror(net.minecraft.world.phys.Vec3.ZERO);
		}
	}

	@Override
	protected void init() {
		super.init();
		int cx = this.width / 2;
		int y = this.height / 2 - 30;
		this.typeButton = Button.builder(Component.empty(), b -> cycleType())
			.bounds(cx - 100, y, 200, 20)
			.build();
		this.alignButton = Button.builder(Component.empty(), b -> cycleAlign())
			.bounds(cx - 100, y + 28, 200, 20)
			.build();
		addRenderableWidget(typeButton);
		addRenderableWidget(alignButton);
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(cx - 50, y + 70, 100, 20)
			.build());
		refreshLabels();
	}

	private void cycleType() {
		if (currentElement instanceof PlaneMirror pm) {
			currentElement = new CrossPlaneMirror(pm.getPosition());
			currentElement.setOrientation(0);
		} else if (currentElement instanceof CrossPlaneMirror cpm) {
			currentElement = new TriplePlaneMirror(cpm.getPosition());
		} else if (currentElement instanceof TriplePlaneMirror tpm) {
			currentElement = new PlaneMirror(tpm.getPosition());
			currentElement.setOrientation(0);
		} else {
			currentElement = new PlaneMirror(currentElement.getPosition());
		}
		refreshLabels();
	}

	private void cycleAlign() {
		currentElement.rotate(true);
		refreshLabels();
	}

	private void refreshLabels() {
		int typeIdx = currentElement instanceof TriplePlaneMirror ? 2 : currentElement instanceof CrossPlaneMirror ? 1 : 0;
		Component typeLabel = SymmetryMirror.mirrorTypeLabels().get(typeIdx);
		typeButton.setMessage(Component.translatable("symmetrywand.screen.mirror_type", typeLabel));
		var tips = currentElement.alignToolTips();
		Component alignLabel = tips.isEmpty() ? Component.empty() : tips.get(currentElement.getOrientationIndex() % Math.max(1, tips.size()));
		alignButton.setMessage(Component.translatable("symmetrywand.screen.orientation", alignLabel));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFF);
	}

	@Override
	public void removed() {
		SymmetryWandItem.configureSettings(wand, currentElement);
		ClientPlayNetworking.send(new ConfigureWandPayload(hand, currentElement));
	}
}
