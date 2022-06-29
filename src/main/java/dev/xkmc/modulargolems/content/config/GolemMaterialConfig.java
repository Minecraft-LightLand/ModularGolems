package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.init.NetworkManager;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@SerialClass
public class GolemMaterialConfig extends BaseConfig {

	public static GolemMaterialConfig get(){
		return NetworkManager.MATERIAL.getMerged();
	}

	@SerialClass.SerialField
	public HashMap<String, HashMap<GolemStatType, Double>> stats = new HashMap<>();

	@SerialClass.SerialField
	public HashMap<String, ArrayList<GolemModifier>> modifiers = new HashMap<>();

	@SerialClass.SerialField
	public HashMap<String, Ingredient> ingredient = new HashMap<>();

	public void validate() {

	}

}
