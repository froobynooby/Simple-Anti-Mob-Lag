package com.froobworld.saml.group.entity.custom;

import com.froobworld.saml.group.entity.EntityGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GroupEvaluator {
    private List<Node> nodes;

    private GroupEvaluator(List<Node> nodes) {
        this.nodes = nodes;
    }

    public GroupEvaluator() {
        this(new ArrayList<>());
    }


    public EntityGroup evaluate() {
        List<Node> workingNodes = new ArrayList<>(nodes);
        ListIterator<Node> iterator = workingNodes.listIterator();

        int openParens = 0;
        List<Node> parenthicalNodes = new ArrayList<>();
        Associativity nextAssociativity = null;
        int nextPrecendence = Integer.MIN_VALUE;

        while(iterator.hasNext()) {
            Node nextNode = iterator.next();
            if(nextNode instanceof ParenNode) {
                boolean wasOpen = openParens > 0;
                if(((ParenNode) nextNode).isOpen()) {
                    openParens++;
                } else {
                    openParens--;
                }

                iterator.remove();
                if(wasOpen) {
                    if(openParens == 0) {
                        iterator.add(new GroupNode(new GroupEvaluator(parenthicalNodes).evaluate()));
                        parenthicalNodes.clear();
                    } else {
                        parenthicalNodes.add(nextNode);
                    }
                }
                if(openParens < 0) {
                    throw new IllegalStateException("Too many close parentheses");
                }
            } else {
                if(openParens > 0) {
                    parenthicalNodes.add(nextNode);
                    iterator.remove();
                } else if(nextNode.precedence() < nextPrecendence) {
                    nextPrecendence = nextNode.precedence();
                    nextAssociativity = nextNode.associativity();
                }
            }
        }
        if(openParens != 0) {
            throw new IllegalStateException("Mismatched parentheses");
        }

        if(workingNodes.size() == 1) {
            Node node = workingNodes.get(0);
            if(node instanceof GroupNode) {
                return ((GroupNode) node).entityGroup;
            } else {
                throw new IllegalStateException("Finished with a single non-group node");
            }
        }

        Node replacement = null;
        List<Integer> affectedIndices = new ArrayList<>();
        for(int i = 0; i < workingNodes.size(); i++) {
            Node node = workingNodes.get(i);
            if(node.precedence() == nextPrecendence && node.associativity() == nextAssociativity) {
                if(node instanceof OperationNode) {
                    Node left = workingNodes.get(i - 1);
                    Node right = workingNodes.get(i + 1);
                    affectedIndices.add(i - 1);
                    affectedIndices.add(i);
                    affectedIndices.add(i + 1);
                    if(left instanceof GroupNode && right instanceof GroupNode) {
                        replacement = new GroupNode(((OperationNode) node).getOperation().apply(((GroupNode) left).entityGroup, ((GroupNode) right).entityGroup));
                    }
                    break;
                }
                if(node instanceof ModifierNode) {
                    Node actsOn = workingNodes.get(node.associativity() == Associativity.LEFT ? (i - 1) : (i + 1));
                    affectedIndices.add(node.associativity() == Associativity.LEFT ? (i - 1) : (i + 1));
                    affectedIndices.add(i);
                    if(actsOn instanceof GroupNode) {
                        replacement = new GroupNode(((ModifierNode) node).getModifier().apply(((GroupNode) actsOn).entityGroup));
                    } else {
                        throw new IllegalStateException("Tried to apply modifier to a non-group node");
                    }
                    break;
                }
            }
        }
        if(replacement != null) {
            affectedIndices.forEach( i -> workingNodes.remove((int) i) );
            workingNodes.add(Collections.min(affectedIndices), replacement);
        } else {
            throw new IllegalStateException("Nothing left to evaluate, but multiple nodes are left");
        }

        return new GroupEvaluator(workingNodes).evaluate();
    }

    public void passGroup(EntityGroup entityGroup) {
        nodes.add(new GroupNode(entityGroup));
    }

    public void passOperation(BiFunction<EntityGroup, EntityGroup, EntityGroup> operation, Associativity associativity, int precedence) {
        nodes.add(new OperationNode(operation, associativity, precedence));
    }

    public void passModifier(Function<EntityGroup, EntityGroup> modifier, Associativity actsOn, int precedence) {
        nodes.add(new ModifierNode(modifier, actsOn, precedence));
    }

    public void openParen() {
        nodes.add(new ParenNode(true));
    }

    public void closeParen() {
        nodes.add(new ParenNode(false));
    }

    private static interface Node {
        public Associativity associativity();
        public int precedence();
    }

    private static class ParenNode implements Node {
        private boolean open;

        public ParenNode(boolean open) {
            this.open = open;
        }


        public boolean isOpen() {
            return open;
        }

        @Override
        public Associativity associativity() {
            return Associativity.LEFT;
        }

        @Override
        public int precedence() {
            return Integer.MIN_VALUE;
        }
    }

    private static class GroupNode implements Node {
        private EntityGroup entityGroup;

        public GroupNode(EntityGroup entityGroup) {
            this.entityGroup = entityGroup;
        }


        public EntityGroup getGroup() {
            return entityGroup;
        }

        @Override
        public Associativity associativity() {
            return null;
        }

        @Override
        public int precedence() {
            return Integer.MAX_VALUE;
        }
    }

    private static class OperationNode implements Node {
        private BiFunction<EntityGroup, EntityGroup, EntityGroup> operation;
        private Associativity associativity;
        private int precedence;

        public OperationNode(BiFunction<EntityGroup, EntityGroup, EntityGroup> operation, Associativity associativity, int precedence) {
            this.operation = operation;
            this.associativity = associativity;
            this.precedence = precedence;
        }


        public BiFunction<EntityGroup, EntityGroup, EntityGroup> getOperation() {
            return operation;
        }

        @Override
        public Associativity associativity() {
            return associativity;
        }

        @Override
        public int precedence() {
            return precedence;
        }
    }

    private static class ModifierNode implements Node {
        private Function<EntityGroup, EntityGroup> modifier;
        private Associativity actsOn;
        private int precedence;

        public ModifierNode(Function<EntityGroup, EntityGroup> modifier, Associativity actsOn, int precedence) {
            this.modifier = modifier;
            this.actsOn = actsOn;
            this.precedence = precedence;
        }


        public Function<EntityGroup, EntityGroup> getModifier() {
            return modifier;
        }

        @Override
        public Associativity associativity() {
            return actsOn;
        }

        @Override
        public int precedence() {
            return precedence;
        }
    }

    public enum Associativity {
        LEFT,
        RIGHT
    }
}
