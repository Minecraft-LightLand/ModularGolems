package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SerialClass
public class GolemMaterialConfig extends BaseConfig {

	@SerialClass.SerialField
	public HashMap<String, HashMap<GolemStatType, Double>> stats = new HashMap<>();

	@SerialClass.SerialField
	public HashMap<String, ArrayList<GolemModifier>> modifiers = new HashMap<>();


}
