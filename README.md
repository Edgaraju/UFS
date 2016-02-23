# Sintaxe da l�gica sentencial (CP)

## Alfabeto

* S�mbolo de Pontua��o: `(`,`)`
* S�mbolo de verdade: `true` , `false`
* S�mbolo proposicionais: `P`, `Q`, `R`, `S`, `P1`, `Q1`, `R1`, `S1`, `P2`, ...
* Conectivos proposicionais: `not`, `and`, `or`, `->`, `<->`

## F�rmulas

* Todo s�mbolo de verdade � uma f�rmula;
* Todo s�mbolo proposicional � uma f�rmula;
* Se ``H`` � uma f�rmula, ent�o, `(not H)` � uma f�rmula;
* Se ``H`` e ``G`` s�o f�rmulas, ent�o, `(H and G)` � uma f�rmula;
* Se ``H`` e ``G`` s�o f�rmulas, ent�o, `(H or G)` � uma f�rmula;
* Se ``H`` e ``G`` s�o f�rmulas, ent�o, `(H -> G)` � uma f�rmula;
* Se ``H`` e ``G`` s�o f�rmulas, ent�o, `(H <-> G)` � uma f�rmula;

## BNF (Backus-Naur Form)

```
Program     ::=     Expression

Expression  ::=     primary-Expression
                    | Expression Operator primary-Expression

primary-Expression  ::=     V-name
                            | TF-symbol
                            | (Expression)
                            | not (Expression)

V-name      ::=     Identifier

TF-symbol   ::=     true | false

Identifier  ::=     [PQRS][[:digit:]]*

Operator    ::=     and | or | -> | <->
```

## Conven��es

### Token.String

```
and     ---->   *
not     ---->   (+1) % 2
true    ---->   1
false   ---->   0
or
```

### Conjun��o (AND)

```
1 x 1   = 1
1 x 0   = 0
0 x 1   = 0
0 x 0   = 0
```