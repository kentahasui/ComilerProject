package token;

import grammarsymbols.TokenType;

public class Operator extends Token{
	/** The integer representation of an operator token's value. 
	 *  Ranges: RELOP = 1 to 6. ADDOP = 1 to 3. MULOP = 1 to 5.
	 */
	private int value;
	private String valueString;
	
	/** Constructor for an operator token. 
	 * 
	 * @param type : Can be a RELOP, ADDOP, or MULOP
	 * @param value : Represents the type
	 */
	public Operator(TokenType type, String valueString){
		super(type);
		this.valueString = valueString;
		this.value = stringToIntValue(valueString);
	}
	
	/** Converts the String representation of the operator 
	 *  to its int equivalent
	 * @param s The string value to convert
	 * @return The int value equivalent
	 */
	public int stringToIntValue(String s){
		if(type == TokenType.RELOP){
			if("=".equals(s)) return 1;
			if("<>".equals(s)) return 2;
			if("<".equals(s)) return 3;
			if(">".equals(s)) return 4;
			if("<=".equals(s)) return 5;
			if(">=".equals(s)) return 6;
		}
		else if(type == TokenType.ADDOP){
			if("+".equals(s)) return 1;
			if("-".equals(s)) return 2;
			if("OR".equals(s)) return 3;
		}
		else if(type == TokenType.MULOP){
			if("*".equals(s)) return 1;
			if("/".equals(s)) return 2;
			if("DIV".equals(s)) return 3;
			if("MOD".equals(s)) return 4;
			if("AND".equals(s)) return 5;
		}
		return 0;
	}
	
	public String getValue(){
		return String.valueOf(value);
	}
	
	public int getIntValue(){
		return value;
	}
	
	/** Returns a string representation of this Operator Token's value field
	 *  Returns an error string if the integer is not a valid value
	 * 
	 * @return A String
	 */
	public String getOpType(){
		return valueString;
	}

}
