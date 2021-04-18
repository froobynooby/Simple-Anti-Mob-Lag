package com.froobworld.saml.data;

import com.froobworld.saml.group.entity.EntityGroup;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class FreezeParameters {
    private boolean doAsync;
    private boolean doCleanup;
    private long maximumOperationTime;
    private double currentTps;
    private double expectedTps;
    private Set<World> worlds;
    private Predicate<LivingEntity> ignorePredicate;
    private Set<EntityGroup> includeFreezeGroups;
    private Set<EntityGroup> excludeFreezeGroups;
    private Set<EntityGroup> includeNerfGroups;
    private Set<EntityGroup> excludeNerfGroups;
    private long minimumFreezeTime;

    private FreezeParameters(boolean doAsync, boolean doCleanup, long maximumOperationTime, double currentTps, double expectedTps, Set<World> worlds, Predicate<LivingEntity> ignorePredicate, Set<EntityGroup> includeFreezeGroups, Set<EntityGroup> excludeFreezeGroups, Set<EntityGroup> includeNerfGroups, Set<EntityGroup> excludeNerfGroups, long minimumFreezeTime) {
        this.doAsync = doAsync;
        this.doCleanup = doCleanup;
        this.maximumOperationTime = maximumOperationTime;
        this.currentTps = currentTps;
        this.expectedTps = expectedTps;
        this.worlds = worlds;
        this.ignorePredicate = ignorePredicate;
        this.includeFreezeGroups = includeFreezeGroups;
        this.excludeFreezeGroups = excludeFreezeGroups;
        this.includeNerfGroups = includeNerfGroups;
        this.excludeNerfGroups = excludeNerfGroups;
        this.minimumFreezeTime = minimumFreezeTime;
    }


    public boolean doAsync() {
        return doAsync;
    }

    public boolean doCleanup() {
        return doCleanup;
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

    public Set<EntityGroup> getIncludeFreezeGroups() {
        return includeFreezeGroups;
    }

    public Set<EntityGroup> getExcludeFreezeGroups() {
        return excludeFreezeGroups;
    }

    public Set<EntityGroup> getIncludeNerfGroups() {
        return includeNerfGroups;
    }

    public Set<EntityGroup> getExcludeNerfGroups() {
        return excludeNerfGroups;
    }

    public long getMinimumFreezeTime() {
        return minimumFreezeTime;
    }

    public static class Builder {
        private boolean doAsync;
        private boolean doCleanup;
        private long maximumOperationTime;
        private double currentTps;
        private double expectedTps;
        private Set<World> worlds;
        private Predicate<LivingEntity> ignorePredicate;
        private Set<EntityGroup> includeFreezeGroups;
        private Set<EntityGroup> excludeFreezeGroups;
        private Set<EntityGroup> includeNerfGroups;
        private Set<EntityGroup> excludeNerfGroups;
        private long minimumFreezeTime;

        public Builder() {
            this.doAsync = true;
            this.doCleanup = false;
            this.maximumOperationTime = 0;
            this.currentTps = 20.0;
            this.expectedTps = 20.0;
            this.worlds = new HashSet<>();
            this.ignorePredicate = e -> false;
            this.includeFreezeGroups = new HashSet<>();
            this.excludeFreezeGroups = new HashSet<>();
            this.includeNerfGroups = new HashSet<>();
            this.excludeNerfGroups = new HashSet<>();
            this.minimumFreezeTime = 0;
        }


        public Builder setDoAsync(boolean doAsync) {
            this.doAsync = doAsync;
            return this;
        }

        public Builder setDoCleanup(boolean doCleanup) {
            this.doCleanup = doCleanup;
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

        public Builder includeFreezeGroup(EntityGroup group) {
            includeFreezeGroups.add(group);
            return this;
        }

        public Builder excludeFreezeGroup(EntityGroup group) {
            excludeFreezeGroups.add(group);
            return this;
        }

        public Builder includeNerfGroup(EntityGroup group) {
            includeNerfGroups.add(group);
            return this;
        }

        public Builder excludeNerfGroup(EntityGroup group) {
            excludeNerfGroups.add(group);
            return this;
        }

        public Builder setMinimumFreezeTime(long minimumFreezeTime) {
            this.minimumFreezeTime = minimumFreezeTime;
            return this;
        }

        public FreezeParameters build() {
            return new FreezeParameters(doAsync, doCleanup, maximumOperationTime, currentTps, expectedTps, worlds, ignorePredicate, includeFreezeGroups, excludeFreezeGroups, includeNerfGroups, excludeNerfGroups, minimumFreezeTime);
        }

    }

}
