package dev.xkmc.modulargolems.init.data;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import dev.xkmc.modulargolems.content.block.TableBlock;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.render.GolemTransformType;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.render.IsInTag;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MGModelGen {

	private static final TextureSlot MID = TextureSlot.create("middle");

	public static void genTable(DataGenContext<Block, TableBlock> ctx, RegistrateBlockModelGenerator pvd) {
		pvd.generate(ctx.get(), TexturedModel.createDefault(block -> new TextureMapping()
						.put(TextureSlot.TOP, new Material(pvd.modLoc("block/table_top")))
						.put(MID, new Material(pvd.modLoc("block/table_middle")))
						.put(TextureSlot.BOTTOM, new Material(pvd.modLoc("block/table_bottom")))
						.put(TextureSlot.PARTICLE, new Material(pvd.modLoc("block/table_particle"))),
				ModelTemplates.create(pvd.modLoc("table").toString(),
						TextureSlot.TOP, MID, TextureSlot.BOTTOM, TextureSlot.PARTICLE
				)));
	}

	public static void genUpgrade(DataGenContext<Item, SimpleUpgradeItem> ctx, RegistrateItemModelGenerator pvd, String modid, String id) {
		pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.conditional(new IsInTag(MGTagGen.BLUE_UPGRADES),
				ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
						ModelLocationUtils.getModelLocation(ctx.get(), "_blue"),
						TextureMapping.layered(
								new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
								new Material(pvd.modLoc("item/blue_arrow"))),
						pvd.modelOutput)),
				ItemModelUtils.conditional(new IsInTag(MGTagGen.POTION_UPGRADES),
						ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
								ModelLocationUtils.getModelLocation(ctx.get(), "_purple"),
								TextureMapping.layered(
										new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
										new Material(pvd.modLoc("item/purple_arrow"))),
								pvd.modelOutput)),
						ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(
										new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id))),
								pvd.modelOutput))
				)));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void genPartItem(DataGenContext<Item, GolemPart<T, P>> ctx, RegistrateItemModelGenerator pvd,
	                 Transformer<P> trans, SpecialModelRenderer.Unbaked<?> model) {
		pvd.itemModelOutput.accept(ctx.get(), build(pvd, trans, ModelLocationUtils.getModelLocation(ctx.get()), model, ctx.get().getPart()));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void genHolderItem(DataGenContext<Item, GolemHolder<T, P>> ctx, RegistrateItemModelGenerator pvd,
	                   Transformer<P> trans, SpecialModelRenderer.Unbaked<?> model) {
		pvd.itemModelOutput.accept(ctx.get(), build(pvd, trans, ModelLocationUtils.getModelLocation(ctx.get()), model, null));
	}

	public static void genFacadeItem(DataGenContext<Item, GolemFacade> ctx, RegistrateItemModelGenerator pvd, SpecialModelRenderer.Unbaked<?> model) {
		var id = ModelLocationUtils.getModelLocation(ctx.get());
		pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.specialModel(id, model));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemModel.Unbaked build(RegistrateItemModelGenerator pvd, Transformer<P> trans, Identifier id,
	                        SpecialModelRenderer.Unbaked<?> model, @Nullable P part) {
		List<SelectItemModel.SwitchCase<ItemDisplayContext>> list = new ArrayList<>();
		ItemModel.Unbaked other = null;
		for (var e : GolemTransformType.values()) {
			var pose = new PoseStack();
			trans.transform(pose, e, part);
			var sid = id.withSuffix("_" + e.name().toLowerCase(Locale.ROOT));
			var ans = ItemModelUtils.specialModel(sid, new Transformation(pose.last().pose()), model);
			if (e == GolemTransformType.OTHER) other = ans;
			else list.add(new SelectItemModel.SwitchCase<>(e.ctx, ans));
		}
		assert other != null;
		return ItemModelUtils.select(new DisplayContext(), other, list);
	}

	public interface Transformer<P extends IGolemPart<P>> {

		void transform(PoseStack pose, GolemTransformType type, @Nullable P part);

	}


}
