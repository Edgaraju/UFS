package interpretador;

import java.util.ArrayList;
import java.util.List;
import checagem.*;

import sintaxe_abstrata.*;

public class Main {

    public static void main(String[] args) {
        //programa1();
        programa2();
        //testes();        
    }
    
    public static void testes() {
        // TipoBaseSemantico x = TipoBaseSemantico.Real;
        // TipoBaseSemantico y = TipoBaseSemantico.Int;
        
        TipoBase esq = new TipoBase(TBase.Real);
        TipoBase dir = new TipoBase(TBase.Real);
        if (esq.tipo.equals(dir.tipo) && esq.tipo.nome != "bool") {
            System.out.println(esq.tipo);
        }
        else {
            try {
                throw new Exception("BinExp: Tipos invalidos!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        
        // TipoArraySemantico vet1 = new TipoArraySemantico(TipoBaseSemantico.Real, 3);
        // TipoArraySemantico vet2 = new TipoArraySemantico(TipoBaseSemantico.Real, 3);
        //System.out.println(x.equals(y));
        //System.out.println(vet1.equals(vet2));
        
        // Checker c = new Checker();
    }
    
    public static void programa1() {
        /* =======================================================
         * Programa #1 em Linguagem X
         * Realiza alguns c�lculos na circunfer�ncia
         * =======================================================
         * 
         * cons real PI := 3.14;
         * var real raio := 6;
         * 
         * function real areaCirculo(var real r)
         *     2 * PI * (r * r);
         * 
         * function real diametroCirculo(var real r)
         *     2 * r;
         * 
         * var real area, comprimento, diametro;
         * area := areaCirculo(raio);
         * diametro := diametroCirculo(raio);
         * comprimento := PI * diametro;
         * 
         * var real[] resultados := {area, diametro, comprimento};
         * var int i := 0;
         * var real soma;
         * var bool par;
         * 
         * while(i < 3){
         *     soma := soma + resultados[i];
         *     i := i + 1;
         * }
         * 
         * if (soma % 2 = 0) par := true;
         * else par := false;
         * 
         */
        
        // Lista contendo todas as declara��es do programa
        List<Dec> d = new ArrayList<Dec>();
        
        // Lista de par�metros das fun��es (neste caso, igual para as duas)
        List<Parametro> parametros = new ArrayList<Parametro>();
        parametros.add( new ParBaseCopia(TBase.Real, "r") );
        
        // Declara e inicializa a constante PI e a vari�vel raio
        d.add( new DecCons(new Cons(new TipoBase(TBase.Real), "PI", new LiteralInt(3))) );
        d.add( new DecVar(new VarInic(new TipoBase(TBase.Real), "raio", new LiteralInt(6))) );
        
        // Declara as fun��es areaCirculo e diametroCirculo
        d.add(
            new Funcao(new TipoBase(TBase.Real), "areaCirculo", parametros,
                new BinExp(BinOp.Mul,
                    new BinExp(BinOp.Mul, new VarExp(new Simples("r")), new VarExp(new Simples("r"))),
                    new BinExp(BinOp.Mul, new VarExp(new Simples("PI")), new LiteralInt(2))
                )
            )
        );
        d.add(
            new Funcao(new TipoBase(TBase.Real), "diametroCirculo", parametros,
                new BinExp(BinOp.Mul, new VarExp(new Simples("r")), new LiteralInt(2))
            )
        );
        
        // Declara as vari�veis area, comprimento e diametro
        d.add( new DecVar(new VarNaoInic(new TipoBase(TBase.Real), "area")) );
        d.add( new DecVar(new VarNaoInic(new TipoBase(TBase.Real), "comprimento")) );
        d.add( new DecVar(new VarNaoInic(new TipoBase(TBase.Real), "diametro")) );
        
        // Lista de parametros que ser�o passados nas chamadas das fun��es
        List<Exp> passagemParametros = new ArrayList<Exp>();
        passagemParametros.add( new VarExp(new Simples("raio")) );
        
        /**
         * FIXME Na BNF comandos e express�es n�o foram herdados de Dec
         * Por isso as instru��es abaixo n�o podem ser adicionados no programa
         */
        // Realiza as atribui��es �s vari�veis area, diametro e comprimento
        new ASSIGN( new Simples("area"), new ChamadaExp("areaCirculo", passagemParametros) );
        new ASSIGN( new Simples("diametro"), new ChamadaExp("diametroCirculo", passagemParametros) );
        new ASSIGN(
            new Simples("comprimento"),
            new BinExp(
                BinOp.Mul, new VarExp(new Simples("PI")), new VarExp(new Simples("diametro"))
            )
        );
        /* END-FIXME */
        
        // Lista com a express�o que define o tamanho do array
        List<Exp> tamanhoArray = new ArrayList<Exp>();
        tamanhoArray.add(new LiteralInt(3));
        
        // Express�es que definem os valores de cada posi��o do array
        List<Exp> valoresArray = new ArrayList<Exp>();
        valoresArray.add( new VarExp(new Simples("area")) );
        valoresArray.add( new VarExp(new Simples("diametro")) );
        valoresArray.add( new VarExp(new Simples("comprimento")) );
        
        // Declara��o e inicializa��o do array
        d.add(
            new DecVar(
                new VarInicExt(new TipoArray(TBase.Real, tamanhoArray), "resultados", valoresArray)
            )
        );
        
        // Declara��o e inicializa��o da vari�vel i
        d.add( new DecVar(new VarInic(new TipoBase(TBase.Int), "i", new LiteralInt(0))) );
        
        // Declara��o das vari�veis soma e par
        d.add( new DecVar(new VarNaoInic(new TipoBase(TBase.Real), "soma")) );
        d.add( new DecVar(new VarNaoInic(new TipoBase(TBase.Bool), "par")) );
        
        /**
         * FIXME Na BNF comandos e express�es n�o foram herdados de Dec
         * Por isso as instru��es abaixo n�o podem ser adicionados no programa
         */
        // Lista de comandos a serem executados no bloco while
        List<DVarConsCom> comandosLopp = new ArrayList<DVarConsCom>();
        comandosLopp.add(
            new Com(
                new ASSIGN(
                    new Simples("soma"),
                    new BinExp(
                        BinOp.Som,
                        new VarExp(new Simples("soma")),
                        new VarExp(
                            new Indexada(
                                new Simples("resultados"), new VarExp(new Simples("i"))
                            )
                        )
                    )
                )
            )
        );
        comandosLopp.add(
            new Com(
                new ASSIGN(
                    new Simples("i"),
                    new BinExp(
                        BinOp.Som, new VarExp(new Simples("i")), new LiteralInt(1)
                    )
                )
            )
        );
        
        // Declara��o do bloco while
        new WHILE(
            new BinExp(
                BinOp.Menor,
                new VarExp(new Simples("i")),
                new LiteralInt(3)
            ),
            new BLOCO(comandosLopp)
        );
        
        // Declara��o do bloco if
        new IF(
            new BinExp(
                BinOp.Igual,
                new LiteralInt(0),
                new BinExp(
                    BinOp.Mod,
                    new VarExp(new Simples("soma")),
                    new LiteralInt(2)
                )
            ),
            new ASSIGN(new Simples("par"), new LiteralBool(true)),
            new ASSIGN(new Simples("par"), new LiteralBool(false))
        );
        /* END-FIXME */
        
        Programa p = new Programa(d);
        System.out.println(p.declaracoes);
    }
    
    public static void programa2() {
        /* =======================================================
         * Programa #2 em Linguagem X
         * Realiza o c�lculo da hipotenusa
         * =======================================================
         * 
         * function real tamanhoHipotenusa(var real l1, var real l2)
         *      (l1 * l1) + (l2 * l2);
         * 
         * procedure main() {
         *     var real lado1 := 6.6;
         *     var int lado2 := 5;
         *     var real hipotenusa;
         *     
         *     hipotenusa := tamanhoHipotenusa(lado1, lado2); // erro, real + int
         *     var real[] resultados := {hipotenusa};
         * }
         * 
         */
        
        // Lista contendo todas as declara��es do programa
        List<Dec> d = new ArrayList<Dec>();
        
        /**********************************************************************
         * Fun��o hipotenusa
         *********************************************************************/
        // Lista de par�metros da fun��o hipotenusa
        List<Parametro> parametros = new ArrayList<Parametro>();
        parametros.add( new ParBaseCopia(TBase.Real, "l1") );
        parametros.add( new ParBaseCopia(TBase.Real, "l2") );
        
        // Declara a fun��o hipotenusa
        d.add(
            new Funcao(new TipoBase(TBase.Real), "tamanhoHipotenusa", parametros,
                new BinExp(BinOp.Mul,
                    new BinExp(BinOp.Mul, new VarExp(new Simples("l1")),
                            new VarExp(new Simples("l1"))),
                    new BinExp(BinOp.Mul, new VarExp(new Simples("l2")),
                            new VarExp(new Simples("l2")))
                )
            )
        );
        
        /**********************************************************************
         * Procedimento main
         *********************************************************************/
        // Declara��es de vari�veis do procedimento main
        List<DVarConsCom> instrucoesMain = new ArrayList<DVarConsCom>();
        instrucoesMain.add( new DV( new VarInic(new TipoBase(TBase.Real),
                            "lado1", new LiteralReal(6.6)) ) );
        instrucoesMain.add( new DV( new VarInic(new TipoBase(TBase.Int),
                            "lado2", new LiteralInt(5)) ) );
        instrucoesMain.add( new DV( new VarNaoInic(new TipoBase(TBase.Real),
                            "hipotenusa") ) );
        
        // Lista de parametros que ser�o passados na chamada da fun��o hipotenusa
        List<Exp> passagemParametros = new ArrayList<Exp>();
        passagemParametros.add( new VarExp(new Simples("lado1")) );
        passagemParametros.add( new VarExp(new Simples("lado2")) );
        
        // Realiza a atribui��o a vari�vel hipotenusa
        instrucoesMain.add(
            new Com(
                new ASSIGN(
                    new Simples("hipotenusa"),
                    new ChamadaExp("tamanhoHipotenusa", passagemParametros)
                )
            )
        );
        
        
        // Lista com a express�o que define o tamanho do array
        List<Exp> tamanhoArray = new ArrayList<Exp>();
        tamanhoArray.add(new LiteralInt(1));
        
        // Express�es que definem os valores de cada posi��o do array
        List<Exp> valoresArray = new ArrayList<Exp>();
        valoresArray.add( new VarExp(new Simples("hipotenusa")) );
        
        // Declara��o e inicializa��o do array
        instrucoesMain.add( new DV( new VarInicExt(new TipoArray(TBase.Real,
                            tamanhoArray), "resultados", valoresArray) ) );
        
        // Corpo do procedimento main
        BLOCO corpoMain = new BLOCO(instrucoesMain);
        // Declara o procedimento main
        d.add( new Procedimento("main", new ArrayList<>(), corpoMain) );
        
        Programa p = new Programa(d);
        Checker c = new Checker();
        c.visitPrograma(p);
        c.erros.mostrar();
    }

}
