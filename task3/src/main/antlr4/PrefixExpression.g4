grammar PrefixExpression;

//arithmetic operations:

PLUS: '+' ;
MINUS: '-' ;
TIMES: '*' ;
DIV: '/' ;
NEGATE: '~' ;

//logical operations:

NOT: '!' ;
AND: '&' ;
OR: '|' ;
XOR: '^' ;

//number comparison operations:

GT: '>' ;
LT: '<' ;
EQ: '==' ;
GE: '>=' ;
LE: '<=' ;
NE: '!=' ;

//other:

IF: 'if' ;
WHILE: 'while' ;
PRINT: 'print' ;
ASSIGN: '=' ;
EMPTY: 'empty' ;
NUMBER: [0-9]+ ;
VARIABLE: [a-zA-Z] [a-zA-Z0-9]* ;
WS: [ \t\r\n] -> skip ;

//grammar:

program: statement* EOF ;

statement: ifStatement | whileStatement | assignmentStatement | printStatement | emptyStatement ;

ifStatement: IF logicalExpression statement statement ;

whileStatement: WHILE logicalExpression statement ;

assignmentStatement: ASSIGN VARIABLE arithmeticExpression ;

printStatement: PRINT expression ;

emptyStatement: EMPTY ;

expression: logicalExpression | arithmeticExpression ;

logicalExpression: numCompOp arithmeticExpression arithmeticExpression
                 | unLogicOp logicalExpression
                 | binLogicOp logicalExpression logicalExpression ;

arithmeticExpression: NUMBER
                    | VARIABLE
                    | unArithmOp arithmeticExpression
                    | binArithmOp arithmeticExpression arithmeticExpression ;

numCompOp: (GT | LT | EQ | GE | LE | NE) ;

unLogicOp: NOT ;

binLogicOp: (AND | OR | XOR) ;

unArithmOp: NEGATE ;

binArithmOp: (PLUS | MINUS | TIMES | DIV) ;