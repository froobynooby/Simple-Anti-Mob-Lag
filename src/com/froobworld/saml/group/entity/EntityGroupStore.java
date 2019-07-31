package com.froobworld.saml.group.entity;

import com.froobworld.saml.Saml;
import com.froobworld.saml.group.entity.groups.DefaultGroup;
import com.froobworld.saml.group.entity.groups.SingularGroup;
import com.froobworld.saml.group.entity.groups.helpers.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class EntityGroupStore {
    private Saml saml;
    private Map<String, EntityGroupParser> parsers;
    private Map<String, EntityGroupParser> helperParsers;
    private CustomGroupParser customGroupParser;

    public EntityGroupStore(Saml saml) {
        this.saml = saml;
        parsers = new HashMap<String, EntityGroupParser>();
        helperParsers = new HashMap<String, EntityGroupParser>();
        customGroupParser = new CustomGroupParser(this);
        addHelpers(saml);
        addDefaults(saml);
    }


    public EntityGroup getGroup(String group, boolean includeHelpers) {
        String[] split = group.split("\\{", 2);

        String groupPart = split[0].toLowerCase();
        JsonObject jsonPart = new JsonParser().parse("{" + (split.length == 1 ? "}" : split[1])).getAsJsonObject();
        boolean conditionalise = false;
        if(groupPart.endsWith("*")) {
            groupPart = groupPart.substring(0, groupPart.length() - 1);
            conditionalise = true;
        }

        if(parsers.containsKey(groupPart)) {
            EntityGroup entityGroup = parsers.get(groupPart).fromJson(jsonPart);

            return conditionalise ? EntityGroup.conditionalise(entityGroup) : entityGroup;
        }
        if(includeHelpers && helperParsers.containsKey(groupPart)) {
            EntityGroup entityGroup = helperParsers.get(groupPart).fromJson(jsonPart);

            return conditionalise ? EntityGroup.conditionalise(entityGroup) : entityGroup;
        }
        if(isNameAcceptable(groupPart) && saml.getCustomGroups() != null && saml.getCustomGroups().keyExists("group." + groupPart)) {
            EntityGroup entityGroup = customGroupParser.parse(groupPart, saml.getCustomGroups().getString("group." + groupPart + ".definition"), jsonPart, saml.getCustomGroups().getSection("group." + groupPart + ".arguments"));

            return conditionalise ? EntityGroup.conditionalise(entityGroup) : entityGroup;
        }
        if(isNameAcceptable(groupPart) && includeHelpers && saml.getCustomGroups() != null && saml.getCustomGroups().keyExists("helper." + groupPart)) {
            EntityGroup entityGroup = customGroupParser.parse(groupPart, saml.getCustomGroups().getString("helper." + groupPart + ".definition"), jsonPart, saml.getCustomGroups().getSection("helper." + groupPart + ".arguments"));

            return conditionalise ? EntityGroup.conditionalise(entityGroup) : entityGroup;
        }

        return null;
    }

    public boolean addParser(String groupName, EntityGroupParser parser) {
        if(!isNameAcceptable(groupName)) {
            return false;
        }
        if(parsers.containsKey(groupName)) {
            return false;
        }
        if(helperParsers.containsKey(groupName)) {
            return false;
        }

        parsers.put(groupName, parser);
        return true;
    }

    public boolean addHelperParser(String groupName, EntityGroupParser parser) {
        if(!isNameAcceptable(groupName)) {
            return false;
        }
        if(parsers.containsKey(groupName)) {
            return false;
        }
        if(helperParsers.containsKey(groupName)) {
            return false;
        }

        helperParsers.put(groupName, parser);
        return true;
    }

    private void addHelpers(Saml saml) {
        helperParsers.put("default_distance", DistanceGroup.parser());
        helperParsers.put("default_total_count", TotalCountGroup.parser());
        helperParsers.put("default_type_count", TypeCountGroup.parser());
        helperParsers.put("default_specific_type", SpecificTypeGroup.parser());
        helperParsers.put("default_same_type", SameTypeGroup.parser());
        helperParsers.put("default_chunk", ChunkGroup.parser());
        helperParsers.put("default_specific_center_type", SpecificCentreTypeGroup.parser());
    }

    private void addDefaults(Saml saml) {
        parsers.put("default_group", DefaultGroup.parser(saml));
        parsers.put("default_singular", SingularGroup.parser());
    }

    private boolean isNameAcceptable(String name) {
        return name.length() != 0 && !name.startsWith("default_") && name.matches("[_0-9a-z]*");
    }
}
