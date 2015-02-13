package token;


public class Token {
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
	
	// Will be overriden by child classes
	public String getValue(){
		return "";
	}
	
	public String getOpType(){
		return "";
	}

}
