package Lex.imagemlt.io;

import Lex.imagemlt.dfa.DFA;


public class CodeGenerator {
    private LexFile lexFile;
    private DFA dfa;
    private static String head="import javafx.util.Pair;\n" +
            "\n" +
            "import java.io.BufferedReader;\n" +
            "import java.io.InputStream;\n" +
            "import java.io.InputStreamReader;\n" +
            "import java.io.Reader;\n" +
            "import java.util.*;\n" +
            "\n" +
            "\n" +
            "public class Scanner {\n" +
            "    public enum STATUS {\n" +
            "        START,\n" +
            "        END,\n" +
            "        NORMAL,\n" +
            "        BOTH\n" +
            "    };\n";
    private static String funcScan="    public Vector<Integer> Scan(String example) throws Exception{\n" +
            "        int begin=0,end=0;\n" +
            "        Vector<Integer> ans=new Vector<>();\n" +
            "        if(example.length()==0)return ans;\n" +
            "        while(true){\n" +
            "            String subPattern=\"\";\n" +
            "            int state=-1;\n" +
            "\n" +
            "            if(symbols.contains(example.charAt(end))){\n" +
            "\n" +
            "                while(end<example.length()&&symbols.contains(example.charAt(end))) {\n" +
            "                    end=end+1;\n" +
            "                    subPattern=example.substring(begin,end);\n" +
            "                    int cuState=match(subPattern);\n" +
            "                    if(cuState!=-1){\n" +
            "                        state=cuState;\n" +
            "                    }\n" +
            "                    else{\n" +
            "                        end--;\n" +
            "                        subPattern=subPattern.substring(0,subPattern.length()-1);\n" +
            "                        break;\n" +
            "                    }\n" +
            "                }\n" +
            "                if(state==-1)\n" +
            "                    throw new Exception(\"invalid token '\"+subPattern+\"'\");\n" +
            "\n" +
            "            }\n" +
            "            else {\n" +
            "                for (end = begin; end < example.length() && example.charAt(end) != ' ' && !symbols.contains(example.charAt(end)); end++)\n" +
            "                    ;\n" +
            "                if(end==begin){\n" +
            "                    end++;\n" +
            "                    while(end<example.length() && example.charAt(begin)==' ') {\n" +
            "                        begin++;\n" +
            "                        end++;\n" +
            "                    }\n" +
            "                }\n" +
            "                subPattern=example.substring(begin,end);\n" +
            "\n" +
            "                state=match(subPattern);\n" +
            "                if(state==-1)\n" +
            "                    throw new Exception(\"invalid token '\"+subPattern+\"'\");\n" +
            "\n" +
            "            }\n" +
            "            if(state!=-1) {\n" +
            "                ans.add(state);\n" +
            "                if (debug)\n" +
            "                    System.out.printf(matchTable[stateIds[state]] + \"\\t%s\\n\", subPattern, subPattern);\n" +
            "            }\n" +
            "            if(end==example.length()){\n" +
            "                break;\n" +
            "            }\n" +
            "            if(symbols.contains(example.charAt(end)) ||symbols.contains(example.charAt(begin)))begin=end;\n" +
            "            else begin=end+1;\n" +
            "\n" +
            "        }\n" +
            "        return ans;\n" +
            "    }\n" +
            "\n" +
            "    public Vector<Integer> Scan(Reader reader) throws Exception{\n" +
            "        BufferedReader bufferedReader=new BufferedReader(reader);\n" +
            "        Vector<Integer>ans=new Vector<>();\n" +
            "        String line=null;\n" +
            "        while((line=bufferedReader.readLine())!=null){\n" +
            "            Vector<Integer> tmp=Scan(line);\n" +
            "            ans.addAll(tmp);\n" +
            "        }\n" +
            "        return ans;\n" +
            "    }\n" +
            "\n" +
            "    public Vector<Integer> Scan(InputStream stream) throws Exception{\n" +
            "        return Scan(new InputStreamReader(stream));\n" +
            "    }\n";
    private static String funcMatch="    private int match(String input){\n" +
            "        Pair<Integer,Integer> tmp;\n" +
            "        boolean end=false;\n" +
            "        Integer matchState=-1;\n" +
            "        Stack<Pair<Integer,Integer>> matchStack=new Stack<>();\n" +
            "\n" +
            "        Integer nextMatch=transitionTable[this.start][(int)input.charAt(0)];\n" +
            "        if(nextMatch!=-1){\n" +
            "            matchStack.push(new Pair<Integer,Integer>(nextMatch,1));\n" +
            "        }\n" +
            "\n" +
            "        while(!matchStack.empty()){\n" +
            "            tmp=matchStack.pop();\n" +
            "            /*if(tmp.getKey().getStatus()== State.STATUS.BOTH || tmp.getKey().getStatus()== State.STATUS.END){\n" +
            "                end=true;\n" +
            "            }*/\n" +
            "            if(tmp.getValue()==input.length()){\n" +
            "                if(status[tmp.getKey()]==STATUS.BOTH || status[tmp.getKey()]== STATUS.END){\n" +
            "                    matchState=tmp.getKey();\n" +
            "                    break;\n" +
            "                }\n" +
            "            }\n" +
            "            else {\n" +
            "                char nextChar = input.charAt(tmp.getValue());\n" +
            "                nextMatch=transitionTable[tmp.getKey()][(int)nextChar];\n" +
            "                if(nextMatch!=-1){\n" +
            "                    matchStack.push(new Pair<Integer, Integer>(nextMatch,tmp.getValue()+1));\n" +
            "                }\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "        return matchState;\n" +
            "    }";
    private static String constructor="private boolean debug=false;\n" +
            "    public Scanner(boolean debug) {\n" +
            "        this.debug=debug;\n" +
            "    }\n";
    public CodeGenerator(LexFile lexFile,DFA dfa){
        this.lexFile=lexFile;
        this.dfa=dfa;
    }
    public String genCode(){
        String dfaTable=dfa.dump();
        String rule=lexFile.getRule();
        String UserCode=lexFile.getCode();

        return head+dfaTable+rule+funcScan+funcMatch+constructor+UserCode+"\n}\n";
    }
}
