package com.froobworld.saml.utils.metric;

import org.bukkit.Location;

public class EuclideanMetric implements Metric {
    private double weightX, weightY, weightZ;

    public EuclideanMetric(double weightX, double weightY, double weightZ) {
        this.weightX = weightX;
        this.weightY = weightY;
        this.weightZ = weightZ;
    }

    public EuclideanMetric() {
        this(1, 1, 1);
    }


    @Override
    public double distance(Location location1, Location location2) {
        return Math.sqrt(distanceSquared(location1, location2));
    }

    @Override
    public double distanceSquared(Location location1, Location location2) {
        return Math.pow(weightX * (location1.getX() - location2.getX()), 2.0) + Math.pow(weightY * (location1.getY() - location2.getY()), 2.0) + Math.pow(weightZ * (location1.getZ() - location2.getZ()), 2.0);
    }

    @Override
    public void weight(double weightX, double weightY, double weightZ) {
        this.weightX = weightX;
        this.weightY = weightY;
        this.weightZ = weightZ;
    }
}
