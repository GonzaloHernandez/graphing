package main;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.QuadCurve2D;

public class Connection {

	//-------------------------------------------------------------------------------------

	static	final int	STILL			= 0;
	static	final int	MARKED			= 1;
	static	final int	FOCUSED			= 2;
	
	//-------------------------------------------------------------------------------------

	private	State				source,target;
	private	int					status;
	private	ConectionType				type;
	private	QuadCurve2D.Double	curve;
	private	Point				start,end,middle,control,text,arrowleft,arrowright,arrow;
	private	int					distance;
	private	double				rotation;
	
	//-------------------------------------------------------------------------------------

	public Connection(State source,State target,ConectionType type) {
		this.source		= source;
		this.target		= target;
		this.type		= type;
		this.curve		= new QuadCurve2D.Double();
		this.status		= FOCUSED;
		this.start		= new Point();
		this.end		= new Point();
		this.middle		= new Point();
		this.control	= new Point();
		this.text		= new Point();
		this.arrowleft	= new Point();
		this.arrowright	= new Point();
		this.arrow		= new Point();
		this.rotation	= 0;
		if (source.equals(target))	this.distance =30; else this.distance = 0; 
	}
	
	//-------------------------------------------------------------------------------------

	public Connection(State source,State target,ConectionType type,int status,int distance,double rotation) {
		this.source		= source;
		this.target		= target;
		this.type		= type;
		this.curve		= new QuadCurve2D.Double();
		this.status		= status;
		this.start		= new Point();
		this.end		= new Point();
		this.middle		= new Point();
		this.control	= new Point();
		this.text		= new Point();
		this.arrowleft	= new Point();
		this.arrowright	= new Point();
		this.arrow		= new Point();
		this.distance	= distance;
		this.rotation	= rotation;	 
	}
	
	//-------------------------------------------------------------------------------------

