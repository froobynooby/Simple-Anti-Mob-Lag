package com.froobworld.saml.utils;

import java.util.Set;

public class SetUtils {

    public static boolean disjoint(Set set1, Set set2) {
        if(set1.size() < set2.size()) {
            for(Object o : set1) {
                if(set2.contains(o)) {
                    return false;
                }
            }
        } else {
            for(Object o : set2) {
                if(set1.contains(o)) {
                    return false;
                }
            }
        }

        return true;
    }

}
