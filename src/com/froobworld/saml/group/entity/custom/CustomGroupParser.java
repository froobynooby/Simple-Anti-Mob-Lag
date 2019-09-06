package com.froobworld.saml.group.entity.custom;

import com.froobworld.saml.group.GroupMetadata;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupOperations;
import com.froobworld.saml.group.entity.EntityGroupStore;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class CustomGroupParser {
    private EntityGroupStore entityGroupStore;
    private Map<Character, BiFunction<EntityGroup, EntityGroup, EntityGroup>> groupOperations;

    public CustomGroupParser(EntityGroupStore entityGroupStore) {
        this.entityGroupStore = entityGroupStore;
        groupOperations = new HashMap<>();
        addGroupOperations();
    }


    public EntityGroup parse(String resultName, String toParse, JsonObject jsonPart, Map<String, Object> arguments) {
        for(String key : arguments.keySet()) {
            if(!key.contains(".")) {
                String replacement;
                if(jsonPart.has(key)) {
                    JsonElement argumentValue = jsonPart.get(key);
                    replacement = argumentValue.isJsonArray() ? argumentValue.toString() : argumentValue.getAsString();
                } else {
                    if(arguments.containsKey(key + ".required") && arguments.get(key + ".required").equals(true)) {
                        throw new IllegalArgumentException("The argument '" + key + "' is required for custom group '" + resultName + "'");
                    }
                    replacement = arguments.containsKey(key + ".default") ? arguments.get(key + ".default").toString() : "null";
                }

                toParse = toParse.replace("%" + key + "%", replacement);
            }
        }

        String[] lines = toParse.split("\\r?\\n");

        Evaluator evaluator = new Evaluator(resultName, entityGroupStore.getGroup(lines[0], true));

        for(int i = 1; i < lines.length; i++) {
            String[] split = lines[i].split(" ", 2);

            String operationsPart = split[0];
            String groupPart = split.length == 1 ? null : split[1];

            for(char c : operationsPart.toCharArray()) {
                if(c == '(') {
                    evaluator.openParen();
                    continue;
                }
                if(c == ')') {
                    evaluator.closeParen();
                    continue;
                }
                if(groupOperations.containsKey(c)) {
                    evaluator.nextOperation(groupOperations.get(c));
                } else {
                    throw new IllegalArgumentException("Unknown operation '" + c + "'");
                }
            }
            if(groupPart != null) {
                evaluator.applyGroup(entityGroupStore.getGroup(groupPart, true));
            }
        }

        return evaluator.evaluate();
    }

    private void addGroupOperations() {
        groupOperations.put('&', (u, v) -> EntityGroupOperations.conjunction(null, u, v));
        groupOperations.put('^', (u, v) -> EntityGroupOperations.weakConjunction(null, u, v));
        groupOperations.put('|', (u, v) -> EntityGroupOperations.disjunction(null, u, v));
    }

    private static class Evaluator {
        private EntityGroup tail;
        private String resultName;
        private BiFunction<EntityGroup, EntityGroup, EntityGroup> nextOperation;
        private Evaluator subEvaluator;

        public Evaluator(String resultName, EntityGroup start) {
            this.tail = start;
            this.resultName = resultName;
        }

        private Evaluator() {}


        public void openParen() {
            if(subEvaluator != null) {
                subEvaluator.openParen();
                return;
            }
            if(nextOperation == null) {
                throw new IllegalStateException("Tried to open parenthesis without a preceding operation");
            }
            subEvaluator = new Evaluator();
        }

        public void closeParen() {
            if(subEvaluator != null && subEvaluator.subEvaluator != null) {
                subEvaluator.closeParen();
                return;
            }
            if(subEvaluator == null) {
                throw new IllegalStateException("Tried to close parenthesis without a matching open parenthesis");
            }
            if(subEvaluator.nextOperation != null) {
                throw new IllegalStateException("Tried to close parenthesis containing an unapplied operation");
            }
            EntityGroup toApply = subEvaluator.evaluate();
            subEvaluator = null;
            applyGroup(toApply);
        }

        public void nextOperation(BiFunction<EntityGroup, EntityGroup, EntityGroup> operation) {
            if(subEvaluator != null) {
                subEvaluator.nextOperation(operation);
                return;
            }
            if(tail == null) {
                throw new IllegalStateException("Tried to apply an operation to nothing");
            }
            nextOperation = operation;
        }

        public void applyGroup(EntityGroup entityGroup) {
            if(subEvaluator != null) {
                subEvaluator.applyGroup(entityGroup);
                return;
            }
            if(tail == null) {
                tail = entityGroup;
                return;
            }
            if(nextOperation == null) {
                throw new IllegalStateException("Tried to apply a group without an operation");
            }
            tail = nextOperation.apply(tail, entityGroup);
            nextOperation = null;
        }

        public EntityGroup evaluate() {
            if(subEvaluator != null) {
                throw new IllegalStateException("Tried to evaluate with unclosed parentheses");
            }
            if(nextOperation != null) {
                throw new IllegalStateException("Tried to evaluate with an unapplied operation");
            }

            return new EntityGroup() {
                @Override
                public Map<String, Object> getSnapshotProperties(LivingEntity entity) {
                    return tail.getSnapshotProperties(entity);
                }

                @Override
                public String getName() {
                    return resultName;
                }

                @Override
                public GroupMetadata getGroupMetadata() {
                    return tail.getGroupMetadata();
                }

                @Override
                public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
                    return tail.getMembershipEligibility(candidate);
                }

                @Override
                public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
                    return tail.groupStatusUpdater();
                }

                @Override
                public void scaleToTps(double tps, double expectedTps) {
                    tail.scaleToTps(tps, expectedTps);
                }
            };
        }

    }
}
