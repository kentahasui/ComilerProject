package lex;

/** 
 * A class used to classify characters. Assumes the encoding is in ASCII. 
 * Values are precomputed and placed into boolean arrays. 
 * We assume that the values fed into the methods will be in basic
 * ASCII, and thus have an integer representation of less than 128. Any other characters
 * will be parsed out by the CharStream class.
 * An alternate representation would use the java.util.BitSet object to store values. This 
 * would save a lot of memory, but accessing the bits in the set would add overhead. Since
 * we are concerned about time efficiency for the lexical analyzer, I have decided against
 * this representation. The input alphabet is small enough that the memory costs are not too
 * terrible. 
 * 
 * @author kentahasui
 *
 */
public class Classification {
	/** Instance of the class. Singleton design pattern */
	private static Classification instance = null;
	/** The size of each array. We assume the encoding is in ASCII, so we 
	 * reserve byte arrays of size 128. */
	public static final int SIZE = 128;
	
	// Arrays: all initialized to false
	/** Array to determine if a character is a digit */
	public static boolean[] digits = new boolean[SIZE];
	/** Array to determine if character is letter */
	public static boolean[] letters = new boolean[SIZE];
	/** Array to determine if character is letter or digit */
	public static boolean[] letterOrDigit = new boolean[SIZE];
	/** Array to determine if character is any operator */
	public static boolean[] operators = new boolean[SIZE];
	/** Array to determine if character is a plus or a minus */
	public static boolean[] plusMinus = new boolean[SIZE];
	/** Array to determine if character is a plus or a minus */
	public static boolean[] relop = new boolean[SIZE];
	/** Array to determine if character is a plus or a minus */
	public static boolean[] mulop = new boolean[SIZE];
	
//== Constructor ======================================================================= //
	
	/** Public getInstance method. */
	public static Classification getInstance(){
		if(instance == null){
			instance = new Classification();
		}
		return instance;
	}
	
	/** Private Constructor. Initializes all of the arrays. */
	private Classification(){
		preCompute();
	}
	
	/** Initializes all of the arrays */
	public void preCompute(){
		initDigits();
		initLetters();
		initLetterOrDigit();
		initOperators();
		initPlusMinus();
		initRelop();
		initMulop();
	}
//== Initialization ======================================================================= //
	/** For all indices in the digit array that are actually digits ('0' to '9' or 48 to 57), 
	 * set the value to 1
	 */
	private void initDigits(){
		// All digits are set to 1. 
		for(int i = '0'; i<='9'; i++){
			digits[i] = true;
		}
	}
	/** Sets the appropriate values in the letters array to true 
	 *  These values are any int values between 'A' and 'Z' or 'a' and 'z'
	 */
	private void initLetters(){
		for(int i = 'A'; i<='Z'; i++){
			letters[i] = true;
		}
		for(int j = 'a'; j<= 'z'; j++){
			letters[j] = true;
		}
	}
	/** Sets the appropriate values in the letterOrDigit array */
	private void initLetterOrDigit(){
		for(int i = 'A'; i<='Z'; i++){
			letterOrDigit[i] = true;
		}
		for(int j = 'a'; j<= 'z'; j++){
			letterOrDigit[j] = true;
		}
		for(int k = '0'; k<='9'; k++){
			letterOrDigit[k] = true;
		}
	}
	/** Sets the appropriate values in the operators array */
	private void initOperators(){
		operators['+'] = true;
		operators['-'] = true;
		operators['*'] = true;
		operators['/'] = true;
		operators['='] = true;
		operators['<'] = true;
		operators['>'] = true;
	}
	/** Sets the appropriate values in the plusMinus array */
	private void initPlusMinus(){
		plusMinus['+'] = true;
		plusMinus['-'] = true;
	}
	/** Sets the appropriate values in the relop array */
	private void initRelop(){
		relop['='] = true;
		relop['<'] = true;
		relop['>'] = true;
	}
	/** Sets the appropriate values in the mulop array */
	private void initMulop(){
		mulop['*'] = true;
		mulop['/'] = true;
	}

//== Public methods ======================================================================= //
	/**
	 * Method to check if a character is a digit
	 * @param ch Any ASCII character (int between 0 and 256)
	 * @return True if a character is between '0' and '9'. False otherwise 
	 */
	public boolean isDigit(char ch){
		return digits[ch];
	}
	/**
	 * Method to check if a character is a letter
	 * @param ch Any ASCII character
	 * @return True if a character is a letter. False otherwise
	 */
	public boolean isLetter(char ch){
		return letters[ch];
	}
	/**
	 * Method to check if a character is a letter or digit
	 * @param ch Any ASCII character
	 * @return True if a character is a letter or digit. False otherwise
	 */
	public boolean isLetterOrDigit(char ch){
		return letterOrDigit[ch];
	}
	
	/** Method to check if a character is an operator */
	public boolean isOperator(char ch){
		return operators[ch];
	}
	/** Method to check if a character is a plus or a minus */
	public boolean isPlusMinus(char ch){
		return plusMinus[ch];
	}
	/** Method to check if a character is a relative operator */
	public boolean isRelop(char ch){
		return relop[ch];
	}
	/** Method to check if a character is a multiplicative operator */
	public boolean isMulop(char ch){
		return mulop[ch];
	}
	
	
}
