package sintaxe_abstrata;

public class VarInic extends DVar {
    public Tipo nomeTipo;
    public String id;
    public Exp exp;
    
    public VarInic(Tipo nome, String id, Exp exp) {
        this.nomeTipo = nome;
        this.id = id;
        this.exp = exp;
    }
    
    @Override
    public Object accept(Visitor vis) {
        return vis.visitVarInic(this);
    }
    
    @Override
    public String toString() {
        return "VarInic: (Tipo " + nomeTipo + ", " + id + ", Exp " + exp + ")";
    }
    
}
