package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 骑乘者在犬形激流旋身时跟随一起旋转。
 * <p>
 * 原版把乘客作为独立实体渲染，拿不到坐骑 setupRotations 的 poseStack，所以旋身时乘客不会跟着转。
 * 这里改成：渲染完乘客后把它的 UUID 记入 {@link #BLOCKED}，{@link DogRiderRenderBlocker} 在 RenderLivingEvent.Pre
 * 里取消它自己的渲染；于是乘客只在犬形已旋转的 poseStack 内被画一次，自然跟着翻滚。
 */
@OnlyIn(Dist.CLIENT)
public class DogRiderLayer extends RenderLayer<DogGolemEntity, DogGolemModel> {

	public static final Set<UUID> BLOCKED = new HashSet<>();

	/**
	 * 座位高度微调（格）。数值越大坐得越高；浮空调小、陷进身体调大。
	 */
	private static final float SEAT_HEIGHT_TRIM = 1.5F;

	public DogRiderLayer(DogGolemRenderer parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int light, DogGolemEntity dog,
		float legAngle, float legSwing, float bob, float ageInTicks, float yRot, float xRot) {
		if (dog.getPassengers().isEmpty() || !dog.isAutoSpinAttack()) return;
		for (Entity passenger : dog.getPassengers()) {
			if (passenger == Minecraft.getInstance().cameraEntity && Minecraft.getInstance().options.getCameraType().isFirstPerson())
				continue;
			BLOCKED.remove(passenger.getUUID());
			// 复刻 DogGolemEntity.positionRider 的鞍座偏移：高度 + 沿身体前后的座位位置
			int index = dog.getPassengers().indexOf(passenger);
			int total = dog.getPassengers().size();
			float width = dog.getBbWidth();
			float offset = index == 0 ? 0.7f : index + (dog.getControllingPassenger() instanceof Player ? 1.7f : 1.2f);
			double seatZ = width / 2 - width / total * offset;
			double seatY = passenger.getMyRidingOffset() + dog.getPassengersRidingOffset();
			pose.pushPose();
			// 刚性挂在背上：原点在身体中心（setupRotations 抬了 bbHeight*0.5），座位高度取相对中心的值。
			pose.translate(0.0F, (float) (seatY - dog.getBbHeight() * 0.5F) + SEAT_HEIGHT_TRIM, (float) -seatZ);
			// 把座位从身体下方翻到背上。
			pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
			pose.mulPose(Axis.YP.rotationDegrees(180.0F));
			// 内层 renderer 在乘客骑乘时用的是「坐骑」的 yBodyRot（不是玩家的），这里叠加坐骑 yaw 抵消，
			// 于是玩家朝向只由坐骑决定、始终朝前，不随视角旋转（取值已实测校准）。
			pose.mulPose(Axis.YP.rotationDegrees(dog.yBodyRot));
			EntityRenderer<? super Entity> r = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(passenger);
			if (r != null) r.render(passenger, 0.0F, bob, pose, buffer, light);
			pose.popPose();
			BLOCKED.add(passenger.getUUID());
		}
	}

}
