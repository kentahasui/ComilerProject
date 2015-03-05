package junittests;
import static org.junit.Assert.*;
import grammarsymbols.NonTerminal;
import grammarsymbols.TokenType;

import org.junit.Test;

import parser.*;

public class ParseTableTest {
	ParseTable pTable = new ParseTable();
	
	/** Tests to see if the parse table was successfully initialized
	 * Tests the first two rows, and some rows at the bottom of the table
	 */
	@Test
	public void tableTest(){
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
	}

}
