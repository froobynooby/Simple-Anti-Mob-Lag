package com.froobworld.saml.group.entity;

import com.google.gson.JsonObject;

public interface EntityGroupParser<G extends EntityGroup> {
    public G fromJson(JsonObject jsonObject);
}
