package ambiente;

import java.util.HashMap;
import java.util.List;
import checagem.*;

/**
 * Atua como uma f�brica dos Flyweights para gerar objetos da classe concreta
 * VinculavelFuncProc
 */
public class AmbienteFuncProc {
    public static final HashMap<String, Vinculavel> tabela = new HashMap<>();
    
    /**
     * Procura ou cria na tabela de s�mbolos uma fun��o
     * 
     * @param id
     * @param p
     * @param r
     * @return
     */
    public VinculavelFuncProc lookupFuncProc(String id, List<TipoSemantico> p, TipoSemantico r) {
        VinculavelFuncProc x = (VinculavelFuncProc) tabela.get(id);
        if(x == null) {
            x = new VinculavelFuncProc(p, r);
            tabela.put(id, x);
            System.out.println("Criando FuncProc no ambiente : " + x.isFunc + " " + id);
        }
        return x;
    }
    
    /**
     * Procura ou cria na tabela de s�mbolos um procedimento
     * 
     * @param id
     * @param p
     * @return
     */
    public VinculavelFuncProc lookupFuncProc(String id, List<PassagemTipoSemantico> p) {
        VinculavelFuncProc x = (VinculavelFuncProc) tabela.get(id);
        if(x == null) {
            x = new VinculavelFuncProc(p);
            tabela.put(id, x);
            System.out.println("Criando FuncProc no ambiente : " + x.isFunc + " " + id);
        }
        return x;
    }
}
