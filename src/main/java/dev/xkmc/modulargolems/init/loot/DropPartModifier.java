package dev.xkmc.modulargolems.init.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class DropPartModifier extends LootModifier {

	public static final MapCodec<DropPartModifier> CODEC = RecordCodecBuilder.mapCodec(i ->
			LootModifier.codecStart(i).and(
					Identifier.CODEC.fieldOf("material").forGetter(e -> e.material)
			).apply(i, DropPartModifier::new));

	private final Identifier material;

	protected DropPartModifier(LootItemCondition[] conditionsIn, int priority, Identifier material) {
		super(conditionsIn, priority);
		this.material = material;
	}

	protected DropPartModifier(int priority, Identifier material, LootItemCondition... conditionsIn) {
		super(conditionsIn, priority);
		this.material = material;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext ctx) {
		MetalGolemPartType type = MetalGolemPartType.values()[ctx.getRandom().nextInt(4)];
		loot.add(GolemPart.setMaterial(type.toItem().getDefaultInstance(), material));
		return loot;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

}
