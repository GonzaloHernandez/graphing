package main;

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
	
	protected	boolean	showVertexSequence;
	protected	boolean showVertexValue;
	protected	boolean showVertexType;
	protected	boolean showVertexLabel;
	protected	boolean	showEdgeSequence;
	protected	boolean	showEdgeValue;
	protected	boolean	showEdgeType;
	protected	boolean	showEdgeLabel;

	protected	boolean allowFirsVertex;
	protected	boolean firstZero;
	protected	int		gridScale;

	protected	String	comment;
	protected	int		measure;
	protected	boolean exportAuto;
	protected	int		exportType;

	protected	Dictionary dictionary;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(){
		this.showVertexSequence		= true;
		this.showVertexValue		= false;
		this.showVertexType			= false;
		this.showVertexLabel		= false;

		this.showEdgeSequence		= true;
		this.showEdgeValue			= false;
		this.showEdgeType			= false;
		this.showEdgeLabel			= false;

		this.comment				= "";
		this.measure				= 1; // 0=Centimeters  1=Inch
		this.allowFirsVertex		= true;
		this.firstZero				= true;
		this.gridScale				= 15;
		this.exportAuto				= false;
		this.exportType				= 0;
		this.dictionary				= new Dictionary();
	}

}
