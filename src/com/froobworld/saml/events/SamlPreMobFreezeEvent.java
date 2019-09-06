package com.froobworld.saml.events;

import com.froobworld.saml.data.FreezeParameters;
import com.froobworld.saml.data.FreezeReason;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SamlPreMobFreezeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private FreezeParameters.Builder freezeParametersBuilder;
    private FreezeReason reason;

    public SamlPreMobFreezeEvent(FreezeReason reason) {
        this.cancelled = false;
        this.freezeParametersBuilder = new FreezeParameters.Builder();
        this.reason = reason;
    }


    public FreezeParameters.Builder getFreezeParametersBuilder() {
        return freezeParametersBuilder;
    }

    public FreezeParameters getCurrentFreezeParameters() {
        return freezeParametersBuilder.build();
    }

    public FreezeReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
