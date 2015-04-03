package symboltable;
import grammarsymbols.TokenType;

public class ConstantEntry extends SymbolTableEntry{
	
	public ConstantEntry(String name){
		super(name);
	}
	public ConstantEntry(String name, TokenType type){
		super(name, type);
	}
	
	@Override
	public boolean isConstant(){
		return true;
	}
	
	@Override
	public void print () {
		System.out.println("Constant Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println();
	}

}
