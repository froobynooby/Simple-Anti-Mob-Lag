package com.froobworld.saml.group;

import java.util.HashSet;
import java.util.Set;

public class GroupedObject<T> {
    private T object;
    private Set<Group<T>> groups;

    public GroupedObject(T object) {
        this.object = object;
        this.groups = new HashSet<Group<T>>();
    }


    public T getObject() {
        return object;
    }

    public Set<Group<T>> getGroups() {
        return groups;
    }

}
