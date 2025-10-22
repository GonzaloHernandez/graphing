package main;
public class EdgeType {
	
	//-------------------------------------------------------------------------------------

	private	int		number;
	private	String	name,symbols;
	
	//-------------------------------------------------------------------------------------

	public EdgeType(int number,String name,String symbols) {
		this.number		= number;
		this.name		= name;
		this.symbols	= symbols;
	}

	//-------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}
	
	//-------------------------------------------------------------------------------------

	public String getSymbols() {
		return symbols;
	}
	
	//-------------------------------------------------------------------------------------

	public int getNumber() {
		return number;
	}
	
	//-------------------------------------------------------------------------------------

	public void setName(String name){
		this.name	= name;
	}
	
	//-------------------------------------------------------------------------------------

	public void setSymbols(String symbols){
		this.symbols	= symbols;
	}

	//-------------------------------------------------------------------------------------

	public void setNumber(int number){
		this.number	= number;
	}
}