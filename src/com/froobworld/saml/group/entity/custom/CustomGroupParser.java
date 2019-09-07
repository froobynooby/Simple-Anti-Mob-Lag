package com.froobworld.saml.group.entity.custom;

import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupOperations;
import com.froobworld.saml.group.entity.EntityGroupStore;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CustomGroupParser {
    private EntityGroupStore entityGroupStore;
    private Map<Character, BiFunction<EntityGroup, EntityGroup, EntityGroup>> groupOperations;
    private Map<Character, GroupOperation> operations;
    private Map<Character, GroupModifier> modifiers;

    public CustomGroupParser(EntityGroupStore entityGroupStore) {
        this.entityGroupStore = entityGroupStore;
        groupOperations = new HashMap<>();
        operations = new HashMap<>();
        modifiers = new HashMap<>();
        addGroupOperations();
        addGroupModifiers();
    }


    public EntityGroup parse(String resultName, String toParse, JsonObject jsonPart, Map<String, Object> arguments) throws ParseException {
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

        GroupEvaluator groupEvaluator = new GroupEvaluator();
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int cursorPosition = 0;
            while(line.length() > 0) {
                int remove = 0;
                for(char c : line.toCharArray()) {
                    if(c == ' ') {
                        remove++;
                        continue;
                    }
                    if(c == '(') {
                        groupEvaluator.openParen();
                        remove++;
                        continue;
                    }
                    if(c == ')') {
                        groupEvaluator.closeParen();
                        remove++;
                        continue;
                    }
                    if(modifiers.containsKey(c)) {
                        GroupModifier modifier = modifiers.get(c);
                        groupEvaluator.passModifier(modifier.modifier, modifier.actsOn, modifier.precedence);
                        remove++;
                        continue;
                    }
                    if(operations.containsKey(c)) {
                        GroupOperation operation = operations.get(c);
                        groupEvaluator.passOperation(operation.operation, operation.associativity, operation.precedence);
                        remove++;
                        continue;
                    }
                    break;
                }
                if(remove >= line.length()) {
                    line = "";
                } else {
                    line = line.substring(remove);
                }
                cursorPosition += remove;
                if(line.length() > 0) {
                    String possibleGroupString = getFirstPossibleGroupSubstring(line);
                    if(possibleGroupString == null) {
                        throw new ParseException("Problem parsing line " + i +": '" + lines[i] + "' at column " + cursorPosition, -1);
                    }
                    groupEvaluator.passGroup(entityGroupStore.getGroup(possibleGroupString, true));
                    if(possibleGroupString.length() >= line.length()) {
                        line = "";
                    } else {
                        line = line.substring(possibleGroupString.length());
                    }
                    cursorPosition += possibleGroupString.length();
                }
            }
        }
        return groupEvaluator.evaluate();
    }

    private void addGroupOperations() {
        groupOperations.put('&', (u, v) -> EntityGroupOperations.conjunction(null, u, v));
        groupOperations.put('^', (u, v) -> EntityGroupOperations.weakConjunction(null, u, v));
        groupOperations.put('|', (u, v) -> EntityGroupOperations.disjunction(null, u, v));

        operations.put('&', new GroupOperation((u, v) -> EntityGroupOperations.conjunction(null, u, v), 2, GroupEvaluator.Associativity.LEFT));
        operations.put('^', new GroupOperation((u, v) -> EntityGroupOperations.weakConjunction(null, u, v), 3, GroupEvaluator.Associativity.LEFT));
        operations.put('|', new GroupOperation((u, v) -> EntityGroupOperations.disjunction(null, u, v), 4, GroupEvaluator.Associativity.LEFT));
    }

    private void addGroupModifiers() {
        modifiers.put('*', new GroupModifier(EntityGroup::conditionalise, 1, GroupEvaluator.Associativity.RIGHT));
        modifiers.put('!', new GroupModifier(EntityGroup::negate, 1, GroupEvaluator.Associativity.RIGHT));
    }

    private static String getFirstPossibleGroupSubstring(String line) {
        StringBuilder subStringBuilder = new StringBuilder();
        int openBraces = 0;
        boolean startedJsonPart = false;
        boolean inQuotes = false;
        boolean escapeNext = false;

        for(char c : line.toCharArray()) {
            if(!startedJsonPart) {
                if(Character.isLetter(c) && Character.isLowerCase(c) || c == '_' || Character.isDigit(c)) {
                    subStringBuilder.append(c);
                } else if(c == '{') {
                    subStringBuilder.append(c);
                    startedJsonPart = true;
                    openBraces++;
                } else {
                    return subStringBuilder.length() == 0 ? null : subStringBuilder.toString();
                }
                if(subStringBuilder.length() == 0) {
                    return subStringBuilder.toString();
                }
            } else {
                if(subStringBuilder.length() == 0) {
                    return null;
                }
                if(c == '\\') {
                    subStringBuilder.append(c);
                    escapeNext = true;
                    continue;
                } else if(c == '{') {
                    subStringBuilder.append(c);
                    if(!(escapeNext || inQuotes)) {
                        openBraces++;
                    }
                } else if(c == '}') {
                    subStringBuilder.append(c);
                    if(!(escapeNext || inQuotes)) {
                        openBraces--;
                    }
                } else if(c == '"') {
                    subStringBuilder.append(c);
                    if(!escapeNext) {
                        inQuotes = !inQuotes;
                    }
                } else {
                    subStringBuilder.append(c);
                }

                if(openBraces == 0) {
                    return subStringBuilder.toString();
                }
                escapeNext = false;
            }
        }

        return subStringBuilder.length() == 0 || openBraces > 0 ? null : subStringBuilder.toString();
    }

    private static class GroupOperation {
        private BiFunction<EntityGroup, EntityGroup, EntityGroup> operation;
        private int precedence;
        private GroupEvaluator.Associativity associativity;

        public GroupOperation(BiFunction<EntityGroup, EntityGroup, EntityGroup> operation, int precedence, GroupEvaluator.Associativity associativity) {
            this.operation = operation;
            this.precedence = precedence;
            this.associativity = associativity;
        }
    }

    private static class GroupModifier {
        private Function<EntityGroup, EntityGroup> modifier;
        private int precedence;
        private GroupEvaluator.Associativity actsOn;

        public GroupModifier(Function<EntityGroup, EntityGroup> modifier, int precedence, GroupEvaluator.Associativity actsOn) {
            this.modifier = modifier;
            this.precedence = precedence;
            this.actsOn = actsOn;
        }
    }
}
