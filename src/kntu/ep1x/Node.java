package kntu.ep1x;

import java.util.Arrays;

enum Type {
    BLOCK,
    CONDITION,
    COMMAND,
    EPSILON,
    IF,
    ELSEIF,
    ELSE
}

public class Node {
    private boolean isLeaf = false;
    private String[] body;
    private Type type;
    public Node(boolean bool,Type type){
        this.body = null;
        isLeaf = bool;
        this.type = type;
    }
    public Node(String[] body,boolean bool,Type type){
        this.body = body;
        isLeaf = bool;
        this.type = type;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public Type getType() {
        return type;
    }

    public String[] getBody() {
        return body;
    }

    public String getString(){
        if(body == null) return "␣";
        return body[0];
    }

    @Override
    public String toString() {
        return isLeaf ? getString() : "<"+type.toString()+">";
    }
}
