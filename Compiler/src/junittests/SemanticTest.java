package junittests;
import static org.junit.Assert.*;
import java.util.*;
import grammarsymbols.*;
import org.junit.Test;
import semanticActions.*;
import token.*;
import drivers.ParseDriver;
import errors.SemanticError;
import symboltable.*;

public class SemanticTest {
	Token t = new Token(TokenType.PROGRAM); // Generic token
	
	
	/** Tests Semantic Actions 1, 2, 4, 6, 7 and 13 */
	@Test
	public void testPhase1() throws SemanticError{
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
	
	
	/** Tests semantic actions for generating code (9, 55, and 56) 
	 * as well as actions for arithmetic expressions (30, 40, 42, 43, 44, 45, 46) */
	@Test
	public void testPhase2() throws SemanticError{
		System.out.println();
		System.out.println("Simple Test");
		ParseDriver driver = new ParseDriver("resources/pascal_files/simple.pas");
		driver.run();
		
		System.out.println();
		System.out.println("Mod Test");
		driver = new ParseDriver("resources/pascal_files/mod.pas");
		driver.run();
		
		System.out.println();
		System.out.println("Expression Test");
		driver = new ParseDriver("resources/pascal_files/expression.pas");
		driver.run();
		
		System.out.println();
		System.out.println("Exp Test");
		driver = new ParseDriver("resources/pascal_files/exptest.pas");
		driver.run();
	}
	
	/** Tests actions for subscripted array variables and relational expressions */
	@Test
	public void testPhase3(){
		System.out.println("Array test");
		ParseDriver driver = new ParseDriver("resources/pascal_files/array.pas");
		driver.run();
		System.out.println();
		System.out.println("Array Ref test");
		driver = new ParseDriver("resources/pascal_files/arrayref.pas");
		driver.run();
		
		System.out.println();
		System.out.println("If test");
		driver = new ParseDriver("resources/pascal_files/if.pas");
		driver.run();
	}
	
	/** Tests the generate function */
	@Test
	public void generateTest(){
		SemanticActions actions = new SemanticActions();
		Quadruples quads = actions.getQuads();
		actions.generate("hello");
		assertTrue(quads.getNextQuad() == 1);
		assertArrayEquals(new String[] {"hello"}, quads.getQuad(0));
		
		VariableEntry varE = new VariableEntry("A", TokenType.IDENTIFIER);
		varE.setAddress(1);
		actions.generate("move", new ConstantEntry("100"), varE);
		assertTrue(quads.getNextQuad()==3);
		assertArrayEquals(new String[] {"move", "100", "_0"}, quads.getQuad(1));
		assertArrayEquals(new String[]{"move", "_0", "_1"}, quads.getQuad(2));
		
		actions.generate("PROCBEGIN", new ProcedureEntry("MAIN"));
		assertTrue(quads.getNextQuad() == 4);
		assertArrayEquals(new String[] {"PROCBEGIN", "main"}, quads.getQuad(3));
	}
	
	/** Tests the create function */
	@Test
	public void createTest(){
		SemanticActions actions = new SemanticActions();
		assertEquals("$$t0", actions.create("t", TokenType.INTCONSTANT).getName());
		assertEquals("$$t1",actions.create("t", TokenType.IDENTIFIER).getName());
		assertEquals("$$t2",actions.create("t", TokenType.REALCONSTANT).getName());
	}
	
	/** Tests the typeCheck function */
	@Test
	public void typeCheckTest(){
		System.out.println("kenta");
		SemanticActions actions = new SemanticActions();
		VariableEntry idInt = new VariableEntry("v1", TokenType.INTEGER);
		VariableEntry idReal = new VariableEntry("v2", TokenType.REAL);
		VariableEntry idInt2 = new VariableEntry("v3", TokenType.INTEGER);
		VariableEntry idReal2 = new VariableEntry("v4", TokenType.REAL);
		VariableEntry error = new VariableEntry("v5", TokenType.ADDOP);
		assertTrue(actions.typeCheck(idInt, idInt2) == 0);
		assertTrue(actions.typeCheck(idReal, idReal2) == 1);
		assertTrue(actions.typeCheck(idReal, idInt2) == 2);
		assertTrue(actions.typeCheck(idInt, idReal) == 3);
		assertTrue(actions.typeCheck(idReal, error) == 4);
		assertTrue(actions.typeCheck(error, idInt) == 4);
	}
	
	
	/** Tests for the <code>makeList()</code> and the <code>merge</code> methods */
	@Test
	public void listTests(){
		SemanticActions actions = new SemanticActions();
		List<Integer> l1 = actions.makeList(30);
		List<Integer> l2 = actions.makeList(200);
		List<Integer> l3 = actions.merge(l1, l2);
		List<Integer> test1 = new ArrayList<Integer>();
		test1.add(30);
		test1.add(200);
		assertEquals(test1, l3);
		l2.add(40);
		l2.add(7);
		List<Integer> l4 = actions.merge(l1, l2);
		test1.add(40);
		test1.add(7);
		assertEquals(test1, l4);
		l4.add(90);
		assertFalse(test1.equals(l4));
	}
	

}
