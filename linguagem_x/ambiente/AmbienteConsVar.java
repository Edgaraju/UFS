package ambiente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import checagem.*;

/**
 * Gerencia a tabela de s�mbolos de vari�veis e constantes.
 * Atua como uma f�brica dos Flyweights para gerar objetos da classe concreta
 * VinculavelConsVar.
 */
public class AmbienteConsVar {
    /**
     * Hash Identificador -> VinculavelConsVar, que cont�m os dados sobre tipo. 
     */
    public static final HashMap<String, ArrayList<VinculavelConsVar>> tabela =
            new HashMap<String, ArrayList<VinculavelConsVar>>();
    
    /**
     * N�vel atual de escopo da tabela de s�mbolos
     */
    public static int nivel = 0;
    
    /**
     * Pilha que cont�m a lista com todos os identificadores de cada escopo.
     */
    public static Stack<ArrayList<String>> simbolosEscopo =
            new Stack<ArrayList<String>>();
    
    /**
     * Adiciona um identificador na pilha
     * 
     * @param id
     */
    private void adicionaPilha(String id) {
        ArrayList<String> lista;
        if (simbolosEscopo.isEmpty()) {
            lista = new ArrayList<String>();
            lista.add(id);
            simbolosEscopo.push(lista);
        }
        else {
            lista = simbolosEscopo.peek();
            lista.add(id);
        }
    }
    
    /**
     * Remove um identificador da pilha
     * 
     * @param id
     */
    public void deletaPilha(String id) {
        ArrayList<String> lista = simbolosEscopo.peek();
        lista.remove(id);
    }
    
    /**
     * Adiciona uma vincul�vel na lista do hash dentro do n�vel da tabela,
     * se ainda n�o existir uma vincul�vel naquele mesmo n�vel de escopo.
     * 
     * @param id
     * @param b
     * @param t
     * @return
     */
    public boolean add(String id, boolean b, TipoSemantico t) {
        boolean r = false;
        ArrayList<VinculavelConsVar> listaItens = tabela.get(id);
        VinculavelConsVar x = new VinculavelConsVar(b, t, nivel);
        
        // Se a lista ainda n�o existe, cria
        if (listaItens == null) {
            listaItens = new ArrayList<VinculavelConsVar>();
            listaItens.add(x);
            tabela.put(id, listaItens);
            this.adicionaPilha(id);
            System.out.println(
                "Criando ConsVar no ambiente : " + x.isVar + " " + x.tipo + " " + id
            );
            r = true;
        }
        else {
            // A lista j� existe, verifica se s�o escopos diferentes
            if (! listaItens.contains(x)) {
                listaItens.add(x);
                this.adicionaPilha(id);
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
     * Retorna a inst�ncia de um elemento na tabela dentro do escopo atual.
     * 
     * @param id
     * @return
     */
    public VinculavelConsVar get(String id) {
        ArrayList<VinculavelConsVar> listaItens = tabela.get(id);
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
     * @param n
     * @return
     */
    public boolean contem(String id, int n) {
        ArrayList<VinculavelConsVar> listaItens = tabela.get(id);
        if (listaItens != null) {
            for (VinculavelConsVar x : listaItens)
                if (x.nivelEscopo == n) return true;
        }
        return false;
    }
    
    /**
     * Remove um identificador da tabela de s�mbolos
     * 
     * @param id
     * @return
     */
    public VinculavelConsVar deleta(String id) {
        ArrayList<VinculavelConsVar> l = tabela.get(id);
        return l.remove(l.size() - 1);
    }
    
    /**
     * Incrementa um n�vel de escopo
     */
    public void comecaEscopo() {
        ArrayList<String> lista = new ArrayList<String>();
        nivel++;
        simbolosEscopo.push(lista);
    }
    
    /**
     * Decrementa um n�vel de escopo
     */
    public void terminaEscopo() {
        ArrayList<String> lista = simbolosEscopo.pop();
        for (String st : lista)
            this.deleta(st);
        nivel--;
    }
}
