package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.events.SamlMobFreezeEvent;
import com.froobworld.saml.events.SamlMobUnfreezeEvent;
import com.froobworld.saml.events.SamlPreMobFreezeEvent;
import com.froobworld.saml.events.SamlPreMobUnfreezeEvent;
import org.bukkit.Bukkit;

public class MainLoopTask implements Runnable {
    private Saml saml;

    public MainLoopTask(Saml saml) {
        this.saml = saml;
        Bukkit.getScheduler().runTask(saml, this);
    }


    @Override
    public void run() {
        long ticksPerOperation = saml.getSamlConfig().getLong("ticks-per-operation");
        Bukkit.getScheduler().scheduleSyncDelayedTask(saml, this, ticksPerOperation <= 0 ? 1200 : ticksPerOperation);

        double currentTps = saml.getTpsSupplier().get();
        double unfreezingThresholdTps = saml.getSamlConfig().getDouble("tps-unfreezing-threshold");
        double freezingThresholdTps = saml.getSamlConfig().getDouble("tps-freezing-threshold");

        if(currentTps >= unfreezingThresholdTps) {
            SamlPreMobUnfreezeEvent samlPreMobUnfreezeEvent = new SamlPreMobUnfreezeEvent(SamlMobUnfreezeEvent.UnfreezeReason.MAIN_TASK);
            Bukkit.getPluginManager().callEvent(samlPreMobUnfreezeEvent);
            if(!samlPreMobUnfreezeEvent.isCancelled()) {
                UnfreezeTask.unfreezeToParameters(saml, samlPreMobUnfreezeEvent.getCurrentUnfreezeParameters(), SamlMobUnfreezeEvent.UnfreezeReason.MAIN_TASK);
            }
        } else if(currentTps < freezingThresholdTps) {
            SamlPreMobFreezeEvent samlPreMobFreezeEvent = new SamlPreMobFreezeEvent(SamlMobFreezeEvent.FreezeReason.MAIN_TASK);
            samlPreMobFreezeEvent.getFreezeParametersBuilder().setCurrentTps(currentTps);
            samlPreMobFreezeEvent.getFreezeParametersBuilder().setExpectedTps(freezingThresholdTps);
            Bukkit.getPluginManager().callEvent(samlPreMobFreezeEvent);
            if(!samlPreMobFreezeEvent.isCancelled()) {
                FreezeTask.freezeToParameters(saml, samlPreMobFreezeEvent.getCurrentFreezeParameters(), SamlMobFreezeEvent.FreezeReason.MAIN_TASK);
            }
        }
    }
}
