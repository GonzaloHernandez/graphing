package main;
import java.util.Vector;

class Dictionary {
	public String graph,graph1;
	public String vertex,vertex1;
	public String vertexValue,vertexValue1;
	public String edge,edge1;
	public String edgeValue,edgeValue1;

	public Dictionary(){
		this.graph			= "Graph";
		this.vertex			= "Vertex";
		this.vertexValue	= "Value";
		this.edge			= "Edge";
		this.edgeValue		= "Cost";
		this.graph1			= "g";
		this.vertex1		= "v";
		this.vertexValue1	= "";
		this.edge1			= "e";
		this.edgeValue1		= "";
	}
}

public class GrapherSettings {

	public static final String[] exportTypes = {"Json", "Dzn", "Ajacency Matrix (Mzn)", "Lists"};

	//---------------------------------------------------------------------------
	
	protected	boolean	showTypeNames;
	protected	boolean	showVertexSequence;
	protected	boolean	showConnectionSequence;
	protected	String	comment;
	protected	Vector	<EdgeType>types;
	protected	int	measure;

	protected	boolean showVertexPriorities;
	protected	boolean allowFirsVertex;
	protected	boolean firstZero;
	protected	boolean exportAuto;
	protected	int		exportType;

	protected	Dictionary dictionary;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(boolean nameTypes,boolean numberVertices,String comment,
	Vector <EdgeType>types){
		this.showTypeNames			= nameTypes;
		this.showVertexSequence		= numberVertices;
		this.showConnectionSequence	= false;
		this.comment				= comment;
		this.types					= types;
		this.measure				= 1; // 0=Centimeters  1=Inch
		this.showVertexPriorities	= false;
		this.allowFirsVertex		= true;
		this.firstZero				= true;
		this.exportAuto				= false;
		this.exportType				= 0;
		this.dictionary				= new Dictionary();
	}

}
