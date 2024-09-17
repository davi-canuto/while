grammar Enquanto;

programa : seqComando;     // sequência de comandos

seqComando: (comando ';')* ;

comando: ID ':=' expressao                               # atribuicao
       | 'skip'                                          # skip
       | 'se' booleano 'entao' comando 'senao' comando   # se
       | 'enquanto' booleano 'faca' comando              # enquanto
       | 'exiba' TEXTO                                   # exiba
       | 'escreva' expressao                             # escreva
       | '{' seqComando '}'                              # bloco
       | 'para' ID 'de' expressao 'ate' expressao ('passo' INT)? 'faca' comando			# para
       | ID '(' (ID (',' ID)*)? ')' '=' expressao										# defFuncao
       ;

expressao: INT                                           # inteiro
         | 'leia'                                        # leia
         | ID                                            # id
         | expressao '*'   expressao                     # opBin
         | expressao '^'   expressao                     # opBin
         | expressao '/'   expressao                     # opBin
         | expressao ('+' | '-') expressao               # opBin
         | '(' expressao ')'                             # expPar
         ;

booleano: BOOLEANO                                       # bool
        | expressao '=' expressao                        # opRel
        | expressao '<=' expressao                       # opRel
        | expressao '>=' expressao                       # opRel
        | expressao '>' expressao                        # opRel
        | expressao '<' expressao                        # opRel
        | expressao '<>' expressao                       # opRel
        | 'nao' booleano                                 # naoLogico
        | booleano 'e' booleano                          # eLogico
        | booleano 'ou' booleano                         # ouLogico
        | booleano 'ou exclusivo' booleano               # ouExclusivoLogico
        | '(' booleano ')'                               # boolPar
        ;


BOOLEANO: 'verdadeiro' | 'falso';
INT: ('0'..'9')+ ;
ID: ('a'..'z')+;
TEXTO: '"' .*? '"';

Comentario: '#' .*? '\n' -> skip;
Espaco: [ \t\n\r] -> skip;