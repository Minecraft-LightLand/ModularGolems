package dev.xkmc.modulargolems.content.item.wand;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * One key per omnipotent wand function, none bound by default; the player assigns their own. {@link
 * RegisterKeyMappingsEvent} only ever fires on the mod bus, hence the explicit {@code bus = Bus.MOD}.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum OmniWandKeys {

	COMMAND("key.modulargolems.omni_wand_key.command"),
	RETRIEVAL("key.modulargolems.omni_wand_key.retrieval"),
	SUMMON("key.modulargolems.omni_wand_key.summon"),
	RIDER("key.modulargolems.omni_wand_key.rider"),
	SQUAD("key.modulargolems.omni_wand_key.squad");

	public static final String CATEGORY = "key.categories.modulargolems";

	public final String id;
	public final KeyMapping map;

	OmniWandKeys(String id) {
		this.id = id;
		this.map = new KeyMapping(id, KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, CATEGORY);
	}

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		for (var k : values()) event.register(k.map);
	}

}
