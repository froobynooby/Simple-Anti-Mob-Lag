package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.utils.EntityFreezer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

public class UnfreezeOnShutdownTask implements Runnable {
    private Saml saml;

    public UnfreezeOnShutdownTask(Saml saml) {
        this.saml = saml;
    }


    @Override
    public void run() {
        if(saml.getSamlConfig().getBoolean("unfreeze-on-shutdown")) {
            for(World world : Bukkit.getWorlds()) {
                for(LivingEntity entity : world.getLivingEntities()) {
                    if(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") ? EntityFreezer.isSamlFrozen(saml, entity) : EntityFreezer.isFrozen(entity)) {
                        if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(entity::hasMetadata)) {
                            continue;
                        }
                        EntityFreezer.unfreezeEntity(saml, entity);
                    }
                }
            }
        }
    }
}
