package com.froobworld.saml.data;

import com.froobworld.saml.group.entity.EntityGroup;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class FreezeParameters {
    private boolean doAsync;
    private boolean broadcastToConsole;
    private boolean broadcastToOps;
    private long maximumOperationTime;
    private double currentTps;
    private double expectedTps;
    private Set<World> worlds;
    private Predicate<LivingEntity> ignorePredicate;
    private Set<EntityGroup> includeGroups;
    private Set<EntityGroup> excludeGroups;
    private long minimumFreezeTime;

    private FreezeParameters(boolean doAsync, boolean broadcastToConsole, boolean broadcastToOps, long maximumOperationTime, double currentTps, double expectedTps, Set<World> worlds, Predicate<LivingEntity> ignorePredicate, Set<EntityGroup> includeGroups, Set<EntityGroup> excludeGroups, long minimumFreezeTime) {
        this.doAsync = doAsync;
        this.broadcastToConsole = broadcastToConsole;
        this.broadcastToOps = broadcastToOps;
        this.maximumOperationTime = maximumOperationTime;
        this.currentTps = currentTps;
        this.expectedTps = expectedTps;
        this.worlds = worlds;
        this.ignorePredicate = ignorePredicate;
        this.includeGroups = includeGroups;
        this.excludeGroups = excludeGroups;
        this.minimumFreezeTime = minimumFreezeTime;
    }


    public boolean doAsync() {
        return doAsync;
    }

    public boolean broadcastToConsole() {
        return broadcastToConsole;
    }

    public boolean broadcastToOps() {
        return broadcastToOps;
    }

    public long getMaximumOperationTime() {
        return maximumOperationTime;
    }

    public double getCurrentTps() {
        return currentTps;
    }

    public double getExpectedTps() {
        return expectedTps;
    }

    public Set<World> getWorlds() {
        return worlds;
    }

    public Predicate<LivingEntity> getIgnorePredicate() {
        return ignorePredicate;
    }

    public Set<EntityGroup> getIncludeGroups() {
        return includeGroups;
    }

    public Set<EntityGroup> getExcludeGroups() {
        return excludeGroups;
    }

    public long getMinimumFreezeTime() {
        return minimumFreezeTime;
    }

    public static class Builder {
        private boolean doAsync;
        private boolean broadcastToConsole;
        private boolean broadcastToOps;
        private long maximumOperationTime;
        private double currentTps;
        private double expectedTps;
        private Set<World> worlds;
        private Predicate<LivingEntity> ignorePredicate;
        private Set<EntityGroup> includeGroups;
        private Set<EntityGroup> excludeGroups;
        private long minimumFreezeTime;

        public Builder() {
            this.doAsync = true;
            this.broadcastToConsole = true;
            this.broadcastToOps = false;
            this.maximumOperationTime = 0;
            this.currentTps = 20.0;
            this.expectedTps = 20.0;
            this.worlds = new HashSet<World>();
            this.ignorePredicate = e -> false;
            this.includeGroups = new HashSet<EntityGroup>();
            this.excludeGroups = new HashSet<EntityGroup>();
            this.minimumFreezeTime = 0;
        }


        public Builder setDoAsync(boolean doAsync) {
            this.doAsync = doAsync;
            return this;
        }

        public Builder broadcastToConsole(boolean broadcastToConsole) {
            this.broadcastToConsole = broadcastToConsole;
            return this;
        }

        public Builder broadcastToOps(boolean broadcastToOps) {
            this.broadcastToOps = broadcastToOps;
            return this;
        }

        public Builder setMaximumOperationTime(long maximumOperationTime) {
            this.maximumOperationTime = maximumOperationTime;
            return this;
        }

        public Builder setCurrentTps(double currentTps) {
            this.currentTps = currentTps;
            return this;
        }

        public Builder setExpectedTps(double expectedTps) {
            this.expectedTps = expectedTps;
            return this;
        }

        public Builder addWorld(World world) {
            this.worlds.add(world);
            return this;
        }

        public Builder addIgnorePredicate(Predicate<LivingEntity> ignorePredicate) {
            this.ignorePredicate = this.ignorePredicate.or(ignorePredicate);
            return this;
        }

        public Builder includeGroup(EntityGroup group) {
            includeGroups.add(group);
            return this;
        }

        public Builder excludeGroup(EntityGroup group) {
            excludeGroups.add(group);
            return this;
        }

        public Builder setMinimumFreezeTime(long minimumFreezeTime) {
            this.minimumFreezeTime = minimumFreezeTime;
            return this;
        }

        public FreezeParameters build() {
            return new FreezeParameters(doAsync, broadcastToConsole, broadcastToOps, maximumOperationTime, currentTps, expectedTps, worlds, ignorePredicate, includeGroups, excludeGroups, minimumFreezeTime);
        }

    }

}
