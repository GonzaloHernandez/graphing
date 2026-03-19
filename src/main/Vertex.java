package main;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Vector;

public class Vertex {
	
	//-------------------------------------------------------------------------------------

	static	final int	STILL	= 0;
	static	final int	FOCUSED	= 1;
	static	final int	MARKED	= 2;
	
	static	final int	RADIUS	= 15;
	
	//-------------------------------------------------------------------------------------
	
	private	int		number,x,y;
	private	int		status;
	private String	value;
	private Type	type;
	private String	label;
	private boolean active;
	
	private	Vector	<Edge>outs;
	private	Vector	<Edge>ins;
	private	boolean	accepted;

	//-------------------------------------------------------------------------------------
	
	public Vertex(int number,int x,int y) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= FOCUSED;
		this.accepted	= false;
		this.value		= "0";
		this.type		= null;
		this.label		= "";
		this.active		= true;
		this.outs		= new Vector<Edge>();
		this.ins		= new Vector<Edge>();
	}
	
	//-------------------------------------------------------------------------
	
	public Vertex(int number,int x,int y,int status,boolean accepted) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		this.value		= "0";
		this.type		= null;
		this.label		= "";
		this.active		= true;
		this.outs		= new Vector<Edge>();
		this.ins		= new Vector<Edge>();
	}
	
	//-------------------------------------------------------------------------
	
	public Vertex(int number,int x,int y,int status,boolean accepted,
		String value,Type type,String label) 
	{
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		this.value		= value;
		this.type		= type;
		this.label		= label;
		this.active		= true;
		this.outs		= new Vector<Edge>();
		this.ins		= new Vector<Edge>();
	}

	//-------------------------------------------------------------------------
	
	public int draw(Graphics2D g,GrapherSettings settings,
					int edgeSequence,boolean hidden) 
	{		
		if (hidden && !active) return edgeSequence + outs.size();

		for (int i=0 ; i<outs.size() ; i++) {
			Edge edge = (Edge)outs.elementAt(i);
			if (hidden && !edge.isActive()) {
				edgeSequence++;
			}
			else {
				edgeSequence = edge.draw(g,settings,edgeSequence,hidden);
			}
		}

		Color LIGHT_BLUE	= new Color(220,220,255);
		Color LIGHT_RED		= new Color(235,168,168);

		Color foreColor = null;
		Color backColor = null;

		if (status==FOCUSED && active) {
			foreColor = Color.BLACK;
			backColor = LIGHT_RED;
		}
		else if (status==FOCUSED && !active)	{
			foreColor = Color.RED;
			backColor = Color.WHITE;
		}
		else if (status!=FOCUSED && active) {
			foreColor = Color.BLACK;
			backColor = LIGHT_BLUE;
		}
		else if (status!=FOCUSED && !active) {
			foreColor = new Color(220,220,220);
			backColor = Color.WHITE;
		}
		
		int t=0;
		if (type != null && settings.showVertexType) {
			t=type.getId(); 
		} else{ 
			t = 0;
		}

		g.setStroke(new BasicStroke(accepted?2.0f:1.0f));

		switch(t) {
			case 0:	
				g.setColor(backColor);
				g.fillOval(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
				g.setColor(foreColor);
				g.drawOval(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
				break;
			case 1:	
				g.setColor(backColor);
				g.fillRect(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
				g.setColor(foreColor);
				g.drawRect(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
				break;
			case 2:	{
				int[] xs = {x,x+RADIUS,x,x-RADIUS};
				int[] ys = {y-RADIUS,y,y+RADIUS,y};
				g.setColor(backColor);
				g.fillPolygon(xs,ys,4);
				g.setColor(foreColor);
				g.drawPolygon(xs,ys,4);
			}	break;
			case 3:	{
				int[] xs = {x-RADIUS,x,x+RADIUS};
				int[] ys = {y+RADIUS,y-RADIUS,y+RADIUS};
				g.setColor(backColor);
				g.fillPolygon(xs,ys,3);
				g.setColor(foreColor);
				g.drawPolygon(xs,ys,3);
			}	break;
			default: {
				int[] xs = {x-RADIUS/2,x+RADIUS/2,x+RADIUS,x+RADIUS,x+RADIUS/2,x-RADIUS/2,x-RADIUS,x-RADIUS};
				int[] ys = {y-RADIUS,y-RADIUS,y-RADIUS/2,y+RADIUS/2,y+RADIUS,y+RADIUS,y+RADIUS/2,y-RADIUS/2};
				g.setColor(backColor);
				g.fillPolygon(xs,ys,8);
				g.setColor(foreColor);
				g.drawPolygon(xs,ys,8);
			} break;
		}

		g.setStroke(new BasicStroke(1.0f));

		if (settings.showVertexSequence) {

			Lexicon lex = settings.lexicon;

			int first = settings.firstZero?0:1;
			g.setColor(Color.darkGray);
			if (settings.showVertexValue || settings.showVertexLabel) {
				g.setFont(new Font("Arial",Font.ITALIC,9));
				g.drawString(lex._vertex,x-1-(3*(new String(""+number)).length()),y+RADIUS+9);
				g.setFont(new Font("Arial",Font.ITALIC,7));
				g.drawString(""+(number+first),x+3-(3*(new String(""+number)).length()),y+RADIUS+11);
			}
			else {
				g.setFont(new Font("Arial",Font.ITALIC,13));
				g.drawString(lex._vertex,x-2-(3*(new String(""+number)).length()),y+4);
				g.setFont(new Font("Arial",Font.ITALIC,8));
				g.drawString(""+(number+first),x+3-(3*(new String(""+number)).length()),y+8);
			}
		}
		if (settings.showVertexValue) {
			if (!value.equals(settings.showVertexValueDiff)){
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial",Font.PLAIN,10));
				g.drawString(""+value,x-(2*(new String(""+value)).length()),y+4);
			}
		} 
		else if (settings.showVertexLabel) {
			if (!label.equals(settings.showVertexLabelDiff)){
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial",Font.PLAIN,9));
				g.drawString(""+label,x-(2*(new String(""+label)).length()),y+4);
			}
		}
		return edgeSequence;
	}
	
	//-------------------------------------------------------------------------------------

	public void setNumber(int number){
		this.number = number;
	}
	
	//-------------------------------------------------------------------------------------

	public void setStatus(int status) {
		this.status	= status;
	}
	
	//-------------------------------------------------------------------------------------

	public void setLocation(int mousex,int mousey) {
		x = mousex;
		y = mousey;
	}
	
	//-------------------------------------------------------------------------------------

	public void setAccepted(boolean accepted){
		this.accepted = accepted;
	}
	
	//-------------------------------------------------------------------------------------

	public void setType(Type type){
		this.type = type;
	}

	//-------------------------------------------------------------------------------------

	public void setValue(String value){
		this.value = value;
	}
	
	//-------------------------------------------------------------------------------------

	public void setLabel(String label){
		this.label = label;
	}

	//-------------------------------------------------------------------------------------

	public void setActive(boolean act){
		this.active = act;
	}

	//-------------------------------------------------------------------------------------

	public void setActive(boolean act, boolean propagate){
		if (act == this.active) return;
		this.active = act;
		if (propagate) {
			for (int i=0 ; i<outs.size() ; i++) {
				Edge c = (Edge)outs.elementAt(i);
				if (act == true && !c.getTarget().isActive()) continue;
				c.setActive(act, propagate);
			}
			for (int i=0 ; i<ins.size() ; i++) {
				Edge c = (Edge)ins.elementAt(i);
				if (act == true && !c.getSource().isActive()) continue;
				c.setActive(act, propagate);
			}
		}
	}

	//-------------------------------------------------------------------------------------

	// public void setMouseDiference(int mousex,int mousey) {
	// 	diferencex	= mousex - x;
	// 	diferencey	= mousey - y;
	// }
	
	//-------------------------------------------------------------------------------------

	public int getStatus() {
		return status;
	}
	
	//-------------------------------------------------------------------------------------

	public int getNumber() {
		return number;
	}
	
	//-------------------------------------------------------------------------------------

	public Type getType() {
		return type;
	}
	
	//-------------------------------------------------------------------------------------

	public String getValue() {
		return value;
	}

	//-------------------------------------------------------------------------------------

	public String getLabel(){
		return label;
	}
	
	//-------------------------------------------------------------------------------------
	
	public int getX() {
		return x;
	}
	
	//-------------------------------------------------------------------------------------

	public int getY() {
		return y;
	}

	//-------------------------------------------------------------------------------------

	public boolean isActive() {
		return active;
	}

	//-------------------------------------------------------------------------------------

	public boolean isAccepted(){
		return accepted;
	}
	//-------------------------------------------------------------------------------------

	public boolean isArea(int mousex,int mousey) {
		int distance = (int)Math.sqrt(Math.pow(Math.abs(mousex-x),2)+Math.pow(Math.abs(mousey-y),2));
		
		if (distance < RADIUS)	return true;
		else					return false;
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addEdge(Edge c) {
		outs.add(c);
		c.getTarget().ins.add(c);
	}

	//-------------------------------------------------------------------------------------
	
	public void addEdge(Vertex target) {
		for (int c=0;c<getOuts().size();c++){
			if (getOuts().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		Edge c = new Edge(this,target);
		outs.add(c);
		target.ins.add(c);
		arrangeEdges(target);
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addEdge(Vertex target,Type type,int distance,
		double rotation,String value,boolean active) 
	{
		for (int c=0;c<getOuts().size();c++){
			if (getOuts().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		Edge c = new Edge(this,target,Edge.STILL,distance,rotation,type,value,"");
		c.setActive(active);
		outs.add(c);
	}
	
	//-------------------------------------------------------------------------------------

	public Vector <Edge>getOuts() {
		return outs;
	}
	
	//-------------------------------------------------------------

	public void deleteEdge(Edge edge){
		outs.remove(edge);
	}

	//-------------------------------------------------------------

	public void deleteAllEdges(){
		outs.removeAllElements();
	}
	
	//-------------------------------------------------------------

	public void arrangeEdges (Vertex target){
		if (target.equals(this)) return;
		
		Edge directedEdge	= null;
		Edge backEdge		=null;
		
		for (int c=0;c<getOuts().size();c++){
			if (getOuts().elementAt(c).getTarget().equals(target)) { 
				directedEdge	= getOuts().elementAt(c);
			}
		}

		for (int c=0;c<target.getOuts().size();c++){
			if (target.getOuts().elementAt(c).getTarget().equals(this)) { 
				backEdge		= target.getOuts().elementAt(c);
			}
		}
		
		if (directedEdge != null && backEdge == null){
			directedEdge.setDistance(0);
		}
		else if (directedEdge == null && backEdge != null){
			backEdge.setDistance(0);
		}
		else {
			directedEdge.setDistance(15);
			backEdge.setDistance(15);
		}
	}
}
