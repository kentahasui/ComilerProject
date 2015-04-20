package symboltable;
import java.util.*;

import grammarsymbols.TokenType;

public class ProcedureEntry extends SymbolTableEntry{
	int numberOfParameters;
	List<TokenType> parameterInfo;
	
	/* Constructors */
	public ProcedureEntry(String name){
		super(name, TokenType.PROCEDURE);
		parameterInfo = new LinkedList<TokenType>();
	}
	
	public ProcedureEntry(String name, int numberOfParameters){
		this(name);
		this.numberOfParameters = numberOfParameters;
	}
	
	public ProcedureEntry(String name, int numberOfParameters, List<TokenType> paramInfo){
		this(name);
		this.numberOfParameters = numberOfParameters;
		this.parameterInfo = paramInfo;
	}
	
	/* Getters */
	public int getNumberOfParameters(){
		return numberOfParameters;
	}
	public List<TokenType> getParameterInfo(){
		return parameterInfo;
	}
	
	/* Setters */
	public void setNumberOfParameters(int number){
		numberOfParameters = number;
	}
	public void setParameterInfo(List<TokenType> paramInfo){
		this.parameterInfo = paramInfo;
	}
	
	@Override
	public boolean isProcedure(){
		return true;
	}
	
	@Override
	public void print () {
		System.out.println("Procedure Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   NumParams: " + this.getNumberOfParameters());
		System.out.print("   ParamInfo: ");
		for(TokenType t: this.getParameterInfo()){
			System.out.print(t.toString());
		}
		System.out.println();
		System.out.println();
	}

}
