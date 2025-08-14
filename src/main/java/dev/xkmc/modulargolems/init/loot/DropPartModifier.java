package dev.xkmc.modulargolems.init.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class DropPartModifier extends LootModifier {

	public static final Codec<DropPartModifier> CODEC = RecordCodecBuilder.create(i ->
			LootModifier.codecStart(i)
					.and(ResourceLocation.CODEC.fieldOf("entity").forGetter(e -> e.entity))
					.and(ResourceLocation.CODEC.fieldOf("material").forGetter(e -> e.material))
					.apply(i, DropPartModifier::new));

	private final ResourceLocation entity;
	private final ResourceLocation material;

	protected DropPartModifier(LootItemCondition[] conditionsIn, ResourceLocation entity, ResourceLocation material) {
		super(conditionsIn);
		this.entity = entity;
		this.material = material;
	}

	protected DropPartModifier(ResourceLocation entity, ResourceLocation material, LootItemCondition... conditionsIn) {
		super(conditionsIn);
		this.entity = entity;
		this.material = material;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext ctx) {
		if (!ctx.hasParam(LootContextParams.THIS_ENTITY)) return loot;
		var e = ctx.getParam(LootContextParams.THIS_ENTITY);
		if (!entity.equals(ForgeRegistries.ENTITY_TYPES.getKey(e.getType()))) return loot;
		MetalGolemPartType type = MetalGolemPartType.values()[ctx.getRandom().nextInt(4)];
		loot.add(GolemPart.setMaterial(type.toItem().getDefaultInstance(), material));
		return loot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

}
