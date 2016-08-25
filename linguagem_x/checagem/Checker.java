package checagem;

import java.util.ArrayList;
import java.util.List;
import sintaxe_abstrata.*;
import ambiente.*;
import utilitarios.*;


public class Checker extends Visitor {
    public AmbienteConsVar aConsVar = new AmbienteConsVar();
    public AmbienteFuncProc aFuncProc = new AmbienteFuncProc();
    public RegistroErros erros = new RegistroErros();
    
    public Checker() {
        /**
         * O c�digo abaixo � apenas para fins de exemplo
         * N�o tem signific�ncia nenhuma 
         */
        // Insere a "vari�vel" "x" do tipo "real" na tabela de s�mbolos
        /*VinculavelConsVar xVinc = (VinculavelConsVar) aConsVar.lookup("x", true, TipoBaseSemantico.Real);
        VinculavelConsVar yVinc = new VinculavelConsVar(true, TipoBaseSemantico.Int);
        System.out.println(aConsVar.tamanho());
        aConsVar.tabela.put("x", yVinc);
        System.out.println(aConsVar.get("x").tipo);
        System.out.println(aConsVar.tamanho());
        aConsVar.delete("x");
        System.out.println(aConsVar.tamanho());*/
        
        /*
        // Insere a "fun��o" "calculaSoma" do tipo "real" na tabela de s�mbolos
        List<PassagemTipoSemantico> p = new ArrayList<PassagemTipoSemantico>();
        p.add(new PassagemTipoSemantico(TipoBaseSemantico.Real));
        //VinculavelFuncProc x = (VinculavelFuncProc) aFuncProc.lookupFuncProc("calculaSoma", p);
        
        aConsVar.add("x", true, TipoBaseSemantico.Int);
        aConsVar.add("y", false, TipoBaseSemantico.Real);
        aConsVar.comecaEscopo();
        aConsVar.add("x", true, TipoBaseSemantico.Int);
        aConsVar.terminaEscopo();
        aConsVar.add("y", false, TipoBaseSemantico.Real);
        aConsVar.comecaEscopo();
        aConsVar.add("y", false, TipoBaseSemantico.Real);
        
        erros.reportar(01, "Que eu quiser");
        erros.mostrar();
        */
    }
    
    /**
     * Dada uma inst�ncia de TBase (sint�tico) retorna uma equivalente do tipo
     * TipoBaseSemantico (sem�ntico).
     * 
     * @param b
     * @return
     */
    private TipoBaseSemantico paraSemantico(TBase b) {
        switch (b.nome) {
            case "int":
                return TipoBaseSemantico.Int;
            
            case "bool":
                return TipoBaseSemantico.Bool;
            
            case "real":
                return TipoBaseSemantico.Real;
    
            default:
                return null;
        }
    }
    
    /**
     * Dado um TBase (sint�tico) e uma dimens�o, retorna um equivalente
     * do tipo TipoArraySemantico (sem�ntico).
     * 
     * @param b Tipo base sint�tico (sintaxe abstrata)
     * @param d Dimens�o do array
     * @return  TipoArraySemantico
     */
    private TipoArraySemantico paraSemantico(TBase b, int d) {
        return new TipoArraySemantico(paraSemantico(b), d);
    }

    @Override
    public Object visitAssign(ASSIGN a) {
        VinculavelConsVar tVar = (VinculavelConsVar) a.var.accept(this);
        TipoSemantico tExp = (TipoSemantico) a.exp.accept(this);
        
        if (! tVar.isVar) {
            erros.reportar(303, "Imposs�vel realizar atribui��o em constante");
        }
        if (tVar.tipo.equals(TipoBaseSemantico.Real)
                && tExp.equals(TipoBaseSemantico.Int)) {
            a.exp = new IntParaReal(a.exp);
            tExp = (TipoSemantico) a.exp.accept(this);
        }
        if (! tVar.tipo.equals(tExp)) {
            erros.reportar(400, "N�o � poss�vel atribuir " + tExp + " em "
                    + tVar.tipo);
        }
            
        return tVar.tipo;
    }

