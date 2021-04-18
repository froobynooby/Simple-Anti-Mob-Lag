package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.data.FreezeReason;
import com.froobworld.saml.events.SamlPreMobFreezeEvent;
import com.froobworld.saml.utils.ParameterisedEntityFreezer;
import org.bukkit.Bukkit;

public class PassiveFreezeTask implements Runnable {
    private Saml saml;

    public PassiveFreezeTask(Saml saml) {
        this.saml = saml;
        run();
    }


    @Override
    public void run() {
        long ticksPerOperation = saml.getSamlConfig().getLong(ConfigKeys.CNF_TICKS_PER_PASSIVE_FREEZE_TASK);
        Bukkit.getScheduler().scheduleSyncDelayedTask(saml, this, ticksPerOperation <= 0 ? 1200 : ticksPerOperation);

        boolean enablePassiveFreezeTask = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ENABLE_PASSIVE_FREEZE_TASK);

        if(enablePassiveFreezeTask) {
            SamlPreMobFreezeEvent preMobFreezeEvent = new SamlPreMobFreezeEvent(FreezeReason.PASSIVE);
            Bukkit.getPluginManager().callEvent(preMobFreezeEvent);

            if (!preMobFreezeEvent.isCancelled()) {
                ParameterisedEntityFreezer.freezeToParameters(saml, preMobFreezeEvent.getCurrentFreezeParameters(), FreezeReason.PASSIVE);
            }
        }
    }
}