	public void draw(Graphics g,GrapherSettings settings) {
		
		double	angle,hypotenuse,alpha,theta,grandHypotenuse,beta,gamma,dx,dy;
		
		if (source.equals(target)){
			switch (status) {
				case MARKED:	g.setColor(Color.YELLOW);	break;
				case STILL:		g.setColor(Color.BLACK);	break;
				case FOCUSED:	g.setColor(Color.RED);		break;
			}
			
			control.x	= (int)(source.getX() + Math.cos(rotation) * distance);
			control.y	= (int)(source.getY() - Math.sin(rotation) * distance);
			
			g.drawArc(control.x-distance,control.y-distance,distance*2,distance*2,0,360);
			
			text.x	= (int)(source.getX() + Math.cos(rotation) * distance*2);
			text.y	= (int)(source.getY() - Math.sin(rotation) * distance*2);
			
			//--- arrow ---
			
			alpha	= Math.asin(((double)State.RADIUS/2)/distance) * 2 - rotation;
			end.x	= (int)(control.x - (Math.cos(alpha)*distance));
			end.y	= (int)(control.y - (Math.sin(alpha)*distance));
			
			hypotenuse	= Math.sqrt(distance*distance*2);
			
			beta	= alpha + Math.PI/4;
			
			arrow.x	= control.x - (int)(Math.cos(beta) * hypotenuse);
			arrow.y	= control.y - (int)(Math.sin(beta) * hypotenuse);	
		}
		else {
		
			//--- middle point ---
			
			hypotenuse	= (int)Math.sqrt(Math.pow(Math.abs(target.getX()-source.getX()),2)+Math.pow(Math.abs(target.getY()-source.getY()),2));
			if (Math.abs(source.getY()-target.getY())<Math.abs(source.getX()-target.getX()))
				angle	= Math.asin(Math.abs(target.getY()-source.getY())/hypotenuse);
			else
				angle	= Math.acos(Math.abs(target.getX()-source.getX())/hypotenuse);
			
			if (source.getX()<target.getX())	middle.x	= (int)(source.getX() + Math.cos(angle) * (hypotenuse /2));
			else								middle.x	= (int)(source.getX() - Math.cos(angle) * (hypotenuse /2));
			if (source.getY()<target.getY())	middle.y	= (int)(source.getY() + Math.sin(angle) * (hypotenuse /2));
			else								middle.y	= (int)(source.getY() - Math.sin(angle) * (hypotenuse /2));
	
			grandHypotenuse	= hypotenuse;
			
			//--- text point ---
			
			hypotenuse	= (int)Math.sqrt(Math.pow((distance/2),2)+Math.pow(grandHypotenuse/2,2));
			if ((source.getX()<=target.getX() && source.getY()<=target.getY()) || (source.getX()>target.getX() && source.getY()>target.getY()) )
				alpha	= Math.asin((distance/2)/hypotenuse);
			else
				alpha	= Math.asin(((distance*-1)/2)/hypotenuse);
			theta	= angle - alpha;
					
			if (source.getX() <= middle.x)	text.x		= (int)(source.getX() + Math.cos(theta) * (hypotenuse));
			else							text.x		= (int)(source.getX() - Math.cos(theta) * (hypotenuse));
	
			if (source.getY() <= middle.y)	text.y		= (int)(source.getY() + Math.sin(theta) * (hypotenuse));
			else							text.y		= (int)(source.getY() - Math.sin(theta) * (hypotenuse));
			
			//--- control point ---
			
			hypotenuse	= (int)Math.sqrt(Math.pow(distance,2)+Math.pow(grandHypotenuse/2,2));
			if ((source.getX()<=target.getX() && source.getY()<=target.getY()) || (source.getX()>target.getX() && source.getY()>target.getY()) )
				alpha	= Math.asin(distance/hypotenuse);
			else
				alpha	= Math.asin((distance*-1)/hypotenuse);
				
			theta	= angle - alpha;
			
			if (source.getX() <= middle.x)	control.x	= (int)(source.getX() + Math.cos(theta) * (hypotenuse));
			else							control.x	= (int)(source.getX() - Math.cos(theta) * (hypotenuse));
	
			if (source.getY() <= middle.y)	control.y	= (int)(source.getY() + Math.sin(theta) * (hypotenuse));
			else							control.y	= (int)(source.getY() - Math.sin(theta) * (hypotenuse));
	
			//--- start point ---
			
			if (Math.abs(source.getY()-control.y)<Math.abs(source.getX()-control.y))
				angle	= Math.asin(Math.abs(control.y-source.getY())/hypotenuse);
			else
				angle	= Math.acos(Math.abs(control.x-source.getX())/hypotenuse);
					
			if (source.getX()<control.x)	start.x		= (int)(source.getX() + Math.cos(angle) * State.RADIUS);
			else							start.x		= (int)(source.getX() - Math.cos(angle) * State.RADIUS);
			
			if (source.getY()<control.y)	start.y		= (int)(source.getY() + Math.sin(angle) * State.RADIUS);
			else							start.y		= (int)(source.getY() - Math.sin(angle) * State.RADIUS);
			
			//--- end point ---
			
			if (Math.abs(control.y-target.getY())<Math.abs(control.x-target.getX()))
				angle	= Math.asin(Math.abs(target.getY()-control.y)/hypotenuse);
			else
				angle	= Math.acos(Math.abs(target.getX()-control.x)/hypotenuse);
					
			if (control.x<target.getX())	end.x		= (int)(control.x + Math.cos(angle) * (hypotenuse - State.RADIUS-1));
			else							end.x		= (int)(control.x - Math.cos(angle) * (hypotenuse - State.RADIUS-1));
			
			if (control.y<target.getY())	end.y		= (int)(control.y + Math.sin(angle) * (hypotenuse - State.RADIUS-1));
			else							end.y		= (int)(control.y - Math.sin(angle) * (hypotenuse - State.RADIUS-1));
	
			//--- draw arc ---
			
			switch (status) {
				case MARKED:	g.setColor(Color.YELLOW);	break;
				case STILL:		if (type!=null) g.setColor(Color.BLACK); else g.setColor(Color.GRAY); break;
				case FOCUSED:	g.setColor(Color.RED);		break;
			}
			
			Graphics2D g2 = (Graphics2D)g;
			curve.setCurve(start,control,end);
			g2.draw(curve);
			
			arrow.x	= control.x;
			arrow.y	= control.y;
		}

		//--- draw arrow ---
		
		hypotenuse	= (int)Math.sqrt(Math.pow(end.x-arrow.x,2)+Math.pow(end.y-arrow.y,2));
		
		//--- left arrow point ---
		
		alpha	= Math.asin((end.x-arrow.x)/hypotenuse);
		beta	= Math.PI/6;
		if (arrow.y<end.y)	gamma	= alpha - beta; 
		else				gamma	= alpha + beta;
		
		dx	= Math.sin(gamma)*10;
		dy	= Math.cos(gamma)*10;

		arrowleft.x = end.x - (int)(dx);
		if (arrow.y<end.y)	arrowleft.y = end.y - (int)(dy);
		else				arrowleft.y = end.y + (int)(dy);

		//--- right arrow point ---
		
		alpha	= Math.asin((end.x-arrow.x)/hypotenuse);
		beta	= Math.PI/6;
		if (arrow.y<end.y)	gamma	= alpha + beta; 
		else				gamma	= alpha - beta;
		
		dx	= Math.sin(gamma)*10;
		dy	= Math.cos(gamma)*10;

		arrowright.x = end.x - (int)(dx);
		if (arrow.y<end.y)	arrowright.y = end.y - (int)(dy);
		else				arrowright.y = end.y + (int)(dy);
		
		g.drawLine(arrowleft.x,arrowleft.y,end.x,end.y);
		g.drawLine(arrowright.x,arrowright.y,end.x,end.y);

		//--- draw label ---
		
		if (type!=null && settings.nameTypes) {
			g.setColor(Color.BLUE);
			String label = type.getName();
			drawCenterString(g,label,text);
		}
		
	}
	