    @Override
    public Object visitBinExp(BinExp e) {
        TipoSemantico tipoRetorno = null, tEsq, tDir;
        Object esq = e.expEsq.accept(this);
        Object dir = e.expDir.accept(this);
        
        tEsq = (esq instanceof VinculavelConsVar)
                ? ((VinculavelConsVar) esq).tipo
                : (TipoSemantico) esq;
        
        tDir = (dir instanceof VinculavelConsVar)
                ? ((VinculavelConsVar) dir).tipo
                : (TipoSemantico) dir;
        
        switch (e.operacao.token) {
        case "+":
        case "-":
        case "*":
        case "/":
        case "%":
            if (tEsq.equals(TipoBaseSemantico.Bool)
                    || tDir.equals(TipoBaseSemantico.Bool)) {
                // Algum lado da opera��o � boolean
                erros.reportar(101, "Opera��o [" + e.operacao.token + "] n�o"
                        + " permitida com booleanos.");
                // Retornando int apenas para prosseguir a checagem sem erros
                tipoRetorno = TipoBaseSemantico.Int;
            }
            else {
                // Ambos os lados s�o num�ricos
                if (tEsq.equals(tDir)) {
                    tipoRetorno = tEsq;
                }
                else if (tEsq.equals(TipoBaseSemantico.Real)
                        && tDir.equals(TipoBaseSemantico.Int)) {
                    e.expDir = new IntParaReal(e.expDir);
                    tipoRetorno = TipoBaseSemantico.Real;
                }
                else {
                    e.expEsq = new IntParaReal(e.expEsq);
                    tipoRetorno = TipoBaseSemantico.Real;
                }
            }
            break;
        
        case "<":
        case "=":
            if (tEsq.equals(tDir)) {
                tipoRetorno = TipoBaseSemantico.Bool;
            }
            else if (tEsq.equals(TipoBaseSemantico.Real)
                    && tDir.equals(TipoBaseSemantico.Int)) {
                e.expDir = new IntParaReal(e.expDir);
                tipoRetorno = TipoBaseSemantico.Bool;
            }
            else if (tEsq.equals(TipoBaseSemantico.Int)
                    && tDir.equals(TipoBaseSemantico.Real)) {
                e.expEsq = new IntParaReal(e.expEsq);
                tipoRetorno = TipoBaseSemantico.Bool;
            }
            else {
                erros.reportar(101, "Imposs�vel realizar a opera��o ["
                        + e.operacao.token + "] entre os tipos" + tEsq
                        + " e " + tDir); 
                // Retornando bool apenas para prosseguir a checagem sem erros
                tipoRetorno = TipoBaseSemantico.Bool;
            }
            break;
        
        case "and":
        case "or":
            if (tEsq.equals(tDir) && tEsq.equals(TipoBaseSemantico.Bool)) {
                tipoRetorno = tEsq;
            }
            else {
                erros.reportar(101, "Imposs�vel realizar a opera��o ["
                        + e.operacao.token + "] entre os tipos" + tEsq
                        + " e " + tDir);
                // Retornando bool apenas para prosseguir a checagem sem erros
                tipoRetorno = TipoBaseSemantico.Bool;
            }
            break;
        }
        return tipoRetorno;
    }

    @Override
    public Object visitBloco(BLOCO b) {
        aConsVar.comecaEscopo();
        for (DVarConsCom c : b.lista) {
            c.accept(this);
        }
        aConsVar.terminaEscopo();
        return null;
    }

    @Override
    public Object visitBlocoExp(BlocoExp b) {
        aConsVar.comecaEscopo();
        for (DCons d : b.listaCons) {
            d.accept(this);
        }
        TipoSemantico t = (TipoSemantico) b.exp.accept(this);
        aConsVar.terminaEscopo();
        return t;
    }

