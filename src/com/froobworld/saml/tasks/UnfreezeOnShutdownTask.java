package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.events.SamlMobUnfreezeEvent;
import com.froobworld.saml.utils.EntityFreezer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class UnfreezeOnShutdownTask implements Runnable {
    private Saml saml;

    public UnfreezeOnShutdownTask(Saml saml) {
        this.saml = saml;
    }


    @Override
    public void run() {
        if(saml.getSamlConfig().getBoolean("unfreeze-on-shutdown")) {
            List<LivingEntity> unfrozenMobs = new ArrayList<LivingEntity>();
            for(World world : Bukkit.getWorlds()) {
                for(LivingEntity entity : world.getLivingEntities()) {
                    if(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") ? EntityFreezer.isSamlFrozen(saml, entity) : EntityFreezer.isFrozen(entity)) {
                        if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(entity::hasMetadata)) {
                            continue;
                        }
                        EntityFreezer.unfreezeEntity(saml, entity);
                        unfrozenMobs.add(entity);
                    }
                }
            }
            SamlMobUnfreezeEvent mobUnfreezeEvent = new SamlMobUnfreezeEvent(unfrozenMobs, SamlMobUnfreezeEvent.UnfreezeReason.SHUTDOWN);
            Bukkit.getPluginManager().callEvent(mobUnfreezeEvent);
        }
    }
}
