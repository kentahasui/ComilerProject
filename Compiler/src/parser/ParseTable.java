package parser;

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
	// number of Token Types (Terminal Types), which is conveniently the number of NonTerminal Types
	public final int SIZE = 38;
	// The integer that represents an error
	public final int ERRORCODE = 999;
	
	/** Constructor. Initializes the table */
	public ParseTable(){
		initializeTable();
	}
	
	/** Creates and fills the table */
	private void initializeTable(){
		// Initialize the table
		matrix = new int[SIZE][SIZE+1];
		// Fill the table
		fillTable();
	}
	
	/** Method to fill the parse table with the appropriate values. 
	 * This method uses the CharStream class to extract characters 
	 * from the parsetable-2const.dat file
	 */
	private void fillTable(){
		// Holds the current indices of the matrix
		int row = 0;
		int column = 0;
		// A buffer that holds the values of the matrix
		StringBuilder currentValue = new StringBuilder();
		// Opens the file
		CharStream stream = new CharStream("resources/parsetable-2const.dat");
		if(!stream.isOpen()){
			System.err.println("An error occurred. The file is not open.");
		}
		// We now extract characters from the file one by one until we hit the end of file
		try{
			char c = stream.currentChar();
			while(c != stream.EOF){
				// If c is a digit or a '-', it is part of a number and we append to the buffer
				// We also figure out the indices for the matrix
				if(Classification.getInstance().isDigit(c) || c=='-'){
					currentValue.append(c);
					row = (stream.lineNumber() - 2) / 2;
					column = (stream.indexNumber() - 4) / 4;
				}
				// When we hit a blank space, we have found a number! 
				// Convert the string to an int, and place the value into the matrix
				else if(c == stream.BLANK){
					int value = Integer.parseInt(currentValue.toString());
					//matrix[row][column] = value;
					System.out.printf("Row: %d , Column: %d, Value: %d \n", row, column, value );
					// Reset the buffer
					currentValue = new StringBuilder();
				}
				// Get the next character
				c = stream.currentChar();
			}
		}catch(CompilerError e){
		}
	}

	public static void main(String[] args){
		ParseTable table = new ParseTable();
	}

}
