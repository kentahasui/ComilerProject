package symboltable;
import java.util.*;

import grammarsymbols.TokenType;

public class FunctionEntry extends SymbolTableEntry implements SubroutineEntry{
	int numberOfParameters;
	// Linked list of parameter info types
	List<ParmInfoEntry> parameterInfo;
	SymbolTableEntry result;
	
	public FunctionEntry(String name){
		super(name, TokenType.FUNCTION);
		parameterInfo = new LinkedList<ParmInfoEntry>();
	}
	
	public FunctionEntry(String name, int numberOfParameters, List<ParmInfoEntry> parameterInfo, SymbolTableEntry result){
		this.numberOfParameters = numberOfParameters;
		this.parameterInfo = parameterInfo;
		this.result = result;
	}
	
	/* Getters */
	public int getNumberOfParameters(){
		return numberOfParameters;
	}
	@Override
	public List<ParmInfoEntry> getParameterInfo(){
		return parameterInfo;
	}
	public SymbolTableEntry getResult(){
		return result;
	}
	
	/* Setters */
	@Override
	public void setNumberOfParameters(int number){
		numberOfParameters = number;
	}
	public void setParameterInfo(List<ParmInfoEntry> paramInfo){
		this.parameterInfo = paramInfo;
	}
	public void setResult(SymbolTableEntry result){
		this.result = result;
	}
	
	/* Parameter manipulation*/
	@Override
	public void addParameter(ParmInfoEntry p){
		parameterInfo.add(p);
	}
	public ParmInfoEntry getParameter(int index){
		return parameterInfo.get(index);
	}
	public List<ParmInfoEntry> getParameterList(){
		return parameterInfo;
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
		for(ParmInfoEntry p: this.getParameterInfo()){
			System.out.print(p.toString());
		}
		System.out.println();
		System.out.println("   Result  : " + this.getResult().getName());
		System.out.println();
	}

}
