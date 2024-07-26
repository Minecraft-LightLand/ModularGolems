package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SerialClass
public class SquadConfig {

	@SerialField
	protected UUID captainId = null;

	@SerialField
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
