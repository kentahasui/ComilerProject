package drivers;
import parser.*;
import errors.*;

public class ParseDriver {
	Parser parser;
	
	// Default constructor: opens the "resources/parsetest.dat" file. 
	// Hard-coded path to a file in a local directory
	public ParseDriver(){
		parser = new Parser("resources/parsetest.dat");
	}
	
	/** Constructor. Takes a path to a file as a parameter. */
	public ParseDriver(String filename){
		parser = new Parser(filename);
	}

	
	protected void run() 
	{ 
		try{
			parser.parse();
		}catch(CompilerError e){
			System.out.println(e.getMessage());
			return;
		}
		System.out.println("Compilation successful");
	}
	
	public static void main(String[] args){
		ParseDriver pd;
		// If no arguments are given, use default constructor.
	    // Opens the parsetest.dat file using a hard-coded path to the local "resources" directory
		if(args.length == 0){
			pd = new ParseDriver();
		}
		// Otherwise use the parameter to the program as a parameter for the parse driver
		else{
			pd = new ParseDriver(args[0]);
		}
		// Parse the file
		pd.run();
	}
}
