package semanticActions;
import java.util.ArrayList;

/** Class to store intermediate code as it is generated */
public class Quadruples {
	/** A Collection of Quadruples, which are themselves arrays of Strings */
	private ArrayList<String[]> quadArray;
	/** Index of the next quadruple entry */
	private int nextQuad;
	
	public Quadruples(){
		quadArray = new ArrayList<String[]>();
		nextQuad = 0;
	}
	
	/** Returns one of the four fields within the quadruple at index quadIndex */
	public String getField(int quadIndex, int field){
		String[] quad = quadArray.get(quadIndex);
		return quad[field];
	}
	
	/** Sets the value of a given field in a given quadruple */
	public void setField(int quadIndex, int field, String value){
		quadArray.get(quadIndex)[field] = value;
	}
	
	/** Returns the index of the next quadruple */
	public int getNextQuad(){
		return nextQuad;
	}
	
	/** Increment the nextQuad index */
	public void incrementNextQuad(){
		nextQuad++;
	}
	
	/** Adds a new generated quadruple to the list of quadruples */
	public void addQuad(String[] quadruple){
		quadArray.add(quadruple);
		nextQuad++;
	}
	
	/** Returns the quadruple at the given index */
	public String[] getQuad(int index){
		return quadArray.get(index);
	}
	
	/** Prints the contents of the array of quadruples */
	public void print(){
		// Print "CODE"
		System.out.println(quadArray.get(0)[0]);
		// Iterate through the quadruple array
		for(int index = 1; index<quadArray.size(); index++){
			String[] quadruple = quadArray.get(index);
			System.out.printf("%2d: ", index);
			for(int field = 0; field<quadruple.length; field++){
				System.out.print(quadruple[field]); 
				// Print appropriate commas
				if((field>0) && (field < quadruple.length - 1)){
					System.out.print(",");
				}
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	

}
