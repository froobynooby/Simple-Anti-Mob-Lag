package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.data.FrozenEntityData;
import com.froobworld.saml.data.UnfreezeParameters;
import com.froobworld.saml.events.SamlMobUnfreezeEvent;
import com.froobworld.saml.utils.EntityFreezer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UnfreezeTask {

    public static void unfreezeToParameters(Saml saml, UnfreezeParameters unfreezeParameters, SamlMobUnfreezeEvent.UnfreezeReason reason) {
        int numberUnfrozen = 0;
        List<LivingEntity> unfrozenList = new ArrayList<>();
        for(World world : unfreezeParameters.getWorlds()) {
            for(LivingEntity entity : world.getLivingEntities()) {
                if(unfreezeParameters.getUnfreezeLimit() != -1 && numberUnfrozen >= unfreezeParameters.getUnfreezeLimit()) {
                    break;
                }
                if(EntityFreezer.isFrozen(entity)) {
                    if (!unfreezeParameters.getIgnorePredicate().test(entity)) {
                        Optional<FrozenEntityData> frozenEntityData = FrozenEntityData.getFrozenEntityData(saml, entity);
                        if(frozenEntityData.isPresent()) {
                            if(unfreezeParameters.includeAllGroups() || frozenEntityData.get().getGroups().stream().anyMatch(unfreezeParameters.getIncludeGroups()::contains)) {
                                if(frozenEntityData.get().getGroups().stream().noneMatch(unfreezeParameters.getExcludeGroups()::contains)) {
                                    if(unfreezeParameters.ignoreRemainingTime() || frozenEntityData.get().getTimeAtFreeze() + frozenEntityData.get().getMinimumFreezeTime() <= System.currentTimeMillis()) {
                                        EntityFreezer.unfreezeEntity(saml, entity);
                                        unfrozenList.add(entity);
                                        numberUnfrozen++;
                                    }
                                }
                            }
                        } else {
                            EntityFreezer.unfreezeEntity(saml, entity);
                            unfrozenList.add(entity);
                            numberUnfrozen++;
                        }
                    }
                }
            }
        }
        if(!unfrozenList.isEmpty()) {
            SamlMobUnfreezeEvent samlMobUnfreezeEvent = new SamlMobUnfreezeEvent(unfrozenList, reason);
            Bukkit.getPluginManager().callEvent(samlMobUnfreezeEvent);
        }
    }
}
