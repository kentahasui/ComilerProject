package grammarsymbols;

public interface GrammarSymbol {
	
	public int getIndex ();
	public boolean isToken ();
	public boolean isNonTerminal();
	public boolean isAction();

}
