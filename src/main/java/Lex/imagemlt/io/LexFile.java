package Lex.imagemlt.io;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class LexFile {

    private BufferedReader reader;

    public LexFile(String Path) throws Exception{
        this.matchTable=new Vector<>();
        this.regExps=new Vector<>();
        this.reader = new BufferedReader(new FileReader(Path));
        if(reader==null){
            System.out.println(Path);
        }
        readDefination();
        readRule();
        readUserCode();


    }
    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    private String rule;
    private String defination;
    private String code;

    public Vector<String> getRegExps() {
        return regExps;
    }

    public void setRegExps(Vector<String> regExps) {
        this.regExps = regExps;
    }

    public Vector<String> getMatchTable() {
        return matchTable;
    }

    public void setMatchTable(Vector<String> matchTable) {
        this.matchTable = matchTable;
    }

    private Vector<String> matchTable;
    private Vector<String> regExps;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefination() {
        return defination;
    }

    public void setDefination(String defination) {
        this.defination = defination;
    }

    private void readRule() throws Exception{
        this.rule="";
        while(true){
            String line=reader.readLine();
            if(line==null) throw new Exception("Lex Format Error");
            else if(line.startsWith("%%"))return;
            else{
                this.rule+=line+"\n";
            }
        }
    }
    private void readDefination() throws Exception{

        while(true){
            String line=reader.readLine();
            if(line==null) throw new Exception("Lex Format Error");
            else if(line.startsWith("%%"))return;
            else{
                String[] splits=line.split("[ \t]",2);
                if(splits.length!=2)continue;
                matchTable.add(splits[0]);
                regExps.add(splits[1]);
            }
        }
    }
    private void readUserCode()throws Exception{
        this.code="";
        while(true){
            String line=reader.readLine();
            if(line==null) return;
            else{
                this.code+=line+"\n";
            }
        }
    }


}
