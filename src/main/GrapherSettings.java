package main;
import java.util.Vector;

public class GrapherSettings {

	//---------------------------------------------------------------------------
	
	protected	boolean	nameTypes;
	protected	boolean	numberStates;
	protected	String	comment;
	protected	Vector	<ConectionType>types;
	protected	int	measure;
	
	//---------------------------------------------------------------------------
	
	public GrapherSettings(boolean nameTypes,boolean numberStates,String comment,Vector <ConectionType>types){
		this.nameTypes		= nameTypes;
		this.numberStates	= numberStates;
		this.comment		= comment;
		this.types			= types;
		this.measure		= 1; // 0=Centimeters  1=Inch
	}
}
