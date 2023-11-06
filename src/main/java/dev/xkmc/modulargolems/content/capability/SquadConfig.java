package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SerialClass
public class SquadConfig {

	@SerialClass.SerialField
	protected UUID captainId = null;

	@SerialClass.SerialField
	protected double radius = 0D;

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Nullable
	public UUID getCaptainId() {
		return captainId;
	}

	public void setCaptainId(@Nullable UUID captainId) {
		this.captainId = captainId;
	}

}
