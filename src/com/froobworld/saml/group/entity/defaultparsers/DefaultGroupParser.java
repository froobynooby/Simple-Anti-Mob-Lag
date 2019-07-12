package com.froobworld.saml.group.entity.defaultparsers;

import com.froobworld.saml.Saml;
import com.froobworld.saml.group.GroupOperations;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.helpergroups.DistanceGroup;
import com.froobworld.saml.group.entity.helpergroups.TotalCountGroup;
import com.google.gson.JsonObject;

public class DefaultGroupParser implements EntityGroupParser {
    private Saml saml;

    public DefaultGroupParser(Saml saml) {
        this.saml = saml;
    }

    @Override
    public EntityGroup fromJson(JsonObject jsonObject) {
        DistanceGroup.Builder distanceGroupBuilder = new DistanceGroup.Builder(saml.getSamlConfig().getDouble("group-maximum-radius"));
        TotalCountGroup.Builder totalCountGroupBuilder = new TotalCountGroup.Builder(saml.getSamlConfig().getDouble("group-minimum-size"));
        if(saml.getSamlConfig().getBoolean("use-smart-scaling")) {
            distanceGroupBuilder.setScaleToTps(true);
            totalCountGroupBuilder.setScaleToTps(true);
        }

        return EntityGroup.transformGroupOperation( (u, v) -> GroupOperations.conjunction("default_group", u, v) ).apply(distanceGroupBuilder.build(), totalCountGroupBuilder.build());
    }
}
