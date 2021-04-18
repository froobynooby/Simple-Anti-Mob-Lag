package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.data.FreezeReason;
import com.froobworld.saml.data.UnfreezeReason;
import com.froobworld.saml.events.SamlPreMobFreezeEvent;
import com.froobworld.saml.events.SamlPreMobUnfreezeEvent;
import com.froobworld.saml.utils.ParameterisedEntityFreezer;
import org.bukkit.Bukkit;

public class TpsFreezeTask implements Runnable {
    private Saml saml;

    public TpsFreezeTask(Saml saml) {
        this.saml = saml;
        Bukkit.getScheduler().runTask(saml, this);
    }


    @Override
    public void run() {
        long ticksPerOperation = saml.getSamlConfig().getLong(ConfigKeys.CNF_TICKS_PER_TPS_FREEZE_TASK);
        Bukkit.getScheduler().scheduleSyncDelayedTask(saml, this, ticksPerOperation <= 0 ? 1200 : ticksPerOperation);

        boolean enableTpsFreezeTask = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ENABLE_TPS_FREEZE_TASK);
        double currentTps = saml.getTpsSupplier().getTps();
        double currentTpsStandardDeviation = saml.getTpsSupplier().getTpsStandardDeviation();
        double unfreezingThresholdTps = saml.getSamlConfig().getDouble(ConfigKeys.CNF_TPS_UNFREEZING_THRESHOLD);
        double freezingThresholdTps = saml.getSamlConfig().getDouble(ConfigKeys.CNF_TPS_FREEZING_THRESHOLD);
        double unfreezingConfidenceTpsRange = saml.getSamlConfig().getDouble(ConfigKeys.CNF_TPS_UNFREEZE_CONFIDENCE_RANGE);

        if(currentTps - unfreezingConfidenceTpsRange * currentTpsStandardDeviation >= unfreezingThresholdTps) {
            SamlPreMobUnfreezeEvent samlPreMobUnfreezeEvent = new SamlPreMobUnfreezeEvent(UnfreezeReason.TPS);
            Bukkit.getPluginManager().callEvent(samlPreMobUnfreezeEvent);
            if(!samlPreMobUnfreezeEvent.isCancelled()) {
                ParameterisedEntityFreezer.unfreezeToParameters(saml, samlPreMobUnfreezeEvent.getCurrentUnfreezeParameters(), UnfreezeReason.TPS);
            }
        } else if(enableTpsFreezeTask && currentTps < freezingThresholdTps) {
            SamlPreMobFreezeEvent samlPreMobFreezeEvent = new SamlPreMobFreezeEvent(FreezeReason.TPS);
            samlPreMobFreezeEvent.getFreezeParametersBuilder().setCurrentTps(currentTps);
            samlPreMobFreezeEvent.getFreezeParametersBuilder().setExpectedTps(freezingThresholdTps);
            Bukkit.getPluginManager().callEvent(samlPreMobFreezeEvent);
            if(!samlPreMobFreezeEvent.isCancelled()) {
                ParameterisedEntityFreezer.freezeToParameters(saml, samlPreMobFreezeEvent.getCurrentFreezeParameters(), FreezeReason.TPS);
            }
        }
    }
}
