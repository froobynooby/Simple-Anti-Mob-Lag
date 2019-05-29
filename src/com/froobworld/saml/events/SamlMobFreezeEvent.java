package com.froobworld.saml.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class SamlMobFreezeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private List<LivingEntity> mobsToFreeze;

    public SamlMobFreezeEvent(List<LivingEntity> mobsToFreeze) {
        this.mobsToFreeze = mobsToFreeze;
        this.cancelled = false;
    }


    public List<LivingEntity> getMobsToFreeze() {
        return mobsToFreeze;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
