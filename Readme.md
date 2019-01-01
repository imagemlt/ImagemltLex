# ImagemltLex

SEU编译原理大作业 词法分析器

目前版本运行DFA.main()可以测试一小段词法分析
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