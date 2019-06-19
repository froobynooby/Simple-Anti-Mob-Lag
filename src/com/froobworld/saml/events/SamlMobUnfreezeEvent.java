package com.froobworld.saml.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SamlMobUnfreezeEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    private List<LivingEntity> unfrozenMobs;
    private UnfreezeReason unfreezeReason;

    public SamlMobUnfreezeEvent(List<LivingEntity> unfrozenMobs, UnfreezeReason unfreezeReason) {
        this.unfrozenMobs = unfrozenMobs;
        this.unfreezeReason = unfreezeReason;
    }

    public SamlMobUnfreezeEvent(LivingEntity mobToUnfreeze, UnfreezeReason unfreezeReason) {
        this(new ArrayList<LivingEntity>(Collections.singletonList(mobToUnfreeze)), unfreezeReason);
    }

    public List<LivingEntity> getUnfrozenMobs() {
        return new ArrayList<LivingEntity>(unfrozenMobs);
    }

    public UnfreezeReason getUnfreezeReason() {
        return unfreezeReason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static enum UnfreezeReason {
        MAIN_TASK,
        INTERACTION,
        DAMAGE,
        CHUNK_UNLOAD,
        UNFREEZE_CACHED_CHUNK,
        SHUTDOWN,
        CUSTOM
    }
}
