package semanticActions;
import errors.*;

import java.util.*;

import lex.*;
import parser.*;
import symboltable.*;
import token.Constant;
import token.Identifier;
import token.Token;
import drivers.*;
import grammarsymbols.*;

public class SemanticActions {
	
	private final int TABLE_SIZE = 37;
	// Stack for semantic actions
	private ArrayDeque<Object> semanticStack ;	// Stack of semantic objects
//	Quadruples not used until Phase 2
//	private Quadruples quads ;
	private boolean insert ;		// Insert or Search mode
	private boolean isArray ; 		// Array Variable or Simple Variable
	private boolean global ;		// Global Environment or Local Environment
	private boolean isParm;			// Is a parameter?
	private int globalMemory ;		// Offset from global memory
	private int localMemory ;		// Offset from local memory
	
	private SymbolTable globalTable;	// Keyword Table for global variables
	private SymbolTable localTable;
	private SymbolTable constantTable;	// Keyword Table for constants
	

	public SemanticActions() {
		semanticStack = new ArrayDeque<Object>();
//		quads = new Quadruples();
		insert = true;		// Insert mode for symbol table
		isArray = false;	// Simple variable
		isParm = false;
		global = true;		// Global Environment
		globalMemory = 0;
		localMemory = 0;
		globalTable = new SymbolTable(TABLE_SIZE);
		constantTable = new SymbolTable(TABLE_SIZE);
		InstallBuiltins(globalTable);
	}

	/** Method to install built-in (reserved) procedure names main, read, and write */
	public void InstallBuiltins(SymbolTable table){
		/* Fills the Global Table with the built-in procedures main, read and write */
		// Main has 0 parameters
		table.insert(new ProcedureEntry("MAIN", 0));
		table.insert(new ProcedureEntry("READ"));
		table.insert(new ProcedureEntry("WRITE"));
	}
	
	public void Execute (SemanticAction action, Token token)  throws SemanticError {
		
		int actionNumber = action.getIndex();
		
		System.out.println("calling action : " + actionNumber + " with token " + token.getValue());
//		System.out.println("calling action : " + actionNumber + " with token " + token.getType());

		switch (actionNumber)
		{
		
		case 1  : {	// INSERT/SEARCH = INSERT
			insert = true;	// Insert mode
			break;
		}
		case 2: {	// INSERT/SEARCH = SEARCH
			insert = false;	// Search mode
			break;
		}
		case 3: {
			if(!(semanticStack.peek() instanceof TokenType)){
				System.out.println("Expected a TokenType at top of stack");
			}
			TokenType type = (TokenType) semanticStack.pop();
			if(isArray){ /* Array declaration */
				
				if(!(semanticStack.peek() instanceof Constant)){
					System.out.println("Expected a constant token at top of stack (in an array declaration)" );
				}
		//////// Get the value of the upper bound integer constant from the stack. 
				// If this value is not yet in the constant table, insert it. 
				Constant upToken = (Constant) semanticStack.pop();
				String upString = upToken.getValue();
				/* Look up the string in the constant table. If not found, insert a new entry */
				ConstantEntry upConstant = (ConstantEntry) constantTable.lookup(upString);
				if(upConstant == null){
					upConstant = new ConstantEntry(upString, TokenType.INTCONSTANT);
					constantTable.insert(upConstant);
				}
				int upperBound = upConstant.getIntValue();
//				int upperBound = Integer.parseInt(upToken.getValue());
//				int upperBound = Integer.parseInt((String) semanticStack.pop());
				if(!(semanticStack.peek() instanceof Constant)){
					System.out.println("Expected a constant token at top of stack (in an array declaration)" );
				}
		/////// Get value of lower bound integer from the stack. Insert into constant table
				Constant lowToken = (Constant) semanticStack.pop();
				String lowString = lowToken.getValue();
				/* Look up the string in the constant table. If not found, insert a new entry */
				ConstantEntry lowConstant = (ConstantEntry) constantTable.lookup(lowString);
				if(lowConstant == null){
					lowConstant = new ConstantEntry(lowString, TokenType.INTCONSTANT);
					constantTable.insert(lowConstant);
				}
				// Calculate the lower bound
				int lowerBound = lowConstant.getIntValue();
//				int lowerBound = Integer.parseInt(lowToken.getValue());
//				int lowerBound = Integer.parseInt((String) semanticStack.pop());
				
				int memorySize = (upperBound - lowerBound)+1;
				// For each ID on the semantic stack: 
				while(!semanticStack.isEmpty() && semanticStack.peek() instanceof Identifier){
					Identifier id = (Identifier)semanticStack.pop();
					// Create new array entry
					ArrayEntry arrEntry = new ArrayEntry(id.getValue(), type, upperBound, lowerBound);
					// If in global environment, insert into global table
					if(global){
						arrEntry.setAddress(globalMemory);
						globalMemory += memorySize;
						globalTable.insert(arrEntry);
					}
					// If in local environment, insert into local table 
					else{
						arrEntry.setAddress(localMemory);
						localMemory += memorySize;
						localTable.insert(arrEntry);
					}
				}
			}
			else{	/* Simple Variable Declaration */
				// For each ID on the semantic stack: 
				while(!semanticStack.isEmpty() && semanticStack.peek() instanceof Identifier){
					Identifier id = (Identifier)semanticStack.pop();
					// Create a new variable entry
					VariableEntry varEntry = new VariableEntry(id.getValue(), type);
					// If in global environment, insert into global symbol table and update the addresses
					if(global){
						varEntry.setAddress(globalMemory);
						globalMemory++;
						globalTable.insert(varEntry);
					}
					// If in local environment, insert into local table
					else{
						varEntry.setAddress(localMemory);
						localMemory++;
						localTable.insert(varEntry);
					}
				}
			}
			isArray = false; // Back to simple variable declaration
			break;
		}
		case 4: {	// Push TYPE
			semanticStack.push(token.getType());
			break;
		}
		case 6: {	// ARRAY/SIMPLE = ARRAY
			isArray = true;	// Found an array declaration: array mode
			break;
		}
		case 7: {	// Push CONSTANT
			// The token passed should be a constant, so we just push it onto the stack
			// It must be an integer, since it is inside an array declaration
			if(token.getType()!= TokenType.INTCONSTANT){
				/////// ERROR!!
				System.out.println("ERROR");
			}
			semanticStack.push(token);
			break;
		}
		case 13: {	// Push ID
			// The token passed should be an identifier, so we should simply push it onto the stack
			semanticStack.push(token);
			break;
		}
		default: {
			// Nothing!
		} 

		}// End switch
		
	}
	
	public ArrayDeque getStack(){
		return semanticStack;
	}
	public boolean isInsert(){
		return insert;
	}
	public boolean isArray(){
		return isArray;
	}
	public boolean isGlobal(){
		return global;
	}
	
	public void semanticStackDump(){
		Iterator iter = semanticStack.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
	}
	
	/** Prints the contents of the global table */
	public void dumpGlobalTable(){
		globalTable.dumpTable();
	}
	/** Prints the contents of the constant table */
	public void dumpConstantTable(){
		constantTable.dumpTable();
	}


}
