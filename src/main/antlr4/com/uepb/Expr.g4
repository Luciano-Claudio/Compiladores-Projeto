grammar Expr;

prog: stat* EOF;

stat
    : 'var' ID ('=' expr)? ';'                                          #DeclVar
    | ID '=' expr ';'                                                 #Atribuicao
    | 'if' '(' boolExpr ')' '{' stat* '}'
     elseifClause* elseClause?    #IfStat
    | 'while' '(' boolExpr ')' '{' stat* '}'                           #WhileStat
    | 'print' '(' expr ')' ';'                                          #PrintStat
    | 'print' '(' STRING ')' ';'                                        #PrintStrStat
    ;

elseifClause: 'else' 'if' '(' boolExpr ')' '{' stat* '}';
elseClause  : 'else' '{' stat* '}';

expr
    : O1=expr OP=('+'|'-') O2=term    #SomaSub
    | term                             #TermPassthrough
    ;

term
    : O1=term OP=('*'|'/') O2=factor  #MulDiv
    | factor                           #FactorPassthrough
    ;

factor
    : O1=atom '^' O2=factor    #Potencia
    | atom                      #AtomPassthrough
    ;

atom
    : '(' NESTED=expr ')'         #Parenteses
    | NUMBER                       #Numero
    | ID                           #UsoVariavel
    | 'input' '(' STRING ')'        #Input
    ;

boolExpr
    : O1=boolExpr 'or' O2=boolAnd    #Or
    | boolAnd                        #OrPassthrough
    ;

boolAnd
    : O1=boolAnd 'and' O2=boolNot    #And
    | boolNot                        #AndPassthrough
    ;

boolNot
    : 'not' B=boolNot    #Not
    | boolAtom          #NotPassthrough
    ;

boolAtom
    : '(' NESTED=boolExpr ')'                          #BoolParenteses
    | O1=expr OP=('<='|'>='|'=='|'!='|'<'|'>') O2=expr      #Comparacao
    ;


NUMBER  : [0-9]+ ('.' [0-9]+)?;
ID      : [a-zA-Z_][a-zA-Z_0-9]*;
STRING  : '"' (~["\r\n])* '"';
COMMENT : '//' ~[\r\n]* -> skip;
WS      : [ \t\r\n]+ -> skip;