	//-------------------------------------------------------------------------------------

	public boolean isArea(int px,int py) {
		int x = px;
		int	y = py;
		
		int		xi,yi,xf,yf;
		double	angle,otherangle,hypotenuse;
		
		if (source.equals(target)){
			hypotenuse	= (int)Math.sqrt(Math.pow(Math.abs(control.x-x),2)+Math.pow(Math.abs(control.y-y),2));
			
			if (hypotenuse <= distance) return true;
			else						return false;
		}
		
		if (curve.contains(x,y)) return true;
		
		hypotenuse	= (int)Math.sqrt(Math.pow(Math.abs(target.getX()-source.getX()),2)+Math.pow(Math.abs(target.getY()-source.getY()),2));
		if (Math.abs(source.getY()-target.getY())<Math.abs(source.getX()-target.getX()))
			angle	= Math.asin(Math.abs(target.getY()-source.getY())/hypotenuse);
		else
			angle	= Math.acos(Math.abs(target.getX()-source.getX())/hypotenuse);
		
		if (source.getX()<target.getX()) {	
			xi	= (int)(source.getX() + Math.cos(angle) * State.RADIUS);
			xf	= (int)(source.getX() + Math.cos(angle) * (hypotenuse - State.RADIUS-3));
		}
		else {			
			xi	= (int)(source.getX() - Math.cos(angle) * State.RADIUS);
			xf	= (int)(source.getX() - Math.cos(angle) * (hypotenuse - State.RADIUS-3));
		}
		
		if (source.getY()<target.getY()) {	
			yi	= (int)(source.getY() + Math.sin(angle) * State.RADIUS);
			yf	= (int)(source.getY() + Math.sin(angle) * (hypotenuse - State.RADIUS-3));
		}
		else {			
			yi	= (int)(source.getY() - Math.sin(angle) * State.RADIUS);
			yf	= (int)(source.getY() - Math.sin(angle) * (hypotenuse - State.RADIUS-3));
		}
		
		if (xi<xf && (x<xi || x>xf))	return false;
		if (xi>xf && (x<xf || x>xi))	return false;
		if (yi<yf && (y<yi || y>yf))	return false;
		if (yi>yf && (y<yf || y>yi))	return false;
		
		hypotenuse	= (int)Math.sqrt(Math.pow(Math.abs(x-source.getX()),2)+Math.pow(Math.abs(y-source.getY()),2));
		if (Math.abs(source.getY()-y)<Math.abs(source.getX()-x))
			otherangle	= Math.asin(Math.abs(y-source.getY())/hypotenuse);
		else
			otherangle	= Math.acos(Math.abs(x-source.getX())/hypotenuse);
		
		if (Math.abs(angle - otherangle) > 0.02) return false;
		return true;
	}
	
	//-------------------------------------------------------------------------------------

	public boolean isControlArea(int x,int y) {
		if (x>=control.x-3 && x<=control.x+3 && y>=control.y-3 && y<=control.y+3) return true;
		return false;
	}
	
	//-------------------------------------------------------------------------------------

	public void setStatus(int status) {
		this.status	= status;
	}

	//-------------------------------------------------------------------------------------

	public void setType(ConectionType type) {
		this.type	= type;
	}
	
	//-------------------------------------------------------------

	public void setDistance(int distance){
		this.distance = distance;
	}
	//-------------------------------------------------------------

	public void setAmountDistance(int dif){
		if (source.equals(target) && distance + dif < State.RADIUS) return;
		distance	+=	dif;
	}

	//-------------------------------------------------------------
	
	public void setAmountRotation(double dif){
		rotation	+=	dif;
		if (rotation>2*Math.PI){
			rotation -= 2*Math.PI;
		}			
	}

	//-------------------------------------------------------------

	public State getSource(){
		return source;
	}
	
	//-------------------------------------------------------------

	public State getTarget(){
		return target;
	}
	
	//-------------------------------------------------------------

	public ConectionType getType(){
		return type;
	}	
	
	//-------------------------------------------------------------
	
	public int getDistance(){
		return distance;
	}

	//-------------------------------------------------------------

	public double getRotation(){
		return rotation;
	}
	
	//-------------------------------------------------------------

	static public void drawCenterString(Graphics g,String s,Point p){
		FontMetrics metrics			= g.getFontMetrics();
		int			widthString		= metrics.stringWidth(s);//charsWidth(s.toCharArray(),0,s.length());
		int			heightString	= metrics.getAscent();
		
		g.drawString(s,p.x-widthString/2,p.y+heightString/2);		
	}
	
}