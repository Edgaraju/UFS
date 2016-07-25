package stm;

public class AssignStm extends Stm {

	public String id;
	public Exp exp;
	public AssignStm(String i, Exp e) { id = i; exp = e; }
	
	@Override
	String print() {
		// TODO Auto-generated method stub
		return id + " " + exp.print();
	}
	
}
