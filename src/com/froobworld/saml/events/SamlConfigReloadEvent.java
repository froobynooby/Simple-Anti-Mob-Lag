package com.froobworld.saml.events;

import com.froobworld.saml.SamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SamlConfigReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private SamlConfiguration config;
    private SamlConfiguration advancedConfig;
    private SamlConfiguration messages;

    public SamlConfigReloadEvent(SamlConfiguration config, SamlConfiguration advancedConfig, SamlConfiguration messages) {
        this.config = config;
        this.advancedConfig = advancedConfig;
        this.messages = messages;
    }


    public SamlConfiguration getConfig() {
        return this.config;
    }

    public SamlConfiguration getAdvancedConfig() {
        return this.advancedConfig;
    }

    public SamlConfiguration getMessages() {
        return this.messages;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