    @Override
    public Object visitChamada(CHAMADA c) {
        VinculavelFuncProc vinculo = aFuncProc.get(c.id);
        
        if (vinculo == null) {
            erros.reportar(404, "Procedimento " + c.id + " n�o encontrado no "
                    + "ambiente!");
        }
        else {
            if (vinculo.isFunc) {
                erros.reportar(001, "O identificador chamado � uma fun��o,"
                        + " utilize CHAMADA apenas com procedimentos.");
            }
            if (c.listaExp.size() != vinculo.paramProc.size()){
                erros.reportar(002, "O n�mero de par�metros da chamada � "
                        + "diferente dos par�metros da assinatura do "
                        + "procedimento.");
            }
            else {
                PassagemTipoSemantico p1; // par�metro formal
                Object p2; // par�metro real
                
                for (int i = 0, count = c.listaExp.size(); i < count; i++) {
                    p1 = vinculo.paramProc.get(i);
                    Exp e = c.listaExp.get(i);
                    p2 = e.accept(this);
                    
                    if (! p1.isCopia) {
                        // Se a passagem de par�metros � por refer�ncia
                        if (p2 instanceof VinculavelConsVar) {
                            VinculavelConsVar v = (VinculavelConsVar) p2;
                            if (! v.isVar) {
                                erros.reportar(220, "O procedimento " + c.id
                                        + " exige passagem de par�metros por"
                                        + "refer�ncia. Constantes s�o inv�lidas!");
                            }
                        }
                        else {
                            erros.reportar(333, "O procedimento " + c.id
                                    + "exige passagem de par�metros por "
                                    + "refer�ncia. Para cham�-lo passe uma "
                                    + "vari�vel.");
                        }
                    }
                    
                    if (p2 instanceof VinculavelConsVar) {
                        VinculavelConsVar v = (VinculavelConsVar) p2;
                        if (v.tipo.equals(TipoBaseSemantico.Int)
                                && p1.tipo.equals(TipoBaseSemantico.Real)) {
                            // Adiciona o TypeCast na hierarquia de classes
                            e = new IntParaReal(e);
                        }
                        else if (! v.tipo.equals(p1.tipo)) {
                            erros.reportar(202, "O par�metro da chamada � "
                                    + "incompat�vel com o par�metro da"
                                    + " assinatura do procedimento.");
                        }
                    }
                    else {
                        if ( ((TipoSemantico) p2).equals(TipoBaseSemantico.Int)
                                && p1.tipo.equals(TipoBaseSemantico.Real)) {
                            // Adiciona o TypeCast na hierarquia de classes
                            e = new IntParaReal(e);
                        }
                        else if (! p1.tipo.equals((TipoSemantico) p2)) {
                            erros.reportar(202, "O par�metro da chamada � "
                                    + "incompat�vel com o par�metro da assinatura");
                        }
                    }
                }
            }
        }
        
        return null;
    }

    @Override
    public Object visitChamadaExp(ChamadaExp c) {
        VinculavelFuncProc f = aFuncProc.get(c.id);
        
        if (f == null) {
            erros.reportar(404, "Fun��o " + c.id + " n�o encontrada no "
                    + "ambiente!");
        }
        else {
            if (! f.isFunc) {
                erros.reportar(001, "O identificador chamado � um procedimento,"
                        + " utilize ChamadaExp apenas com fun��es.");
            }
            if (c.listaExp.size() != f.paramFunc.size()){
                erros.reportar(002, "O n�mero de par�metros da chamada � "
                        + "diferente dos par�metros da assinatura da "
                        + "fun��o.");
            }
            else {
                PassagemTipoSemantico p1; // par�metro formal
                Object p2; // par�metro real
                
                for (int i = 0, count = c.listaExp.size(); i < count; i++) {
                    p1 = f.paramFunc.get(i);
                    Exp e = c.listaExp.get(i);
                    p2 = e.accept(this);
                    
                    if (p2 instanceof VinculavelConsVar) {
                        VinculavelConsVar v = (VinculavelConsVar) p2;
                        if (v.tipo.equals(TipoBaseSemantico.Int)
                                && p1.tipo.equals(TipoBaseSemantico.Real)) {
                            // Adiciona o TypeCast na hierarquia de classes
                            e = new IntParaReal(e);
                        }
                        else if (! v.tipo.equals(p1.tipo)) {
                            erros.reportar(202, "O par�metro da chamada � "
                                    + "incompat�vel com o par�metro da"
                                    + " assinatura da fun��o.");
                        }
                    }
                    else {
                        if ( ((TipoSemantico) p2).equals(TipoBaseSemantico.Int)
                                && p1.tipo.equals(TipoBaseSemantico.Real)) {
                            // Adiciona o TypeCast na hierarquia de classes
                            e = new IntParaReal(e);
                        }
                        else if (! p1.tipo.equals((TipoSemantico) p2)) {
                            erros.reportar(202, "O par�metro da chamada � "
                                    + "incompat�vel com o par�metro da assinatura"
                                    + " da fun��o");
                        }
                    }
                }
            }
        }
        
        return f.tipoRetorno;
    }

