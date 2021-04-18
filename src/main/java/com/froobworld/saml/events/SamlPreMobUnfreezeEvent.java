package com.froobworld.saml.events;

import com.froobworld.saml.data.UnfreezeParameters;
import com.froobworld.saml.data.UnfreezeReason;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SamlPreMobUnfreezeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private UnfreezeParameters.Builder unfreezeParametersBuilder;
    private UnfreezeReason reason;

    public SamlPreMobUnfreezeEvent(UnfreezeReason reason) {
        this.cancelled = false;
        this.unfreezeParametersBuilder = new UnfreezeParameters.Builder();
        this.reason = reason;
    }


    public UnfreezeParameters.Builder getUnfreezeParametersBuilder() {
        return unfreezeParametersBuilder;
    }

    public UnfreezeParameters getCurrentUnfreezeParameters() {
        return unfreezeParametersBuilder.build();
    }

    public UnfreezeReason getReason() {
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
