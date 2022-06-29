package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.modulargolems.content.core.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemTypeRegistry {

	public static final L2Registrate.RegistryInstance<GolemModifier> MODIFIERS = REGISTRATE.newRegistry("modifier", GolemModifier.class);
	public static final L2Registrate.RegistryInstance<GolemStatType> STAT_TYPES = REGISTRATE.newRegistry("stat_type", GolemStatType.class);
	public static final L2Registrate.RegistryInstance<GolemMaterial> MATERIALS = REGISTRATE.newRegistry("material", GolemMaterial.class);

}