    @Override
    public Object visitCom(Com c) {
        c.comando.accept(this);
        return null;
    }

    @Override
    public Object visitCons(Cons c) {
        TipoSemantico t = (TipoSemantico) c.tipo.accept(this);
        aConsVar.add(c.id, false, t);
        return null;
    }

    @Override
    public Object visitConsComp(ConsComp c) {
        TipoSemantico t = (TipoSemantico) c.tipo.accept(this);
        aConsVar.add(c.id, false, t);
        return null;
    }

    @Override
    public Object visitConsExt(ConsExt c) {
        TipoSemantico t = (TipoSemantico) c.tipo.accept(this);
        aConsVar.add(c.id, false, t);
        return null;
    }

    @Override
    public Object visitDC(DC d) {
        d.cons.accept(this);
        return null;
    }

    @Override
    public Object visitDecCons(DecCons d) {
        d.cons.accept(this);
        return null;
    }

    @Override
    public Object visitDecVar(DecVar d) {
        d.var.accept(this);
        return null;
    }

    @Override
    public Object visitDV(DV d) {
        d.var.accept(this);
        return null;
    }

    @Override
    public Object visitFuncao(Funcao f) {
        TipoSemantico retorno = (TipoSemantico) f.tipo.accept(this);
        
        List<PassagemTipoSemantico> l = new ArrayList<PassagemTipoSemantico>();
        aConsVar.comecaEscopo();
        for (Parametro p : f.listaParam) {
            l.add((PassagemTipoSemantico) p.accept(this));
        }
        f.exp.accept(this);
        aConsVar.terminaEscopo();
        aFuncProc.lookupFuncProc(f.id, l, retorno);
        return retorno;
    }

    @Override
    public Object visitIf(IF i) {
        TipoSemantico t = (TipoSemantico) i.exp.accept(this);
        if (! t.equals(TipoBaseSemantico.Bool)) {
            erros.reportar(203, "IF com express�o do tipo " + t + " inv�lido.");
        }
        i.comandoVerdade.accept(this);
        i.comandoFalso.accept(this);
        return null;
    }

    @Override
    public Object visitIndexada(Indexada i) {
        // TODO
        //return i.var.accept(this);
        return TipoBaseSemantico.Real;
    }
    
    @Override
    public Object visitIntParaReal(IntParaReal i) {
        return TipoBaseSemantico.Real;
    }

    @Override
    public Object visitLiteralBool(LiteralBool l) {
        return TipoBaseSemantico.Bool;
    }

    @Override
    public Object visitLiteralInt(LiteralInt l) {
        return TipoBaseSemantico.Int;
    }
    
    @Override
    public Object visitLiteralReal(LiteralReal l) {
        return TipoBaseSemantico.Real;
    }

    @Override
    public Object visitMenos(Menos m) {
         TipoSemantico t = (TipoSemantico) m.exp.accept(this);
         if (t.equals(TipoBaseSemantico.Int)
                 || t.equals(TipoBaseSemantico.Real)) {
             return t;
         }
         else {
             erros.reportar(101, "Opera��o uni�ria de Menos inv�lida para " + t);
             return TipoBaseSemantico.Int;
         }
    }

    @Override
    public Object visitNao(Nao n) {
        TipoSemantico t = (TipoSemantico) n.exp.accept(this);
        if (t.equals(TipoBaseSemantico.Bool)) {
            return t;
        }
        else {
            erros.reportar(102, "Opera��o un�ria de Nega��o inv�lida para " + t);
            return TipoBaseSemantico.Bool;
        }
    }

    @Override
    public Object visitParArrayCopia(ParArrayCopia p) {
        aConsVar.add(p.id, true, paraSemantico(p.tipo, p.dimensao));
        return new PassagemTipoSemantico(paraSemantico(p.tipo, p.dimensao));
    }

