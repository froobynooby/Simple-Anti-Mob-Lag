package com.froobworld.saml.group;

import java.util.HashSet;
import java.util.Set;

public class GroupedObject<T> {
    private T object;
    private Set<Group> groups;

    public GroupedObject(T object) {
        this(object, new HashSet<Group>());
    }

    public GroupedObject(T object, Set<Group> groups) {
        this.object = object;
        this.groups = groups;
    }


    public T getObject() {
        return object;
    }

    public Set<Group> getGroups() {
        return groups;
    }

}
