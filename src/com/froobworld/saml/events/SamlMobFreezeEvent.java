package com.froobworld.saml.events;

import com.froobworld.saml.data.FreezeParameters;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class SamlMobFreezeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private List<LivingEntity> mobsToFreeze;
    private FreezeParameters freezeParameters;
    private FreezeReason reason;

    public SamlMobFreezeEvent(List<LivingEntity> mobsToFreeze, FreezeParameters freezeParameters, FreezeReason reason) {
        this.mobsToFreeze = mobsToFreeze;
        this.freezeParameters = freezeParameters;
        this.reason = reason;
    }


    public FreezeParameters getFreezeParameters() {
        return freezeParameters;
    }

    public List<LivingEntity> getMobsToFreeze() {
        return mobsToFreeze;
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

    public static enum FreezeReason {
        MAIN_TASK,
        COMMAND,
        CUSTOM
    }
}
