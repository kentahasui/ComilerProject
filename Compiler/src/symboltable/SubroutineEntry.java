package symboltable;

import grammarsymbols.TokenType;

import java.util.List;

/** Interface for ProcedureEntries and FunctionEntries, since they have many of the same methods */
public interface SubroutineEntry {
	
	/* Getters */
	public String getName();
	public TokenType getType();
	public int getNumberOfParameters();
	public List<ParmInfoEntry> getParameterInfo();
	
	/* Setters */
	public void setName(String newName);
	public void setType(TokenType type);
	public void setNumberOfParameters(int number);
	public void setParameterInfo(List<ParmInfoEntry> paramInfo);
	
	/* Parameter manipulation*/
	public void addParameter(ParmInfoEntry p);
	public ParmInfoEntry getParameter(int index);
	
	public default boolean isFunction(){
		return false;
	}
	public default boolean isProcedure(){
		return false;
	}
	
	public void print();
}


