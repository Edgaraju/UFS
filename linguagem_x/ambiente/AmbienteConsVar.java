package ambiente;

import java.util.ArrayList;
import java.util.HashMap;

import checagem.*;

/**
 * Gerencia a tabela de s�mbolos de vari�veis e constantes.
 * Atua como uma f�brica dos Flyweights para gerar objetos da classe concreta
 * VinculavelConsVar.
 */
public class AmbienteConsVar {
    public static final HashMap<String, Vinculavel> tabela = new HashMap<>();
    public static final HashMap<String, ArrayList<VinculavelConsVar>> tabela2 =
            new HashMap<String, ArrayList<VinculavelConsVar>>();
    
    /**
     * Adiciona uma vincul�vel na lista do hash dentro do escopo padr�o 0, se
     * ainda n�o existir uma vincul�vel naquele mesmo n�vel de escopo.
     * 
     * @param id
     * @param b
     * @param t
     * @return
     */
    public boolean add(String id, boolean b, TipoSemantico t) {
        return this.add(id, b, t, 0);
    }
    
    /**
     * Adiciona uma vincul�vel na lista do hash dentro do escopo especificado,
     * se ainda n�o existir uma vincul�vel naquele mesmo n�vel de escopo.
     * 
     * @param id
     * @param b
     * @param t
     * @param nivel
     * @return
     */
    public boolean add(String id, boolean b, TipoSemantico t, int nivel) {
        boolean r = false;
        ArrayList<VinculavelConsVar> listaItens = tabela2.get(id);
        VinculavelConsVar x = new VinculavelConsVar(b, t, nivel);
        
        // Se a lista ainda n�o existe, cria
        if (listaItens == null) {
            listaItens = new ArrayList<VinculavelConsVar>();
            listaItens.add(x);
            tabela2.put(id, listaItens);
            System.out.println(
                "Criando ConsVar no ambiente : " + x.isVar + " " + x.tipo + " " + id
            );
            r = true;
        }
        else {
            // A lista j� existe, verifica se s�o escopos diferentes
            if (! listaItens.contains(x)) {
                listaItens.add(x);
                System.out.println(
                    "Criando ConsVar no ambiente no [Escopo " + nivel + "]: " +
                    x.isVar + " " + x.tipo + " " + id 
                );
                r = true;
            }
            else System.out.println("Ja tem [" + x.isVar + " " + x.tipo + " " + id + "] !");
        }
        return r;
    }
    
    /**
     * Retorna a inst�ncia de um elemento na tabela dentro do escopo padr�o.
     * 
     * @param id
     * @return
     */
    public VinculavelConsVar get(String id) {
        return this.get(id, 0);
    }
    
    /**
     * Retorna a inst�ncia de um elemento na tabela dentro do escopo
     * especificado.
     * 
     * @param id
     * @param nivel
     * @return
     */
    public VinculavelConsVar get(String id, int nivel) {
        ArrayList<VinculavelConsVar> listaItens = tabela2.get(id);
        for (VinculavelConsVar x : listaItens)
            if (x.nivelEscopo == nivel) return x;
        return null;
    }
    
    /**
     * Retorna true se a tabela cont�m o s�mbolo dentro do escopo padr�o.
     * 
     * @param id
     * @return
     */
    public boolean contem(String id) {
        return this.contem(id, 0);
    }
    
    /**
     * Retorna true se a tabela cont�m o s�mbolo dentro do escopo especificado.
     * 
     * @param id
     * @param nivel
     * @return
     */
    public boolean contem(String id, int nivel) {
        ArrayList<VinculavelConsVar> listaItens = tabela2.get(id);
        if (listaItens != null) {
            for (VinculavelConsVar x : listaItens)
                if (x.nivelEscopo == nivel) return true;
        }
        return false;
    }
    
    public VinculavelConsVar deleta(String id) {
        return deleta(id, 0);
    }
    
    public VinculavelConsVar deleta(String id, int nivel) {
        // TODO
        return null;
    }
    
    public void comecaEscopo() {
        // TODO
    }
    
    public void terminaEscopo() {
        // TODO
    }
}
