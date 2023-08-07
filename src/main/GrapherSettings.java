package main;
import java.util.Vector;

public class GrapherSettings {

	//---------------------------------------------------------------------------
	
	protected	boolean	showTypeNames;
	protected	boolean	showStateSequence;
	protected	String	comment;
	protected	Vector	<ConnectionType>types;
	protected	int	measure;

	protected	boolean showStatePriorities;
	protected	boolean allowStateZero;

	protected	int programmingView;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(boolean nameTypes,boolean numberStates,String comment,Vector <ConnectionType>types){
		this.showTypeNames		= nameTypes;
		this.showStateSequence	= numberStates;
		this.comment			= comment;
		this.types				= types;
		this.measure			= 1; // 0=Centimeters  1=Inch
		this.showStatePriorities	= false;
		this.allowStateZero		= false;
		this.programmingView	= 2;
	}

}
