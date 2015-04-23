package token;

import grammarsymbols.TokenType;

public class Operator extends Token{
	/** String representation of a pascal operator */
	private String valueString;
	/** String representation of a TVI operator */
	private String tviCode;
	
	/** Constructor for an operator token. 
	 * 
	 * @param type : Can be a RELOP, ADDOP, or MULOP
	 * @param value : Represents the type
	 */
	public Operator(TokenType type, String valueString){
		super(type);
		this.valueString = valueString;
		this.tviCode = toTVICode(valueString);
	}
	
	/** Converts a pascal operator to a tvi operator. 
	 * AND, OR, DIV, and MOD return the string "error", since they do not have valid 
	 * tvi opcode equivalents
	 */
	private String toTVICode(String s){
		if(type == TokenType.RELOP){
			if("=".equals(s)) return "beq";
			else if("<>".equals(s)) return "bne";
			else if("<".equals(s)) return "blt";
			else if(">".equals(s)) return "bgt";
			else if("<=".equals(s)) return "ble";
			else if(">=".equals(s)) return "bge";
			else return "error";
		}
		else if(type == TokenType.ADDOP){
			if("+".equals(s)) return "add";
			else if("-".equals(s)) return "sub";
			else return "error";
		}
		else if(type == TokenType.MULOP){
			if("*".equals(s)) return "mul";
			if("/".equals(s)) return "div";
			else return "error";
		}
		else return "error";
	}
	
	/** Returns the tvi code for an operator 
	 * Valid tvi codes include beq, bne, blt, bgt, ble, bge, add, sub, mul, and div*/
	public String getTVICode(){
		return tviCode;
	}
	
	public String getValue(){
		return valueString;
	}
	

}
