# ImagemltLex

SEU编译原理大作业 词法分析器

java -jar ImagemltLex.jar sample.lex Scanner.java


运行DFA.main()可以测试一小段词法分析
```$xslt
==============生成dfa==============
源dfa表:
102 States,2767 Edges
化简后的dfa:
25 States,489 Edges
=============dfa匹配测试============
hit a match:12
matched word type:<ELSE>
===============Scanner=============
int main(){int a=123;int b=654;if(a==b)a=a+b;else a=a-b;return 0;}
<ID,int>	int
<ID,main>	main
<LB>	(
<RB>	)
<BEGIN>	{
<ID,int>	int
<ID,a>	a
<ASSIGN>	=
<DIGITS,123>	123
<SEM>	;
<ID,int>	int
<ID,b>	b
<ASSIGN>	=
<DIGITS,654>	654
<SEM>	;
<IF>	if
<LB>	(
<ID,a>	a
<EQUAL>	==
<ID,b>	b
<RB>	)
<ID,a>	a
<ASSIGN>	=
<ID,a>	a
<ADD>	+
<ID,b>	b
<SEM>	;
<ELSE>	else
<ID,a>	a
<ASSIGN>	=
<ID,a>	a
<SUB>	-
<ID,b>	b
<SEM>	;
<ID,return>	return
<DIGITS,0>	0
<SEM>	;
<END>	}

Process finished with exit code 0

```

lex文件示例
```
+ +
- -
* \*
/ /
( \(
) \)
= =
== ==
; ;
{ {
} }
if if
else else
while while
int int
float float
id [a-z][a-z0-9]*
digits \d\d*
%%
	public enum STATUS {
        	START,
        	END,
        	NORMAL,
        	BOTH
    	};
	private HashSet<Character> symbols=new HashSet<>(
		Arrays.asList(new Character[]{
			'(',')',',','=','+','-','*','/',';','{','}'
		})
	);
	private String[] matchTable={"<ADD>","<SUB>","<MUL>","<DIV>","<LB>","<RB>","<ASSIGN>","<EQUAL>","<SEM>","<BEGIN>","<END>","<IF>","<ELSE>","<WHILE>","<TYPE,int>","<TYPE,float>","<ID,%s>","<DIGITS,%s>"};

%%

public Scanner() {

    }

    private int match(String input){
        Pair<Integer,Integer> tmp;
        boolean end=false;
        Integer matchState=-1;
        Stack<Pair<Integer,Integer>> matchStack=new Stack<>();

            Integer nextMatch=transitionTable[this.start][(int)input.charAt(0)];
            if(nextMatch!=null){
                matchStack.push(new Pair<Integer,Integer>(nextMatch,1));
            }

        while(!matchStack.empty()){
            tmp=matchStack.pop();
            /*if(tmp.getKey().getStatus()== State.STATUS.BOTH || tmp.getKey().getStatus()== State.STATUS.END){
                end=true;
            }*/
            if(tmp.getValue()==input.length()){
                if(status[tmp.getKey()]==STATUS.BOTH || status[tmp.getKey()]== STATUS.END){
                    matchState=tmp.getKey();
                    break;
                }
            }
            else {
                char nextChar = input.charAt(tmp.getValue());
                nextMatch=transitionTable[tmp.getKey()][(int)nextChar];
                if(nextMatch!=-1){
                    matchStack.push(new Pair<Integer, Integer>(nextMatch,tmp.getValue()+1));
                }
            }
        }
        return matchState;
    }
    public void Scan(String example){
        int begin=0,end=0;
        int code=0;
        while(true){
            String subPattern="";
            int state=-1;
            if(symbols.contains(example.charAt(end))){

                while(end<example.length()&&symbols.contains(example.charAt(end))) {
                    end=end+1;
                    subPattern=example.substring(begin,end);
                    //System.out.println(subPattern);
                    int cuState=match(subPattern);
                    if(cuState!=-1){
                        state=cuState;
                    }
                    else{
                        end--;
                        subPattern=subPattern.substring(0,subPattern.length()-1);
                        break;
                    }
                }
                if(state==-1){
                    System.out.println("invalid syntax");
                    break;
                }
            }
            else {
                for (end = begin; end < example.length() && example.charAt(end) != ' ' && !symbols.contains(example.charAt(end)); end++)
                    ;
                subPattern=example.substring(begin,end);
                //System.out.println(subPattern);
                state=match(subPattern);
                if(state==-1){
                    System.out.println("invalid syntax");
                    break;
                }
            }


            System.out.printf(matchTable[stateIds[state]]+"\t%s\n",subPattern,subPattern);
            code++;
            if(end==example.length()){
                break;
            }
            if(symbols.contains(example.charAt(end)) ||symbols.contains(example.charAt(begin)))begin=end;
            else begin=end+1;

        }
    }

    public static void main(String args[]){
	BufferedRaeder reader=new BufferedReader(new FileReader(args[1]));
	String code="";
	String line=null;
	while((line=reader.readLine())!=null){
		code=code+line;
	}
	reader.close();
        new Scanner().Scan(code);
    }

```
