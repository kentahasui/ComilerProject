package token;

import grammarsymbols.TokenType;


public class Token{
	public TokenType type; 
	
	public Token(TokenType type){
		this.type = type;
	}
	
	public Token(){
		super();
	}
	
	/** A function to find this token's TokenType 
	 * 
	 * @return The enumerated TokenType for this Token
	 */
	public TokenType getType(){
		return this.type;
	}
	
	/** Gets the value of the token. Returns the token type for simple Tokens. */
	public String getValue(){
		return this.type.toString();
	}
	
	/** Gets the String representation of an Operator token's type. Returns 
	 * the empty string for non-operator tokens
	 */
	public String getOpType(){
		return "";
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other instanceof Token){
			Token otherToken = (Token)other;
			if(this.getType() == otherToken.getType()){
				if(this.getValue().equals(((Token) other).getValue())){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.getValue().hashCode();
	}

}
