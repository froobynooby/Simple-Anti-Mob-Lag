package com.froobworld.saml.events;

import com.froobworld.saml.Config;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SamlConfigReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Config config;

    public SamlConfigReloadEvent(Config config) {
        this.config = config;
    }


    public Config getConfig() {
        return this.config;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
