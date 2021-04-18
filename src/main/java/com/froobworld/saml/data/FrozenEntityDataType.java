package com.froobworld.saml.data;

import com.google.gson.JsonParser;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class FrozenEntityDataType implements PersistentDataType<String, FrozenEntityData> {

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<FrozenEntityData> getComplexType() {
        return FrozenEntityData.class;
    }

    @Override
    public String toPrimitive(FrozenEntityData freezeData, PersistentDataAdapterContext persistentDataAdapterContext) {
        return freezeData.toJsonObject().toString();
    }

    @Override
    public FrozenEntityData fromPrimitive(String string, PersistentDataAdapterContext persistentDataAdapterContext) {
        return FrozenEntityData.fromJsonObject(new JsonParser().parse(string).getAsJsonObject());
    }
}
