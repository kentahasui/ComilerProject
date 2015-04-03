package symboltable;
import grammarsymbols.*;

public class VariableEntry extends SymbolTableEntry {

	int address;

/***********************************************************
  These variables are used later */
  
	boolean parm = false, functionResult = false, reserved = false;
/***********************************************************/

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public VariableEntry() {
	}

	public VariableEntry(String Name) {
		super(Name);
	}
	
	public VariableEntry(String Name, TokenType type) {
		super(Name, type);
	} 

	@Override
	public boolean isVariable() { 
		return true; 
	}
	
	@Override
	public void print () {
		
		System.out.println("Variable Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   Address : " + this.getAddress());
		System.out.println();
	}

/***********************************************************

The methods below are not used until later
*/

// A function result will be stored as a variable entry
	public boolean isFunctionResult() {
		return functionResult;
	}

	public void setFunctionResult() {
		this.functionResult = true;
	}
	
// this flag indicates if the variable is a parameter to a procedure or function
	public boolean isParameter() {
		return parm;
	}

// read, write, and main are reserved	
	public boolean isReserved() {
		return reserved;
	}

	public void setParameter (boolean parm) {
		this.parm = parm;
	}
	
	public void setParm() {
		this.parm = true;
	} 
	public void makeReserved() {
		this.reserved = true;
	} 
/***************************************************************/

	
}