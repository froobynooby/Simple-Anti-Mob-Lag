package com.froobworld.saml.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Predicate;

public class SamlPreMobFreezeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Predicate<LivingEntity> shouldIgnorePredicate;

    public SamlPreMobFreezeEvent() {
        this.cancelled = false;
        shouldIgnorePredicate = e -> false;
    }


    public void addShouldIgnorePredicate(Predicate<LivingEntity> predicate) {
        shouldIgnorePredicate = shouldIgnorePredicate.or(predicate);
    }

    public Predicate<LivingEntity> getShouldIgnorePredicate() {
        return shouldIgnorePredicate;
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
