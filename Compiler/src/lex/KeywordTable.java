package lex;
import grammarsymbols.TokenType;

import java.util.HashMap;

import token.*;

/** A table class, which represents the keywords in Pascal */ 
public class KeywordTable{
	HashMap<String, Token> table;
	
	/** Constructor for a keyword table. 
	 *  Initializes the table so all of the keywords are reserved */
	public KeywordTable(){
		table = new HashMap<String, Token>();
		reserveKeywords();
	}
	
	/** Method that places all Pascal Keywords in the table */
	private void reserveKeywords(){
		// Reserve the normal keywords
		table.put("PROGRAM", new Token(TokenType.PROGRAM));
		table.put("BEGIN", new Token(TokenType.BEGIN));
		table.put("END", new Token(TokenType.END));
		table.put("VAR", new Token(TokenType.VAR));
		table.put("FUNCTION", new Token(TokenType.FUNCTION));
		table.put("PROCEDURE", new Token(TokenType.PROCEDURE));
		table.put("RESULT", new Token(TokenType.RESULT));
		table.put("INTEGER", new Token(TokenType.INTEGER));
		table.put("REAL", new Token(TokenType.REAL));
		table.put("ARRAY", new Token(TokenType.ARRAY));
		table.put("OF", new Token(TokenType.OF));
		table.put("IF", new Token(TokenType.IF));
		table.put("THEN", new Token(TokenType.THEN));
		table.put("ELSE", new Token(TokenType.ELSE));
		table.put("DO", new Token(TokenType.DO));
		table.put("WHILE", new Token(TokenType.WHILE));
		table.put("NOT", new Token(TokenType.NOT));
	    // Reserve the operator keywords
		table.put("OR", new Operator(TokenType.ADDOP, "OR"));
		table.put("DIV", new Operator(TokenType.MULOP, "DIV"));
		table.put("MOD", new Operator(TokenType.MULOP, "MOD"));
		table.put("AND", new Operator(TokenType.MULOP, "AND"));
	}
	
	/** Returns true if the given string is a reserved keyword or operator word, and false otherwise */
	public boolean isKeyword(String s){
		return table.containsKey(s);
	}

}
