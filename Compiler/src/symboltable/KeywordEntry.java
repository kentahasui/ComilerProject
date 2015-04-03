package symboltable;
import token.*;

/** Class to insert keywords into the keyword table */
public class KeywordEntry extends SymbolTableEntry{
	Token token;
	
	/** Constructor */
	public KeywordEntry(String name, Token token){
		super(name, token.getType());
		this.token = token;
	}
	
	/** Returns the token encapsulated in this entry */
	public Token getToken(){
		return token;
	}
	
	@Override
	public boolean isKeyword(){
		return true;
	}
	
	public void print(){
		System.out.println("Keyword Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println();
	}
	
	

}
