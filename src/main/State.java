package main;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Vector;

public class State {
	
	//-------------------------------------------------------------------------------------

	static	final int	STILL	= 0;
	static	final int	FOCUSED	= 1;
	static	final int	MARKED	= 2;
	
	static	final int	RADIUS	= 15;
	
	//-------------------------------------------------------------------------------------
	
	private	int		x,y,number;
	private	int		status;
	private	int		diferencex,diferencey;
	private	Vector	<Connection>connections;
	private	Vector	<Connection>arrivals;
	private	boolean	accepted;

	private int		value;
	private int		owner;
	private boolean active;

	//-------------------------------------------------------------------------------------
	
	public State(int number,int x,int y) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= FOCUSED;
		this.accepted	= false;
		this.value		= 0;
		this.owner		= 0;
		this.active		= true;
		connections		= new Vector<Connection>();
		arrivals		= new Vector<Connection>();
	}
	
	//-------------------------------------------------------------------------------------
	
	public State(int number,int x,int y,int status,boolean accepted) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		this.value		= 0;
		this.owner		= 0;
		this.active		= true;
		connections		= new Vector<Connection>();
		arrivals		= new Vector<Connection>();
	}
	
	//-------------------------------------------------------------------------------------
	
	public State(int number,int x,int y,int status,boolean accepted,int value,int owner) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		this.value		= value;
		this.owner		= owner;
		this.active		= true;
		connections		= new Vector<Connection>();
		arrivals		= new Vector<Connection>();
	}

	//-------------------------------------------------------------------------------------
	
	public int draw(Graphics2D g,GrapherSettings settings,int connectionSequence,boolean hidden) {
		
		if (hidden && !active) return connectionSequence + connections.size();

		for (int i=0 ; i<connections.size() ; i++) {
			Connection connection = (Connection)connections.elementAt(i);
			if (hidden && !connection.getTarget().isActive()) {
				connectionSequence++;
			}
			else {
				connectionSequence = connection.draw(g,settings,connectionSequence,hidden);
			}
		}

		Color foreColor = Color.GRAY;;
		Color backColor = new Color(220,220,255);

		if (status==FOCUSED)	foreColor = new Color(200,0,0);
		else if (active)		foreColor = Color.BLACK;

		if (!active)			backColor = Color.WHITE;
		
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
		if (settings.showStateSequence) {
			int first = settings.firstZero?0:1;
			g.setColor(Color.GRAY);
			if (settings.showStatePriorities) {
				g.setFont(new Font("Arial",Font.ITALIC,9));
				g.drawString(""+(number+first),x-(3*(new String(""+number)).length()),y+RADIUS-1);
			}
			else {
				g.setFont(new Font("Arial",Font.ITALIC,13));
				g.drawString(""+(number+first),x-(3*(new String(""+number)).length()),y+4);
			}
		}
		if (settings.showStatePriorities) {
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
		x = (int)(Math.round((mousex-diferencex)/10.0)*10);
		y = (int)(Math.round((mousey-diferencey)/10.0)*10);
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
				Connection c = (Connection)connections.elementAt(i);
				if (act == true && !c.getTarget().isActive()) continue;
				c.setActive(act, propagate);
			}
			for (int i=0 ; i<arrivals.size() ; i++) {
				Connection c = (Connection)arrivals.elementAt(i);
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

	public void setMouseDiference(int mousex,int mousey) {
		diferencex	= mousex - x;
		diferencey	= mousey - y;
	}
	
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
	
	public void addConnection(Connection c) {
		connections.add(c);
		c.getTarget().arrivals.add(c);
	}

	//-------------------------------------------------------------------------------------
	
	public void addConnection(State target) {
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		Connection c = new Connection(this,target,null);
		connections.add(c);
		target.arrivals.add(c);
		arrangeConnections(target);
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addConnection(State target,ConnectionType type,int distance,double rotation,int value,boolean active) {
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		Connection c = new Connection(this,target,type,Connection.STILL,distance,rotation,value);
		c.setActive(active);
		connections.add(c);
	}
	
	//-------------------------------------------------------------------------------------

	public Vector <Connection>getConnections() {
		return connections;
	}
	
	//-------------------------------------------------------------

	public void deleteConnecion(Connection connection){
		connections.remove(connection);
	}

	//-------------------------------------------------------------

	public void deleteAllConnecion(){
		connections.removeAllElements();
	}
	
	//-------------------------------------------------------------

	public void arrangeConnections (State target){
		if (target.equals(this)) return;
		
		Connection directedConnection	= null;
		Connection backConnection		=null;
		
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
