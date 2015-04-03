package junittests;
import static org.junit.Assert.*;
import grammarsymbols.NonTerminal;
import grammarsymbols.TokenType;
import org.junit.Test;
import parser.*;

public class ParseTableTest {
	ParseTable pTable = new ParseTable();
	
	/** Tests to see if the parse table was successfully initialized
	 * 
	 */
	@Test
	public void tableTest(){
		// Tests the first two rows, and some rows at the bottom of the table
		assertEquals(pTable.getCode(TokenType.PROGRAM, NonTerminal.program), 1);
		assertEquals(pTable.getCode(TokenType.PROGRAM, NonTerminal.Goal), 65);
	
		assertEquals(pTable.getCode(TokenType.BEGIN, NonTerminal.declarations), -6);
		assertEquals(pTable.getCode(TokenType.BEGIN, NonTerminal.sub_declarations), -16);
		assertEquals(pTable.getCode(TokenType.BEGIN, NonTerminal.compound_statement), 25);
		assertEquals(pTable.getCode(TokenType.BEGIN, NonTerminal.statement_list), 26);
		assertEquals(pTable.getCode(TokenType.BEGIN, NonTerminal.statement), 29);
		assertEquals(pTable.getCode(TokenType.BEGIN, NonTerminal.elementary_statement), 35);
		
		assertEquals(pTable.getCode(TokenType.UNARYMINUS, NonTerminal.expression), 45);
		assertEquals(pTable.getCode(TokenType.UNARYMINUS, NonTerminal.expression_list), 42);
		assertEquals(pTable.getCode(TokenType.UNARYMINUS, NonTerminal.simple_expression), 49);
		assertEquals(pTable.getCode(TokenType.UNARYMINUS, NonTerminal.sign), 64);
		
		assertEquals(pTable.getCode(TokenType.UNARYPLUS, NonTerminal.expression), 45);
		assertEquals(pTable.getCode(TokenType.UNARYPLUS, NonTerminal.expression_list), 42);
		assertEquals(pTable.getCode(TokenType.UNARYPLUS, NonTerminal.simple_expression), 49);
		assertEquals(pTable.getCode(TokenType.UNARYPLUS, NonTerminal.sign), 63);
		
		// Test if table is filled with acceptable values
		for(TokenType terminal: TokenType.values()){
			for(NonTerminal nonterminal: NonTerminal.values()){
				// Last 3 rows are empty
				if(terminal == TokenType.ERROR || terminal == TokenType.FILE || terminal == TokenType.ENDOFFILE){
					assertEquals(pTable.getCode(terminal, nonterminal), 0);
				}
				else{
					int code = pTable.getCode(terminal, nonterminal);
					// Error codes
					if(code >= 999){
						// Error message for <program> and <goal> are the same
						if(nonterminal.getIndex() == 0){
							nonterminal = NonTerminal.Goal;
						}
						assertEquals(code - 999, nonterminal.getIndex());
					}
					// Non-Error productions: 67 productions in the RHS table
					else{
						assertTrue(code <= 67 && code > -67);
					}
				}
			}
			
		}
	}
	
	/** Tests for the error table */
	public void errorTableTest(){
		// Tests that all entries in the error table are filled
		for(NonTerminal x: NonTerminal.values()){
			assertTrue(pTable.getErrorMessage(x.getIndex()) != null);
		}
	}

}
