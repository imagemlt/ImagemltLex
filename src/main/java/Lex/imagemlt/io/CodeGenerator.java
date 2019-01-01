package Lex.imagemlt.io;

import Lex.imagemlt.dfa.DFA;


public class CodeGenerator {
    private LexFile lexFile;
    private DFA dfa;
    public CodeGenerator(LexFile lexFile,DFA dfa){
        this.lexFile=lexFile;
        this.dfa=dfa;
    }
    public String genCode(){
        String dfaTable=dfa.dump();
        String rule=lexFile.getRule();
        String UserCode=lexFile.getCode();
        String head="import javafx.util.Pair;\n" +
                "\n" +
                "import java.util.Arrays;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.Stack;\n" +
                "\n" +
                "public class Scanner {\n" +
                "    public enum STATUS {\n" +
                "        START,\n" +
                "        END,\n" +
                "        NORMAL,\n" +
                "        BOTH\n" +
                "    };";
        return head+dfaTable+rule+UserCode+"\n}\n";
    }
}
