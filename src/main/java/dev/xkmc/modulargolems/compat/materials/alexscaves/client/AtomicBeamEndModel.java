package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

/**
 * Alex's Caves' Tremorzilla beam impact model, drawn with the animated
 * {@code tremorzilla_beam_end_0/1/2} textures at the beam endpoint.
 */
public class AtomicBeamEndModel extends AdvancedEntityModel<Entity> {

	private static final int TEXTURE_SIZE = 128;

	private final AdvancedModelBox root;
	private final AdvancedModelBox base;
	private final AdvancedModelBox flame;
	private final AdvancedModelBox cubeR1;
	private final AdvancedModelBox cubeR2;
	private final AdvancedModelBox flame2;
	private final AdvancedModelBox cubeR3;
	private final AdvancedModelBox cubeR4;
	private final AdvancedModelBox flame3;
	private final AdvancedModelBox cubeR5;
	private final AdvancedModelBox cubeR6;
	private final AdvancedModelBox flame4;
	private final AdvancedModelBox cubeR7;
	private final AdvancedModelBox cubeR8;

	public AtomicBeamEndModel() {
		texWidth = TEXTURE_SIZE;
		texHeight = TEXTURE_SIZE;
		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);
		base = new AdvancedModelBox(this);
		base.setRotationPoint(0.0F, 0.0F, 0.0F);
		root.addChild(base);
		base.setTextureOffset(0, 0).addBox(-11.0F, 0.0F, -11.0F, 22.0F, 0.0F, 22.0F, 0.0F, false);
		flame = new AdvancedModelBox(this);
		flame.setRotationPoint(0.0F, 0.0F, -11.0F);
		base.addChild(flame);
		cubeR1 = new AdvancedModelBox(this);
		cubeR1.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame.addChild(cubeR1);
		setRotateAngle(cubeR1, 0.3927F, 0.0F, 0.0F);
		cubeR1.setTextureOffset(3, 38).addBox(-12.0F, -24.0F, 0.0F, 24.0F, 24.0F, 0.0F, 0.0F, false);
		cubeR2 = new AdvancedModelBox(this);
		cubeR2.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame.addChild(cubeR2);
		setRotateAngle(cubeR2, 0.7854F, 0.0F, 0.0F);
		cubeR2.setTextureOffset(0, 22).addBox(-15.0F, -16.0F, 0.0F, 30.0F, 16.0F, 0.0F, 0.0F, false);
		flame2 = new AdvancedModelBox(this);
		flame2.setRotationPoint(0.0F, 0.0F, 11.0F);
		base.addChild(flame2);
		cubeR3 = new AdvancedModelBox(this);
		cubeR3.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame2.addChild(cubeR3);
		setRotateAngle(cubeR3, -0.3927F, 0.0F, 0.0F);
		cubeR3.setTextureOffset(3, 38).addBox(-12.0F, -24.0F, 0.0F, 24.0F, 24.0F, 0.0F, 0.0F, true);
		cubeR4 = new AdvancedModelBox(this);
		cubeR4.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame2.addChild(cubeR4);
		setRotateAngle(cubeR4, -0.7854F, 0.0F, 0.0F);
		cubeR4.setTextureOffset(0, 22).addBox(-15.0F, -16.0F, 0.0F, 30.0F, 16.0F, 0.0F, 0.0F, true);
		flame3 = new AdvancedModelBox(this);
		flame3.setRotationPoint(11.0F, 0.0F, 0.0F);
		base.addChild(flame3);
		cubeR5 = new AdvancedModelBox(this);
		cubeR5.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame3.addChild(cubeR5);
		setRotateAngle(cubeR5, 0.0F, 0.0F, 0.3927F);
		cubeR5.setTextureOffset(3, 14).addBox(0.0F, -24.0F, -13.0F, 0.0F, 24.0F, 24.0F, 0.0F, true);
		cubeR6 = new AdvancedModelBox(this);
		cubeR6.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame3.addChild(cubeR6);
		setRotateAngle(cubeR6, 0.0F, 0.0F, 0.7854F);
		cubeR6.setTextureOffset(0, -8).addBox(0.0F, -16.0F, -15.0F, 0.0F, 16.0F, 30.0F, 0.0F, true);
		flame4 = new AdvancedModelBox(this);
		flame4.setRotationPoint(-11.0F, 0.0F, 0.0F);
		base.addChild(flame4);
		cubeR7 = new AdvancedModelBox(this);
		cubeR7.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame4.addChild(cubeR7);
		setRotateAngle(cubeR7, 0.0F, 0.0F, -0.3927F);
		cubeR7.setTextureOffset(3, 14).addBox(0.0F, -24.0F, -12.0F, 0.0F, 24.0F, 24.0F, 0.0F, false);
		cubeR8 = new AdvancedModelBox(this);
		cubeR8.setRotationPoint(0.0F, 0.0F, 0.0F);
		flame4.addChild(cubeR8);
		setRotateAngle(cubeR8, 0.0F, 0.0F, -0.7854F);
		cubeR8.setTextureOffset(0, -8).addBox(0.0F, -16.0F, -15.0F, 0.0F, 16.0F, 30.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, cubeR1, cubeR2, cubeR3, cubeR4, cubeR5, cubeR6, cubeR7, cubeR8, base, flame, flame2, flame3, flame4);
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
	}

}
