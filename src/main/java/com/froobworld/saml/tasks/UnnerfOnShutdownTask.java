package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.utils.EntityNerfer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

public class UnnerfOnShutdownTask implements Runnable {
    private Saml saml;

    public UnnerfOnShutdownTask(Saml saml) {
        this.saml = saml;
    }


    @Override
    public void run() {
        for(World world : Bukkit.getWorlds()) {
            for(LivingEntity entity : world.getLivingEntities()) {
                EntityNerfer.unnerf(saml, entity);
            }
        }
    }
}
