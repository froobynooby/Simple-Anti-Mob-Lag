package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import com.froobworld.saml.events.SamlConfigReloadEvent;
import com.froobworld.saml.events.SamlPreMobFreezeEvent;
import com.froobworld.saml.utils.CompatibilityUtils;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
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
        if(event.getConfig().getBoolean("use-advanced-config")) {
            if(!saml.getAdvancedConfig().isLoaded()) {
                saml.getAdvancedConfig().loadFromFile();
            }
        }
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
        boolean ignoreLoveMode = saml.getSamlConfig().getBoolean("ignore-love-mode");
        Set<String> neverFreeze = new HashSet<String>(saml.getSamlConfig().getStringList("never-freeze"));

        HashMap<EntityType, Boolean> typedIgnoreTamed = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Boolean> typedIgnoreNamed = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Boolean> typedIgnoreLeashed = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Boolean> typedIgnoreLoveMode = new HashMap<EntityType, Boolean>();
        if(saml.getSamlConfig().getBoolean("use-advanced-config")) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists("ignore-tamed." + entityType.name())) {
                    typedIgnoreTamed.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-tamed." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-named." + entityType.name())) {
                    typedIgnoreNamed.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-named." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-leashed." + entityType.name())) {
                    typedIgnoreLeashed.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-leashed." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-love-mode." + entityType.name())) {
                    typedIgnoreLoveMode.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-love-mode." + entityType.name()));
                }
            }
        }

        event.addShouldIgnorePredicate( e -> (neverFreeze.contains(e.getType().name())) );
        event.addShouldIgnorePredicate( e -> (typedIgnoreTamed.getOrDefault(e.getType(), ignoreTamed) && e instanceof Tameable && ((Tameable) e).getOwner() != null) );
        event.addShouldIgnorePredicate( e -> (typedIgnoreNamed.getOrDefault(e.getType(), ignoreNamed) && e.getCustomName() != null) );
        event.addShouldIgnorePredicate( e -> (typedIgnoreLeashed.getOrDefault(e.getType(), ignoreLeashed) && e.isLeashed()) );
        event.addShouldIgnorePredicate( e -> (CompatibilityUtils.ANIMAL_LOVE_MODE && typedIgnoreLoveMode.getOrDefault(e.getType(), ignoreLoveMode) && e instanceof Animals && ((Animals) e).isLoveMode()) );
    }

}