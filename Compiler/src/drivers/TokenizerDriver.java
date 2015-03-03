package drivers;
import lex.*;
import token.*;


public class TokenizerDriver
{

	public TokenizerDriver()
	{
		super();
	}

	protected void run()   // change this function to call GetNextToken until all tokens are read
	{ 
		Tokenizer tokenizer = new Tokenizer("resources/lextest.dat");

		Token token = new Token();
		token =	tokenizer.GetNextToken();

		while (!(token.getType() == TokenType.ENDOFFILE))
		{
			System.out.print("Recognized Token:  " + token.getType());
			if ((token.getType() == TokenType.IDENTIFIER) || (token.getType() == TokenType.REALCONSTANT) 
					|| (token.getType() == TokenType.INTCONSTANT) )
				System.out.print(" Value : " + token.getValue());
			else if ((token.getType() == TokenType.RELOP)
					|| (token.getType() == TokenType.ADDOP) || (token.getType() == TokenType.MULOP))
				System.out.print(" OpType : " + token.getOpType());
			else if (token.getType() == TokenType.ERROR){
				System.out.println();
				System.err.println("Driver is exiting");
				return;
			}
			System.out.println();

			token = tokenizer.GetNextToken();
		}
		// Prints EOF
		System.out.println("Recognized Token:  " + token.getType());
	}


	public static void main(String[] args)
	{
		TokenizerDriver test = new TokenizerDriver();
		test.run();
	}
}
