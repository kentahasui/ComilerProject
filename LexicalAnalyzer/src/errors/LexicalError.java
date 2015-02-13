package errors;

/** Exception class thrown when a lexical error is encountered. */
public class LexicalError extends CompilerError
{
   public LexicalError(Type errorNumber, String message)
   {
      super(errorNumber, message);
   }
   
   /** The header for the error message. 
    * @param lineNumber  line number where error occurs 
    * @param lineContent the line itself
    * 
    * @return
    */
   private static String errorStart(int lineNumber, String lineContent){
	   return ">>> ERROR AT LINE " + lineNumber + ": " + lineContent + "\n>>> ";
   }

   // Factory methods to generate the lexical exception types.

   /** Lexical error thrown when a left curly bracket '{' is found within a comment */
   public static LexicalError BadComment()
   {
      return new LexicalError(Type.BAD_COMMENT,
                              ">>> ERROR: Cannot include { inside a comment.");
   }
   
   /** Lexical error thrown when a left curly bracket '{' is found within a comment  */
   public static LexicalError BadComment(int lineNumber, String lineContent)
   {
	   return new LexicalError(Type.BAD_COMMENT, 
			   errorStart(lineNumber, lineContent) + 
			   "Cannot include { inside a comment");
   }

   public static LexicalError IllegalCharacter(char c)
   {
      return new LexicalError(Type.ILLEGAL_CHARACTER,
                              ">>> ERROR: Illegal character: " + c);
   }
   
   /** Lexical error thrown when an illegal character is encountered */
   public static LexicalError IllegalCharacter(char c, int lineNumber, String lineContent)
   {
	   return new LexicalError(Type.ILLEGAL_CHARACTER,
			   errorStart(lineNumber, lineContent)
			   + ": Illegal character: " + c);
   }

   public static LexicalError UnterminatedComment()
   {
      return new LexicalError(Type.UNTERMINATED_COMMENT,
                              ">>> ERROR: Unterminated comment.");
   }
   
   /** A lexical error thrown when an unterminated comment (set of {}) is encountered */
   public static LexicalError UnterminatedComment(int lineNumber, String lineContent)
   {
	   return new LexicalError(Type.UNTERMINATED_COMMENT, 
			   errorStart(lineNumber, lineContent) + 
			   ": Unterminated comment.");
   }
   
   /** A lexical error we throw when comment brackets are unmatched 
    * (or when we see a right curly bracket before we see a left one). 
    */
   public static LexicalError UnmatchedComment(int lineNumber, String lineContent){
	   return new LexicalError(Type.UNMATCHED_COMMENT, 
			   errorStart(lineNumber, lineContent) +  
			   ": Unmatched comment bracket. Cannot have a } before a corresponding {");
   }
   
   /** A lexical error thrown when an identifier is longer than 64 characters  */
   public static LexicalError IdentifierTooLong(int lineNumber, String lineContent){
	   return new LexicalError(Type.IDENTIFIER_TOO_LONG, 
			   errorStart(lineNumber, lineContent) + "This identifier is too long. "
			   	+ "It exceeds 64 characters");
   }
   
   /** A lexical error thrown when a constant is longer than 64 characters */
   public static LexicalError ConstantTooLong(int lineNumber, String lineContent){
	   return new LexicalError(Type.CONSTANT_TOO_LONG, 
			   errorStart(lineNumber, lineContent) + ": This Constant is too long. "
			   	+ "It exceeds 64 characters");
   }
   
   /** A lexical error thrown when we have a non-e letter in a constant */
   public static LexicalError BadConstant(int lineNumber, String lineContent){
	   return new LexicalError(Type.BAD_CONSTANT, 
			   errorStart(lineNumber, lineContent) + 
			   "Cannot have a (non-E) letter in a constant");
   }
   
   /** A lexical error thrown when a digit does not follow a dot inside a constant. */
   public static LexicalError IllegalFLoat(int lineNumber, String lineContent){
	   return new LexicalError(Type.ILLEGAL_FLOAT, 
			   errorStart(lineNumber, lineContent) + 
			   "There is no digit after the decimal point");
   }
   
   /** A lexical error thrown when there are two decimal points inside of a constant */
   public static LexicalError IllegalFloat2(int lineNumber, String lineContent){
	   return new LexicalError(Type.ILLEGAL_FLOAT, 
			   errorStart(lineNumber, lineContent) + 
			   "There are two decimal points in a number");
   }
   
   /** A lexical error thrown when a decimal is encountered after an "E" inside of a constant */
   public static LexicalError IllegalExponentDecimal(int lineNumber, String lineContent){
	   return new LexicalError(Type.ILLEGAL_EXPONENT_DECIMAL, 
			   errorStart(lineNumber, lineContent) +
			   "Only integers are allowed to be exponents"
					   + "(Can't have a '.' after an E in a number)");
   }
   
   /** A lexical error thrown when something other than a '+', '-' or a digit follows an 'E' inside of a constant */
   public static LexicalError IllegalExponent(int lineNumber, String lineContent){
	   return new LexicalError(Type.ILLEGAL_EXPONENT, 
			   errorStart(lineNumber, lineContent) + 
			   "An integer must follow an exponent delcaration");
   }
   
   /** A lexical error thrown when a decimal is not preceded by a digit, or an 
    *  ENDMARKER token is followed by a letter or digit
    */
   public static LexicalError IllegalDecimal(int lineNumber, String lineContent){
	   return new LexicalError(Type.ILLEGAL_DECIMAL, 
			   errorStart(lineNumber, lineContent) + 
			   "A digit MUST precede a decimal in a constant");
   }
}