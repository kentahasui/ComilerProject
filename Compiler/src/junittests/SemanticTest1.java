package junittests;
import static org.junit.Assert.*;
import grammarsymbols.SemanticAction;
import grammarsymbols.TokenType;

import org.junit.Test;

import parser.Parser;
import semanticActions.SemanticActions;
import token.*;
import drivers.ParseDriver;
import errors.SemanticError;

public class SemanticTest1 {
	Token t = new Token(TokenType.PROGRAM); // Generic token
	
	@Test
	public void test1() throws SemanticError{
		SemanticActions actions = new SemanticActions();
		actions.Execute(SemanticAction.action1, new Token(TokenType.ADDOP));
		assertTrue(actions.isInsert());
		actions.Execute(SemanticAction.action2, t);
		assertFalse(actions.isInsert());
		actions.Execute(SemanticAction.action6, t);
		assertTrue(actions.isArray());
		actions.Execute(SemanticAction.action4, new Token(TokenType.INTCONSTANT));
		assertTrue(actions.getStack().contains(TokenType.INTCONSTANT));
		Constant constant1 = new Constant(TokenType.INTCONSTANT, "100");
		actions.Execute(SemanticAction.action7, constant1);
		assertTrue(actions.getStack().contains(constant1));
		Identifier id = new Identifier("hello");
		actions.Execute(SemanticAction.action13, id);
		assertTrue(actions.getStack().contains(id));
		actions.semanticStackDump();
	}
	
	@Test
	public void test2() throws SemanticError{
		System.out.println();
		ParseDriver driver = new ParseDriver("resources/pascal_files/expression.pas");
		driver.run();
		System.out.println();
		driver = new ParseDriver("resources/pascal_files/array.pas");
		driver.run();
	}

}
