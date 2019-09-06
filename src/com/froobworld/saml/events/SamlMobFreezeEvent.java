package com.froobworld.saml.events;

import com.froobworld.saml.data.FreezeParameters;
import com.froobworld.saml.data.FreezeReason;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class SamlMobFreezeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private List<LivingEntity> mobsAffected;
    private List<LivingEntity> mobsFrozen;
    private List<LivingEntity> mobsNerfed;
    private long timeTaken;
    private FreezeParameters freezeParameters;
    private FreezeReason reason;

    public SamlMobFreezeEvent(List<LivingEntity> mobsAffected, List<LivingEntity> mobsFrozen, List<LivingEntity> mobsNerfed, long timeTaken, FreezeParameters freezeParameters, FreezeReason reason) {
        this.mobsAffected = mobsAffected;
        this.mobsFrozen = mobsFrozen;
        this.mobsNerfed = mobsNerfed;
        this.timeTaken = timeTaken;
        this.freezeParameters = freezeParameters;
        this.reason = reason;
    }


    public FreezeParameters getFreezeParameters() {
        return freezeParameters;
    }

    public List<LivingEntity> getMobsAffected() {
        return mobsAffected;
    }

    public List<LivingEntity> getMobsFrozen() {
        return mobsFrozen;
    }

    public List<LivingEntity> getMobsNerfed() {
        return mobsNerfed;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public FreezeReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
