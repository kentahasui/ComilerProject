package symboltable;
import java.util.*;
import java.util.Map.Entry;

public class SymbolTable {
	// Table of entries: each name maps to an entry
	private Map<String, SymbolTableEntry> table;
	
	/** Constructor */
	public SymbolTable(int size){
		// Initialize the table
		table = new LinkedHashMap<String, SymbolTableEntry>(size);
	}
	
	/** Looks up a given token in the symbol table. Returns the token if found, 
	 * returns null if not. 
	 */
	public SymbolTableEntry lookup(String name){
		if(table.containsKey(name)) return table.get(name);
		else return null;
	}
	
	public boolean contains(String name){
		return table.containsKey(name);
	}
	
	/** Inserts a Token-SymbolTableEntry (key-value) pair into the table.
	 * Does nothing if the table already contains an entry with the same name */
	public void insert(SymbolTableEntry entry){
		String eName = entry.getName();
		if(!table.containsKey(eName)){
			table.put(eName, entry);
		}
	}
	
	public Map<String, SymbolTableEntry> returnTable(){
		return table;
	}
	
	/** Prints out the contents of the symbol table */
	public void dumpTable(){
		System.out.println("Printing table: ");
		// Iterates through the hash table and prints the key-value pairs
		for(Entry<String, SymbolTableEntry> entry: table.entrySet()){
			entry.getValue().print();
		}
		System.out.println();
	}

}
