package Lex.imagemlt.nfa;

import javafx.util.Pair;

import java.util.*;

public class NFA {
    public static HashSet<Character> letters=new HashSet<Character>(
            Arrays.asList(
            new Character[]{'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x',
            'y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'}
            )
    );
    public static HashSet<Character> digits=new HashSet<Character>(
            Arrays.asList(
                    new Character[]{
                            '1','2','3','4','5','6','7','8','9','0'
                    }
            )
    );
    public State getBeginState() {
        return beginState;
    }

    public void setBeginState(State beginState) {
        this.beginState = beginState;
    }

    public State getEndState() {
        return endState;
    }

    public void setEndState(State endState) {
        this.endState = endState;
    }

    public State beginState;

    public Vector<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Vector<Edge> edges) {
        this.edges = edges;
    }

    public State endState;

    public Vector<State> getStates() {
        return states;
    }

    public void setStates(Vector<State> states) {
        this.states = states;
    }

    private Vector<State> states;
    private Vector<Edge> edges;
    public NFA(State beginState,State endState){
        this.beginState=beginState;
        this.endState=endState;
        beginState.setStatus(State.STATUS.START);
        endState.setStatus(State.STATUS.END);
        this.states=new Vector<>();
        this.edges=new Vector<>();
        this.states.add(beginState);
        this.states.add(endState);
        //this.edges.addAll(this.beginState.getEdges());
        //this.edges.addAll(this.endState.getEdges());

    }
    public NFA(NFA nfa){
       HashMap<State,Integer> map=new HashMap<>();
       this.states=new Vector<State>();
       this.edges=new Vector<Edge>();
       int i=0;
       for(State state:nfa.getStates()){
           State tmp=new State();
           tmp.setId(state.getId());
           tmp.setStatus(state.getStatus());
           map.put(state,i);
           this.states.add(tmp);
           i++;
       }
       for(Edge edge:nfa.getEdges()){
           Edge tmp=new Edge(this.states.get(map.get(edge.getEdegeFrom())),this.states.get(map.get(edge.getEdegeTo())));
           if(edge.getCharacter()!=null)
           tmp.setCharacter(edge.getCharacter());
           tmp.setEdge(edge.getEdge());
           this.states.get(map.get(edge.getEdegeFrom())).getEdges().add(tmp);
           this.edges.add(tmp);
       }
       this.beginState=this.states.get(map.get(nfa.beginState));
       this.endState=this.states.get(map.get(nfa.endState));

    }

    @org.jetbrains.annotations.Nullable
    public static NFA Reg2NFA(String regExp){
        regExp="("+regExp+")";
        int i=0;
        //int statusid=0;
        int statesid=0;
        Stack<Pair<Integer,Integer>> xor_stack=new Stack<>();
        Stack<Integer> blur_stack=new Stack<>();
        Vector<NFA> basenfas=new Vector<>();
        int blur_level=0;
        boolean slashState=false;
        boolean squareBlurState=false;
        while(true){
            char c=regExp.charAt(i);
            //System.out.printf("%d,%c\n",i,c);
            if(slashState){
                switch(c){
                    case 'd':{
                        Vector<NFA> digit_nfas=new Vector<>();
                        for(Character ch:digits) {
                            State beginState = new State();
                            State endState = new State();
                            Edge edge = new Edge(beginState, endState);
                            edge.setCharacter(ch);
                            beginState.getEdges().add(edge);
                            NFA nfa = new NFA(beginState, endState);
                            nfa.getEdges().add(edge);
                            //basenfas.add(nfa);
                            digit_nfas.add(nfa);
                        }
                        NFA nfa=digit_nfas.get(0);
                        for(int j=1;j<digit_nfas.size();j++){
                            nfa.or(digit_nfas.get(j));
                        }
                        basenfas.add(nfa);
                        slashState = false;
                        break;
                    }
                    case '(':{

                    }
                    case ')':{

                    }
                    case '[':{

                    }
                    case ']':{

                    }
                    case '*':{

                    }
                    case '+':{

                    }

                    case '\\':{

                    }
                    case '|':{
                        State beginState=new State();
                        State endState=new State();
                        Edge edge=new Edge(beginState,endState);
                        edge.setCharacter(c);
                        beginState.getEdges().add(edge);
                        NFA nfa=new NFA(beginState,endState);
                        nfa.getEdges().add(edge);
                        basenfas.add(nfa);
                        slashState=false;
                        break;
                    }
                    default:{
                        System.out.println("invalid use of \\");
                        return null;
                    }
                }
            }
            else {
                if(squareBlurState){
                    /*if(!digits.contains(c) || letters.contains(c)){
                        System.out.println("invalid use of [");
                        return null;
                    }*/
                    int nextBlur=regExp.indexOf(']',i);
                    if(nextBlur==-1){
                        System.out.println("[] not close");
                        return null;
                    }
                    HashSet<Character> chars=new HashSet<>();
                    String subpattern=regExp.substring(i,nextBlur);
                    //System.out.println(subpattern);
                    for(int j=0;j<subpattern.length();j++){
                        if(subpattern.charAt(j)=='-') {
                            if (j == 0 || j == subpattern.length() - 1) {
                                System.out.println("invalid use of - in []");
                                return null;
                            }
                            char leftChar = subpattern.charAt(j - 1);
                            char rightChar = subpattern.charAt(j + 1);
                            if (rightChar <= leftChar) {
                                System.out.println("invalid use of - in []");
                                return null;
                            }
                            for (char ch = leftChar; ch <= rightChar; ch++) {
                                chars.add(ch);
                            }
                        }
                        else{
                            chars.add(subpattern.charAt(j));
                        }

                    }
                    Vector<NFA> join_nfas=new Vector<>();
                    for(Character basechar:chars){
                        State beginState = new State();
                        State endState = new State();
                        Edge edge = new Edge(beginState, endState);
                        edge.setEdge(Edge.NOT_EPSILON);
                        edge.setCharacter(basechar);
                        beginState.getEdges().add(edge);
                        NFA nfa = new NFA(beginState, endState);
                        nfa.edges.add(edge);
                        join_nfas.add(nfa);
                    }
                    NFA nfa=join_nfas.get(0);
                    for(int j=1;j<join_nfas.size();j++){
                        nfa.or(join_nfas.get(j));
                    }
                    basenfas.add(nfa);

                    i=nextBlur;
                    squareBlurState=false;
                    //System.out.println(regExp.charAt(i));
                    //break;
                }
                else
                switch (c) {
                    case '[': {
                        squareBlurState=true;
                        break;
                    }
                    case ']': {
                        System.out.println("jump out of squareBlurState!!");
                        squareBlurState=false;
                        break;
                    }

                    case '(': {
                        blur_stack.push(basenfas.size());
                        blur_level++;
                        break;
                    }
                    case '\\': {
                        slashState = true;
                        break;
                    }
                    case ')': {
                        if (blur_stack.empty()) {
                            System.out.println("invalid regexp:blur");
                            return null;
                        }

                        int lastkey = -1;
                        if (!xor_stack.empty()) {

                            Vector<NFA> xor_nfas = new Vector<NFA>();
                            while (!xor_stack.empty()) {
                                Pair<Integer, Integer> xor_pos = xor_stack.pop();
                                if (xor_pos.getValue() != blur_level) break;
                                int key = xor_pos.getKey();
                                NFA nfa = basenfas.get(key);
                                basenfas.remove(key);
                                lastkey = key;
                                for (int j = key; j < basenfas.size(); ) {
                                    nfa.cat(basenfas.get(j));
                                    basenfas.remove(j);
                                }
                                xor_nfas.add(nfa);
                            }

                            if (xor_nfas.size() <= 0) {
                                System.out.println("Invalid regexp:|");
                                return null;
                            }
                            int blur_pos = blur_stack.pop();
                            NFA nfa = basenfas.get(blur_pos);
                            basenfas.remove(blur_pos);
                            for (int j = 1; j < lastkey - blur_pos; j++) {
                                nfa.cat(basenfas.get(blur_pos + j));
                                basenfas.remove(blur_pos + j);
                            }
                            //NFA nfa=xor_nfas.get(0);
                            for (int j = 0; j < xor_nfas.size(); j++) {
                                nfa.or(xor_nfas.get(j));
                            }
                            basenfas.add(nfa);
                        } else {
                            int blur_pos = blur_stack.pop();
                            if (blur_pos >= basenfas.size()) {
                                System.out.println("bracks empty");
                                return null;
                            }
                            NFA nfa = basenfas.get(blur_pos);
                            basenfas.remove(blur_pos);
                            for (int j = blur_pos; j < basenfas.size(); ) {
                                nfa.cat(basenfas.get(j));
                                basenfas.remove(j);
                            }
                            basenfas.add(nfa);
                        }
                        blur_level--;
                        break;
                    }
                    case '|': {
                        Pair<Integer, Integer> xor_pos = new Pair<>(basenfas.size(), blur_level);
                        xor_stack.push(xor_pos);
                        break;
                    }
                    case '*': {
                        NFA nfa = basenfas.get(basenfas.size() - 1);
                        basenfas.remove(nfa);
                        nfa.star();
                        basenfas.add(nfa);
                        break;
                    }
                    case '+':{
                        NFA nfa=basenfas.get(basenfas.size()-1);
                        basenfas.remove(nfa);
                        NFA nfa2=new NFA(nfa);
                        nfa.star();
                        nfa2.cat(nfa);

                        basenfas.add(nfa2);
                        break;
                    }
                    default: {
                        State beginState = new State();
                        State endState = new State();
                        Edge edge = new Edge(beginState, endState);
                        edge.setCharacter(c);
                        beginState.getEdges().add(edge);
                        NFA nfa = new NFA(beginState, endState);
                        nfa.getEdges().add(edge);
                        basenfas.add(nfa);
                        break;
                    }


                }
            }
            i++;
            if(i>=regExp.length())break;
        }
        if(basenfas.size()<=0){
            System.out.println("invalid RegExp");
            return null;
        }

        NFA nfa=basenfas.get(0);
        for(int j=1;j<basenfas.size();j++){
            //basenfas.get(j).print();
            nfa.cat(basenfas.get(j));
        }
        System.gc();
        return nfa;

    }
    // ab
    public void cat(NFA nfa2){
        Edge edge=new Edge(this.endState,nfa2.beginState);
        edge.setEdge(Edge.EPSILON);
        this.endState.getEdges().add(edge);
        this.edges.add(edge);
        this.endState.setStatus(State.STATUS.NORMAL);
        this.endState=nfa2.endState;
        nfa2.beginState.setStatus(State.STATUS.NORMAL);
        for(State state:nfa2.states){
            this.states.add(state);
        }

        for(Edge e:nfa2.edges){
            this.edges.add(e);
        }
    }

    public void keepEnd_or(NFA nfa2){
        State newBeginState=new State();
        State newEndState=new State();
        nfa2.beginState.setStatus(State.STATUS.NORMAL);
        //nfa2.endState.setStatus(State.STATUS.NORMAL);
        Edge startEpsilon1=new Edge(newBeginState,this.beginState);
        startEpsilon1.setEdge(Edge.EPSILON);
        Edge startEpsilon2=new Edge(newBeginState,nfa2.beginState);
        startEpsilon2.setEdge(Edge.EPSILON);
        newBeginState.getEdges().add(startEpsilon1);
        newBeginState.getEdges().add(startEpsilon2);
        Edge endEpsilon1=new Edge(this.endState,newEndState);
        endEpsilon1.setEdge(Edge.EPSILON);

        this.endState.getEdges().add(endEpsilon1);
        Edge endEpsilon2=new Edge(nfa2.endState,newEndState);
        endEpsilon2.setEdge(Edge.EPSILON);
        nfa2.endState.getEdges().add(endEpsilon2);
        for(State state:nfa2.states){
            this.states.add(state);
        }
        for(Edge e:nfa2.edges){
            this.edges.add(e);
        }
        this.edges.add(startEpsilon1);
        this.edges.add(startEpsilon2);
        this.edges.add(endEpsilon1);
        this.edges.add(endEpsilon2);
        this.states.add(newBeginState);
        this.states.add(newEndState);
        this.endState=newEndState;
        this.beginState=newBeginState;
        //rebuild(newBeginState,newEndState);

    }
    // a|b
    public void or(NFA nfa2){
        State newBeginState=new State();
        State newEndState=new State();
        nfa2.beginState.setStatus(State.STATUS.NORMAL);
        nfa2.endState.setStatus(State.STATUS.NORMAL);
        Edge startEpsilon1=new Edge(newBeginState,this.beginState);
        startEpsilon1.setEdge(Edge.EPSILON);
        Edge startEpsilon2=new Edge(newBeginState,nfa2.beginState);
        startEpsilon2.setEdge(Edge.EPSILON);
        newBeginState.getEdges().add(startEpsilon1);
        newBeginState.getEdges().add(startEpsilon2);
        Edge endEpsilon1=new Edge(this.endState,newEndState);
        endEpsilon1.setEdge(Edge.EPSILON);

        this.endState.getEdges().add(endEpsilon1);
        Edge endEpsilon2=new Edge(nfa2.endState,newEndState);
        endEpsilon2.setEdge(Edge.EPSILON);
        nfa2.endState.getEdges().add(endEpsilon2);
        for(State state:nfa2.states){
            this.states.add(state);
        }
        for(Edge e:nfa2.edges){
            this.edges.add(e);
        }
        this.edges.add(startEpsilon1);
        this.edges.add(startEpsilon2);
        this.edges.add(endEpsilon1);
        this.edges.add(endEpsilon2);
        this.states.add(newBeginState);
        this.states.add(newEndState);
        rebuild(newBeginState,newEndState);

    }

    // a*
    public void star(){
        State newBeginState=new State();
        //newBeginState.setStatus(states.size());
        State newEndState=new State();
        //newEndState.setStatus(states.size()+1);
        Edge beginEpsilon=new Edge(newBeginState,this.beginState);
        beginEpsilon.setEdge(Edge.EPSILON);
        newBeginState.getEdges().add(beginEpsilon);
        Edge endEpsilon=new Edge(this.endState,newEndState);
        endEpsilon.setEdge(Edge.EPSILON);
        endState.getEdges().add(endEpsilon);
        Edge midEpsilon=new Edge(this.endState,this.beginState);
        midEpsilon.setEdge(Edge.EPSILON);
        endState.getEdges().add(midEpsilon);
        Edge linkEpsilon=new Edge(newBeginState,newEndState);
        linkEpsilon.setEdge(Edge.EPSILON);
        newBeginState.getEdges().add(linkEpsilon);
        this.states.add(newBeginState);
        this.states.add(newEndState);
        this.edges.add(beginEpsilon);
        this.edges.add(endEpsilon);
        this.edges.add(midEpsilon);
        this.edges.add(linkEpsilon);
        this.rebuild(newBeginState,newEndState);
    }

    public void print_brief(){
        System.out.printf("%d States,%d Edges\n",states.size(),edges.size());
    }

    public void rebuild(State beginState,State endState) {
        this.beginState.setStatus(State.STATUS.NORMAL);
        this.endState.setStatus(State.STATUS.NORMAL);
        this.beginState=beginState;
        this.endState=endState;
        this.beginState.setStatus(State.STATUS.START);
        this.endState.setStatus(State.STATUS.END);
    }
    public void rename(){
        int i=0;
        for(State state:this.states){

            state.setId(i);
            i++;
        }
    }
    public void print(){
        System.out.printf("%d\tStates,\t%d\tEdges\n",this.states.size(),this.edges.size());
        System.out.printf("Start Node:\t%d,\tEnd Node:\t%d\n",this.beginState.getId(),this.endState.getId());
        Stack<State> copyStates=new Stack<State>();

        copyStates.push(beginState);
        State tmp,cursor;

        //myStates.push(this.beginState);
        while(!copyStates.empty()) {
            tmp=copyStates.pop();

            for (Edge edge :tmp.getEdges()) {

                System.out.print(edge.getEdegeFrom().getStatus());
                System.out.print("\t");
                System.out.print(edge.getEdegeFrom().getId());
                System.out.print("\t");
                System.out.print(edge.getEdegeTo().getStatus());
                System.out.print("\t");
                System.out.print(edge.getEdegeTo().getId());
                System.out.print("\t");
                System.out.print(edge.getEdge());
                System.out.print("\t"+edge.getCharacter());
                System.out.println();

                if(!edge.getEdegeTo().getVisted()) {
                    //System.out.println("visited");
                    edge.getEdegeTo().setVisited(true);
                    if(edge.getEdegeTo()!=tmp)
                    copyStates.push(edge.getEdegeTo());
                }
            }
        }
        for(State state:this.states){
            state.setVisited(false);
        }
    }
    public void listStates(){
        for(State state:this.states){
            System.out.print(state.getId());
            System.out.print("\t");
            System.out.println(state.getStatus());

        }
    }
    public static void main(String args[]){
        NFA nfa=NFA.Reg2NFA("[ab]+");
        nfa.rename();
        //nfa.print();
    }
}