    @Override
    public Object visitParArrayRef(ParArrayRef p) {
        aConsVar.add(p.id, true, paraSemantico(p.tipo, p.dimensao));
        return new PassagemTipoSemantico(paraSemantico(p.tipo, p.dimensao), false);
    }

    @Override
    public Object visitParBaseCopia(ParBaseCopia p) {
        aConsVar.add(p.id, true, paraSemantico(p.tipo));
        return new PassagemTipoSemantico(paraSemantico(p.tipo));
    }

    @Override
    public Object visitParBaseRef(ParBaseRef p) {
        aConsVar.add(p.id, true, paraSemantico(p.tipo));
        return new PassagemTipoSemantico(paraSemantico(p.tipo), false);
    }

    @Override
    public Object visitProcedimento(Procedimento p) {
        if (! aFuncProc.contem(p.id)) {
            List<PassagemTipoSemantico> l = new ArrayList<PassagemTipoSemantico>();
            for (Parametro param : p.listaParam) {
                l.add((PassagemTipoSemantico) param.accept(this));
            }
            
            aFuncProc.lookupFuncProc(p.id, l);
            p.comando.accept(this);
        }
        else {
            erros.reportar(200, "O procedimento " + p.id + " j� est� no ambiente.");
        }
        
        return null;
    }
    
    @Override
    public Object visitPrograma(Programa p) {
        for (Dec d : p.declaracoes) {
            d.accept(this);
        }
        return null;
    }

    @Override
    public Object visitSimples(Simples s) {
        VinculavelConsVar v = aConsVar.get(s.id);
        // Se for nulo reporta o erro e assume uma vari�vel do tipo inteiro
        if (v == null) {
            erros.reportar(404, "Identificador " + s.id
                    + " n�o encontrado no ambiente!");
            v = new VinculavelConsVar(true, TipoBaseSemantico.Int);
        }
        return v;
    }

    @Override
    public Object visitTipoArray(TipoArray t) {
        TipoArraySemantico s = new TipoArraySemantico(
            this.paraSemantico(t.base),
            t.exp.size()
        );
        return s; 
    }

    @Override
    public Object visitTipoBase(TipoBase t) {
        return this.paraSemantico(t.tipo);
    }

    @Override
    public Object visitVarExp(VarExp v) {
        return v.var.accept(this);
    }

    @Override
    public Object visitVarInic(VarInic v) {
        TipoSemantico t = (TipoSemantico) v.nomeTipo.accept(this);
        if (! aConsVar.add(v.id, true, t)) {
            erros.reportar(200, "O identificador " + v.id
                    + " n�o foi adicionado no ambiente.");
        }
        return null;
    }

    @Override
    public Object visitVarInicComp(VarInicComp v) {
        TipoSemantico t = (TipoSemantico) v.nomeTipo.accept(this);
        if (! aConsVar.add(v.id, true, t)) {
            erros.reportar(200, "O identificador " + v.id
                    + " n�o foi adicionado no ambiente.");
        }
        return null;
    }

    @Override
    public Object visitVarInicExt(VarInicExt v) {
        TipoSemantico t = (TipoSemantico) v.nomeTipo.accept(this);
        if (! aConsVar.add(v.id, true, t)) {
            erros.reportar(200, "O identificador " + v.id
                    + " n�o foi adicionado no ambiente.");
        }
        return null;
    }

    @Override
    public Object visitVarNaoInic(VarNaoInic v) {
        TipoSemantico t = (TipoSemantico) v.nomeTipo.accept(this);
        if (! aConsVar.add(v.id, true, t)) {
            erros.reportar(200, "O identificador " + v.id
                    + " n�o foi adicionado no ambiente.");
        }
        return null;
    }

    @Override
    public Object visitWhile(WHILE w) {
        TipoSemantico t = (TipoSemantico) w.exp.accept(this);
        if (! t.equals(TipoBaseSemantico.Bool)) {
            erros.reportar(103, "A condi��o do WHILE deve ser do tipo bool."
                    + " Um " + t + " foi passado.");
        }
        w.comando.accept(this);
        return null;
    }
}
