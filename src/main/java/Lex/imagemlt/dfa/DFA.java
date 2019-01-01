package Lex.imagemlt.dfa;

import Lex.imagemlt.nfa.Edge;
import Lex.imagemlt.nfa.NFA;
import Lex.imagemlt.nfa.State;
import apple.laf.JRSUIConstants;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;

public class DFA {
    public Vector<State> states;
    public Vector<Edge> edges;
    public HashMap<State,HashMap<Character,State>> transitionTable;
    public Vector<State> starts;
    public Vector<State> ends;

    public DFA(NFA nfa){
        states=new Vector<>();
        edges=new Vector<>();
        starts=new Vector<>();
        ends=new Vector<>();
        transitionTable=new HashMap<>();
        HashMap<HashSet<State>, HashMap<Character,HashSet<State>>>TransitionTable=getTransitionTable(nfa);
        Iterator iter = TransitionTable.entrySet().iterator();
        Vector<HashSet<State>>vect=new Vector<>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            HashSet<State> key = (HashSet<State>)entry.getKey();
            State state=new State();
            State.STATUS status=getStatus(key);
            state.setStatus(status);
            state.setId(getId(key));

            if(status== State.STATUS.START || status== State.STATUS.BOTH){
                starts.add(state);
            }
            else if(status== State.STATUS.END){
                ends.add(state);
            }

            //state.setId(states.size());
            states.add(state);
            vect.add(key);
        }
        iter=TransitionTable.entrySet().iterator();
        int i=0;
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            HashMap<Character,HashSet<State>> map=(HashMap<Character,HashSet<State>>)entry.getValue();
            Iterator it=map.entrySet().iterator();
            HashMap<Character,State> transTable=new HashMap<>();
            while(it.hasNext()){
                Map.Entry en=(Map.Entry)it.next();
                HashSet<State> val=(HashSet<State>) en.getValue();
                Edge edge=new Edge(states.get(i),states.get(vect.indexOf(val)));

                edge.setCharacter((Character)en.getKey());
                transTable.put((Character)en.getKey(),edge.getEdegeTo());
                states.get(i).getEdges().add(edge);
                edges.add(edge);
            }
            transitionTable.put(states.get(i),transTable);
            i++;
        }


    }

    public int getId(HashSet<State>set){
        int end=-1;
        for(State state:set){
            if(state.getStatus()== State.STATUS.END || state.getStatus()==State.STATUS.BOTH){
                //System.out.println(state.getId());
                if(end==-1){
                    end=state.getId();
                }
                else{
                    if(end>state.getId()){
                        end=state.getId();
                    }
                }
            }
        }
        return end;
    }

    public void simplization(){
        //Stack<Vector<HashSet<State>>> divideStack=new Stack<>();
        Vector<HashSet<State>> beginState=new Vector<>();
        //HashSet<State> I1=new HashSet<>();
        //HashSet<State> I2=new HashSet<>();
        for(State state:this.states){
            if(beginState.isEmpty()){
                HashSet<State> set=new HashSet<>();
                set.add(state);
                beginState.add(set);
            }
            else{
                boolean addFlag=false;
                for(HashSet<State>set:beginState){
                    State representor=set.iterator().next();
                    if(state.getId()==representor.getId()){
                        addFlag=true;
                        set.add(state);
                    }
                }
                if(!addFlag){
                    HashSet<State> set=new HashSet<>();
                    set.add(state);
                    beginState.add(set);
                }
            }
            /*if(state.getStatus()==State.STATUS.END || state.getStatus()== State.STATUS.BOTH){
                I1.add(state);
            }
            else{
                I2.add(state);
            }*/
        }
        /*if(I1.size()>0)
        beginState.add(I1);
        if(I2.size()>0)
        beginState.add(I2);
        */
        Vector<HashSet<State>> tmp=(beginState);
        while(true){
            boolean divide=false;
            //tmp=divideStack.pop();
            Vector<HashSet<State>> newlevel=new Vector<>();
            for(HashSet<State>set:tmp){
                Vector<HashSet<State>> sublevel=new Vector<>();
                for(State state:set){
                    if(sublevel.size()==0){
                        HashSet<State> s=new HashSet<>();
                        s.add(state);
                        sublevel.add(s);
                    }
                    else{
                        boolean addFlag=false;
                        for(HashSet<State>subSet:sublevel){
                            if(compare(state, subSet.iterator().next(),tmp)){
                                addFlag=true;
                                subSet.add(state);
                                break;
                            }

                        }
                        if(!addFlag){
                            HashSet<State> s=new HashSet<>();
                            s.add(state);
                            sublevel.add(s);
                        }

                    }
                }
                newlevel.addAll(sublevel);
            }
            if(newlevel.size()==tmp.size()){
                break;
            }
            tmp=newlevel;

        }
        HashMap<State,HashMap<Character,State>> newTransitionTable=new HashMap<>();
        Vector<State> newStates=new Vector<>();
        Vector<Edge> edges=new Vector<>();
        Vector<HashSet<State>> hashSet=new Vector<>();
        this.starts.clear();
        this.ends.clear();
        for(HashSet<State>set:tmp){
           State state=set.iterator().next();
           state.setStatus(getStatus(set));
           state.setId(getId(set));
           newStates.add(state);
           hashSet.add(set);
        }
        int i=0;
        for(State state:newStates){
            state.getEdges().clear();
            Iterator it=transitionTable.get(state).entrySet().iterator();
            newTransitionTable.put(state,new HashMap<Character, State>());
            while(it.hasNext()){
                Map.Entry entry=(Map.Entry)it.next();
                HashSet<State> moved=move(state,(Character)entry.getKey(),tmp);
                int index=hashSet.indexOf(moved);
                State to=newStates.get(index);
                newTransitionTable.get(state).put((Character)entry.getKey(),to);
                Edge edge=new Edge(state,to);
                edge.setCharacter((Character)entry.getKey());
                state.getEdges().add(edge);
                edges.add(edge);
            }
            if(state.getStatus()== State.STATUS.START || state.getStatus()== State.STATUS.BOTH)this.starts.add(state);
            if(state.getStatus()==State.STATUS.END)this.ends.add(state);
        }
        this.states=newStates;
        this.edges=edges;
        this.transitionTable=newTransitionTable;
    }

    private HashSet<State> move(State state,Character c,Vector<HashSet<State>> vect){
        State moved=transitionTable.get(state).get(c);
        for(HashSet<State>set:vect){
            if(set.contains(moved)){
                return set;
            }
        }
        return null;
    }

    private boolean compare(State state1,State state2,Vector<HashSet<State>> vect){

        HashMap trans1=transitionTable.get(state1);
        HashMap trans2=transitionTable.get(state2);
        if(trans1.size()!=trans2.size())
        {
            return false;
        }
        Iterator it=trans1.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            Character key=(Character)entry.getKey();

            if(move(state1,key,vect)!=move(state2,key,vect)){
                return false;
            }
        }
        return true;
    }

    public State match(String input){
        Pair<State,Integer> tmp;
        boolean end=false;
        State matchState=null;
        Stack<Pair<State,Integer>> matchStack=new Stack<>();
        for(State begin:this.starts){
            State nextMatch=transitionTable.get(begin).get(input.charAt(0));
            if(nextMatch!=null){
                matchStack.push(new Pair<State,Integer>(nextMatch,1));
            }
        }
        while(!matchStack.empty()){
            tmp=matchStack.pop();
            /*if(tmp.getKey().getStatus()== State.STATUS.BOTH || tmp.getKey().getStatus()== State.STATUS.END){
                end=true;
            }*/
            if(tmp.getValue()==input.length()){
                if(tmp.getKey().getStatus()==State.STATUS.BOTH || tmp.getKey().getStatus()== State.STATUS.END){
                    matchState=tmp.getKey();
                    break;
                }
            }
            else {
                char nextChar = input.charAt(tmp.getValue());
                State nextMatch=transitionTable.get(tmp.getKey()).get(nextChar);
                if(nextMatch!=null){
                    matchStack.push(new Pair<State,Integer>(nextMatch,tmp.getValue()+1));
                }
            }
        }
        return matchState;
    }


    public HashSet<State> getClosure(State state){
        HashSet<State> ans=new HashSet<>();
        Stack<State> stateStack=new Stack<>();
        stateStack.push(state);
        ans.add(state);
        State tmp;
        while(!stateStack.empty()){

            tmp=stateStack.pop();

            for(Edge edge:tmp.getEdges()){
                if(edge.getEdge()==Edge.EPSILON){

                    State stateTo=edge.getEdegeTo();
                    if(!ans.contains(stateTo)){

                        ans.add(stateTo);
                        stateStack.add(stateTo);
                    }
                }
            }
        }
        return ans;
    }
    public HashSet<State> getTransClosure(HashSet<State> state,Character character){
        HashSet<State> ans=new HashSet<>();
        HashSet<State> result=new HashSet<>();
        Stack<State> stateStack=new Stack<>();

        for(State tmp:state){

            for(Edge edge:tmp.getEdges()){

                if(edge.getCharacter()!=null && edge.getCharacter().equals(character)){
                    if(!ans.contains(edge.getEdegeTo())){

                        ans.add(edge.getEdegeTo());
                    }
                }
            }
        }
        for(State tmp:ans){
            result.addAll(getClosure(tmp));
        }

        return result;
    }


    public HashMap<HashSet<State>, HashMap<Character,HashSet<State>>> getTransitionTable(NFA nfa){
        State beginState=nfa.getBeginState();
        Stack<HashSet<State>> states=new Stack<>();
        states.push(getClosure(beginState));
        HashSet<State> tmp;
        HashMap<HashSet<State>, HashMap<Character,HashSet<State>>> ans=new HashMap<>();
        while(!states.empty()){
            tmp=states.pop();
            ans.put(tmp,new HashMap<Character, HashSet<State>>());
            HashMap<Character,HashSet<State>> tmpMap=ans.get(tmp);
            for(int i=0;i<=127;i++){
                HashSet<State> s=getTransClosure(tmp,(char)i);
                if(s.size()>0) {
                    tmpMap.put((char) i, s);
                    if (!ans.containsKey(s)) {

                        states.push(s);
                    }
                }
            }
        }


        return ans;
    }


    public void rename(){
        int i=0;
        for(State state:this.states){

            state.setId(i);
            i++;
        }
    }

    public State.STATUS getStatus(HashSet<State> states){
        boolean start=false;
        boolean end=false;
        for(State state:states){
            if(state.getStatus()==State.STATUS.START || state.getStatus()==State.STATUS.BOTH){
                start=true;
            }
            if(state.getStatus()==State.STATUS.END || state.getStatus()==State.STATUS.BOTH){
                end=true;
            }
        }
        if(start && end)return State.STATUS.BOTH;
        if(start)return State.STATUS.START;
        if(end)return State.STATUS.END;
        return State.STATUS.NORMAL;
    }

    public void print(){
        System.out.printf("%d States,%d Edges\n",states.size(),edges.size());

        for(State beginState:starts) {
            Stack<State> copyStates = new Stack<State>();

            copyStates.push(beginState);
            State tmp, cursor;

            //myStates.push(this.beginState);
            while (!copyStates.empty()) {

                tmp = copyStates.pop();

                for (Edge edge : tmp.getEdges()) {

                    System.out.print(edge.getEdegeFrom().getStatus());
                    System.out.print("\t");
                    System.out.print(edge.getEdegeFrom().getId());
                    System.out.print("\t");
                    System.out.print(edge.getEdegeTo().getStatus());
                    System.out.print("\t");
                    System.out.print(edge.getEdegeTo().getId());
                    System.out.print("\t");
                    System.out.print(edge.getEdge());
                    System.out.print("\t" + edge.getCharacter());
                    System.out.println();

                    if (!edge.getEdegeTo().getVisted()) {
                        //System.out.println("visited");
                        edge.getEdegeTo().setVisited(true);
                        if(edge.getEdegeTo()!=tmp)
                        copyStates.push(edge.getEdegeTo());
                    }
                }
            }
        }
            for (State state : this.states) {
                state.setVisited(false);
            }

    }

    public void print_brief(){
        System.out.printf("%d States,%d Edges\n",states.size(),edges.size());
    }

    public String dump(){
        String states="STATUS[] status={\n";
        String stateIds="int[] stateIds={\n";
        HashMap<State,Integer> idMap=new HashMap<>();
        int i=0;
        for(State state:this.states){
            idMap.put(state,i);
            i++;
        }
        for(i=0;i<this.states.size();i++){
            states=states+"\tSTATUS."+this.states.get(i).getStatus().toString()+",\n";
            stateIds=stateIds+"\t"+this.states.get(i).getId()+",\n";
        }
        states=states+"};\n";
        stateIds=stateIds+"};\n";
        String table="int[][] transitionTable={\n";
        for(State state:this.states) {
            table=table+"\t{";
            for (int j = 0; j < 128; j++) {
                State to=transitionTable.get(state).get((char)j);
                if(to==null)table=table+"-1,";
                else {

                    table=table+idMap.get(to).toString()+",";
                }
            }
            table=table+"},\n";
        }
        table=table+"};\n";
        String start="int start=";
        start=start+idMap.get(this.starts.get(0))+";\n";
        return states+stateIds+table+start;

    }

    public static void main(String args[]){
        //匹配规则
        String[] matchTable={"<ADD>","<SUB>","<MUL>","<DIV>","<LB>","<RB>","<ASSIGN>","<EQUAL>","<SEM>","<BEGIN>","<END>","<IF>","<ELSE>","<WHILE>","<TYPE,int>","<TYPE,float>","<ID,%s>","<DIGITS,%s>"};
        String[] regExps={"+","-","\\*","/","\\(","\\)","=","==",";","{","}","if","else","while","int","float","[a-z][a-z0-9]*","\\d\\d*"};
        HashSet<Character> symbols=new HashSet<>(
                Arrays.asList(new Character[]{
                      '(',')',',','=','+','-','*','/',';','{','}'
                })
        );
        for(int i=0;i<matchTable.length;i++){
            System.out.printf("%s %s\n",matchTable[i],regExps[i]);
        }
        NFA base_nfa=null;
        for(int i=0;i<matchTable.length;i++){
            NFA nfa=NFA.Reg2NFA(regExps[i]);
            nfa.endState.setId(i);
            if(base_nfa==null){
                base_nfa=nfa;
            }
            else{
                base_nfa.keepEnd_or(nfa);
            }
        }
        DFA dfa=new DFA(base_nfa);
        System.out.println("==============生成dfa==============");
        System.out.println("源dfa表:");
        dfa.print_brief();
        System.out.println("化简后的dfa:");
        dfa.simplization();
        dfa.print();
        System.out.println("=============dfa匹配测试============");
        State match=dfa.match("else");
        if(match!=null){
            System.out.printf("hit a match:%d\n",match.getId());
            System.out.printf("matched word type:%s\n",matchTable[match.getId()]);
        }
        else{
            System.out.println("not match!");
        }
        System.out.println("===============Scanner=============");
        String example="int main(){int a=123;int b=654;if(a==b)a=a+b;else a=a-b;return 0;}";
        System.out.println(example);
        int begin=0,end=0;
        int code=0;
        while(true){
            String subPattern="";
            State state=null;
            if(symbols.contains(example.charAt(end))){

                while(end<example.length()&&symbols.contains(example.charAt(end))) {
                    end=end+1;
                    subPattern=example.substring(begin,end);
                    //System.out.println(subPattern);
                    State cuState=dfa.match(subPattern);
                    if(cuState!=null){
                        state=cuState;
                    }
                    else{
                        end--;
                        subPattern=subPattern.substring(0,subPattern.length()-1);
                        break;
                    }
                }
                if(state==null){
                    System.out.println("invalid syntax");
                    break;
                }
            }
            else {
                for (end = begin; end < example.length() && example.charAt(end) != ' ' && !symbols.contains(example.charAt(end)); end++)
                    ;
                subPattern=example.substring(begin,end);
                //System.out.println(subPattern);
                state=dfa.match(subPattern);
                if(state==null){
                    System.out.println("invalid syntax");
                    break;
                }
            }


            System.out.printf(matchTable[state.getId()]+"\t%s\n",subPattern,subPattern);
            code++;
            if(end==example.length()){
                break;
            }
            if(symbols.contains(example.charAt(end)) ||symbols.contains(example.charAt(begin)))begin=end;
            else begin=end+1;

        }

        //System.out.println(dfa.dump());
    }

}
