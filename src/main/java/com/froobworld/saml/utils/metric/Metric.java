package com.froobworld.saml.utils.metric;

import org.bukkit.Location;

public interface Metric {
    public double distance(Location location1, Location location2);
    public double distanceSquared(Location location1, Location location2);
    public void weight(double weightX, double weightY, double weightZ);
}
