package dev.xkmc.modulargolems.content.entity.dog;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DogRideClient {

	public static boolean isSprintKeyDown() {
		return Minecraft.getInstance().options.keySprint.isDown();
	}

}
