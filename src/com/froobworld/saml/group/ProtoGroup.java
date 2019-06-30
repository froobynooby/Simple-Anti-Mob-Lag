package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

import java.util.*;

public class ProtoGroup {
    private Group group;
    private GroupStatusUpdater groupStatusUpdater;
    private LivingEntity centre;
    private List<LivingEntity> entities;

    public ProtoGroup(Group group, LivingEntity centre) {
        this.group = group;
        this.groupStatusUpdater = group.groupStatusUpdater();
        this.centre = centre;
        this.entities = new ArrayList<LivingEntity>();

        entities.add(centre);
        groupStatusUpdater.updateStatus(centre);
    }


    public boolean symmetricAddMemberConditional(ProtoGroup otherProtogroup) {
        if(group.inProtoGroup(centre, otherProtogroup)) {
            addMember(otherProtogroup.centre);
            otherProtogroup.addMember(centre);
            return true;
        }
        return false;
    }

    private void addMember(LivingEntity entity) {
        entities.add(entity);
        groupStatusUpdater.updateStatus(entity);
    }

    public LivingEntity getCentre() {
        return centre;
    }

    public boolean isGroup() {
        return groupStatusUpdater.isGroup();
    }

    public int size() {
        return entities.size();
    }

    public Iterator<LivingEntity> membersIterator() {
        return entities.iterator();
    }

}
