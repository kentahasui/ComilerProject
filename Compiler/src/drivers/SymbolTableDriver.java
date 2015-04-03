package drivers;
import errors.LexicalError;
import grammarsymbols.TokenType;
import lex.*;
import symboltable.*;
import token.*;

public class SymbolTableDriver {
	
	public Tokenizer tokenizer;
	private static final int SIZE = 37;
	public SymbolTable GlobalTable;
	public SymbolTable ConstantTable;
	
	/** Default constructor: opens the "resources/parsetest.dat" file. 
	 Hard-coded path to a file in a local directory. Called when no
	 command-line arguments are supplied */
	public SymbolTableDriver() {
		tokenizer = new Tokenizer("resources/symtabtest.dat");
	}
	
	/** Constructor. Takes a path to a file as a parameter. */
	public SymbolTableDriver(String fileName){
		tokenizer = new Tokenizer(fileName); 
	}
	
	public void run(){
		GlobalTable = new SymbolTable(SIZE);
		ConstantTable = new SymbolTable(SIZE);
		
		/* Fills the Global Table with the built-in functions main, read and write */
		GlobalTable.insert(new FunctionEntry("MAIN"));
		GlobalTable.insert(new FunctionEntry("READ"));
		GlobalTable.insert(new FunctionEntry("WRITE"));
		
		/* Read through the file and fill the symbol tables */
		Token token;
		try{
			token = tokenizer.GetNextToken();
			while(token.getType() != TokenType.ENDOFFILE){
				// If the token is a constant, add to the constant table
				if((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)){
					ConstantTable.insert(new ConstantEntry(token.getValue(), token.getType()));
				} else if (token.getType() == TokenType.IDENTIFIER) {
                    //  If it is an identifier add it to Global table
                    // as a variable entry
					GlobalTable.insert(new VariableEntry(token.getValue(), token.getType()));
                }
				token = tokenizer.GetNextToken();
			}
		} catch(LexicalError e){
			System.err.println(e);
		}
		GlobalTable.dumpTable();
		ConstantTable.dumpTable();
	}
	
	/** Main method to call the run method */
	public static void main(String[] args){
		SymbolTableDriver driver;
		// Use the supplied command-line argument as a parameter for the tokenizer
		if(args.length >0){
			driver = new SymbolTableDriver(args[0]);
		}
		// If no command-line argument is given, open the default file (symtabtest.dat)
		else{
			driver = new SymbolTableDriver();
		}
		driver.run();
	}
	
	

}
