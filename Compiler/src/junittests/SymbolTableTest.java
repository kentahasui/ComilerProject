package junittests;
import grammarsymbols.TokenType;
import org.junit.Test;
import drivers.SymbolTableDriver;
import symboltable.*;
import static org.junit.Assert.*;

public class SymbolTableTest {
	final int SIZE = 37;
	SymbolTable globals;
	SymbolTable constants;
	SymbolTableDriver driver;
	
	@Test
	public void emptyTest(){
		SymbolTable table = new SymbolTable(SIZE);
		assertTrue(table.lookup("hello")==null);
		assertTrue(table.lookup("MAIN")==null);
		assertTrue(table.lookup("READ")==null);
		assertTrue(table.lookup("WRITE")==null);
		// No duplicate entries
		table.insert(new VariableEntry("HELLOOOO"));
		table.insert(new ConstantEntry("HELLOOOO"));
		assertEquals(table.returnTable().size(), 1);
	}
	
	@Test
	public void symtabTest(){
		driver = new SymbolTableDriver("resources/symtabtest.dat");
		driver.run();
		globals = driver.GlobalTable;
		constants = driver.ConstantTable;
		assertNull(globals.lookup("HELLO"));
		assertTrue(globals.lookup("MAIN")!=null);
		// case sensitive
		assertTrue(globals.lookup("main") == null);
		assertTrue(globals.lookup("READ")!=null);
		assertTrue(globals.lookup("PUT")!=null);
		assertTrue(globals.lookup("SHOW")!=null);
		assertTrue(globals.lookup("GLOBAL")!=null);
		assertTrue(globals.lookup("WHERE")!=null);
		
		assertNull(constants.lookup("MAIN"));
		assertTrue(constants.lookup("12345")!=null);
		assertTrue(constants.lookup("67890")!=null);
		assertTrue(constants.lookup("234.5E7")!=null);
		assertTrue(constants.lookup("234.5e7")==null);
		assertNull(constants.lookup("1"));
		
		for(SymbolTableEntry e: globals.returnTable().values()){
			assertTrue(e.getType()==TokenType.IDENTIFIER || e.getType() == TokenType.FUNCTION);
			assertTrue(e.isFunction() || e.isVariable());
		}
		
		for(SymbolTableEntry c: constants.returnTable().values()){
			assertTrue(c.isConstant());
		}
	}
	
	@Test
	public void parsetestTest(){
		driver = new SymbolTableDriver("resources/parsetest.dat");
		driver.run();
		globals = driver.GlobalTable;
		constants = driver.ConstantTable;
		assertTrue(globals.lookup("READ")!=null);
		assertTrue(globals.lookup("MAIN")!=null);
		assertTrue(globals.lookup("WRITE")!=null);
		assertTrue(globals.lookup("INPUT")!=null);
		assertTrue(globals.lookup("OUTPUT")!=null);
		assertTrue(globals.lookup("A")!=null);
		assertTrue(globals.lookup("H")!=null);
		assertTrue(globals.lookup("Z")!=null);
		assertTrue(globals.lookup("I")!=null);
		assertTrue(globals.lookup("X")!=null);
		assertTrue(globals.lookup("Y")!=null);
		assertTrue(globals.lookup("W")!=null);
		assertTrue(globals.lookup("A")!=null);
		assertTrue(globals.lookup("B")!=null);
		
		assertTrue(constants.lookup("1")!=null);
		assertTrue(constants.lookup("5")!=null);
		assertTrue(constants.lookup("1608")!=null);
		assertTrue(constants.lookup("20.5")!=null);
		assertTrue(constants.lookup("23E10")!=null);
	}
	
	@Test
	public void arrayTest(){
		driver = new SymbolTableDriver("resources/pascal_files/array.pas");
		driver.run();
		globals = driver.GlobalTable;
		constants = driver.ConstantTable;
		assertTrue(globals.lookup("ARRAYTEST")!=null);
		assertTrue(globals.lookup("INPUT")!=null);
		assertTrue(globals.lookup("OUTPUT")!=null);
		assertTrue(globals.lookup("M")!=null);
		assertNull(globals.lookup("X"));
		assertNull(globals.lookup("THUNDER"));
		
		assertTrue(constants.lookup("1")!=null);
		assertTrue(constants.lookup("5")!=null);
		assertTrue(constants.lookup("2")!=null);
		assertTrue(constants.lookup("3")!=null);
		assertTrue(constants.lookup("4")!=null);
		assertNull(constants.lookup("6"));
		assertNull(constants.lookup("7"));
	}
	
	@Test
	public void recursionTest(){
		driver = new SymbolTableDriver("resources/pascal_files/recursion.pas");
		driver.run();
		globals = driver.GlobalTable;
		constants = driver.ConstantTable;
		assertTrue(globals.lookup("RECURSIONTEST")!=null);
		assertTrue(globals.lookup("X")!=null);
		assertTrue(globals.lookup("Y")!=null);
		assertTrue(globals.lookup("GCD")!=null);
		assertTrue(globals.lookup("A")!=null);
		assertTrue(globals.lookup("B")!=null);
		assertTrue(globals.lookup("INPUT")!=null);
		assertTrue(globals.lookup("OUTPUT")!=null);
	}

}
