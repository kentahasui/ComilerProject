package semanticActions;
import errors.*;
import java.util.*;
import lex.*;
import parser.*;
import symboltable.*;
import token.Constant;
import token.Identifier;
import token.Operator;
import token.Token;
import grammarsymbols.*;

public class SemanticActions {
	/* Flag to indicate whether or not to print contents of the stack after each action */
	private static boolean printInfo = false;
	
	private final int TABLE_SIZE = 37;
	// Stack for semantic actions
	private ArrayDeque<Object> semanticStack ;	// Stack of semantic objects
	private Quadruples quads;		// Collection of quadruple entries
	
	// Flags for variable declarations
	private boolean insert ;		// Insert or Search mode
	private boolean isArray ; 		// Array Variable or Simple Variable
	private boolean global ;		// Global Environment or Local Environment
	
	// Memory allocation
	private int globalMemory ;		// Offset from global memory
	private int localMemory ;		// Offset from local memory
	private int globalAlloc; 		// Quadruple array location of ALLOC statement for global memory
	private int localAlloc; 		// Quadruple array location of ALLOC statement for local memory
	
	// Symbol Tables
	private SymbolTable globalTable;	// Symbol Table for global variables
	private SymbolTable localTable;		// Symbol Table for local variables
	private SymbolTable constantTable;	// Keyword Table for constants
	
	// Current Function
	private SymbolTableEntry currentFunction;	// Symbol table entry for function being parsed
	
	// Counter and prefixes for temporary variables
	private int tempCounter;		// Counter to give unique names to a temporary variable
	private final String tempPrefix = "$$"; 	// Prefix for temporary variables
	
	// Stack for number of parameters in proc declaration or call
	private ArrayDeque<Integer> parmCount;
	// Stack of pointers into a list of parameters for procedures/functions
	private ArrayDeque<Integer> nextParm; 
	
	// For error messages
	private Tokenizer lexer;
	
	public SemanticActions() {
		semanticStack = new ArrayDeque<Object>();
		quads = new Quadruples();
		insert = true;		// Insert mode for symbol table
		isArray = false;	// Simple variable
		global = true;		// Global Environment
		globalMemory = 0;	// Initialize global memory
		localMemory = 0;	// Initialize local memory
		globalTable = new SymbolTable(TABLE_SIZE);	// Create a table for global variables
		constantTable = new SymbolTable(TABLE_SIZE);// Create a table for constant values
		InstallBuiltins(globalTable);	// Install built-in procedures and reserve their names
		tempCounter = 0;				// Counter for temporary variables
		currentFunction = null;			// Pointer to the current function
		parmCount = new ArrayDeque<Integer>();
		nextParm = new ArrayDeque<Integer>();	
	}
	
	public SemanticActions(Parser parser){
		this();
		this.lexer = parser.getLexer();
	}

	/** Method to install built-in (reserved) procedure names main, read, and write */
	public void InstallBuiltins(SymbolTable table){
		/* Fills the Global Table with the built-in procedures main, read and write 
		 * We make each entry reserved, so no other variable or procedure can have the same name*/
		// Main has 0 parameters
		ProcedureEntry main = new ProcedureEntry("MAIN", 0);
		main.makeReserved();
		ProcedureEntry read = new ProcedureEntry("READ");
		read.makeReserved();
		ProcedureEntry write = new ProcedureEntry("WRITE");
		write.makeReserved();
		table.insert(main);
		table.insert(read);
		table.insert(write);
	}
	
	/** Creates a new memory location */
	public VariableEntry create(String name, TokenType type){
		String newName = tempPrefix + name + tempCounter;
		// Increment counter so same name is not used two times
		tempCounter++;
		VariableEntry entry = new VariableEntry(newName, type);
		// Set the entry's address to be NEGATIVE value, so we know it is a temporary address
		// Insert into appropriate table
		if(global){
			entry.setAddress(0 - globalMemory);
			globalMemory++;
			globalTable.insert(entry);
		}
		else{
			entry.setAddress(0 - localMemory);
			localMemory++;
			entry.makeLocal();
			localTable.insert(entry);
		}
		return entry;
	}
	
/*///////////////////// GENERATE FUNCTIONS ///////////////////////////////////// */
	public void generate(String tviCode){
		String[] quadruple = {tviCode};
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, String operand){
		String[] quadruple = {tviCode, operand};
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1){
		String[] quadruple = new String[2];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quads.addQuad(quadruple);
	}
//	public void generate(String tviCode, String operand1, String operand2){
//		String[] quadruple = new String[3];
//		quadruple[0] = tviCode;
//		quadruple[1] = operand1;
//		quadruple[2] = operand2;
//		quads.addQuad(quadruple);
//	}
	public void generate(String tviCode, String operand1, SymbolTableEntry operand2){
		String[] quadruple = new String[3];
		quadruple[0] = tviCode;
		quadruple[1] = operand1;
		quadruple[2] = getStringAddress(operand2);
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1, String operand2){
		String[] quadruple = new String[3];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quadruple[2] = operand2;
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2){
		String[] quadruple = new String[3];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quadruple[2] = getStringAddress(operand2);
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1, 
			SymbolTableEntry operand2, SymbolTableEntry operand3){
		String[] quadruple = new String[4];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quadruple[2] = getStringAddress(operand2);
		quadruple[3] = getStringAddress(operand3);
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1, 
			SymbolTableEntry operand2, String operand3){
		String[] quadruple = new String[4];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quadruple[2] = getStringAddress(operand2);
		quadruple[3] = operand3;
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1, 
			String operand2, String operand3){
		String[] quadruple = new String[4];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quadruple[2] = operand2;
		quadruple[3] = operand3;
		quads.addQuad(quadruple);
	}
	public void generate(String tviCode, SymbolTableEntry operand1, 
			String operand2, SymbolTableEntry operand3){
		String[] quadruple = new String[4];
		quadruple[0] = tviCode;
		quadruple[1] = getStringAddress(operand1);
		quadruple[2] = operand2;
		quadruple[3] = getStringAddress(operand3);
		quads.addQuad(quadruple);
	}
	
	/** Special generate method to take care of PARAM statements. Makes 
	 * sure that parameters are passed by reference
	 * @param tviCode Must be "param"
	 * @param operand A VariableEntry or ConstantEntry
	 */
	public void generateParam(String tviCode, SymbolTableEntry operand){
		String[] quadruple = new String[2];
		quadruple[0] = tviCode;
		// Constants are never parameters
		if(operand.isConstant()){
			// Create new temporary variable
			VariableEntry entry = create("t", operand.getType());
			// Move the constant value into temporary variable
			generate("move", operand.getName(), entry);
			quadruple[1] = "@" + getStringAddress(entry);
			quads.addQuad(quadruple);
			return;
		}
		String stringValue = String.valueOf(Math.abs(operand.getAddress()));
		if(operand.isParameter()){
			quadruple[1] = "%" + stringValue;
		}
		// If the parameter to be pushed onto the stack is a global variable
		else if(operand.isGlobal()){
			quadruple[1] = "@_" + stringValue;
		}
		// If the parameter is a local variable
		else{
			quadruple[1] = "@%" + stringValue;
		}
		quads.addQuad(quadruple);
	}
	
