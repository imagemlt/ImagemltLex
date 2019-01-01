package Lex;

import Lex.imagemlt.dfa.DFA;
import Lex.imagemlt.io.CodeGenerator;
import Lex.imagemlt.io.LexFile;
import Lex.imagemlt.nfa.NFA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            LexFile lexFile = new LexFile(args[1]);
            Vector<String> matchTable=lexFile.getMatchTable();
            Vector<String> regExps=lexFile.getRegExps();
            for (int i = 0; i < matchTable.size(); i++) {
                System.out.printf("%s %s\n", matchTable.get(i), regExps.get(i));
            }
            NFA base_nfa = null;
            for (int i = 0; i < matchTable.size(); i++) {
                NFA nfa = NFA.Reg2NFA(regExps.get(i));
                nfa.endState.setId(i);
                if (base_nfa == null) {
                    base_nfa = nfa;
                } else {
                    base_nfa.keepEnd_or(nfa);
                }
            }
            DFA dfa = new DFA(base_nfa);
            dfa.simplization();
            dfa.print();
            CodeGenerator generator=new CodeGenerator(lexFile,dfa);
            String result=generator.genCode();
            BufferedWriter writer=new BufferedWriter(new FileWriter(args[2]));
            writer.write(result);
            writer.flush();
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
