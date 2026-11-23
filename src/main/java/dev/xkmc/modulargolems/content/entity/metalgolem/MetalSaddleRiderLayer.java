package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 肩鞍乘客跟着大型傀儡一起渲染，从而继承它的走路左右摆动。
 * <p>
 * 关键：本层的 {@code pose} 已经过 {@code MetalGolemRenderer} 的完整变换链：
 * {@code setupRotations}（Y(180 - bodyRot) + 抬升 bbHeight/2 + Z(swayRoll)）
 * → {@code scale(getScale())} → {@code translate(0, -1.501, 0)}。
 * <p>
 * 因此这里必须把 {@code positionRider} 算出的世界偏移反推回模型空间，不能再凭符号猜：
 * 世界偏移 = Y(-bodyRot) · (r·tx, -r·(ty - 1.501), -r·tz)，
 * 要它等于 Y(-bodyRot) · (seatX, seatY, seatZ)，就得到
 * <pre>
 *   tx =  seatX / r
 *   ty =  1.501 - seatY / r
 *   tz = -seatZ / r
 * </pre>
 * 少除以 r 会随体型放大，ty 写反就会出现「数值越小反而越高」。
 */
@OnlyIn(Dist.CLIENT)
public class MetalSaddleRiderLayer extends RenderLayer<MetalGolemEntity, MetalGolemModel> {

	public static final Set<UUID> BLOCKED = new HashSet<>();

	/** {@code GolemDefaultLayer} 在模型空间里做的固定下移量。 */
	private static final double MODEL_TRANSLATE_Y = 1.501D;

	public MetalSaddleRiderLayer(MetalGolemRenderer parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int light, MetalGolemEntity golem,
					   float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (golem.getPassengers().isEmpty() || !golem.hasShoulderSaddle()) return;
		float scale = golem.getScale();
		if (scale <= 0) return;
		for (Entity passenger : golem.getPassengers()) {
			if (passenger == Minecraft.getInstance().cameraEntity && Minecraft.getInstance().options.getCameraType().isFirstPerson())
				continue;
			BLOCKED.remove(passenger.getUUID());
			int index = golem.getPassengers().indexOf(passenger);
			if (index < 0) continue;
			var seat = golem.getSeatOffset(index);
			double seatY = passenger.getMyRidingOffset() + golem.getPassengersRidingOffset();
			pose.pushPose();
			pose.translate(seat.x() / scale, (float) (MODEL_TRANSLATE_Y - seatY / scale), -seat.z() / scale);
			// 抵消父级旋转，让乘客回到世界坐标系（否则会被父级的 scale(-1,-1,1) 上下翻转）。
			// 父级净旋转 R = Y(180 - bodyRot) * Z(sway) * Z(180)。
			// 这里补 Z(180) → Y(180 + bodyRot)：
			//   净旋转 = Y(180-bodyRot) * Z(sway) * Z(180) * Z(180) * Y(180+bodyRot)
			//          = Y(180-bodyRot) * Z(sway) * Y(180-bodyRot)^-1
			// 正好是"绕大型傀儡朝向轴的摇摆"，与傀儡身体的左右摆动完全一致、且不随视角变化。
			// （之前多补了一个 Z(-sway)，因为 Y(180+bodyRot) 已经是 Y(180-bodyRot) 的逆，
			//   结果变成 Roll*Z(-sway) 的残留项，随 bodyRot 变化，才表现为不同视角摇晃幅度不同。）
			pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
			pose.mulPose(Axis.YP.rotationDegrees(180.0F + golem.yBodyRot));
			EntityRenderer<? super Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(passenger);
			if (renderer != null) renderer.render(passenger, 0.0F, partialTick, pose, buffer, light);
			pose.popPose();
			BLOCKED.add(passenger.getUUID());
		}
	}

}
