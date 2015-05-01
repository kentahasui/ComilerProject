package symboltable;
import grammarsymbols.TokenType;

/** Class to keep track of parameter information 
 * Represents a single parameter*/
public class ParmInfoEntry {
	/** Token type: must be INTEGER or REAL */
	private TokenType type;
	private boolean isArray;
	// Upper and lower bounds for array
	private int upperBound;
	private int lowerBound;
	
	/**
	 * Constructor for parminfo entry
	 * @param type Either INTEGER or REAL
	 * @param isArray True if the parameter is an array, false otherwise
	 */
	public ParmInfoEntry(TokenType type, boolean isArray){
		this.type = type;
		this.isArray = isArray;
		upperBound = 0; lowerBound = 0;
	}
	
	public boolean isArray(){
		return isArray;
	}
	
	public TokenType getType(){
		return type;
	}
	
	// Getters and setters
	public void setLowerBound(int num){
		upperBound = num;
	}
	public void setUpperBound(int num){
		upperBound = num;
	}
	public void setBounds(int lowerBound, int upperBound){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	public int getLowerBound(){
		return lowerBound;
	}
	public int getUpperBound(){
		return upperBound;
	}
	
	// ToString
	@Override
	public String toString(){
		// Simple or array variable
		String S_A = "Simple";
		if(isArray) S_A = "Array";
		return "[" + type.toString() + ": " + S_A + "] ";
	}

}
