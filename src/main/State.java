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
	private	boolean	accepted;

	private int		value;
	private int		owner;

	//-------------------------------------------------------------------------------------
	
	public State(int number,int x,int y) {
		this.number		= number;
		this.x			= x;
		this.y			= y;
		this.status		= FOCUSED;
		this.accepted	= false;
		this.value		= 0;
		this.owner		= 0;
		connections		= new Vector<Connection>();
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
		connections		= new Vector<Connection>();
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
		connections		= new Vector<Connection>();
	}

	//-------------------------------------------------------------------------------------
	
	public void draw(Graphics2D g,GrapherSettings settings) {
		
		for (int i=0 ; i<connections.size() ; i++) {
			Connection connection = (Connection)connections.elementAt(i);
			connection.draw(g,settings);
		}
		switch (status) {
			case FOCUSED:	g.setColor(Color.RED);		break;
			case MARKED:	g.setColor(Color.YELLOW);	break;
			case STILL:		g.setColor(new Color(220,220,255));	break;
		}
		
		if (owner % 2 == 0) {
			g.fillOval(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
			g.setColor(Color.BLACK);
			g.drawOval(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
		}
		else {
			g.fillRect(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
			g.setColor(Color.BLACK);
			g.drawRect(x-RADIUS,y-RADIUS,RADIUS*2,RADIUS*2);
		}

		if (accepted) g.drawOval(x-RADIUS+3,y-RADIUS+3,(RADIUS-3)*2,(RADIUS-3)*2);
		if (settings.showStateSequence) {
			g.setColor(Color.GRAY);
			if (settings.showStatePriorities) {
				g.setFont(new Font("Arial",Font.ITALIC,9));
				g.drawString(""+number,x-(3*(new String(""+number)).length()),y+RADIUS-1);
			}
			else {
				g.setFont(new Font("Arial",Font.ITALIC,13));
				g.drawString(""+number,x-(3*(new String(""+number)).length()),y+4);
			}
		}
		if (settings.showStatePriorities) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial",Font.PLAIN,13));
			g.drawString(""+value,x-(3*(new String(""+value)).length()),y+4);
		}

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

	public void setValue(int value){
		this.value = value;
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
