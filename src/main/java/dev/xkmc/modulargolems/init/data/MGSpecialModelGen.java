package dev.xkmc.modulargolems.init.data;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.entity.render.GolemTransformType;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.render.GolemFacadeRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemHolderRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemPartRenderer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MGSpecialModelGen {

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void genPartItem(DataGenContext<Item, GolemPart<T, P>> ctx, RegistrateItemModelGenerator pvd,
	                 Transformer<P> trans, Identifier golemType) {
		pvd.itemModelOutput.accept(ctx.get(), build(trans, new GolemPartRenderer.Unbaked(golemType), ctx.get().getPart()));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void genHolderItem(DataGenContext<Item, GolemHolder<T, P>> ctx, RegistrateItemModelGenerator pvd,
	                   Transformer<P> trans, Identifier golemType) {
		pvd.itemModelOutput.accept(ctx.get(), build(trans, new GolemHolderRenderer.Unbaked(golemType), null));
	}

	public static void genFacadeItem(DataGenContext<Item, GolemFacade> ctx, RegistrateItemModelGenerator pvd) {
		pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.specialModel(ModularGolems.loc("block/facade"), new GolemFacadeRenderer.Unbaked()));
	}

	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemModel.Unbaked build(Transformer<P> trans, SpecialModelRenderer.Unbaked<?> model, @Nullable P part) {
		List<SelectItemModel.SwitchCase<ItemDisplayContext>> list = new ArrayList<>();
		ItemModel.Unbaked other = null;
		for (var e : GolemTransformType.values()) {
			var pose = new PoseStack();
			trans.transform(pose, e, part);
			var sid = ModularGolems.loc("block/clay");
			var ans = ItemModelUtils.specialModel(sid, new Transformation(pose.last().pose()), model);
			if (e == GolemTransformType.OTHER) other = ans;
			else list.add(new SelectItemModel.SwitchCase<>(e.ctx, ans));
		}
		assert other != null;
		return ItemModelUtils.select(new DisplayContext(), other, list);
	}

	public static void transformMetalGolem(PoseStack stack, GolemTransformType transform, @Nullable MetalGolemPartType part) {
		switch (transform) {
			case FIRST:
				break;
			case THIRD: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case ENTITY: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case DEF: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 0.45f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.15, 0);
				return;
			}
			case OTHER:
				stack.translate(0, 0, 0.5);
				break;
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.375f;
			stack.scale(size, size, size);
			stack.translate(0, -2.2, 0);
		} else if (part == MetalGolemPartType.BODY) {
			float size = 0.525f;
			stack.scale(size, size, size);
			stack.translate(0, -1, 0);
		} else if (part == MetalGolemPartType.LEG) {
			float size = 0.6f;
			stack.scale(size, size, size);
			stack.translate(0, -2.2, 0);
		} else if (part == MetalGolemPartType.LEFT) {
			float size = 0.55f;
			stack.scale(size, size, size);
			stack.translate(-0.7, -1.7, 0);
		}
	}

	public static void transformHumanoid(PoseStack stack, GolemTransformType transform, @Nullable HumanoidGolemPartType part) {
		switch (transform) {
			case FIRST:
				break;
			case THIRD: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case ENTITY: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case DEF: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 0.5f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.5, 0);
				return;
			}
			case OTHER:
				stack.translate(0, 0, 0.5);
				break;
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.45f;
			stack.scale(size, size, size);
			stack.translate(0, -2, 0);
		} else if (part == HumanoidGolemPartType.BODY) {
			float size = 0.65f;
			stack.scale(size, size, size);
			stack.translate(0, -1.2, 0);
		} else if (part == HumanoidGolemPartType.LEGS) {
			float size = 0.8f;
			stack.scale(size, size, size);
			stack.translate(0, -2, 0);
		} else if (part == HumanoidGolemPartType.ARMS) {
			float size = 0.6f;
			stack.scale(size, size, size);
			stack.translate(0, -1.5, 0);
		}
	}

	public static void transformDog(PoseStack stack, GolemTransformType transform, @Nullable DogGolemPartType part) {
		switch (transform) {
			case FIRST:
				break;
			case THIRD: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.5F;
				stack.scale(size, size, size);
				break;
			}
			case ENTITY: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.5F;
				stack.scale(size, size, size);
				break;
			}
			case DEF: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 1f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.5, 0);
				return;
			}
			case OTHER:
				stack.translate(0.1, 0, 0.5);
				float size = 0.75F;
				stack.scale(size, size, size);
				break;
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.8f;
			stack.scale(size, size, size);
			stack.translate(0, -1.9, 0);
		} else if (part == DogGolemPartType.BODY) {
			float size = 0.9f;
			stack.scale(size, size, size);
			stack.translate(0, -1.6, 0);
		} else if (part == DogGolemPartType.LEGS) {
			float size = 1f;
			stack.scale(size, size, size);
			stack.translate(0, -1.9, 0);
		}
	}

	public interface Transformer<P extends IGolemPart<P>> {

		void transform(PoseStack pose, GolemTransformType type, @Nullable P part);

	}
}
