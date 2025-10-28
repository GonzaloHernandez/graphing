package main;
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
	
	private	int		x,y,number;
	private	int		status;
	private	Vector	<Edge>connections;
	private	Vector	<Edge>arrivals;
	private	boolean	accepted;

	private int		value;
	private int		owner;
	private boolean active;

	//-------------------------------------------------------------------------------------
	
	public Vertex(int number,int x,int y) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= FOCUSED;
		this.accepted	= false;
		this.value		= 0;
		this.owner		= 0;
		this.active		= true;
		connections		= new Vector<Edge>();
		arrivals		= new Vector<Edge>();
	}
	
	//-------------------------------------------------------------------------------------
	
	public Vertex(int number,int x,int y,int status,boolean accepted) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		this.value		= 0;
		this.owner		= 0;
		this.active		= true;
		connections		= new Vector<Edge>();
		arrivals		= new Vector<Edge>();
	}
	
	//-------------------------------------------------------------------------------------
	
	public Vertex(int number,int x,int y,int status,boolean accepted,int value,int owner) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		this.value		= value;
		this.owner		= owner;
		this.active		= true;
		connections		= new Vector<Edge>();
		arrivals		= new Vector<Edge>();
	}

	//-------------------------------------------------------------------------------------
	
	public int draw(Graphics2D g,GrapherSettings settings,int connectionSequence,boolean hidden) {
		
		if (hidden && !active) return connectionSequence + connections.size();

		for (int i=0 ; i<connections.size() ; i++) {
			Edge connection = (Edge)connections.elementAt(i);
			if (hidden && !connection.isActive()) {
				connectionSequence++;
			}
			else {
				connectionSequence = connection.draw(g,settings,connectionSequence,hidden);
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
			foreColor = Color.GRAY;
			backColor = Color.WHITE;
		}
		
		switch(owner) {
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
			case 2:	
				int[] xs = {x-RADIUS/2,x+RADIUS/2,x+RADIUS,x+RADIUS,x+RADIUS/2,x-RADIUS/2,x-RADIUS,x-RADIUS};
				int[] ys = {y-RADIUS,y-RADIUS,y-RADIUS/2,y+RADIUS/2,y+RADIUS,y+RADIUS,y+RADIUS/2,y-RADIUS/2};
				g.setColor(backColor);
				g.fillPolygon(xs,ys,8);
				g.setColor(foreColor);
				g.drawPolygon(xs,ys,8);
				break;
		}

		if (accepted) {
			g.setColor(foreColor);
			g.drawOval(x-RADIUS+3,y-RADIUS+3,(RADIUS-3)*2,(RADIUS-3)*2);
		}
		if (settings.showVertexSequence) {

			Dictionary dict = settings.dictionary;

			int first = settings.firstZero?0:1;
			g.setColor(Color.darkGray);
			if (settings.showVertexPriorities) {
				g.setFont(new Font("Arial",Font.ITALIC,9));
				g.drawString(dict.vertex1,x-1-(3*(new String(""+number)).length()),y+RADIUS+9);
				g.setFont(new Font("Arial",Font.ITALIC,7));
				g.drawString(""+(number+first),x+3-(3*(new String(""+number)).length()),y+RADIUS+11);
			}
			else {
				g.setFont(new Font("Arial",Font.ITALIC,13));
				g.drawString(dict.vertex1,x-2-(3*(new String(""+number)).length()),y+4);
				g.setFont(new Font("Arial",Font.ITALIC,8));
				g.drawString(""+(number+first),x+3-(3*(new String(""+number)).length()),y+8);
			}
		}
		if (settings.showVertexPriorities) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial",Font.PLAIN,13));
			g.drawString(""+value,x-(3*(new String(""+value)).length()),y+4);
		}
		return connectionSequence;
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

	public void setValue(int value){
		this.value = value;
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
			for (int i=0 ; i<connections.size() ; i++) {
				Edge c = (Edge)connections.elementAt(i);
				if (act == true && !c.getTarget().isActive()) continue;
				c.setActive(act, propagate);
			}
			for (int i=0 ; i<arrivals.size() ; i++) {
				Edge c = (Edge)arrivals.elementAt(i);
				if (act == true && !c.getSource().isActive()) continue;
				c.setActive(act, propagate);
			}
		}
	}

	//-------------------------------------------------------------------------------------

	public void setOwner(int owner){
		this.owner = owner;
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

	public int getValue() {
		return value;
	}
	
	//-------------------------------------------------------------------------------------

	public int getOwner() {
		return owner;
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
	
	public void addConnection(Edge c) {
		connections.add(c);
		c.getTarget().arrivals.add(c);
	}

	//-------------------------------------------------------------------------------------
	
	public void addConnection(Vertex target) {
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		Edge c = new Edge(this,target,null);
		connections.add(c);
		target.arrivals.add(c);
		arrangeConnections(target);
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addConnection(Vertex target,EdgeType type,int distance,double rotation,int value,boolean active) {
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		Edge c = new Edge(this,target,type,Edge.STILL,distance,rotation,value);
		c.setActive(active);
		connections.add(c);
	}
	
	//-------------------------------------------------------------------------------------

	public Vector <Edge>getConnections() {
		return connections;
	}
	
	//-------------------------------------------------------------

	public void deleteConnecion(Edge connection){
		connections.remove(connection);
	}

	//-------------------------------------------------------------

	public void deleteAllConnecion(){
		connections.removeAllElements();
	}
	
	//-------------------------------------------------------------

	public void arrangeConnections (Vertex target){
		if (target.equals(this)) return;
		
		Edge directedConnection	= null;
		Edge backConnection		=null;
		
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				directedConnection	= getConnections().elementAt(c);
			}
		}

		for (int c=0;c<target.getConnections().size();c++){
			if (target.getConnections().elementAt(c).getTarget().equals(this)) { 
				backConnection		= target.getConnections().elementAt(c);
			}
		}
		
		if (directedConnection != null && backConnection == null){
			directedConnection.setDistance(0);
		}
		else if (directedConnection == null && backConnection != null){
			backConnection.setDistance(0);
		}
		else {
			directedConnection.setDistance(15);
			backConnection.setDistance(15);
		}
	}
}
