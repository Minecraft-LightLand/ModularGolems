package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.base.advancements.AdvancementGenerator;
import dev.xkmc.l2library.base.advancements.CriterionBuilder;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.advancement.*;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.FrameType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class AdvGen {

	public static void genAdvancements(RegistrateAdvancementProvider pvd) {
		AdvancementGenerator gen = new AdvancementGenerator(pvd, ModularGolems.MODID);
		var root = gen.new TabBuilder("golems")
				.root("root", GolemItems.GOLEM_TEMPLATE.get(), CriterionBuilder.item(Items.CLAY_BALL),
						"Welcome to Modular Golems", "Craft your army!");
		var arm = root
				.create("start", GolemItems.GOLEM_BODY.get(), CriterionBuilder.item(TagGen.GOLEM_PARTS),
						"The Beginning of Everything", "Craft a Golem Template and use Stone Cutter to cut it into a golem part")
				.create("apply", GolemPart.setMaterial(GolemItems.GOLEM_BODY.asStack(),
								new ResourceLocation(ModularGolems.MODID, "iron")),
						CriterionBuilder.one(PartCraftTrigger.ins()),
						"Heavy Metal Piece", "Apply metal ingots onto golem parts in an anvil.");
		arm.create("apply_sculk", GolemPart.setMaterial(GolemItems.GOLEM_BODY.asStack(),
						new ResourceLocation(ModularGolems.MODID, "sculk")),
				CriterionBuilder.one(PartCraftTrigger.withMat(new ResourceLocation(ModularGolems.MODID, "sculk"))),
				"Bad Memories", "Make a sculk golem part.").type(FrameType.CHALLENGE);
		arm.create("dog", GolemItems.HOLDER_DOG.get().withUniformMaterial(
						new ResourceLocation(ModularGolems.MODID, "iron")),
				CriterionBuilder.item(GolemItems.HOLDER_DOG.get()),
				"Royal Cold Doggy", "Craft a Dog Golem.");
		arm.create("humanoid", GolemItems.HOLDER_HUMANOID.get().withUniformMaterial(
								new ResourceLocation(ModularGolems.MODID, "iron")),
						CriterionBuilder.item(GolemItems.HOLDER_HUMANOID.get()),
						"You but Tougher", "Craft a Humaniod Golem.")
				.create("fully_equipped", GolemHolder.toEntityIcon(
								GolemItems.HOLDER_HUMANOID.get().withUniformMaterial(
										new ResourceLocation(ModularGolems.MODID, "iron")),
								Items.DIAMOND_HELMET.getDefaultInstance(),
								Items.DIAMOND_CHESTPLATE.getDefaultInstance(),
								Items.DIAMOND_LEGGINGS.getDefaultInstance(),
								Items.DIAMOND_BOOTS.getDefaultInstance(),
								Items.DIAMOND_SWORD.getDefaultInstance(),
								Items.SHIELD.getDefaultInstance()
						),
						CriterionBuilder.one(GolemEquipTrigger.ins(6)),
						"Fully Equipped", "Give Humaniod Golem full armor set, a sword, and a shield.").type(FrameType.GOAL)
				.create("oops", GolemItems.HOLDER_HUMANOID.get().withUniformMaterial(
								new ResourceLocation(ModularGolems.MODID, "gold")),
						CriterionBuilder.one(GolemBreakToolTrigger.ins()),
						"Oops...", "Let Humanoid Golem break a piece of equipment").type(FrameType.CHALLENGE);

		var golem = arm.create("craft", GolemItems.HOLDER_GOLEM.get().withUniformMaterial(
						new ResourceLocation(ModularGolems.MODID, "iron")),
				CriterionBuilder.item(TagGen.GOLEM_HOLDERS),
				"A Brand New Golem", "Craft a Golem Holder with metal golem parts.");
		golem.create("anvil_fix", Items.ANVIL,
						CriterionBuilder.one(GolemAnvilFixTrigger.ins()),
						"Healing the Wounds", "Repair a metal golem with ingots in an anvil")
				.create("hot_fix", Items.IRON_INGOT,
						CriterionBuilder.one(GolemHotFixTrigger.ins()),
						"Repair in Battle", "Repair a metal golem with ingots directly")
				.create("kill_warden", Items.SCULK_CATALYST,
						CriterionBuilder.one(GolemKillTrigger.byType(EntityType.WARDEN)),
						"Ship of Theseus", "Let a golem kill a Warden").type(FrameType.CHALLENGE);
		golem.create("retrieve", GolemItems.RETRIEVAL_WAND.get(),
				CriterionBuilder.item(GolemItems.RETRIEVAL_WAND.get()),
				"Everyone Comes Back", "Craft a retrieval wand");
		golem.create("command", GolemItems.COMMAND_WAND.get(),
				CriterionBuilder.item(GolemItems.COMMAND_WAND.get()),
				"Guard Your Home", "Craft a command wand");
		var upgrade = golem.create("upgrade", GolemItems.EMPTY_UPGRADE.get(),
				CriterionBuilder.item(TagGen.GOLEM_UPGRADES),
				"Upgrade Your Golem", "Obtain an upgrade");
		upgrade.create("full", GolemItems.EMPTY_UPGRADE.get(),
				CriterionBuilder.one(UpgradeApplyTrigger.withRemain(0)),
				"No More Room", "Apply the maximum number of upgrades on a golem").type(FrameType.TASK);
		upgrade.create("recycle", GolemItems.RECYCLE.get(),
				CriterionBuilder.one(UpgradeApplyTrigger.withUpgrade(GolemItems.RECYCLE.get())),
				"Immortal Golem", "Apply a recycle upgrade on a golem").type(FrameType.CHALLENGE);
		upgrade.create("sponge", GolemItems.SPONGE.get(),
						CriterionBuilder.one(UpgradeApplyTrigger.withUpgrade(GolemItems.SPONGE.get())),
						"Waterlogged Golem", "Apply a sponge upgrade on a golem").type(FrameType.GOAL)
				.create("kill_creeper", Items.TNT,
						CriterionBuilder.one(GolemKillTrigger.byType(EntityType.CREEPER)),
						"Anti-Terrorism Operation", "Let a golem kill a Creeper").type(FrameType.CHALLENGE);
		upgrade.create("swim", GolemItems.SWIM.get(),
						CriterionBuilder.one(UpgradeApplyTrigger.withUpgrade(GolemItems.SWIM.get())),
						"Undersea Warrior", "Apply a swim upgrade on a golem").type(FrameType.GOAL)
				.create("kill_guardian", Items.PRISMARINE_SHARD,
						CriterionBuilder.one(GolemKillTrigger.byType(EntityType.GUARDIAN)),
						"Legend of the Atlantis", "Let a golem kill a Guardian").type(FrameType.CHALLENGE);
		root.finish();
	}

}