	/** Method to get string representation of a symbol table entry's address */
	public String getStringAddress(SymbolTableEntry operand){
		// If the operand is a constant, place into actual memory location
		// Return the newly generated temporary variable's address
		if(operand.isConstant()){
			// Create new temporary variable
			VariableEntry entry = create("t", operand.getType());
			// Move the constant value into temporary variable
			generate("move", operand.getName(), entry);
			return getStringAddress(entry);
		}
		if(operand.isProcedure() || operand.isFunction()){
			return operand.getName().toLowerCase();
		}
		// Convert operand's value to string
		String stringValue = String.valueOf(Math.abs(operand.getAddress()));
		if(operand.isGlobal() || operand.isFunctionResult()){ // If global variable
			stringValue = "_" + stringValue;
		}
		else{ // If local variable
			stringValue = "%" + stringValue;
			if(operand.isParameter()){
				stringValue = "^" + stringValue;
			}
		}
		return stringValue;
	}
/*///////////////////// END GENERATE FUNCTIONS ///////////////////////////////////// */
	
	/** Checks the types of 2 ids 
	 * @param id1 A Symbol Table Entry
	 * @param id2 A Symbol Table Entry
	 * @return 0 if id1 and id2 are both integers <br>
	 * 1 if id1 and id2 are both reals <br>
	 * 2 if id1 is real and id2 is integer <br>
	 * 3 if id1 is integer and id2 is real <br>
	 * 4 if unexpected types*/
	public int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2){
		// If both are same type
		if(id1.getType() == id2.getType()){
			// Both integers
			if(id1.getType() == TokenType.INTEGER){
				return 0;
			}
			// Both reals
			if(id2.getType() == TokenType.REAL){
				return 1;
			}
			// Error: unexpected types
			return 4;
		}
		// Different types
		else{
			if(id1.getType() == TokenType.REAL &&
					id2.getType() == TokenType.INTEGER){
				return 2;
			}
			if(id1.getType() == TokenType.INTEGER &&
					id2.getType() == TokenType.REAL){
				return 3;
			}
			// Error: unexpected types
			return 4;
		}
	}
	
	/** Creates and returns a list of integers, which are indices into the array of quadruples. 
	 * @param i An index that is inserted into the list
	 * @return A list of Integers
	 */
	public List<Integer> makeList(int i){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(i);
		return list;
	}
	
	/** Concatenates the lists p1 and p2, and returns the new list. 
	 *  This method copies all elements of both lists into the new list
	 *  @param list1 A list of Integers
	 *  @param list2 A list of Integers
	 *  @return A new List of Integers*/
	public List<Integer> merge(List<Integer> list1, List<Integer>list2){
		// Creates new list, with initial capacity set to avoid constantly resizing
		List<Integer> newList = new ArrayList<Integer>(list1.size() + list2.size());
		// Add all elements of two lists into the new list
		newList.addAll(list1);
		newList.addAll(list2);
		return newList;
	}
	
	/** Inserts target label for each of the statements on the list pointed to by p */
	public void BackPatch(List<Integer> list, int label){
		// Iterate through the list
		for(Integer number: list){
			// Extract quadruple from array
			String[] quadruple = quads.getQuad(number);
			// Update the last field in the quadruple
			quads.setField(number, quadruple.length-1, String.valueOf(label));
		}
	}
	
	public void Execute (SemanticAction action, Token token)  throws SemanticError {
		
		int actionNumber = action.getIndex();
		
		if(printInfo){
			System.out.println("calling action : " + actionNumber + " with token " + token.getValue());
			System.out.println("ParmCount: " + parmCount.toString());
			System.out.println("NextParm: " + nextParm.toString());
			System.out.println();
			semanticStackDump();
		}		
		
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
			TokenType type = (TokenType) semanticStack.pop();
			if(isArray){ /* Array declaration */
				// Get the value of the upper bound integer constant from the stack. 
				// If this value is not yet in the constant table, insert it. 
				Constant upToken = (Constant) semanticStack.pop();
				String upString = upToken.getValue();
				/* Look up the string in the constant table. If not found, insert a new entry */
				ConstantEntry upConstant = (ConstantEntry) constantTable.lookup(upString);
				if(upConstant == null){
					upConstant = new ConstantEntry(upString, TokenType.INTEGER);
					constantTable.insert(upConstant);
				}
				int upperBound = upConstant.getIntValue();
				// Get value of lower bound integer from the stack. Insert into constant table
				Constant lowToken = (Constant) semanticStack.pop();
				String lowString = lowToken.getValue();
				/* Look up the string in the constant table. If not found, insert a new entry */
				ConstantEntry lowConstant = (ConstantEntry) constantTable.lookup(lowString);
				if(lowConstant == null){
					lowConstant = new ConstantEntry(lowString, TokenType.INTEGER);
					constantTable.insert(lowConstant);
				}
				// Calculate the lower bound
				int lowerBound = lowConstant.getIntValue();
				int memorySize = (upperBound - lowerBound)+1;
				// For each ID on the semantic stack: 
				while(semanticStack.peek() instanceof Identifier){
					Identifier id = (Identifier)semanticStack.pop();
					// Create new array entry
					ArrayEntry arrEntry = new ArrayEntry(id.getValue(), type, upperBound, lowerBound);
					// If in global environment, insert into global table
					if(global){
						arrEntry.setAddress(globalMemory);
						globalMemory += memorySize;
						SymbolTableEntry prevEntry = globalTable.lookup(id.getValue());
						// If there are multiply declared variables, print an error message
						if(prevEntry != null){
							// Halt execution if the declared variable is reserved
							if(prevEntry.isReserved()){
								throw SemanticError.ReservedVariable(lexer.getLineNumber(),
										lexer.getCurrentLine(), id.getValue());
							}
							try{
								throw SemanticError.MultiplyDeclaredVariable(lexer.getLineNumber(),
										lexer.getCurrentLine(), id.getValue());
							}catch(SemanticError e){ System.out.println(e.getMessage()); }
							// If the entry is already reserved, we throw another error
						}
						globalTable.insert(arrEntry);
					}
					// If in local environment, insert into local table 
					else{
						arrEntry.setAddress(localMemory);
						localMemory += memorySize;
						// If there are multiply declared variables, print an error message
						if(localTable.lookup(id.getValue())!= null){
							try{
								throw SemanticError.MultiplyDeclaredVariable(lexer.getLineNumber(),
										lexer.getCurrentLine(), id.getValue());
							}catch(SemanticError e){ System.out.println(e.getMessage()); }
						}
						arrEntry.makeLocal();
						localTable.insert(arrEntry);
					}
				}
			}
			else{	/* Simple Variable Declaration */
				// For each ID on the semantic stack: 
				while(semanticStack.peek() instanceof Identifier){
					Identifier id = (Identifier)semanticStack.pop();
					// Create a new variable entry
					VariableEntry varEntry = new VariableEntry(id.getValue(), type);
					// If in global environment, insert into global symbol table and update the addresses
					if(global){
						varEntry.setAddress(globalMemory);
						globalMemory++;
						SymbolTableEntry prevEntry = globalTable.lookup(id.getValue());
						// If variable is already declared, print an error message
						if(prevEntry != null){
							if(prevEntry.isReserved()){
								throw SemanticError.ReservedVariable(lexer.getLineNumber(),
										lexer.getCurrentLine(), id.getValue());
							}
							try{
								throw SemanticError.MultiplyDeclaredVariable(lexer.getLineNumber(),
										lexer.getCurrentLine(), id.getValue());
							}catch(SemanticError e){ System.out.println(e.getMessage()); }
						}
						globalTable.insert(varEntry);
					}
					// If in local environment, insert into local table
					else{
						varEntry.setAddress(localMemory);
						localMemory++;
						// If variable is already declared, print error message
						if(localTable.lookup(id.getValue())!= null){
							try{
								throw SemanticError.MultiplyDeclaredVariable(lexer.getLineNumber(),
										lexer.getCurrentLine(), id.getValue());
							}catch(SemanticError e){ System.out.println(e.getMessage()); }
						}
						varEntry.makeLocal();
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
		case 5: { // Generate alloc and PROCBEGIN statements for a procedure
			insert = true;
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
			generate("PROCBEGIN", id);
			localAlloc = quads.getNextQuad();
			generate("alloc", "_");
			break;
		}
		case 6: {	// ARRAY/SIMPLE = ARRAY
			isArray = true;	// Found an array declaration: array mode
			break;
		}
		case 7: {	// Push CONSTANT
			// The token passed should be a constant, so we just push it onto the stack
			// It must be an integer, since it is inside an array declaration
			semanticStack.push(token);
			break;
		}
		case 9: { // Only called when in global environment, at the start of the program
			// For each id on semantic stack
			while(semanticStack.peek() instanceof Identifier){
				Identifier id = (Identifier) semanticStack.pop();
				// Insert id into symbol table
				VariableEntry entry = new VariableEntry(id.getValue(), id.getType());
				entry.makeReserved(); // mark as restricted
				globalTable.insert(entry);
			}
			insert = true;	// Insert mode
			// Generate code
			generate("CODE");
			generate("call", globalTable.lookup("MAIN"), "0");
			generate("exit");
			break;
		}
		case 11: { // Ends procedure or function. Frees local memory
			global = true;
			// Delete local symbol table entries
			localTable = null;
			currentFunction = null;
			// Fill in quad at location LOCAL_STORE with value of local_mem: allocate local memory
			quads.setField(localAlloc, 1, Integer.toString(localMemory));
			// Free the memory
			generate("free", String.valueOf(localMemory));
			// End procedure
			generate("PROCEND");
			break;
		}
		case 13: {	// Push ID
			// The token passed should be an identifier, so we should simply push it onto the stack
			semanticStack.push(token);
			break;
		}
		case 15: { // Function declaration
			// Create new symboltable entry
			FunctionEntry newFunction = new FunctionEntry(token.getValue());
			// To Fix: Set result variable
			VariableEntry result = create(token.getValue(), TokenType.INTEGER);
			result.setFunctionResult();
			newFunction.setResult(result);
			globalTable.insert(newFunction);
			semanticStack.push(newFunction);
			// Set local environment
			global = false;
			localTable = new SymbolTable(TABLE_SIZE);
			localMemory = 0;
			break;
		}
		case 16: { // Tail of function declaration: set result variable
			TokenType type = (TokenType)semanticStack.pop();
			FunctionEntry id = (FunctionEntry) semanticStack.peek();
			// Sets result variable's type
			id.getResult().setType(type);
			// Sets current function
			currentFunction = id;
			break;
		}
		case 17: { /* Procedure declaration: after the name is processed */
			// Insert id in symbol table
			ProcedureEntry newProcedure = new ProcedureEntry(token.getValue());
			globalTable.insert(newProcedure);
			semanticStack.push(newProcedure);
			/* set up local environment */
			global = false; 
			localTable = new SymbolTable(TABLE_SIZE);
			localMemory = 0;
			break;
		}
		case 19: { /* Procedure/Function declaration, before parameters are processed */
			// Push a new counter for the number of parameters, initialized to 0
			parmCount.push(0);
			break;
		}
		case 20: { /* After parameter list for a procedure declaration. */
			SymbolTableEntry procEntry = (SymbolTableEntry) semanticStack.peek();
			// Set the procedure's parameter count
			int paramCount =  parmCount.pop();
			procEntry.setNumberOfParameters(paramCount);
			break;
		}
		case 21: { /* After a set of parameters declared of the same type (real, integer, array) */
			// Procedure entry
			SubroutineEntry subroutine = null;
			// Get type
			TokenType type = (TokenType) semanticStack.pop();
			for(Object o: semanticStack){
				if(o instanceof ProcedureEntry){
					subroutine = (ProcedureEntry)o;
					break;
				}
				if(o instanceof FunctionEntry){
					subroutine = (FunctionEntry)o;
					break;
				}
			}
			// Loop through all parameters on stack
			Constant upToken = null;
			int upperBound = 0;
			Constant lowToken = null;
			int lowerBound = 0;
			while(semanticStack.peek() instanceof Token){
					ParmInfoEntry parameterInfo;
					// If the parameter is an array
					if(isArray){
						if(upToken == null){
							upToken = (Constant) semanticStack.pop();
							upperBound = Integer.valueOf(upToken.getValue());
							lowToken = (Constant) semanticStack.pop();
							lowerBound = Integer.valueOf(lowToken.getValue());
						}
						Token id = (Token)semanticStack.pop();
						// Create new arrayEntry
						ArrayEntry parameter = 
								new ArrayEntry(id.getValue(), type, upperBound, lowerBound);
						parameter.setAddress(localMemory);
						parameter.setParm();
						parameter.makeLocal();
						localTable.insert(parameter);
						// Find out parameter type
						parameterInfo = new ParmInfoEntry(type, true);
						// Set bounds of array
						parameterInfo.setBounds(lowerBound, upperBound);
					}
					else{ // Simple variable
						Token id = (Token)semanticStack.pop();
						// Create new symbol table entry
						VariableEntry parameter = new VariableEntry(id.getValue(), type);
						parameter.setAddress(localMemory);
						parameter.setParm();
						parameter.makeLocal();
						localTable.insert(parameter);
						// Create new parameter info entry
						parameterInfo = new ParmInfoEntry(type, false);
					}
					// Add parameters, increment the parameter count
					subroutine.addParameter(parameterInfo);
					incrementParmCount();
					// Increment local memory
					localMemory++;
			}
			isArray = false; // ARRAY/SIMPLE = SIMPLE
			break;
		}
		case 22: { /* Set gotos for false IF statements */
			EType eType = (EType)semanticStack.pop();
			if(eType != EType.RELATIONAL){
				throw SemanticError.RelationalTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
			}
			List<Integer> EFalse = (List<Integer>) semanticStack.pop();
			List<Integer> ETrue = (List<Integer>) semanticStack.peek();
			semanticStack.push(EFalse);
			BackPatch(ETrue, quads.getNextQuad());
			break;
		}
		case 24: { /* Start of While loop! */
			Integer beginLoop = quads.getNextQuad();
			generate(";; Start of loop ;;");
			semanticStack.push(beginLoop);
			break;
		}
		case 25: { /* Evaluate the condition in the while loop */
			EType eType = (EType)semanticStack.pop();
			if(eType != EType.RELATIONAL){
				throw SemanticError.RelationalTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
			}
			// Update the ETrue values, but keep both lists on the stack
			List<Integer> EFalse = (List<Integer>) semanticStack.pop();
			List<Integer> ETrue = (List<Integer>) semanticStack.peek();
			semanticStack.push(EFalse);
			BackPatch(ETrue, quads.getNextQuad());
			break;
		}
		case 26: { /* Generate Goto Statement for while loop */
			// Pop off stack in reverse order
			List<Integer> EFalse = (List<Integer>) semanticStack.pop();
			List<Integer> ETrue = (List<Integer>) semanticStack.pop();
			Integer beginLoop = (Integer)semanticStack.pop();
			generate("goto", String.valueOf(beginLoop));
			BackPatch(EFalse, quads.getNextQuad());
			generate(";; End of loop ;;");
			break;
		}
		case 27: { /* Start of ELSE statement */
			// Create new list of integers
			List<Integer> SkipElse = makeList(quads.getNextQuad());
			List<Integer> EFalse = null;
			for(Object obj: semanticStack)  {
		        if(obj instanceof List){
		        	EFalse = (List<Integer>) obj;
		        	break;
		        }
		      }
			semanticStack.push(SkipElse);
			generate("goto", "_");
			BackPatch(EFalse, quads.getNextQuad());
			break;
		}
		case 28: { /* End of ELSE Statement */
			// Pop off stack in reverse order
			List<Integer> SkipElse = (List<Integer>) semanticStack.pop();
			List<Integer> EFalse = (List<Integer>) semanticStack.pop();
			List<Integer> ETrue = (List<Integer>) semanticStack.pop();
			// Set the goto if we skip the else statement
			BackPatch(SkipElse, quads.getNextQuad());
			break;
		}
		case 29: { /* If there is no ELSE statement */
			// Pop off stack in reverse order
			List<Integer> EFalse = (List<Integer>) semanticStack.pop();
			List<Integer> ETrue = (List<Integer>) semanticStack.pop();
			BackPatch(EFalse, quads.getNextQuad());
			break;
		}
		case 30: { /* After Identifier is referenced in an expression */
			SymbolTableEntry entry;
			String idName = token.getValue();
			// If local environment, check local table
			if(!global){
				entry = localTable.lookup(idName);
				// If it is found in local table, push onto stack and end the action
				if(entry != null){
					semanticStack.push(entry);
					semanticStack.push(EType.ARITHMETIC);
					break;
				}
			}
			// If in global environment, or if the token was NOT found in local table, 
			// check the local table
			entry = globalTable.lookup(idName);
			if(entry == null){ // If the id is not found in symbol table
				entry = new VariableEntry(token.getValue(), token.getType());
				// Sets flag to indicate entry was entered as result of error condition
				((VariableEntry) entry).makeError();
				// Insert into global table to prevent further errors
				globalTable.insert(entry);
				// Throw and exception, but keep executing
				try{
					throw SemanticError.UndeclaredVariable(lexer.getIndexNumber(), lexer.getCurrentLine(), idName);
				}catch(SemanticError e){
					System.out.println(e.getMessage());
				}
				// Insert the token into the symbol table
			}
			semanticStack.push(entry);
			semanticStack.push(EType.ARITHMETIC);
			break;
		}
		case 31: { /* Evaluate ASSIGNMENT statements :=*/
			EType eType = (EType)semanticStack.pop();
			if(eType != EType.ARITHMETIC){
				throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
			}
			SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
			SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
			EType eT = (EType)semanticStack.pop();
			SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
			// Check types
			int typeCheck = typeCheck(id1,id2);
			if(typeCheck == 3) System.out.println("Error from case 31");
			if(typeCheck == 2){ // id1 is real, id2 is int
				// Convert id2 to real number
				VariableEntry temp = create("t", TokenType.REAL);
				generate("ltof", id2, temp);
				// If no subscript, generate a simple assignment statement
				if(offset.isNull()){
					generate("move", temp, id1);
				}
				else{ // if offset is not null: store inside array
					generate("stor", temp, offset, id1);
				}
			}
			else{ // Both share same types
				if(offset.isNull()){
					// Assignment statement: Move value at address id2 to value at addr id1
					generate("move", id2, id1);
				}
				else{
					// Store statement: store value in array
					generate("stor", id2, offset, id1);
				}
			}
			break;
		}
		case 32: {  /* Look up a token in the symbol table */
			// Look up current token (id) in the symbol table
			SymbolTableEntry entry = null;
			if(!global){
				entry = localTable.lookup(token.getValue());
			}
			if(entry == null){
				entry = globalTable.lookup(token.getValue());
			}
			// If the id is not an array entry, throw an error
			if(!entry.isArray()){
				VariableEntry id = (VariableEntry) entry;
				// Prevents the same error messages from being printed over and over again
				if(!id.isError()){
					// Flag as error entry
					id.makeError();
					try{
						throw SemanticError.SimpleSubscripts(lexer.getLineNumber(), 
								lexer.getCurrentLine(), entry.getName());
					}catch(SemanticError e){
						System.out.println(e.getMessage());
					}
				}
			}
			break;
		}
		case 33: { /* Evaluate array references */
			EType eType = (EType)semanticStack.pop();
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
			// If it's not an integer variable, throw error
			if(id.getType() != TokenType.INTEGER){
				throw SemanticError.IntegerExpected(lexer.getLineNumber(),
						lexer.getCurrentLine(), id.getName());
			}
			SymbolTableEntry temp1 = (SymbolTableEntry)semanticStack.pop();
			// ARRAY_NAME is first array id on stack
			ArrayEntry arrEntry = null;
			for(Object o: semanticStack){
				if(o instanceof ArrayEntry){
					arrEntry = (ArrayEntry)o;
					break;
				}
			}
			// If an array is not found, or if a simple var was referenced as a simple variable
			if(arrEntry == null){
				VariableEntry temp = create("t", TokenType.INTEGER);
				// Calculate the offset into the array
				generate("sub", temp1, String.valueOf(1), temp);
				// Push temp variable onto stack
				semanticStack.push(temp);
				break;
			}
			// Check if array indices are in bounds
			generate("blt", id, String.valueOf(arrEntry.getLowerBound()), String.valueOf(quads.getNextQuad()+3));
			generate("bgt", id, String.valueOf(arrEntry.getUpperBound()), String.valueOf(quads.getNextQuad()+2));
			generate("goto", String.valueOf(quads.getNextQuad()+4));
			generate("print", "\"Array index out of bounds\"");
			generate("newl");
			generate("exit");
			VariableEntry temp = create("t", TokenType.INTEGER);
			// Calculate the offset into the array
			generate("sub", temp1, String.valueOf(arrEntry.getLowerBound()), temp);
			// Push temp variable onto stack
			semanticStack.push(temp);
			break;
		}
		case 34: { /* Either push a null offset or call the function handling subroutine */
			SymbolTableEntry id = null;
			for(Object o: semanticStack){
				if(o instanceof SymbolTableEntry){
					id = (SymbolTableEntry)o;
					break;
				}
			}
			if(id != null && id.isFunction()){
				Execute(SemanticAction.action52, token);
			}
			else{
				// If we reference an array without subscripts, print an error message
				// The only time an array can be referenced without subscripts is if it is 
				// an argument in a procedure/function
				if(id.isArray() && parmCount.isEmpty()){
					if(!id.isError()){
						id.makeError();
						try{
							throw SemanticError.MissingSubscripts(lexer.getLineNumber(),
									lexer.getCurrentLine(), id.getName());
						}catch(SemanticError e){System.out.println(e.getMessage());}
					}
				}
				SymbolTableEntry entry = new SymbolTableEntry("Null");
				entry.makeNull();
				semanticStack.push(entry);
			}
			break;
		}
		case 35: { // Start of parameters in a procedure call
			parmCount.push(0);
			EType eType = (EType) semanticStack.pop();
			nextParm.push(0);
			semanticStack.push(eType);
			break;
		}
		case 36: { /* Procedure or function call with no parameters */
			// POP ETYPE
			semanticStack.pop();
			ProcedureEntry proc = (ProcedureEntry)semanticStack.pop();
			if(proc.getNumberOfParameters() != 0){
				System.out.println("ERROR");
			}
			break;
		}
		case 37: { /* Parameter type and count checking for procedure/function call */
			EType eType = (EType) semanticStack.pop();
			if(eType != EType.ARITHMETIC){
				throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
			}
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
			// Check that all parameter declarations are correct
			if(!(id.isVariable() || id.isConstant() || id.isArray() || id.isFunctionResult())){
				throw SemanticError.UnexpectedSubroutine(lexer.getLineNumber(),
						lexer.getCurrentLine(), id.getName());
			}
			// Increment parmcount.top
			incrementParmCount();
			// Get the procedure or declaration at bottom of stack
			SubroutineEntry subroutine = null;
			for(Object o: semanticStack){
				if(o instanceof FunctionEntry){
					subroutine = (FunctionEntry)o;
					break;
				}
				if(o instanceof ProcedureEntry){
					subroutine = (ProcedureEntry)o;
					break;
				}
			}
			String subName = subroutine.getName();
			// If the subroutine is not READ or WRITE
			if((!("READ".equals(subName))) && (!("WRITE".equals(subName)))){
				// If number of parameters does not match, throw an error
				if(parmCount.peek() > subroutine.getNumberOfParameters()){
					throw SemanticError.ParameterMiscount(lexer.getLineNumber(), lexer.getCurrentLine(), subName);
				}
				int nextIndex = nextParm.pop();
				ParmInfoEntry nextParameter = subroutine.getParameter(nextIndex);
				nextIndex++;
				nextParm.push(nextIndex);
				// If the parameter types do not match up, throw an error
				if(id.getType() != nextParameter.getType()){
					throw SemanticError.UnmatchedParameterTypes(lexer.getLineNumber(), lexer.getCurrentLine(),
							subName, id.getName(), id.getType(), nextParameter.getType());
				}
				// If the parameter is an array, check if the parameter type and bounds are correct
				if(nextParameter.isArray()){
					if(!id.isArray()){
						throw SemanticError.ArrayParameterError(lexer.getLineNumber(), lexer.getCurrentLine(),
								subName, id.getName());
					}
					ArrayEntry arrID = (ArrayEntry)id;
					if( (arrID.getLowerBound() != nextParameter.getLowerBound()) || 
							(arrID.getUpperBound() != nextParameter.getUpperBound())){
						throw SemanticError.ArrayParameterError(lexer.getLineNumber(), lexer.getCurrentLine(),
								subName, id.getName());
					}
				}
			}
			break;
		}
		case 38: { /* After a RELOP is processed */
			EType eType = (EType)semanticStack.pop();
			// If ETYPE != ARITHMETIC, throw an error message
			if(eType != EType.ARITHMETIC){
				try{
					throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
				}
				catch(SemanticError e){
					System.out.println(e.getMessage());
				}
			}
			// Push the RELOP onto the stack
			semanticStack.push(token);
			break;
		}
		case 39: { /* Evaluate the condition of a relop */
			EType eType = (EType)semanticStack.pop();
			// If ETYPE != ARITHMETIC, throw an error message
			if(eType != EType.ARITHMETIC){
				try{
					throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
				}
				catch(SemanticError e){
					System.out.println(e.getMessage());
				}
			}
			// Pop operands and operator
			SymbolTableEntry id2 = (SymbolTableEntry)semanticStack.pop();
			Operator op = (Operator)semanticStack.pop();
			String tviCode = op.getTVICode(); // TVI opcode
			SymbolTableEntry id1 = (SymbolTableEntry)semanticStack.pop();
			generate(";; Conditional execution: compare " + id1.getName() + " and " + id2.getName() + " ;;");
			// Check the operand types
			int operandTypes = typeCheck(id1, id2);
			if(operandTypes == 2){ // id1 is real and id2 is integer
				// First convert id2 to a real number
				VariableEntry temp = create("t", TokenType.REAL);
				generate("ltof", id2, temp);
				// Then carry out the branch execution
				generate(tviCode, id1, temp, "_");
			}
			else if(operandTypes == 3){ // id1 is integer and id2 is real
				VariableEntry temp = create("t", TokenType.REAL);
				// First convert id1 to a real number
				generate("ltof", id1, temp);
				// Then carry out the branch execution
				generate(tviCode, temp, id2, "_");
			}
			else{ // both operands are same type
				generate(tviCode, id1, id2, "_");
			}
			generate("goto", "_");
			// Create goto labels for true and false conditions
			List<Integer> ETrue = makeList(quads.getNextQuad() - 2);
			List<Integer> EFalse = makeList(quads.getNextQuad() - 1);
			// push onto stack
			semanticStack.push(ETrue);
			semanticStack.push(EFalse);
			semanticStack.push(EType.RELATIONAL);
			break;
		}
		case 40: { /* Push SIGN */
			semanticStack.push(token);
			break;
		}
		case 41: { /* Make NEGATIVE VALUES (Unary minus) */
			EType eType = (EType)semanticStack.pop();
			if(eType != EType.ARITHMETIC){
				throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
			}
			// Pop id, sign
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
			Token sign = (Token)semanticStack.pop();
			// If the sign on stack is a unary minus, negate the id's value and place it in a temp variable
			if(sign.getType() == TokenType.UNARYMINUS){
				/* Unary minus doesn't work for real values, so manually subtract the real number's 
				 * value from 0 and store that in a temporary variable */
				if(id.getType() == TokenType.REAL){
					VariableEntry temp1 = create("t", TokenType.REAL);
					generate("move", "0", temp1);
					VariableEntry temp2 = create("t", TokenType.REAL);
					generate("fsub", temp1, id, temp2);
					semanticStack.push(temp2);
				}
				/* If the id is an integer, we just use the unaryminus operation */
				else{
					VariableEntry temp = create("t", id.getType());
					generate("uminus", id, temp);
					semanticStack.push(temp);
				}
				
			}
			// Otherwise remove the sign from the stack and push the id again
			else{
				semanticStack.push(id);
			}
			semanticStack.push(EType.ARITHMETIC);
			break;
		}
		case 42: {
			EType eType = (EType)semanticStack.pop();
			// If operator == OR
			if(token.getValue().equals("OR")){
				if(eType != EType.RELATIONAL){
					throw SemanticError.RelationalTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
				}
				// // Backpatch for false case of the OR statement
				List<Integer> EFalse = (List<Integer>)semanticStack.peek();
				BackPatch(EFalse, quads.getNextQuad());
			}
			else{
				// check EType == Arithmetic
				if(eType != EType.ARITHMETIC){
					throw SemanticError.RelationalTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
				}
			}
			// Push Addop
			semanticStack.push(token);
			break;
		}
		case 43: { /* Evaluate ADDOPS: +, -, OR */
			// Pop expression type off
			EType eType = (EType) semanticStack.pop();
			if(eType == EType.RELATIONAL){ // If EType is relational
				List<Integer> EFalse2 = (List<Integer>) semanticStack.pop();
				List<Integer> ETrue2 = (List<Integer>) semanticStack.pop();
				Operator operator = (Operator) semanticStack.pop();
				List<Integer> EFalse1 = (List<Integer>)semanticStack.pop();
				List<Integer> ETrue1 = (List<Integer>)semanticStack.pop();
				//Push various things onto stack
				if(operator.getValue().equals("OR")){
					List<Integer> newETrue = merge(ETrue1, ETrue2);
					List<Integer> newEFalse = EFalse2;
					semanticStack.push(newETrue);
					semanticStack.push(newEFalse);
					semanticStack.push(EType.RELATIONAL);
				}
			}
			else{ // EType is arithmetic
				if(eType != EType.ARITHMETIC){
					throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
				}
				// Pop the operands and operators off of the stack
				SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
				Operator op = (Operator)semanticStack.pop();
				SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
				// TVI opcode
				String opCode = op.getTVICode();
				switch(typeCheck(id1, id2)){
				case 0: { // Both integers
					VariableEntry temp = create("t", TokenType.INTEGER);
					generate(opCode, id1, id2, temp);
					semanticStack.push(temp);
					break;
				}
				case 1: { // Both real
					VariableEntry temp = create("t", TokenType.REAL);
					generate("f" + opCode, id1, id2, temp);
					semanticStack.push(temp);
					break;
				}
				case 2: { // id1 is real and id2 is integer
					// Convert id2 to real, and carry out operations
					VariableEntry temp1 = create("t", TokenType.REAL);
					generate("ltof", id2, temp1);
					VariableEntry temp2 = create("t", TokenType.REAL);
					generate("f" + opCode, id1, temp1, temp2);
					semanticStack.push(temp2);
					break;
				}
				case 3: { // id1 is integer and id2 is real
					// Convert id1 to real, and carry out operations
					VariableEntry temp1 = create("t", TokenType.REAL);
					generate("ltof", id1, temp1);
					VariableEntry temp2 = create("t", TokenType.REAL);
					generate("f" + opCode, temp1, id2, temp2);
					semanticStack.push(temp2);
					break;
				}
				default: {
					System.out.println("ERROR");
					break;
				}
				} // End switch
				semanticStack.push(EType.ARITHMETIC);
			}
			break;
		}
		case 44: { /* Fill in goto values for AND statements */
			EType eType = (EType)semanticStack.pop();
			if(eType == EType.RELATIONAL){
				// If the operator is AND, backpatch
				if(token.getValue().equals("AND")){
					List<Integer> EFalse = (List<Integer>)semanticStack.pop();
					List<Integer> ETrue = (List<Integer>)semanticStack.peek();
					BackPatch(ETrue, quads.getNextQuad());
					semanticStack.push(EFalse);
				}
			}
			semanticStack.push(token);
			break;
		}
		case 45: { /* Evaluate MULOPS: *, /, DIV, MOD, AND */
			EType eType = (EType)semanticStack.pop();
			if(eType == EType.RELATIONAL){
				// Pop lists off in reverse order
				List<Integer> EFalse2 = (List<Integer>) semanticStack.pop();
				List<Integer> ETrue2 = (List<Integer>) semanticStack.pop();
				Operator op = (Operator) semanticStack.pop();
				List<Integer> EFalse1 = (List<Integer>)semanticStack.pop();
				List<Integer> ETrue1 = (List<Integer>)semanticStack.pop();
				if(op.getValue().equals("AND")){
					List<Integer> newETrue = ETrue2;
					List<Integer> newEFalse = merge(EFalse1, EFalse2);
					semanticStack.push(newETrue);
					semanticStack.push(newEFalse);
					semanticStack.push(EType.RELATIONAL);
				}
			}
			else{ // Arithmetic expression
				// Pop things off the stack: of the form op1 operator op2
				//Pop second operand
				SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
				// Pop operator
				Operator op = (Operator) semanticStack.pop();
				// Pop first operand
				SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
				String opType = op.getValue();		// String representation of operation
				String tviOpcode = op.getTVICode(); // TVI representation of operation
				if(eType != EType.ARITHMETIC) {
					throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
				}
				int types = typeCheck(id1, id2);
				// Modulus requires integer operands
				if((types != 0) && opType.equals("MOD")){
					throw SemanticError.ModError(lexer.getLineNumber(), lexer.getCurrentLine(),
							id1.getName(), id2.getName());
				}
				// Branched execution depending on the types of the operands
				if(types == 0){ // Both operands are ints
					if(opType.equals("MOD")){ // Modulus tvi code
						generate(";; " + id1.getName() + " MOD " + id2.getName() + " ;;");
						// *** CHECK IF EACH ARGUMENT IS POSITIVE // 
						VariableEntry val1 = create("v", TokenType.INTEGER);
						generate("move", id1, val1);
						VariableEntry val2 = create("v", TokenType.INTEGER);
						generate("move", id2, val2);
						generate("ble", val1, String.valueOf(0), String.valueOf(quads.getNextQuad()+3));
						generate("ble", val2, String.valueOf(0), String.valueOf(quads.getNextQuad()+2));
						generate("goto", String.valueOf(quads.getNextQuad()+4));
						generate("print", "\"Both arguments for MOD must be positive\"");
						generate("newl");
						generate("exit");
						// *** END Value check
						
						VariableEntry temp1 = create("t", TokenType.INTEGER);
						generate("move", id1, temp1);
						VariableEntry temp2 = create("t", TokenType.INTEGER);
						generate("move", temp1, temp2);
						generate("blt", temp1, val2, String.valueOf(quads.getNextQuad() + 3));
						generate("sub", temp2, val2, temp1);
						generate("goto", String.valueOf(quads.getNextQuad()-3));
						semanticStack.push(temp1);
					}
					else if(opType.equals("/")){ // Division operation
						// First convert both operands into real numbers, and then 
						// carry out real-number division, store in a temporary variable
						VariableEntry temp1 = create("t", TokenType.REAL);
						generate("ltof", id1, temp1);
						VariableEntry temp2 = create("t", TokenType.REAL);
						generate("ltof", id2, temp2);
						VariableEntry temp3 = create("t", TokenType.REAL);
						generate("fdiv", temp1, temp2, temp3);
						semanticStack.push(temp3);
					}
					else{
						VariableEntry temp = create("t", TokenType.INTEGER);
						generate(tviOpcode, id1, id2, temp);
						semanticStack.push(temp);
					}
				}
				else if(types == 1){ // If both ids are reals
					 // Integer Division
					if(opType.equals("DIV")){
						// Convert both operands to integers, and divide
						VariableEntry temp1 = create("t", TokenType.INTEGER);
						generate("ftol", id1, temp1);
						VariableEntry temp2 = create("t", TokenType.INTEGER);
						generate("ftol", id2, temp1);
						VariableEntry temp3 = create("t", TokenType.INTEGER);
						generate("div", temp1, temp2, temp3);
						semanticStack.push(temp3);
					}
					// Multiplication(*) or Division(/)
					else {
						VariableEntry temp = create("t", TokenType.REAL);
						generate("f" + tviOpcode, id1, id2, temp);
						semanticStack.push(temp);
					}
				}
				else if(types == 2){ // If id1 is a real and id2 is an int
					// If integer division
					if(opType.equals("DIV")){ 
						// Convert id1 to an integer
						VariableEntry temp1 = create("t", TokenType.INTEGER);
						generate("ftol", id1, temp1);
						VariableEntry temp2 = create("t", TokenType.INTEGER);
						// carry out integer division, and store in temp variable
						generate("div", temp1, id2, temp2);
						semanticStack.push(temp2);
					}
					// Multiplication(*) or Division(/)
					else{
						// Convert id2 into a real
						VariableEntry temp1 = create("t", TokenType.REAL);
						generate("ltof", id2, temp1);
						VariableEntry temp2 = create("t", TokenType.REAL);
						// Carry out floating-poing operation, store in temp2
						generate("f" + tviOpcode, id1, temp1, temp2);
						semanticStack.push(temp2);
					}
				}
				else if(types == 3){ // If id1 is an int and id2 is a real
					// If integer division
					if(opType.equals("DIV")){ 
						// Convert id2 to an integer
						VariableEntry temp1 = create("t", TokenType.INTEGER);
						generate("ftol", id2, temp1);
						// carry out integer division, and store in temp variable
						VariableEntry temp2 = create("t", TokenType.INTEGER);
						generate("div", id1, temp1, temp2);
						semanticStack.push(temp2);
					}
					// Multiplication(*) or Division(/)
					else{
						// Convert id1 into a real
						VariableEntry temp1 = create("t", TokenType.REAL);
						generate("ltof", id1, temp1);
						VariableEntry temp2 = create("t", TokenType.REAL);
						// Carry out floating-poing operation, store in temp2
						generate("f" + tviOpcode, temp1, id2, temp2);
						semanticStack.push(temp2);
					}
				}
				semanticStack.push(EType.ARITHMETIC);
			}
			break;
		}
		case 46: { /* Variable references: look up variable in the symbol table */
			// If token is an identifier, lookup in symbol table and push its entry onto the stack
			if(token instanceof Identifier){
				SymbolTableEntry entry;
				String idName = token.getValue();
				// Lookup the identifier in symbol table
				// If local environment, check local table
				if(!global){
					entry = localTable.lookup(idName);
					// If it is found in local table, push onto stack and end the action
					if(entry != null){
						semanticStack.push(entry);
						semanticStack.push(EType.ARITHMETIC);
						break; // break the switch execution
					}
				}
				// If in global environment, or if the token was NOT found in local table, 
				// check the global table
				entry = globalTable.lookup(idName);
				if(entry == null){ // If the id is not found in symbol table
					entry = new VariableEntry(token.getValue(), token.getType());
					// Sets flag to indicate entry was entered as result of error condition
					((VariableEntry)entry).makeError();
					// Insert into global table to prevent further errors
					globalTable.insert(entry);
					// Throw an error message, but keep executing
					try{
						throw SemanticError.UndeclaredVariable(lexer.getIndexNumber(), lexer.getCurrentLine(), idName);
					}catch(SemanticError e){
						System.out.println(e.getMessage());
					}
					// Insert the token into the symbol table
				}
				semanticStack.push(entry);
			}
			// If the token is a constant, lookup in the constant table
			else if(token instanceof Constant){
				String value = token.getValue();
				ConstantEntry entry = (ConstantEntry) constantTable.lookup(value);
				// If this entry is not found in the table
				if(entry == null){
					// Create a new entry: either of Integer or Real;
					if(token.getType() == TokenType.INTCONSTANT){
						entry = new ConstantEntry(value, TokenType.INTEGER);
					}
					else{
						entry = new ConstantEntry(value, TokenType.REAL);
					}
					constantTable.insert(entry);
				}
				semanticStack.push(entry);
			}
			semanticStack.push(EType.ARITHMETIC);
			break;
		}
		case 47: { /* Evaluation of NOT */
			EType eType = (EType)semanticStack.pop();
			List<Integer> oldEFalse = (List<Integer>) semanticStack.pop();
			List<Integer> oldETrue = (List<Integer>) semanticStack.pop();
			// Switch ETrue and EFalse, for not statements
			List<Integer> newETrue = oldEFalse;
			List<Integer> newEFalse = oldETrue;
			// Push new entries onto stack
			semanticStack.push(newETrue);
			semanticStack.push(newEFalse);
			semanticStack.push(EType.RELATIONAL);
			break;
		}
		case 48: { /* Load a value from an offset into an array */ 
			// Pop offset
			SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
			if(!offset.isNull()){
				// If offset.type != integer, error
				if(offset.getType() != TokenType.INTEGER){
					throw SemanticError.IntegerExpected(lexer.getLineNumber(),
							lexer.getCurrentLine(), offset.getName());
				}
				else{
					// Pop EType
					EType eType = (EType)semanticStack.pop();
					// Pop ID
					SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
					// Create temporary var
					VariableEntry temp = create("t", id.getType());
					// Generate code
					generate("load", id, offset, temp);
					// Push elements onto stack
					semanticStack.push(temp);
					semanticStack.push(EType.ARITHMETIC);
				}
			}
			// Else keep the offset popped off
			break;
		}
		case 49: { // Function declaration
			EType eType = (EType)semanticStack.pop();
			// If the types do not match up, throw error
			if(eType != EType.ARITHMETIC) throw SemanticError.ArithmeticTypeError(lexer.getLineNumber(), lexer.getCurrentLine());
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
			// If we don't have a function, throw an error
			if(!id.isFunction()) throw SemanticError.NonFunction(lexer.getLineNumber(), lexer.getCurrentLine(), id.getName());
			parmCount.push(0);
			nextParm.push(0);
			semanticStack.push(eType);
			break;
		}
		case 50: { // After function call: parameters are now on semantic stack
			/* Push each parameter onto the parameter stack.
			 * REVERSES THE ORDER OF INPUTS FOR DIFFERENT TYPES
			 * This is necessary for files such as "while.pas", with functions 
			 * that have multiple parameters of different types. 
			 */
			TokenType prevType = ((SymbolTableEntry)semanticStack.peek()).getType();
			boolean prevIsArray = ((SymbolTableEntry)semanticStack.peek()).isArray();
			ArrayDeque<LinkedList<SymbolTableEntry>> stackOfLists = new ArrayDeque<LinkedList<SymbolTableEntry>>();
			stackOfLists.push(new LinkedList<SymbolTableEntry>());
			while(semanticStack.peek() instanceof SymbolTableEntry){
				SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
				if(id.getType() != prevType || id.isArray() != prevIsArray){
					prevType = id.getType();
					prevIsArray = id.isArray();
					LinkedList<SymbolTableEntry> newList = new LinkedList<SymbolTableEntry>();
					stackOfLists.push(newList);
				}
				stackOfLists.peek().add(id);
			}
			while(!stackOfLists.isEmpty()){
				LinkedList<SymbolTableEntry> sameType = stackOfLists.pop();
				for(SymbolTableEntry id: sameType){
					generateParam("param", id);
				}
			}
			EType eType = (EType)semanticStack.pop();
			FunctionEntry function = (FunctionEntry)semanticStack.pop();
			if(parmCount.peek() != function.getNumberOfParameters()){
				throw SemanticError.ParameterMiscount(lexer.getLineNumber(), lexer.getCurrentLine(), function.getName());
			}
			// Generate call statement
			generate("call", function, String.valueOf(parmCount.pop()));
			// Move the result variable's contents into a temporary variable
			VariableEntry temp = create(function.getName() + "_RESULT", function.getResult().getType());
			generate("move", function.getResult(), temp);
			semanticStack.push(temp);
			semanticStack.push(EType.ARITHMETIC);
			nextParm.pop();
			break;
		}
		case 51: { // After Procedure call: all parameters are now on stack
			ProcedureEntry procedure = null;
			for(Object o: semanticStack){
				if(o instanceof ProcedureEntry)
					procedure = (ProcedureEntry)o;
			}
			if(procedure.getName().equals("READ")){
				Execute(SemanticAction.action51Read, token);
				break;
			}
			else if(procedure.getName().equals("WRITE")){
				Execute(SemanticAction.action51Write, token);
				break;
			}
			else{
				if(parmCount.peek() != procedure.getNumberOfParameters()){
					throw SemanticError.ParameterMiscount(lexer.getLineNumber(), lexer.getCurrentLine(), procedure.getName());
				}
				// Push each parameter onto the parameter stack.
				// REVERSES THE ORDER OF INPUTS FOR DIFFERENT TYPES: BOTH ARRAY/SIMPLE AND INT/REAL
				TokenType prevType = ((SymbolTableEntry)semanticStack.peek()).getType();
				boolean prevIsArray = ((SymbolTableEntry)semanticStack.peek()).isArray();
				ArrayDeque<LinkedList<SymbolTableEntry>> stackOfLists = new ArrayDeque<LinkedList<SymbolTableEntry>>();
				stackOfLists.push(new LinkedList<SymbolTableEntry>());
				while(semanticStack.peek() instanceof SymbolTableEntry){
					SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
					if(id.getType() != prevType || id.isArray() != prevIsArray){
						prevType = id.getType();
						prevIsArray = id.isArray();
						LinkedList<SymbolTableEntry> newList = new LinkedList<SymbolTableEntry>();
						stackOfLists.push(newList);
					}
					stackOfLists.peek().add(id);
				}
				while(!stackOfLists.isEmpty()){
					LinkedList<SymbolTableEntry> sameType = stackOfLists.pop();
					for(SymbolTableEntry id: sameType){
						generateParam("param", id);
					}
				}
//				while(semanticStack.peek() instanceof SymbolTableEntry){
//					SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
//					generateParam("param", id);
//					localMemory++;
//				}
				// Generate call statement
				generate("call", procedure, String.valueOf(parmCount.pop()));
				/* Pop EType and Procedure entry off the stack */
				EType et = (EType)semanticStack.pop();
				ProcedureEntry pc = (ProcedureEntry)semanticStack.pop();
				nextParm.pop();
			}
			break;
		}
		case 52: { /* Function call with no parameters */
			EType eType = (EType) semanticStack.pop();
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
			if(!id.isFunction()){
				throw SemanticError.NonFunction(lexer.getLineNumber(), lexer.getCurrentLine(), id.getName());
			}
			FunctionEntry functionID = (FunctionEntry)id;
			// Check the number of parameters
			if(functionID.getNumberOfParameters() > 0){
				throw SemanticError.ParameterMiscount(lexer.getLineNumber(), lexer.getCurrentLine(), functionID.getName());
			}
			generate("call", functionID, "0");
			VariableEntry temp = create("t", functionID.getResult().getType());
			generate("move", functionID.getResult(), temp);
			// Push entries onto stack
			semanticStack.push(temp);
			semanticStack.push(EType.ARITHMETIC);
			// ***** Push null offset, assuming we don't have an array
			SymbolTableEntry entry = new SymbolTableEntry("Null");
			entry.makeNull();
			semanticStack.push(entry);
			break;
		}
		case 53: { /* Push result of a function */
			EType eType = (EType) semanticStack.pop();
			SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
			if(id.isFunction()){
				// Throw error
				if(id != currentFunction){
					throw SemanticError.WrongFunction(lexer.getLineNumber(), 
							lexer.getCurrentLine(), id.getName());
				}
				FunctionEntry func = (FunctionEntry)semanticStack.pop();
				// Push id.result
				semanticStack.push(func.getResult());
				semanticStack.push(EType.ARITHMETIC);
			}
			else{
				semanticStack.push(EType.ARITHMETIC);
			}
			break;
		}
		case 54: { /* Check if symbol table entry is a procedure */
			SymbolTableEntry id = globalTable.lookup(token.getValue());
			// If ID is not a procedure, throw error
			if(id == null || !id.isProcedure()){
				throw SemanticError.NonProcedure(lexer.getLineNumber(), 
						lexer.getCurrentLine(), id.getName());
			}
			break;
		}
		case 55: { /* Allocate and free global memory */
			quads.setField(globalAlloc, 1, Integer.toString(globalMemory));
			generate("free", Integer.toString(globalMemory));
			generate("PROCEND");
			break;
		}
		case 56: { /* Procedure call for main */
			// GEN(PROCBEGIN main)
			generate("PROCBEGIN", globalTable.lookup("MAIN"));
			// GLOBAL_STORE = NEXTQUAD
			globalAlloc= quads.getNextQuad();
			// GEN(ALLOC, _)
			generate("alloc", "0");
			break;
		}
		case 100: { /* PROCEDURE CALL FOR WRITE */
			// Push the parameters onto the call stack, in reverse order
			Stack<SymbolTableEntry> tempStack = new Stack<SymbolTableEntry>();
			while(semanticStack.peek() instanceof SymbolTableEntry){
				SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
				tempStack.push(id);
			}
			// Generate read statements for each parameter
			while(!tempStack.empty()){
				SymbolTableEntry id = tempStack.pop();
				generate("print", "\"" + id.getName() + " = \"");
				// finp for real (floats)
				if(id.getType() == TokenType.REAL){
					generate("foutp", id);
				} 
				// inp for integers
				else{
					generate("outp", id);
				}
				generate("newl");
			}
			// Pop off parmcount, and nexparm pointers
			parmCount.pop();
			nextParm.pop();
			EType ET = (EType)semanticStack.pop();
			ProcedureEntry read = (ProcedureEntry) semanticStack.pop();
			break;
		}
		case 101: { /* PROCEDURE CALL FOR READ */
			/* FOR EACH PARAMETER ON STACK: read
			 * Must go from bottom of stack to top of stack, since we are not pushing 
			 * arguments onto the parameter stack */
			Stack<SymbolTableEntry> tempStack = new Stack<SymbolTableEntry>();
			while(semanticStack.peek() instanceof SymbolTableEntry){
				SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
				tempStack.push(id);
			}
			// Generate read statements for each parameter, in reverse order of the stack
			while(!tempStack.empty()){
				SymbolTableEntry id = tempStack.pop();
				generate("print", "\"Input value for variable " + id.getName() + ": \" ");
				// finp for real (floats)
				if(id.getType() == TokenType.REAL){
					generate("finp", id);
				} 
				// inp for integers
				else{
					generate("inp", id);
				}
			}
			parmCount.pop();
			nextParm.pop();
			EType ET = (EType)semanticStack.pop();
			ProcedureEntry read = (ProcedureEntry) semanticStack.pop();
			break;
		}
		default: {
			// Nothing!
		} 

		}// End switch
	} // End execute method
	
	/** Increments the integer at the top of the parmcount stack. Does nothing if stack is empty */
	public void incrementParmCount(){
		if(parmCount.peek() != null){
			Integer top = parmCount.pop();
			top = top + 1;
			parmCount.push(top);
		}
	}
	
	public ArrayDeque<Object> getStack(){
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
			Object next = iter.next();
			if(next instanceof SymbolTableEntry){
				((SymbolTableEntry) next).print();
			}
			else{
				System.out.println(next);
			}
		}
	}
	
	/** Prints the contents of the global table */
	public void dumpGlobalTable(){
		globalTable.dumpTable();
	}
	/** Prints the contents of the local table, if defined */
	public void dumpLocalTable(){
		if(localTable != null){
			localTable.dumpTable();
		}
	}
	/** Prints the contents of the constant table */
	public void dumpConstantTable(){
		constantTable.dumpTable();
	}
	/** Prints generated code */
	public void printGeneratedCode(){
		quads.print();
	}
	public Quadruples getQuads(){
		return quads;
	}
}