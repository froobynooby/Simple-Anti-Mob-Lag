package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

public class GroupOperations {

    public static Group conjunction(String resultName, Group group1, Group group2) {
        return new Group() {
            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup) {
                return group1.inProtoGroup(entity, protoGroup) && group2.inProtoGroup(entity, protoGroup);
            }

            @Override
            public boolean canBeCentre(LivingEntity entity) {
                return group1.canBeCentre(entity) && group2.canBeCentre(entity);
            }

            @Override
            public GroupStatusUpdater groupStatusUpdater() {
                GroupStatusUpdater groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater() {
                    @Override
                    public void updateStatus(LivingEntity entity) {
                        groupStatusUpdater1.updateStatus(entity);
                        groupStatusUpdater2.updateStatus(entity);
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater1.isGroup() && groupStatusUpdater2.isGroup();
                    }
                };
            }
        };
    }

    public static Group weakConjunction(String resultName, Group group1, Group group2) {
        return new Group() {
            private boolean in1 = false;
            private boolean in2 = false;

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup) {
                in1 = false;
                in2 = false;
                if(group1.inProtoGroup(entity, protoGroup)) {
                    in1 = true;
                }
                if(group2.inProtoGroup(entity, protoGroup)) {
                    in2 = true;
                }
                return in1 || in2;
            }

            @Override
            public boolean canBeCentre(LivingEntity entity) {
                return group1.canBeCentre(entity) && group2.canBeCentre(entity);
            }

            @Override
            public GroupStatusUpdater groupStatusUpdater() {
                GroupStatusUpdater groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater() {
                    @Override
                    public void updateStatus(LivingEntity entity) {
                        if(in1) {
                            groupStatusUpdater1.updateStatus(entity);
                        }
                        if(in2) {
                            groupStatusUpdater2.updateStatus(entity);
                        }
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater1.isGroup() && groupStatusUpdater2.isGroup();
                    }
                };
            }
        };
    }

    public static Group disjunction(String resultName, Group group1, Group group2) {
        return new Group() {
            private boolean in1 = false;
            private boolean in2 = false;

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup) {
                in1 = false;
                in2 = false;
                if(group1.canBeCentre(protoGroup.getCentre()) && group1.inProtoGroup(entity, protoGroup)) {
                    in1 = true;
                }
                if(group2.canBeCentre(protoGroup.getCentre()) && group2.inProtoGroup(entity, protoGroup)) {
                    in2 = true;
                }
                return in1 || in2;
            }

            @Override
            public boolean canBeCentre(LivingEntity entity) {
                return group1.canBeCentre(entity) || group2.canBeCentre(entity);
            }

            @Override
            public GroupStatusUpdater groupStatusUpdater() {
                GroupStatusUpdater groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater() {
                    @Override
                    public void updateStatus(LivingEntity entity) {
                        if(in1) {
                            groupStatusUpdater1.updateStatus(entity);
                        }
                        if(in2) {
                            groupStatusUpdater2.updateStatus(entity);
                        }
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater1.isGroup() || groupStatusUpdater2.isGroup();
                    }
                };
            }
        };
    }

}
