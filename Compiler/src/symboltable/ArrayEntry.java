package symboltable;
import grammarsymbols.*;

/** Symbol Table Entry for Array Types */
public class ArrayEntry extends SymbolTableEntry {
	/** Address at which this array is located */
	int address;
	/** Upper bound */
	int upperBound;
	/** Lower Bound */
	int lowerBound;
	
	/* Constructors */
	public ArrayEntry(String name){
		super(name);
	}
	
	public ArrayEntry(String aName, int anAddress, TokenType aType, int upBound, int lowBound){
		super(aName, aType);
		address = anAddress;
		upperBound = upBound;
		lowerBound = lowBound;
	}
	
	public ArrayEntry(String aName, TokenType aType, int upBound, int lowBound){
		super(aName, aType);
		upperBound = upBound;
		lowerBound = lowBound;
	}
	
	/* Getters */
	public int getAddress() {
		return address;
	}
	public int getUpperBound(){
		return upperBound;
	}
	public int getLowerBound(){
		return lowerBound;
	}
	
	/* Setters */
	public void setAddress(int address) {
		this.address = address;
	}
	public void setUpperBound(int upper){
		this.upperBound = upper;
	}
	public void setLowerBound(int lower){
		this.lowerBound = lower;
	}
	
	@Override
	public boolean isArray(){
		return true;
	}
	
	@Override
	public void print () {
		System.out.println("Array Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   Address : " + this.getAddress());
		System.out.println("   Up Bound: " + this.getUpperBound());
		System.out.println("   Lo Bound: " + this.getLowerBound());
		System.out.println();
	}

}
