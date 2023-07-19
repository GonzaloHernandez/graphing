package main;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	private	boolean	accepted;
	
	//-------------------------------------------------------------------------------------
	
	public State(int number,int x,int y) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= FOCUSED;
		this.accepted	= false;
		connections		= new Vector<Connection>();
	}
	
	//-------------------------------------------------------------------------------------
	
	public State(int number,int x,int y,int status,boolean accepted) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= status;
		this.accepted	= accepted;
		connections		= new Vector<Connection>();
	}
	
	//-------------------------------------------------------------------------------------
	
	public void draw(Graphics g1,GrapherSettings settings) {
		
		Graphics2D g = (Graphics2D) g1;

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		for (int i=0 ; i<connections.size() ; i++) {
			Connection connection = (Connection)connections.elementAt(i);
			connection.draw(g,settings);
		}
		switch (status) {
			case FOCUSED:	g.setColor(Color.RED);		break;
			case MARKED:	g.setColor(Color.YELLOW);	break;
			case STILL:		g.setColor(new Color(220,220,255));	break;
		}
		
		g.fillOval(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
		g.setColor(Color.BLACK);
		g.drawOval(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
		if (accepted) g.drawOval(x-RADIUS+3,y-RADIUS+3,(RADIUS-3)*2,(RADIUS-3)*2);
		if (settings.numberStates) g.drawString(""+number,x-(3*(new String(""+number)).length()),y+4);
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
		x = mousex - diferencex;
		y = mousey - diferencey;
	}
	
	//-------------------------------------------------------------------------------------

	public void setAccepted(boolean accepted){
		this.accepted = accepted;
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
	
	public int getX() {
		return x;
	}
	
	//-------------------------------------------------------------------------------------

	public int getY() {
		return y;
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
	
	public void addConnection(State target) {
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		connections.add(new Connection(this,target,null));
		arrangeConnections(target);
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addConnection(State target,ConectionType type,int distance,double rotation) {
		for (int c=0;c<getConnections().size();c++){
			if (getConnections().elementAt(c).getTarget().equals(target)) { 
				return;
			}
		}
		connections.add(new Connection(this,target,type,Connection.STILL,distance,rotation));
		arrangeConnections(target);
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
			directedConnection.setDistance(50);
			backConnection.setDistance(50);
		}
	}
}
