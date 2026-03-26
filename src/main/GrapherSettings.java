package main;

class Lexicon {
	public String graph,		_graph;
	public String vertex,		_vertex;
	public String vertexType,	_vertexType;
	public String vertexValue,	_vertexValue;
	public String vertexLabel,	_vertexLabel;
	public String edge,			_edge;
	public String edgeValue,	_edgeValue;
	public String edgeType,		_edgeType;
	public String edgeLabel,	_edgeLabel;

	public Lexicon(){
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

	static public String capitalize(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		String str = input.trim();
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}

public class GrapherSettings {

	public static final String[] exportTypes = {
		"Json", 
		"Dzn", 
		"Ajacency Matrix (Mzn) for Regular", 
		"Ajacency Matrix with costs(Mzn)", 
		"Lists"};

	//---------------------------------------------------------------------------
	
	protected	boolean	showVertexSequence;
	protected	boolean showVertexValue;
	protected	boolean showVertexType;
	protected	boolean showVertexLabel;
	protected	String	showVertexValueDiff;
	protected	String	showVertexLabelDiff;

	protected	boolean	showEdgeSequence;
	protected	boolean	showEdgeValue;
	protected	boolean	showEdgeType;
	protected	boolean	showEdgeLabel;
	protected	String	showEdgeValueDiff;
	protected	String	showEdgeLabelDiff;

	protected	boolean allowFirsVertex;
	protected	boolean firstZero;
	protected	int		gridScale;

	protected	String	comment;
	protected	int		measure;
	protected	boolean exportAuto;
	protected	int		exportType;

	protected	Lexicon lexicon;

	//---------------------------------------------------------------------------
	
	public GrapherSettings(){
		this.showVertexSequence		= true;
		this.showVertexValue		= false;
		this.showVertexType			= false;
		this.showVertexLabel		= false;
		this.showVertexValueDiff	= "";
		this.showVertexLabelDiff	= "";

		this.showEdgeSequence		= true;
		this.showEdgeValue			= false;
		this.showEdgeType			= false;
		this.showEdgeLabel			= false;
		this.showEdgeValueDiff		= "";
		this.showEdgeLabelDiff		= "";

		this.comment			= "";
		this.measure			= 1; // 0=Centimeters  1=Inch
		this.allowFirsVertex	= true;
		this.firstZero			= true;
		this.gridScale			= 15;
		this.exportAuto			= false;
		this.exportType			= 0;
		this.lexicon			= new Lexicon();
	}

}
