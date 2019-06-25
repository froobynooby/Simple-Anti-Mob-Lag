package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

import java.util.*;

public class ProtoGroup {
    private Group group;
    private GroupStatusUpdater groupStatusUpdater;
    private LivingEntity centre;
    private Map<Integer, List<TypedEntity>> typedSpokes;
    private List<TypedEntity> typedEntities;

    public ProtoGroup(Group group, LivingEntity centre) {
        this.group = group;
        this.groupStatusUpdater = group.groupStatusUpdater();
        this.centre = centre;
        this.typedEntities = new ArrayList<TypedEntity>();
        this.typedSpokes = new HashMap<Integer, List<TypedEntity>>();

        TypedEntity centreTypedEntity = new TypedEntity(centre, group.assignTypeId(centre));
        typedEntities.add(centreTypedEntity);
        typedSpokes.put(centreTypedEntity.getTypeId(), new ArrayList<TypedEntity>(Collections.singleton(centreTypedEntity)));
        groupStatusUpdater.updateStatus(centreTypedEntity);
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
        int typeId = group.assignTypeId(entity);
        TypedEntity typedEntity = new TypedEntity(entity, typeId);
        typedSpokes.putIfAbsent(typeId, new ArrayList<TypedEntity>());
        typedSpokes.get(typeId).add(typedEntity);
        typedEntities.add(typedEntity);
        groupStatusUpdater.updateStatus(typedEntity);
    }

    public LivingEntity getCentre() {
        return centre;
    }

    public boolean isGroup() {
        return groupStatusUpdater.isGroup();
    }

    public int size() {
        return typedEntities.size();
    }

    public int size(int typeId) {
        return typedSpokes.getOrDefault(typeId, Collections.emptyList()).size();
    }

    public Iterator<TypedEntity> membersIterator() {
        return typedEntities.iterator();
    }

    public Iterator<TypedEntity> membersIterator(int typeId) {
        return typedSpokes.getOrDefault(typeId, Collections.emptyList()).iterator();
    }

}
