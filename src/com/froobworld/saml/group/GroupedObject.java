package com.froobworld.saml.group;

import java.util.HashSet;
import java.util.Set;

public class GroupedObject<T> {
    private T object;
    private Set<Group<? super T>> groups;

    public GroupedObject(T object) {
        this(object, new HashSet<Group<? super T>>());
    }

    public GroupedObject(T object, Set<Group<? super T>> groups) {
        this.object = object;
        this.groups = groups;
    }


    public T getObject() {
        return object;
    }

    public Set<Group<? super T>> getGroups() {
        return groups;
    }

}
