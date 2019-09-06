package com.froobworld.saml.data;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class UnfreezeParameters {
    private boolean includeAllGroups;
    private Set<String> includeGroups;
    private Set<String> excludeGroups;
    private EnumSet<FreezeReason> includeFreezeReasons;
    private EnumSet<FreezeReason> excludeFreezeReasons;
    private Set<World> worlds;
    private long unfreezeLimit;
    private boolean ignoreRemainingTime;
    private Predicate<LivingEntity> ignorePredicate;

    private UnfreezeParameters(boolean includeAllGroups, Set<String> includeGroups, Set<String> excludeGroups, EnumSet<FreezeReason> includeFreezeReasons, EnumSet<FreezeReason> excludeFreezeReasons, Set<World> worlds, long unfreezeLimit, boolean ignoreRemainingTime, Predicate<LivingEntity> ignorePredicate) {
        this.includeAllGroups = includeAllGroups;
        this.includeGroups = includeGroups;
        this.excludeGroups = excludeGroups;
        this.includeFreezeReasons = includeFreezeReasons;
        this.excludeFreezeReasons = excludeFreezeReasons;
        this.worlds = worlds;
        this.unfreezeLimit = unfreezeLimit;
        this.ignoreRemainingTime = ignoreRemainingTime;
        this.ignorePredicate = ignorePredicate;
    }


    public boolean includeAllGroups() {
        return includeAllGroups;
    }

    public Set<String> getIncludeGroups() {
        return includeGroups;
    }

    public Set<String> getExcludeGroups() {
        return excludeGroups;
    }

    public EnumSet<FreezeReason> getIncludeFreezeReasons() {
        return includeFreezeReasons;
    }

    public EnumSet<FreezeReason> getExcludeFreezeReasons() {
        return excludeFreezeReasons;
    }

    public Set<World> getWorlds() {
        return worlds;
    }

    public long getUnfreezeLimit() {
        return unfreezeLimit;
    }

    public boolean ignoreRemainingTime() {
        return ignoreRemainingTime;
    }

    public Predicate<LivingEntity> getIgnorePredicate() {
        return ignorePredicate;
    }

    public static class Builder {
        private boolean includeAllGroups;
        private Set<String> includeGroups;
        private Set<String> excludeGroups;
        private EnumSet<FreezeReason> includeFreezeReasons;
        private EnumSet<FreezeReason> excludeFreezeReasons;
        private Set<World> worlds;
        private long unfreezeLimit;
        private boolean ignoreRemainingTime;
        private Predicate<LivingEntity> ignorePredicate;

        public Builder() {
            this.includeAllGroups = true;
            this.includeGroups = new HashSet<>();
            this.excludeGroups = new HashSet<>();
            this.includeFreezeReasons = EnumSet.noneOf(FreezeReason.class);
            this.excludeFreezeReasons = EnumSet.noneOf(FreezeReason.class);
            this.worlds = new HashSet<>();
            this.unfreezeLimit = -1;
            this.ignoreRemainingTime = false;
            this.ignorePredicate = e -> false;
        }


        public Builder includeAllGroups(boolean includeAllGroups) {
            this.includeAllGroups = includeAllGroups;
            return this;
        }

        public Builder includeGroup(String group) {
            includeGroups.add(group);
            return this;
        }

        public Builder excludeGroup(String group) {
            excludeGroups.add(group);
            return this;
        }

        public Builder includeFreezeReason(FreezeReason freezeReason) {
            this.includeFreezeReasons.add(freezeReason);
            return this;
        }

        public Builder excludeFreezeReason(FreezeReason freezeReason) {
            this.excludeFreezeReasons.add(freezeReason);
            return this;
        }

        public Builder addWorld(World world) {
            worlds.add(world);
            return this;
        }

        public Builder setUnfreezeLimit(long unfreezeLimit) {
            this.unfreezeLimit = unfreezeLimit;
            return this;
        }

        public Builder ignoreRemainingTime(boolean ignoreRemainingTime) {
            this.ignoreRemainingTime = ignoreRemainingTime;
            return this;
        }

        public Builder addIgnorePredicate(Predicate<LivingEntity> ignorePredicate) {
            this.ignorePredicate = this.ignorePredicate.or(ignorePredicate);
            return this;
        }

        public UnfreezeParameters build() {
            return new UnfreezeParameters(includeAllGroups, includeGroups, excludeGroups, includeFreezeReasons, excludeFreezeReasons, worlds, unfreezeLimit, ignoreRemainingTime, ignorePredicate);
        }

    }

}
