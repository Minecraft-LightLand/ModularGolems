package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import java.util.UUID;

public class SquadConfig {
    public void setCaptainId(UUID captainId) {
        this.captainId = captainId;
    }

    @SerialClass.SerialField
    protected UUID captainId = null;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
    @SerialClass.SerialField
    protected double radius =0D;
    public UUID getCaptainId(){return captainId;}
}
