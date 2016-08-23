package checagem;

public class PassagemTipoSemantico {
    public TipoSemantico tipo;
    public boolean isCopia;
    
    /**
     * Construtor b�sico, apenas com o tipo sem�ntico
     * Passagem de parametro padr�o � por c�pia
     * 
     * @param t
     */
    public PassagemTipoSemantico(TipoSemantico t) {
        this.tipo = t;
        this.isCopia = true;
    }
    
    /**
     * Construtor para explicitar tipo da passagem de parametro
     * 
     * @param t
     * @param b
     */
    public PassagemTipoSemantico(TipoSemantico t, boolean b) {
        this.tipo = t;
        this.isCopia = b;
    }
    
    @Override
    public String toString() {
        return (isCopia) ? "(valor, " + tipo + ")" : "(ref, " + tipo + ")"; 
    }
}
