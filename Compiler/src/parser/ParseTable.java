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
		// Create the parse table
		matrix = new int[SIZE][SIZE];
		// Create the error table
		errors = new String[SIZE];
		// Fill the parse table
		fillTable();
		// Fills the error table
		fillErrorTable();
		// printTable();
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
					// If we have an error, change to a specific error code
					if(value == ERRORCODE){
						value = getErrorCode(column);
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
	
	/** Method to get an number to index into the error table. 
	 * Depends on the nonterminal on the top of the stack 
	 */
	private int getErrorCode(int column){
		int value = ERRORCODE;
		switch(column){
		// <Program> or <Goal> is on stack, but next token is not 'PROGRAM'
		case 0:{
			value = ERRORCODE + 36;
			break;
		}
		default:{
			value = ERRORCODE + column;
		}
		}
		return value;
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
	public int getCode(TokenType row, GrammarSymbol column){
		return matrix[row.getIndex()][column.getIndex()];
	}

	/** Method to print the entries in the table*/
	public void printTable(){
		for(int[] i: matrix){
			for(int j: i){
				System.out.print(j + "\t");
			}
			System.out.println();
		}
	}
	
	/** Method that fills the error array with specific error messages. 
	 *  These messages will be helpful to the user.
	 *  The error code specified in the parse table will determine which error message is returned
	 */
	private void fillErrorTable(){
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
		// <declaration-list-tail> is on stack, but lookahead token is not a valid keyword or an identifier
		errors[NonTerminal.declaration_list_tail.getIndex()] = 
				"An identifier must be placed after the semicolon";
		// <standard-type> is on stack, and the next token is not an integer or real
		errors[NonTerminal.standard_type.getIndex()] = 
				"The type must be an integer or a real number"; 
		// <array-type> on stack
		errors[NonTerminal.array_type.getIndex()] = 
				"An array declaration must start with keyword ARRAY";
		// <subprogram-declaration> on stack
		errors[NonTerminal.subprogram_declaration.getIndex()] = 
				"Expected this block of code to start with keyword FUNCTION or PROCEDURE";
		// <subprogram-head> on stack
		errors[NonTerminal.subprogram_head.getIndex()] = 
				"Expected this block of code to start with keyword FUNCTION or PROCEDURE";
		// <arguments> on stack
		errors[NonTerminal.arguments.getIndex()] = 
				"Missing a colon after function declaration or a semicolon after procedure declaration";
		// <parameter_list> on stack
		errors[NonTerminal.parameter_list.getIndex()] = 
				"Parameters of a function or procedure must be identifiers";
		// <parameter_list_tail on stack
		errors[NonTerminal.parameter_list_tail.getIndex()] = 
				"Missing a semicolon or right paren";
		// <statement-list> on stack
		errors[NonTerminal.statement_list.getIndex()] = 
				"BEGIN, IF, ELSE, or an identifier expected";
		// <statement> on stack
		errors[NonTerminal.statement.getIndex()] = 
				"BEGIN, IF, ELSE, or an identifier expected";
		// <statement-list-tail> on stack
		errors[NonTerminal.statement_list_tail.getIndex()] = 
				"Semicolon or END expected";
		// <elementary-statement> on stack
		errors[NonTerminal.elementary_statement.getIndex()] = 
				"BEGIN or an identifier expected";
		// <expression> on stack
		errors[NonTerminal.expression.getIndex()] = 
				"A NOT, identifier, constant, or a paren expected";
		// <else-clause> on stack
		errors[NonTerminal.else_clause.getIndex()] = 
				"After a then-statement, an END, ELSE or semicolon expected";
		// <es-tail> on stack
		errors[NonTerminal.es_tail.getIndex()] = 
				"After the identifier, an END, ELSE, semicolon, left paren, left bracket, or an ASSIGNOP expected";
		// <subscript> on stack
		errors[NonTerminal.subscript.getIndex()] = 
				"Badly formed subscript";
		// <parameters> on stack
		errors[NonTerminal.parameters.getIndex()] = 
				"After a list of parameters, a delimeter expected";
		// <expression-list> on stack
		errors[NonTerminal.expression_list.getIndex()] = 
				"An expression must start with an identifier, constant, left paren, or keyword NOT";
		// <expression-list-tail> on stack
		errors[NonTerminal.expression_list_tail.getIndex()] = 
				"Comma or right paren expected";
		// <simple-expression> on stack
		errors[NonTerminal.simple_expression.getIndex()] = 
				"An expression must start with an identifier, constant, left paren, or keyword NOT";
		// <expression-tail> on stack
		errors[NonTerminal.expression_tail.getIndex()] = 
				"There is no end to this expression. Expected an end, then, else, do, operator, semicolon, paren or bracket.";
		// <term> on stack
		errors[NonTerminal.term.getIndex()] = 
				"Expected an identifier, constant, NOT, or a left paren";
		// <simple-expression-tail> on stack
		errors[NonTerminal.simple_expression_tail.getIndex()] = 
				"There is no end to this expression. Expected an end, then, else, do, operator, semicolon, paren or bracket.";
		// <sign> on stack
		errors[NonTerminal.sign.getIndex()] = 
				"Expected a sign (a '+' or a '-')";
		// <factor> on stack
		errors[NonTerminal.factor.getIndex()] = 
				"An expression must start with an identifier, constant, left paren, or keyword NOT";
		// <term-tail> on stack
		errors[NonTerminal.term_tail.getIndex()] = 
				"There is no end to this term. Expected an end, then, else, do, operator, semicolon, paren or bracket.";
		// <factor-tail> on stack
		errors[NonTerminal.factor_tail.getIndex()] = 
				"There is no end to this factor. Expected an end, then, else, do, operator, semicolon, paren or bracket.";
		// <actual-parameters> on stack
		errors[NonTerminal.actual_parameters.getIndex()] = 
				"Missing keywords, operators, or punctuation after the list of parameters";
		// Error when file does not start with keyword PROGRAM 
		errors[NonTerminal.Goal.getIndex()] = 
				"The program must start with the keyword 'PROGRAM'";
		// <constant> on stack
		errors[NonTerminal.constant.getIndex()] = 
				"A constant must either be an integer or a real number";
	}

}
