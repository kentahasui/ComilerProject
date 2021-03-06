package errors;

/**
 * Base class for errors generated by the parts of the compiler.
 */
public abstract class CompilerError extends Exception
{
   /** The type of error.  New types should be added to the enumeration
    * as the compiler generates new errors.
    */
   public enum Type {ILLEGAL_CHARACTER, BAD_COMMENT, UNTERMINATED_COMMENT, UNMATCHED_COMMENT, IDENTIFIER_TOO_LONG, 
	   CONSTANT_TOO_LONG, BAD_CONSTANT, ILLEGAL_FLOAT, ILLEGAL_EXPONENT_DECIMAL, ILLEGAL_EXPONENT, ILLEGAL_DECIMAL,
	   
	   UNMATCHED_TERMINALS, ERROR_PRODUCTION, UNKNOWN_SYMBOL_TYPE, PARSER_QUIT,
	   
	   ARRAY_WITHOUT_SUBSCRIPTS, VARIABLE_WITH_SUBSCRIPTS, UNDECLARED_VARIABLE, 
	   MULTIPLY_DECLARED_VARIABLE, WRONG_FUNCTION, NON_PROCEDURE, NON_FUNCTION, MOD_ERROR, INTEGER_EXPECTED, 
	   ETYPE_ERROR_A, ETYPE_ERROR_R, RESERVED_VARIABLE, PARAMETER_MISCOUNT, UNEXPECTED_SUBROUTINE,
	   UNMATCHED_PARAMETER_TYPES, ARRAY_PARAMETER_ERROR
   };

   /** The type of error represented by this object.  This field is declared
    * as final and must be set in the constructor.
    */
   protected final Type errorType;

   public CompilerError(Type errorType)
   {
      super("Unknown error");
      this.errorType = errorType;
   }

   public CompilerError(Type errorType, String message)
   {
      super(message);
      this.errorType = errorType;
   }

}
