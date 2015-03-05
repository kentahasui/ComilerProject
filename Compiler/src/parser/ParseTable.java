package parser;

import java.util.Arrays;
import errors.CompilerError;
import grammarsymbols.*;
import lex.*;


/** Parse Table class for the parser. 
 * Consists of a two-dimensional array, indexed by a Token Type (Grammar Symbol: a Terminal) and 
 * a Stack Symbol (Grammar Symbol: a NonTerminal). Each entry in this matrix 
 * consists of an integer code representing either a production number, 
 * an accept action indicator, or an error indicator. 
 * @author kentahasui
 *
 */
public class ParseTable {
	// A two-dimensional array
	public int[][] matrix;
	// Array of error messages
	public String[] errors;
	// number of Token Types (Terminal Types), which is conveniently the number of NonTerminal Types
	public final int SIZE = 38;
	// The integer code that represents an error
	public final int ERRORCODE = 999;
	
	/** Constructor. Initializes the table */
	public ParseTable(){
		initializeTable();
	}
	
	/** Creates and fills the table */
	private void initializeTable(){
		// Initialize the table
		matrix = new int[SIZE][SIZE];
		// Initialize the error table
		errors = new String[100];
		// Fill the table
		fillTable();
		initializeErrorTable();
	}
	
	/** Returns a specific error message from the values in the error table */
	public String getErrorMessage(int index){
		if(index >= ERRORCODE){
			// convert error code (retrieved from the parse table)
			// into the index in the error table
			index -= ERRORCODE;
			return errors[index];
		}
		else{
			return "NOT AN ERROR PRODUCTION!";
		}
	}
	
	/** Method to fill the parse table with the appropriate values. 
	 * This method uses the CharStream class to extract characters 
	 * from the parsetable-2const.dat file
	 */
	private void fillTable(){
		// Holds the current indices of the matrix
		int row = 0;
		int column = 0;
		// A buffer that holds the values inside the matrix
		StringBuilder currentValue = new StringBuilder();
		// Opens the file
		CharStream stream = new CharStream("resources/parsetable-2const.dat");
		if(!stream.isOpen()){
			System.err.println("An error occurred. The file is not open.");
		}
		// We now extract characters from the file one by one until we hit the end of file
		try{
			char c = stream.currentChar();
			while(c != CharStream.EOF){
				// If c is a digit or a '-', it is part of a number and we append to the buffer
				// We also figure out the indices for the matrix
				if(Classification.getInstance().isDigit(c) || c=='-'){
					currentValue.append(c);
					row = (stream.lineNumber() - 2) / 2;
					column = (stream.indexNumber() - 4) / 4;
				}
				// When we hit a blank space, we have found a number! 
				// Convert the string to an int, and place the value into the matrix
				else if(c == CharStream.BLANK){
					int value = Integer.parseInt(currentValue.toString());
					// If we have an error, change the error index. 
					if(value == ERRORCODE){
						value = getErrorCode(row, column);
					}
					matrix[row][column] = value;
					// System.out.printf("Row: %d , Column: %d, Value: %d \n", row, column, value );
					// Reset the buffer
					currentValue = new StringBuilder();
				}
				// Get the next character
				c = stream.currentChar();
			}
		}catch(CompilerError e){
		}
	}
	
	private int getErrorCode(int row, int column){
		int value = ERRORCODE;
		
		switch(column){
		// <Program> or <Goal> is on stack, but next token is not 'PROGRAM'
		case 0: case 36:{
			value = ERRORCODE + 20;
			break;
		}
		default:{
			value = ERRORCODE + column;
		}
		}
		return value;
	}
	

	
	/** Method that fills the error array with specific error messages. 
	 *  These messages will be helpful to the user.
	 *  The error code specified in the parse table will determine which error message is returned
	 */
	private void initializeErrorTable(){
		errors[0] = "UNKNOWN_ERROR";
		// <identifier-list> is on stack, but next token is not 'IDENTIFIER'
		errors[NonTerminal.identifier_list.getIndex()] = "Missing an identifier";
		// <declaration> is on stack, but next token is not BEGIN, VAR, FUNCTION, or PROCEDURE
		errors[NonTerminal.declarations.getIndex()] = 
				"A block of code must begin with keyword VAR, BEGIN, FUNCTION, or PROCEDURE";
		// <sub-declaration> is on stack, but next token is not BEGIN, VAR, FUNCTION, or PROCEDURE
		errors[NonTerminal.sub_declarations.getIndex()] = 
				"An inner block of code must begin with keyword BEGIN, FUNCTION, OR PROCEDURE";
		// <compound-statement>
		errors[NonTerminal.compound_statement.getIndex()] = 
				"Missing BEGIN statement";
		// <identifier_list_tail> is on stack
		errors[NonTerminal.identifier_list_tail.getIndex()] = 
				"Missing comma, colon or right paren";
		// <declaration_list> is on stack, but the next token is not a VAR
		errors[NonTerminal.declaration_list.getIndex()] = 
				"Missing an identifier after the keyword 'VAR'";
		// <type> is on stack, but next token is not a valid type
		errors[NonTerminal.type.getIndex()] = 
				"Type not valid: The type must be either an integer, real, or an array ";
		// Error when file does not start with keyword PROGRAM 
		errors[20] = "The program must start with the keyword 'PROGRAM'";
	}
	
	/** Method for phrase-level error recovery */
	private void errorRecoveryAction(int index){
		
	}
	
	/** Method to get the value at the specified column and row 
	 *  The integer returned represents either a production number, 
	 *  accept indicator, or an error indicator */
	public int getCode(int row, int column){
		return matrix[row][column];
	}
	
	/** Method to get the value at the specified column and row, represented as GrammarSymbols 
	 *  The integer returned represents either a production number, 
	 *  accept indicator, or an error indicator */
	public int getCode(GrammarSymbol row, GrammarSymbol column){
		return matrix[row.getIndex()][column.getIndex()];
	}

	/** Method to print the entries in the table*/
	public void printTable(){
		for(int[] i: matrix){
			for(int j: i){
				System.out.print(j + " ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args){
		ParseTable table = new ParseTable();
	}

}
