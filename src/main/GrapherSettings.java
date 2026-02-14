package main;
import java.util.Vector;

class Dictionary {
	public String graph,_graph;
	public String vertex,_vertex;
	public String vertexType,_vertexType;
	public String vertexValue,_vertexValue;
	public String vertexLabel,_vertexLabel;
	public String edge,_edge;
	public String edgeValue,_edgeValue;
	public String edgeType,_edgeType;
	public String edgeLabel,_edgeLabel;

	public Dictionary(){
		this.graph			= "graph";	this._graph			= "g";

		this.vertex			= "vertex";	this._vertex		= "v";
		this.vertexType		= "type";	this._vertexType	= " ";
		this.vertexValue	= "value";	this._vertexValue	= " ";
		this.vertexLabel	= "label";	this._vertexLabel	= " ";

		this.edge			= "edge";	this._edge			= "e";
		this.edgeValue		= "cost";	this._edgeValue		= " ";
		this.edgeType		= "type";	this._edgeType		= " ";
		this.edgeLabel		= "label";	this._edgeLabel		= " ";
	}
}

public class GrapherSettings {

	public static final String[] exportTypes = {
		"Json", 
		"Dzn", 
		"Ajacency Matrix (Mzn)", 
		"Ajacency Matrix with costs(Mzn)", 
		"Lists"};

	//---------------------------------------------------------------------------
	
	protected	boolean	showTypeNames;
	protected	boolean	showVertexSequence;
	protected	boolean	showConnectionSequence;
	protected	String	comment;
	protected	Vector	<Type>types;
	protected	int		measure;

	protected	boolean showVertexPriorities;
	protected	boolean allowFirsVertex;
	protected	boolean firstZero;
	protected	int		gridScale;

	protected	boolean exportAuto;
	protected	int		exportType;

	protected	Dictionary dictionary;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(boolean nameTypes,boolean numberVertices,String comment,
	Vector <Type>types){
		this.showTypeNames			= nameTypes;
		this.showVertexSequence		= numberVertices;
		this.showConnectionSequence	= false;
		this.comment				= comment;
		this.types					= types;
		this.measure				= 1; // 0=Centimeters  1=Inch
		this.showVertexPriorities	= false;
		this.allowFirsVertex		= true;
		this.firstZero				= true;
		this.gridScale				= 10;
		this.exportAuto				= false;
		this.exportType				= 0;
		this.dictionary				= new Dictionary();
	}

}
