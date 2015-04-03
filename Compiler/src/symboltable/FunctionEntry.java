package symboltable;
import java.util.*;

import grammarsymbols.TokenType;

public class FunctionEntry extends SymbolTableEntry{
	int numberOfParameters;
	List<TokenType> parameterInfo;
	TokenType result;
	
	public FunctionEntry(String name){
		super(name, TokenType.FUNCTION);
		parameterInfo = new LinkedList<TokenType>();
	}
	
	public FunctionEntry(String name, int numberOfParameters, List<TokenType> parameterInfo, TokenType result){
		this.numberOfParameters = numberOfParameters;
		this.parameterInfo = parameterInfo;
		this.result = result;
	}
	
	/* Getters */
	public int getNumberOfParameters(){
		return numberOfParameters;
	}
	public List<TokenType> getParameterInfo(){
		return parameterInfo;
	}
	public TokenType getResultType(){
		return result;
	}
	
	/* Setters */
	public void setNumberOfParameters(int number){
		numberOfParameters = number;
	}
	public void setParameterInfo(List<TokenType> paramInfo){
		this.parameterInfo = paramInfo;
	}
	public void setResultType(TokenType result){
		this.result = result;
	}
	
	@Override
	public boolean isFunction(){
		return true;
	}
	
	@Override
	public void print () {
		System.out.println("Function Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   NumParams: " + this.getNumberOfParameters());
		System.out.print("   ParamInfo: ");
		for(TokenType t: this.getParameterInfo()){
			System.out.print(t.toString());
		}
		System.out.println();
		System.out.println("   Result  : " + this.getResultType());
		System.out.println();
	}

}
