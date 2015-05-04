package symboltable;
import java.math.BigDecimal;

import grammarsymbols.TokenType;

public class ConstantEntry extends SymbolTableEntry{
	int intValue;
	double realValue;
	
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
		if(this.getType() == TokenType.INTEGER){
			intValue = Integer.parseInt(this.getName());
		}
		if(this.getType() == TokenType.REAL){
			BigDecimal bd = new BigDecimal(this.getName());
			realValue = bd.doubleValue();
			this.setName(bd.toPlainString());
		}
	}
	
	public int getIntValue(){
		return intValue;
	}
	
	public double getRealValue(){
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
