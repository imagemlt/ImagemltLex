package Lex.imagemlt.nfa;

import java.util.Vector;

public class State {
    static public enum STATUS{
        START,
        END,
        NORMAL,
        BOTH
    }
    public STATUS getStatus() {
        return status;
    }

    private STATUS status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    public Vector<Edge> getEdges() {
        return edges;
    }

    private Vector<Edge> edges;

    public void setVisited(boolean visited) {
        this.visited =visited;
    }
    public boolean getVisted(){
        return this.visited;
    }
    private boolean visited=false;

    public State getCopyState() {
        return copyState;
    }

    public void setCopyState(State copyState) {
        this.copyState = copyState;
    }

    private State copyState=null;

    public State(){
        this.edges =new Vector<Edge>();
    }
    public State(State state){
        this.status=state.status;
        this.edges=new Vector<Edge>();
    }
    public void setStatus(STATUS status){
        this.status=status;
    }


}