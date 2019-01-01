package Lex.imagemlt.nfa;

import java.util.HashSet;

public class Edge {
    final public static int EPSILON=-1;
    final public static int NOT_EPSILON=1;

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = new Character(character);
    }

    private Character character=null;

    public State getEdegeFrom() {
        return edegeFrom;
    }

    private State edegeFrom;

    public State getEdegeTo() {
        return edegeTo;
    }

    private State edegeTo;

    public int getEdge() {
        return edge;
    }

    public void setEdge(int edge) {
        this.edge = edge;
    }

    private int edge;
    public Edge(State from, State to){
        this.edegeFrom=from;
        this.edegeTo=to;
    }
    public Edge(){
        this.edegeFrom=this.edegeTo=null;
    }
    public void setEdegeFrom(State from){
        this.edegeFrom=from;
    }
    public void setEdegeTo(State to){
        this.edegeTo=to;
    }

}
