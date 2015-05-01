package symboltable;

import java.util.List;

import grammarsymbols.TokenType;

/** Base class for entries in the Symbol Table */
public class SymbolTableEntry {
	// Name of the entry
	private String name;
	// Type of the entry
	private TokenType type;
	private boolean isNull = false;		// For null offsets
	private boolean reserved = false; 
	
	/* Constructors */
	public SymbolTableEntry(){
	}
	
	public SymbolTableEntry(String name){
		this.name = name;
	}
	
	public SymbolTableEntry(String name2, TokenType type) {
		this.name = name2;
		this.type = type;
	}

	/* Getters */
	public String getName(){
		return name;
	}
	public TokenType getType(){
		return type;
	}
	public int getAddress(){
		return Integer.MAX_VALUE;	// Dummy value
	}
	
	/* Setters */
	public void setName(String newName){
		name = newName;
	}
	public void setType(TokenType type){
		this.type = type;
	}
	
	/* Methods to check for type of entry */
	public boolean isArray(){
		return false;
	}
	public boolean isConstant(){
		return false;
	}
	
	public boolean isFunction(){
		return false;
	}
	public boolean isProcedure(){
		return false;
	}
	public boolean isVariable(){
		return false;
	}
	public boolean isKeyword(){
		return false;
	}
	public boolean isFunctionResult() {
		return false;
	}
	public boolean isParameter(){
		return false;
	}
	
	public boolean isNull(){
		return isNull;
	}
	
	public void makeNull(){
		isNull = true;
	}
	
	public boolean isReserved(){
		return reserved;
	}
	
	public void makeReserved(){
		reserved = true;
	}
	
	// Methods to be overridden
	public void addParameter(ParmInfoEntry p){
	}
	public void setNumberOfParameters(int paramCount) {

	}
	public List<ParmInfoEntry> getParameterInfo(){
		return null;
	}
	
	public void print(){
		System.out.println("Base Class - SymbolTable Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println();
	}

	

}
