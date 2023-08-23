package dev.xkmc.modulargolems.compat.materials.blazegear;

import com.google.common.collect.ImmutableList;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class DuplicatedBlazeArmsModel<T extends AbstractGolemEntity<?, ?>> extends ListModel<T> {
	private final ModelPart[] blazeSticks = new ModelPart[12];
	private final ImmutableList<ModelPart> parts;
	private float r3 = 7.0F;

	public DuplicatedBlazeArmsModel(ModelPart modelPart) {
		Arrays.setAll(this.blazeSticks, (num) -> modelPart.getChild(getPartName(num)));
		ImmutableList.Builder<ModelPart> builder = ImmutableList.builder();
		builder.addAll(Arrays.asList(this.blazeSticks));
		this.parts = builder.build();
	}

	private static String getPartName(int num) {
		return "part" + num;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		float f = 0.0F;
		CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);

		int k;
		float f5;
		float f7;
		float f9;
		for (k = 0; k < 4; ++k) {
			f5 = Mth.cos(f) * 9.0F;
			f7 = -2.0F + Mth.cos((float) (k * 2) * 0.25F);
			f9 = Mth.sin(f) * 9.0F;
			partdefinition.addOrReplaceChild(getPartName(k), cubelistbuilder, PartPose.offset(f5, f7, f9));
			++f;
		}

		f = 0.7853982F;

		for (k = 4; k < 8; ++k) {
			f5 = Mth.cos(f) * 7.0F;
			f7 = 2.0F + Mth.cos((float) (k * 2) * 0.25F);
			f9 = Mth.sin(f) * 7.0F;
			partdefinition.addOrReplaceChild(getPartName(k), cubelistbuilder, PartPose.offset(f5, f7, f9));
			++f;
		}

		f = 0.47123894F;

		for (k = 8; k < 12; ++k) {
			f5 = Mth.cos(f) * 5.0F;
			f7 = 11.0F + Mth.cos((float) k * 1.5F * 0.5F);
			f9 = Mth.sin(f) * 5.0F;
			partdefinition.addOrReplaceChild(getPartName(k), cubelistbuilder, PartPose.offset(f5, f7, f9));
			++f;
		}

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public Iterable<ModelPart> parts() {
		return this.parts;
	}

	public void setupAnim(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		int rod = livingEntity.getModifiers().getOrDefault(BGCompatRegistry.BLAZING.get(), 0);
		float ySpeed = 1.0F;
		if (livingEntity.level().isRainingAt(livingEntity.blockPosition())) {
			ySpeed += 1.5F * livingEntity.level().getRainLevel(1.0F);
		}

		float y0 = livingEntity.getBbHeight() / 1.8f;
		float dy = -(1.8f - livingEntity.getBbHeight()) * 8;

		float f;
		float y1 = -5.0F;
		float y2 = 0.0F;
		float y3 = 13.1F;
		float r1 = 14.0F;
		float r2 = 12.0F;
		if (livingEntity.isCrouching()) {
			y1 += 3.0F;
			y2 += 4.0F;
			y3 += 0.1F;
			if (this.r3 <= 9.0F) {
				this.r3 += 0.1F;
			}
		} else if (this.r3 >= 7.0F) {
			this.r3 -= 0.1F;
		}

		int k;


		f = 0.47123894F + ageInTicks * Mth.PI * -0.05F;
		for (k = 8; k < 12; ++k) {
			this.blazeSticks[k].visible = rod > 0;
			this.blazeSticks[k].y = y3 + Mth.cos(((float) k * 1.5F + ageInTicks) * 0.5F) * ySpeed;
			this.blazeSticks[k].x = Mth.cos(f) * this.r3 - 1.0F;
			this.blazeSticks[k].z = Mth.sin(f) * this.r3;
			this.blazeSticks[k].y *= y0;
			this.blazeSticks[k].y -= dy;
			++f;
		}
		rod--;
		f = ageInTicks * Mth.PI * -0.1F;
		for (k = 0; k < 4; ++k) {
			this.blazeSticks[k].visible = rod > 0;
			this.blazeSticks[k].y = y1 + Mth.cos(((float) (k * 2) + ageInTicks) * 0.25F) * ySpeed;
			this.blazeSticks[k].x = Mth.cos(f) * r1 - 1.0F;
			this.blazeSticks[k].z = Mth.sin(f) * r1;
			this.blazeSticks[k].y *= y0;
			this.blazeSticks[k].y -= dy;
			++f;
		}
		rod--;
		f = 0.7853982F + ageInTicks * Mth.PI * 0.03F;
		for (k = 4; k < 8; ++k) {
			this.blazeSticks[k].visible = rod > 0;
			this.blazeSticks[k].y = y2 + Mth.cos(((float) (k * 2) + ageInTicks) * 0.25F) * ySpeed;
			this.blazeSticks[k].x = Mth.cos(f) * r2 - 1.0F;
			this.blazeSticks[k].z = Mth.sin(f) * r2;
			this.blazeSticks[k].y *= y0;
			this.blazeSticks[k].y -= dy;
			++f;
		}


	}
}