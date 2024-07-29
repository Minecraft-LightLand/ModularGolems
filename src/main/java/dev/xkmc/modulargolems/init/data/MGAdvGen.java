package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.l2core.serial.advancements.AdvancementGenerator;
import dev.xkmc.l2core.serial.advancements.CriterionBuilder;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.advancement.*;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class MGAdvGen {

	public static void genAdvancements(RegistrateAdvancementProvider pvd) {
		AdvancementGenerator gen = new AdvancementGenerator(pvd, ModularGolems.MODID);
		var root = gen.new TabBuilder("golems")
				.root("root", GolemItems.HOLDER_GOLEM.get().withUniformMaterial(
								ModularGolems.loc("iron")),
						CriterionBuilder.item(Items.CLAY_BALL),
						"Welcome to Modular Golems", "Craft your army!");
		var arm = root
				.create("start", GolemItems.GOLEM_BODY.get(), CriterionBuilder.item(MGTagGen.GOLEM_PARTS),
						"The Beginning of Everything", "Craft a Golem Template and use Stone Cutter to cut it into a golem part")
				.create("apply", GolemPart.setMaterial(GolemItems.GOLEM_BODY.asStack(),
								ModularGolems.loc("iron")),
						CriterionBuilder.one(PartCraftTrigger.ins().build()),
						"Heavy Metal Piece", "Apply metal ingots onto golem parts in an anvil.");
		arm.create("apply_sculk", GolemPart.setMaterial(GolemItems.GOLEM_BODY.asStack(),
						ModularGolems.loc("sculk")),
				CriterionBuilder.one(PartCraftTrigger.withMat(ModularGolems.loc("sculk")).build()),
				"Bad Memories", "Make a sculk golem part.").type(AdvancementType.CHALLENGE);
		arm.create("dog", GolemItems.HOLDER_DOG.get().withUniformMaterial(
						ModularGolems.loc("iron")),
				CriterionBuilder.item(GolemItems.HOLDER_DOG.get()),
				"Royal Cold Doggy", "Craft a Dog Golem.");
		arm.create("humanoid", GolemItems.HOLDER_HUMANOID.get().withUniformMaterial(
								ModularGolems.loc("iron")),
						CriterionBuilder.item(GolemItems.HOLDER_HUMANOID.get()),
						"You but Tougher", "Craft a Humaniod Golem.")
				.create("fully_equipped", GolemHolder.toEntityIcon(
								GolemItems.HOLDER_HUMANOID.get().withUniformMaterial(
										ModularGolems.loc("iron")),
								Items.DIAMOND_HELMET.getDefaultInstance(),
								Items.DIAMOND_CHESTPLATE.getDefaultInstance(),
								Items.DIAMOND_LEGGINGS.getDefaultInstance(),
								Items.DIAMOND_BOOTS.getDefaultInstance(),
								Items.DIAMOND_SWORD.getDefaultInstance(),
								Items.SHIELD.getDefaultInstance()
						),
						CriterionBuilder.one(GolemEquipTrigger.ins(6).build()),
						"Fully Equipped", "Give Humaniod Golem full armor set, a sword, and a shield.").type(AdvancementType.GOAL)
				.create("oops", GolemItems.HOLDER_HUMANOID.get().withUniformMaterial(
								ModularGolems.loc("gold")),
						CriterionBuilder.one(GolemBreakToolTrigger.ins()),
						"Oops...", "Let Humanoid Golem break a piece of equipment").type(AdvancementType.CHALLENGE);

		var golem = arm.create("craft", GolemItems.HOLDER_GOLEM.get().withUniformMaterial(
						ModularGolems.loc("iron")),
				CriterionBuilder.item(MGTagGen.GOLEM_HOLDERS),
				"A Brand New Golem", "Craft a Golem Holder with metal golem parts.");
		golem.create("thunder", Items.LIGHTNING_ROD,
						CriterionBuilder.one(GolemThunderTrigger.ins()),
						"Walking Lightning Rod", "Let a golem with thunder immune be struck with a lightning bolt")
				.type(AdvancementType.CHALLENGE);
		golem.create("anvil_fix", Items.ANVIL,
						CriterionBuilder.one(GolemAnvilFixTrigger.ins().build()),
						"Healing the Wounds", "Repair a metal golem with ingots in an anvil")
				.create("hot_fix", Items.IRON_INGOT,
						CriterionBuilder.one(GolemHotFixTrigger.ins()),
						"Repair in Battle", "Repair a metal golem with ingots directly")
				.create("kill_warden", Items.SCULK_CATALYST,
						CriterionBuilder.one(GolemKillTrigger.byType(EntityType.WARDEN).build()),
						"Ship of Theseus", "Let a golem kill a Warden").type(AdvancementType.CHALLENGE);
		golem.create("summon", GolemItems.DISPENSE_WAND.get(),
						CriterionBuilder.item(GolemItems.DISPENSE_WAND.get()),
						"Airborne Battalion", "Craft a summon wand")
				.create("summon_mass", GolemItems.DISPENSE_WAND.get(),
						CriterionBuilder.one(GolemMassSummonTrigger.atLeast(24).build()),
						"Portable Military", "Summon at least 24 golems at once").type(AdvancementType.CHALLENGE);
		golem.create("retrieve", GolemItems.RETRIEVAL_WAND.get(),
				CriterionBuilder.item(GolemItems.RETRIEVAL_WAND.get()),
				"Everyone Comes Back", "Craft a retrieval wand");
		golem.create("command", GolemItems.COMMAND_WAND.get(),
				CriterionBuilder.item(GolemItems.COMMAND_WAND.get()),
				"Guard Your Home", "Craft a command wand");
		var upgrade = golem.create("upgrade", GolemItems.EMPTY_UPGRADE.get(),
				CriterionBuilder.item(MGTagGen.GOLEM_UPGRADES),
				"Upgrade Your Golem", "Obtain an upgrade");
		upgrade.create("full", GolemItems.EMPTY_UPGRADE.get(),
						CriterionBuilder.one(UpgradeApplyTrigger.withRemain(0).build()),
						"No More Room", "Use up all upgrade slots").type(AdvancementType.TASK)
				.create("max", GolemItems.TALENTED.get(),
						CriterionBuilder.one(UpgradeApplyTrigger.withTotal(12).build()),
						"Above and Beyond", "Apply 12 upgrades on a golem").type(AdvancementType.CHALLENGE);
		upgrade.create("recycle", GolemItems.RECYCLE.get(),
				CriterionBuilder.one(UpgradeApplyTrigger.withUpgrade(GolemItems.RECYCLE.get()).build()),
				"Immortal Golem", "Apply a recycle upgrade on a golem").type(AdvancementType.CHALLENGE);
		upgrade.create("sponge", GolemItems.SPONGE.get(),
						CriterionBuilder.one(UpgradeApplyTrigger.withUpgrade(GolemItems.SPONGE.get()).build()),
						"Waterlogged Golem", "Apply a sponge upgrade on a golem").type(AdvancementType.GOAL)
				.create("kill_creeper", Items.TNT,
						CriterionBuilder.one(GolemKillTrigger.byType(EntityType.CREEPER).build()),
						"Anti-Terrorism Operation", "Let a golem kill a Creeper").type(AdvancementType.CHALLENGE);
		upgrade.create("swim", GolemItems.SWIM.get(),
						CriterionBuilder.one(UpgradeApplyTrigger.withUpgrade(GolemItems.SWIM.get()).build()),
						"Undersea Warrior", "Apply a swim upgrade on a golem").type(AdvancementType.GOAL)
				.create("kill_guardian", Items.PRISMARINE_SHARD,
						CriterionBuilder.one(GolemKillTrigger.byType(EntityType.GUARDIAN).build()),
						"Legend of the Atlantis", "Let a golem kill a Guardian").type(AdvancementType.CHALLENGE);
		root.finish();
	}

}
