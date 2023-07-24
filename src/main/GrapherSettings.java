package main;
import java.util.Vector;

public class GrapherSettings {

	//---------------------------------------------------------------------------
	
	protected	boolean	showTypeNames;
	protected	boolean	showStateNumbers;
	protected	String	comment;
	protected	Vector	<ConectionType>types;
	protected	int	measure;

	protected	boolean showStateValues;
	protected	boolean allowStateZero;

	protected	int programmingView;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(boolean nameTypes,boolean numberStates,String comment,Vector <ConectionType>types){
		this.showTypeNames		= nameTypes;
		this.showStateNumbers	= numberStates;
		this.comment			= comment;
		this.types				= types;
		this.measure			= 1; // 0=Centimeters  1=Inch
		this.showStateValues	= false;
		this.allowStateZero		= false;
		this.programmingView	= 2;
	}

}
