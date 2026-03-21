package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2damagetracker.init.data.DamageTypeAndTagsGen;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MGDamageTypes extends DamageTypeAndTagsGen {

	public static final ResourceKey<DamageType> BEACON = createDamage("bacon_attack");
	public static final ResourceKey<DamageType> ECHO = createDamage("echo_attack");

	public MGDamageTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> pvd, ExistingFileHelper helper) {
		super(output, pvd, helper, ModularGolems.MODID);
		new DamageTypeHolder(BEACON, new DamageType("magic", 0.1f))
				.add(L2DamageTypes.MAGIC, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_ARMOR);

		new DamageTypeHolder(ECHO, new DamageType("sonic_boom", 0.1f))
				.add(L2DamageTypes.MAGIC, DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.BYPASSES_ARMOR)
				.add(L2DamageTypes.BYPASS_MAGIC);
	}

	private static ResourceKey<DamageType> createDamage(String id) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, ModularGolems.loc(id));
	}

}
