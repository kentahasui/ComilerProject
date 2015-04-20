package symboltable;
import grammarsymbols.TokenType;

public class ConstantEntry extends SymbolTableEntry{
	
	int intValue;
	float realValue;
	
	public ConstantEntry(String name){
		super(name);
	}
	
	public ConstantEntry(String name, TokenType type){
		super(name, type);
		setValue();
	}
	
	@Override
	public boolean isConstant(){
		return true;
	}
	
	public void setValue(){
		if(this.getType() == TokenType.INTCONSTANT){
			intValue = Integer.parseInt(this.getName());
		}
	}
	
	public int getIntValue(){
		return intValue;
	}
	
	public float getRealValue(){
		return realValue;
	}
	
	@Override
	public void print () {
		System.out.println("Constant Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println();
	}

}
