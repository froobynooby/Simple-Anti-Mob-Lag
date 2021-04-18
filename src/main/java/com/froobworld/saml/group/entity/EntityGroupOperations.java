package com.froobworld.saml.group.entity;

import com.froobworld.saml.group.GroupOperations;

import java.util.function.BiFunction;

public class EntityGroupOperations {

    public static EntityGroup conjunction(String resultName, EntityGroup entityGroup1, EntityGroup entityGroup2) {
        BiFunction<EntityGroup, EntityGroup, EntityGroup> operation = EntityGroup.transformGroupOperation( (u,v) -> GroupOperations.conjunction(resultName, u, v));
        return operation.apply(entityGroup1, entityGroup2);
    }

    public static EntityGroup weakConjunction(String resultName, EntityGroup entityGroup1, EntityGroup entityGroup2) {
        BiFunction<EntityGroup, EntityGroup, EntityGroup> operation = EntityGroup.transformGroupOperation( (u,v) -> GroupOperations.weakConjunction(resultName, u, v));
        return operation.apply(entityGroup1, entityGroup2);
    }

    public static EntityGroup disjunction(String resultName, EntityGroup entityGroup1, EntityGroup entityGroup2) {
        BiFunction<EntityGroup, EntityGroup, EntityGroup> operation = EntityGroup.transformGroupOperation( (u,v) -> GroupOperations.disjunction(resultName, u, v));
        return operation.apply(entityGroup1, entityGroup2);
    }
}
