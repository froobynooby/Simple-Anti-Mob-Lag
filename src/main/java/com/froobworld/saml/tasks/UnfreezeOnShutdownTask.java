package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.data.UnfreezeReason;
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
        if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_SHUTDOWN)) {
            List<LivingEntity> unfrozenMobs = new ArrayList<>();
            for(World world : Bukkit.getWorlds()) {
                for(LivingEntity entity : world.getLivingEntities()) {
                    if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ONLY_UNFREEZE_TAGGED) ? EntityFreezer.isSamlFrozen(saml, entity) : EntityFreezer.isFrozen(entity)) {
                        if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(entity::hasMetadata)) {
                            continue;
                        }
                        EntityFreezer.unfreezeEntity(saml, entity);
                        unfrozenMobs.add(entity);
                    }
                }
            }
            if(!unfrozenMobs.isEmpty()) {
                SamlMobUnfreezeEvent mobUnfreezeEvent = new SamlMobUnfreezeEvent(unfrozenMobs, UnfreezeReason.SHUTDOWN);
                Bukkit.getPluginManager().callEvent(mobUnfreezeEvent);
            }
        }
    }
}
