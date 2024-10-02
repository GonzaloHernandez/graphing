package main;
import java.util.Vector;

public class GrapherSettings {

	//---------------------------------------------------------------------------
	
	protected	boolean	showTypeNames;
	protected	boolean	showStateSequence;
	protected	boolean	showConnectionSequence;
	protected	String	comment;
	protected	Vector	<ConnectionType>types;
	protected	int	measure;

	protected	boolean showStatePriorities;
	protected	boolean allowFirsState;
	protected	boolean firstZero;

	protected	int programmingView;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(boolean nameTypes,boolean numberStates,String comment,Vector <ConnectionType>types){
		this.showTypeNames			= nameTypes;
		this.showStateSequence		= numberStates;
		this.showConnectionSequence	= false;
		this.comment				= comment;
		this.types					= types;
		this.measure				= 1; // 0=Centimeters  1=Inch
		this.showStatePriorities	= false;
		this.allowFirsState			= false;
		this.firstZero				= false;
		this.programmingView		= 2;
	}

}
