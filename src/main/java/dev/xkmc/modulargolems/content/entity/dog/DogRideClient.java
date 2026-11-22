package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.client.Minecraft;

public class DogRideClient {

	public static boolean isSprintKeyDown() {
		return Minecraft.getInstance().options.keySprint.isDown();
	}

}
