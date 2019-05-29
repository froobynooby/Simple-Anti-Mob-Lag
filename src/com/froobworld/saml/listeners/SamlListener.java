package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import com.froobworld.saml.events.SamlConfigReloadEvent;
import com.froobworld.saml.events.SamlPreMobFreezeEvent;
import com.froobworld.saml.utils.CompatibilityUtils;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SamlListener implements Listener {
    private Saml saml;

    public SamlListener(Saml saml) {
        this.saml = saml;
    }


    @EventHandler
    public void onSamlConfigReload(SamlConfigReloadEvent event) {
        if(event.getConfig().getBoolean("keep-frozen-chunk-cache")) {
            saml.getMobFreezeTask().createChunkCacheIfNotExist();
        }
        if(!event.getConfig().getBoolean("unfreeze-on-shutdown") || !event.getConfig().getBoolean("unfreeze-on-unload")) {
            if(saml.getMobFreezeTask().getFrozenChunkCache() != null) {
                saml.getMobFreezeTask().getFrozenChunkCache().setShouldSaveOnExit();
            }
        }
    }

    @EventHandler
    public void onSamlPreMobFreeze(SamlPreMobFreezeEvent event) {
        boolean ignoreTamed = saml.getSamlConfig().getBoolean("ignore-tamed");
        boolean ignoreNamed = saml.getSamlConfig().getBoolean("ignore-named");
        boolean ignoreLeashed = saml.getSamlConfig().getBoolean("ignore-leashed");
        Set<String> neverFreeze = new HashSet<String>(saml.getSamlConfig().getStringList("never-freeze"));

        event.addShouldIgnorePredicate(new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity entity) {
                return neverFreeze.contains(entity.getType().name());
            }
        });
        event.addShouldIgnorePredicate(new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity entity) {
                return ignoreTamed && entity instanceof Tameable && ((Tameable) entity).getOwner() != null;
            }
        });
        event.addShouldIgnorePredicate(new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity entity) {
                return ignoreNamed && entity.getCustomName() != null;
            }
        });
        event.addShouldIgnorePredicate(new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity entity) {
                return ignoreLeashed && entity.isLeashed();
            }
        });
        event.addShouldIgnorePredicate(new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity entity) {
                return CompatibilityUtils.getIgnoreLoveModeOption(saml.getSamlConfig()) && entity instanceof Animals && ((Animals) entity).isLoveMode();
            }
        });
    }

}