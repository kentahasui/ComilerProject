package symboltable;
import java.util.*;

import grammarsymbols.TokenType;

public class ProcedureEntry extends SymbolTableEntry implements SubroutineEntry{
	int numberOfParameters;
	List<ParmInfoEntry> parameterInfo;
	
	/* Constructors */
	public ProcedureEntry(String name){
		super(name, TokenType.PROCEDURE);
		parameterInfo = new LinkedList<ParmInfoEntry>();
	}
	
	public ProcedureEntry(String name, int numberOfParameters){
		this(name);
		this.numberOfParameters = numberOfParameters;
	}
	
	public ProcedureEntry(String name, int numberOfParameters, List<ParmInfoEntry> paramInfo){
		this(name);
		this.numberOfParameters = numberOfParameters;
		this.parameterInfo = paramInfo;
	}
	
	/* Getters */
	public int getNumberOfParameters(){
		return numberOfParameters;
	}
	@Override
	public List<ParmInfoEntry> getParameterInfo(){
		return parameterInfo;
	}
	
	/* Setters */
	@Override
	public void setNumberOfParameters(int number){
		numberOfParameters = number;
	}
	public void setParameterInfo(List<ParmInfoEntry> paramInfo){
		this.parameterInfo = paramInfo;
	}
	
	/* Parameter manipulation*/
	@Override
	public void addParameter(ParmInfoEntry p){
		parameterInfo.add(p);
	}
	public ParmInfoEntry getParameter(int index){
		return parameterInfo.get(index);
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
		for(ParmInfoEntry p: this.getParameterInfo()){
			System.out.print(p.toString());
		}
		System.out.println();
		System.out.println();
	}

}
