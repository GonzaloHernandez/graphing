package main;
public class Type {
	
	//-------------------------------------------------------------------------------------

	private	int		id;
	private	String	name,description;
	
	//-------------------------------------------------------------------------------------

	public Type(int number,String name,String description) {
		this.id				= number;
		this.name			= name;
		this.description	= description;
	}

	//-------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}
	
	//-------------------------------------------------------------------------------------

	public String getDescription() {
		return description;
	}
	
	//-------------------------------------------------------------------------------------

	public int getId() {
		return id;
	}
	
	//-------------------------------------------------------------------------------------

	public void setName(String name){
		this.name	= name;
	}
	
	//-------------------------------------------------------------------------------------

	public void setDescription(String description){
		this.description	= description;
	}

	//-------------------------------------------------------------------------------------

	public void setId(int number){
		this.id	= number;
	}
}