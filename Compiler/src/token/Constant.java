package token;

public class Constant extends Token{
	/** String representation of the constant. Will eventually
	 *   be a pointer to a symbol table entry. **/
	public String lexeme;
	
	/** Constructor for a Constant Token
	 * 
	 * @param type : Can be either a INTCONSTANT or a REALCONSTANT
	 * @param lexeme: String representation of the constant. Will eventually
	 *                be a pointer to a symbol table entry. 
	 */
	public Constant(TokenType type, String lexeme){
		super(type);
		this.lexeme = lexeme;
	}

	/** Returns a String representation of the constant. Will eventually
	 *   be a pointer to a symbol table entry. **/
	@Override
	public String getValue(){
		return lexeme;
	}
	

}
