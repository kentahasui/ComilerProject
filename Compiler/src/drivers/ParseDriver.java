package drivers;
import parser.*;
import errors.*;

public class ParseDriver {
	
	public ParseDriver(){
		super();
	}
	
	protected void run() 
	{ 
		 Parser parser = new Parser("resources/parsetest.dat");
		// Parser parser = new Parser("resources/pascal_files/recursion.pas");
		try{
			parser.parse();
		}catch(CompilerError e){
			System.err.println(e.getMessage());
		}

	}
	
	public static void main(String[] args){
		ParseDriver pd = new ParseDriver();
		pd.run();
	}

}
