package main;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;

public class Edge {

	//-------------------------------------------------------------------------------------

	static	final int	STILL			= 0;
	static	final int	MARKED			= 1;
	static	final int	FOCUSED			= 2;
	
	//-------------------------------------------------------------------------------------

	private	Vertex				source,target;
	private	int					status;
	private String				value;
	private	Type				type;
	private String				label;
	private boolean				active;

	private	QuadCurve2D.Double	curve;
	private	Point				start,end,middle,control,text,arrowleft,arrowright,arrow;
	private	int					distance;
	private	double				rotation;
	
	//-------------------------------------------------------------------------

	public Edge(Vertex source,Vertex target) {
		this.source		= source;
		this.target		= target;
		this.type		= null;
		this.curve		= new QuadCurve2D.Double();
		this.status		= STILL;
		this.start		= new Point();
		this.end		= new Point();
		this.middle		= new Point();
		this.control	= new Point();
		this.text		= new Point();
		this.arrowleft	= new Point();
		this.arrowright	= new Point();
		this.arrow		= new Point();
		this.rotation	= 0;
		this.value		= "0";
		this.active		= true;
		if (source.equals(target))	this.distance =25; else this.distance = 0; 
	}
	
	//-------------------------------------------------------------------------

	public Edge(Vertex source,Vertex target,int status,int distance,
		double rotation,Type type,String value,String label) 
	{
		this.source		= source;
		this.target		= target;
		this.type		= type;
		this.label		= label;
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
		this.value		= value;
		this.active		= true;
	}
	
	//-------------------------------------------------------------------------

	public int draw(Graphics2D g,GrapherSettings settings,
		int connectionSequence,boolean hidden) 
	{
		if (hidden && !active) return connectionSequence+1;
		
		float[] dashPattern = {6, 4}; // 10 pixels on, 5 pixels off
		Stroke dashed = new BasicStroke(
				1,                      // Line width
				BasicStroke.CAP_BUTT,  // End-cap style
				BasicStroke.JOIN_MITER,// Line join style
				10,                    // Miter limit
				dashPattern,           // Dash pattern
				0                      // Dash phase (offset)
		);
		Stroke originalStroke = g.getStroke();

		double	angle,hypotenuse,alpha,theta,grandHypotenuse,beta,gamma,dx,dy;
		
		if (source.equals(target)){
			switch (status) {
				case STILL:		if (active) g.setColor(Color.GRAY); else g.setColor(Color.lightGray); break;
				case MARKED:	g.setColor(Color.YELLOW);	break;
				case FOCUSED:	g.setColor(Color.RED);		break;
			}
			
			control.x	= (int)(source.getX() + Math.cos(rotation) * distance);
			control.y	= (int)(source.getY() - Math.sin(rotation) * distance);
			
			if (!active) g.setStroke(dashed);
			g.drawArc(control.x-distance,control.y-distance,distance*2,distance*2,0,360);
			g.setStroke(originalStroke);
			
			text.x	= (int)(source.getX() + Math.cos(rotation) * distance*2);
			text.y	= (int)(source.getY() - Math.sin(rotation) * distance*2);
			
			//--- arrow ---
			
			alpha	= Math.asin(((double)Vertex.RADIUS/2)/distance) * 2 + rotation;
			end.x	= (int)(control.x - (Math.cos(alpha)*distance));
			end.y	= (int)(control.y + (Math.sin(alpha)*distance));
			
			hypotenuse	= Math.sqrt(distance*distance*2);
			
			beta	= alpha + Math.PI/4;
			
			arrow.x	= control.x - (int)(Math.cos(beta) * hypotenuse);
			arrow.y	= control.y + (int)(Math.sin(beta) * hypotenuse);	
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
					
			if (source.getX()<control.x)	start.x		= (int)(source.getX() + Math.cos(angle) * Vertex.RADIUS);
			else							start.x		= (int)(source.getX() - Math.cos(angle) * Vertex.RADIUS);
			
			if (source.getY()<control.y)	start.y		= (int)(source.getY() + Math.sin(angle) * Vertex.RADIUS);
			else							start.y		= (int)(source.getY() - Math.sin(angle) * Vertex.RADIUS);
			
			//--- end point ---
			
			if (Math.abs(control.y-target.getY())<Math.abs(control.x-target.getX()))
				angle	= Math.asin(Math.abs(target.getY()-control.y)/hypotenuse);
			else
				angle	= Math.acos(Math.abs(target.getX()-control.x)/hypotenuse);
					
			if (control.x<target.getX())	end.x		= (int)(control.x + Math.cos(angle) * (hypotenuse - Vertex.RADIUS-1));
			else							end.x		= (int)(control.x - Math.cos(angle) * (hypotenuse - Vertex.RADIUS-1));
			
			if (control.y<target.getY())	end.y		= (int)(control.y + Math.sin(angle) * (hypotenuse - Vertex.RADIUS-1));
			else							end.y		= (int)(control.y - Math.sin(angle) * (hypotenuse - Vertex.RADIUS-1));
	
			//--- text point ---

			text.x = (int)(0.25 * start.getX() + 0.5 * control.getX() + 0.25 * end.getX());
		    text.y = (int)(0.25 * start.getY() + 0.5 * control.getY() + 0.25 * end.getY());

			//--- draw arc ---
			
			switch (status) {
				case STILL:		if (active) g.setColor(Color.GRAY); else g.setColor(Color.lightGray); break;
				case MARKED:	g.setColor(Color.YELLOW);	break;
				case FOCUSED:	g.setColor(Color.RED);		break;
			}
			
			Graphics2D g2 = (Graphics2D)g;
			curve.setCurve(start,control,end);

			if (!active)g.setStroke(dashed);
			g2.draw(curve);
			g.setStroke(originalStroke);
			
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
		
		dx	= Math.sin(gamma)*8;
		dy	= Math.cos(gamma)*8;

		arrowleft.x = end.x - (int)(dx);
		if (arrow.y<end.y)	arrowleft.y = end.y - (int)(dy);
		else				arrowleft.y = end.y + (int)(dy);

		//--- right arrow point ---
		
		alpha	= Math.asin((end.x-arrow.x)/hypotenuse);
		beta	= Math.PI/6;
		if (arrow.y<end.y)	gamma	= alpha + beta; 
		else				gamma	= alpha - beta;
		
		dx	= Math.sin(gamma)*8;
		dy	= Math.cos(gamma)*8;

		arrowright.x = end.x - (int)(dx);
		if (arrow.y<end.y)	arrowright.y = end.y - (int)(dy);
		else				arrowright.y = end.y + (int)(dy);
		
		int xs [] = {end.x,arrowleft.x,arrowright.x};
		int ys [] = {end.y,arrowleft.y,arrowright.y};
		g.fillPolygon(xs,ys,3);

		//--- draw label ---
		
		String lab = "";
		String seq = "";
		int l = 0;

		if (settings.showConnectionSequence) {
			seq = ""+connectionSequence;
			l += seq.length() + 1;
		}

		if (settings.showTypeNames) {
			if (type!=null && settings.showTypeNames) {
				if (type.getId()==0) {
					lab += getValue();
				} else {
					lab += type.getName();
				}
			}
			l += lab.length();
		}


		if (settings.showConnectionSequence) {
			Dictionary dict = settings.dictionary;

			g.setColor(Color.BLUE);
			g.setFont(new Font("Arial",Font.ITALIC,9));
			g.drawString(dict.edge1,text.x-(l*5/2),text.y+5);

			if (!lab.isEmpty()) {
				seq += ":";
			}

			g.setFont(new Font("Arial",Font.ITALIC,7));
			g.drawString(seq,text.x-(l*5/2)+5,text.y+7);
		}


		if (settings.showTypeNames) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial",Font.ITALIC,9));
			g.drawString(lab,text.x-(l*5/2)+(seq.length()*5),text.y+5);
		}

		return connectionSequence+1;
	}
	
	//-------------------------------------------------------------------------------------

	public boolean isArea(int px,int py) {
		int x = px;
		int	y = py;
		
		double	angle,hypotenuse;
		
		if (source.equals(target)){
			hypotenuse			= (int)Math.sqrt(Math.pow(Math.abs(control.x-x),2)+Math.pow(Math.abs(control.y-y),2));
			double hypostate	= (int)Math.sqrt(Math.pow(Math.abs(source.getX()-x),2)+Math.pow(Math.abs(source.getY()-y),2));
			
			return (hypotenuse < distance+5 && hypotenuse > distance-5 && hypostate >= Vertex.RADIUS);
		}
		
		int sill=8;
		double	alpha,theta,grandHypotenuse;
		boolean	isInCurve1=false,isInCurve2=false;

		{ // curve 1

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
					
			//--- control point ---
			
			hypotenuse	= (int)Math.sqrt(Math.pow((distance+(sill*(distance>=0?1:-1))),2)+Math.pow(grandHypotenuse/2,2));
			if ((source.getX()<=target.getX() && source.getY()<=target.getY()) || (source.getX()>target.getX() && source.getY()>target.getY()) )
				alpha	= Math.asin((distance+(sill*(distance>=0?1:-1)))/hypotenuse);
			else
				alpha	= Math.asin(((distance+(sill*(distance>=0?1:-1)))*-1)/hypotenuse);
				
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
					
			if (source.getX()<control.x)	start.x		= (int)(source.getX() + Math.cos(angle) * Vertex.RADIUS);
			else							start.x		= (int)(source.getX() - Math.cos(angle) * Vertex.RADIUS);
			
			if (source.getY()<control.y)	start.y		= (int)(source.getY() + Math.sin(angle) * Vertex.RADIUS);
			else							start.y		= (int)(source.getY() - Math.sin(angle) * Vertex.RADIUS);
			
			//--- end point ---
			
			if (Math.abs(control.y-target.getY())<Math.abs(control.x-target.getX()))
				angle	= Math.asin(Math.abs(target.getY()-control.y)/hypotenuse);
			else
				angle	= Math.acos(Math.abs(target.getX()-control.x)/hypotenuse);
					
			if (control.x<target.getX())	end.x		= (int)(control.x + Math.cos(angle) * (hypotenuse - Vertex.RADIUS-1));
			else							end.x		= (int)(control.x - Math.cos(angle) * (hypotenuse - Vertex.RADIUS-1));
			
			if (control.y<target.getY())	end.y		= (int)(control.y + Math.sin(angle) * (hypotenuse - Vertex.RADIUS-1));
			else							end.y		= (int)(control.y - Math.sin(angle) * (hypotenuse - Vertex.RADIUS-1));
	
			curve.setCurve(start,control,end);

			isInCurve1 = curve.contains(x,y);
		}

		{ // curve 2

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
					
			//--- control point ---
			
			hypotenuse	= (int)Math.sqrt(Math.pow((distance-(sill*(distance>=0?1:-1))),2)+Math.pow(grandHypotenuse/2,2));
			if ((source.getX()<=target.getX() && source.getY()<=target.getY()) || (source.getX()>target.getX() && source.getY()>target.getY()) )
				alpha	= Math.asin((distance-(sill*(distance>=0?1:-1)))/hypotenuse);
			else
				alpha	= Math.asin(((distance-(sill*(distance>=0?1:-1)))*-1)/hypotenuse);
				
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
					
			if (source.getX()<control.x)	start.x		= (int)(source.getX() + Math.cos(angle) * Vertex.RADIUS);
			else							start.x		= (int)(source.getX() - Math.cos(angle) * Vertex.RADIUS);
			
			if (source.getY()<control.y)	start.y		= (int)(source.getY() + Math.sin(angle) * Vertex.RADIUS);
			else							start.y		= (int)(source.getY() - Math.sin(angle) * Vertex.RADIUS);
			
			//--- end point ---
			
			if (Math.abs(control.y-target.getY())<Math.abs(control.x-target.getX()))
				angle	= Math.asin(Math.abs(target.getY()-control.y)/hypotenuse);
			else
				angle	= Math.acos(Math.abs(target.getX()-control.x)/hypotenuse);
					
			if (control.x<target.getX())	end.x		= (int)(control.x + Math.cos(angle) * (hypotenuse - Vertex.RADIUS-1));
			else							end.x		= (int)(control.x - Math.cos(angle) * (hypotenuse - Vertex.RADIUS-1));
			
			if (control.y<target.getY())	end.y		= (int)(control.y + Math.sin(angle) * (hypotenuse - Vertex.RADIUS-1));
			else							end.y		= (int)(control.y - Math.sin(angle) * (hypotenuse - Vertex.RADIUS-1));
	
			curve.setCurve(start,control,end);

			isInCurve2 = curve.contains(x,y);
		}

		if (Math.abs(distance) <= sill) {
			if (isInCurve1 || isInCurve2) return true;

			int		xi,yi,xf,yf;
			double	otherangle;
			hypotenuse	= (int)Math.sqrt(Math.pow(Math.abs(target.getX()-source.getX()),2)+Math.pow(Math.abs(target.getY()-source.getY()),2));
			if (Math.abs(source.getY()-target.getY())<Math.abs(source.getX()-target.getX()))
				angle	= Math.asin(Math.abs(target.getY()-source.getY())/hypotenuse);
			else
				angle	= Math.acos(Math.abs(target.getX()-source.getX())/hypotenuse);
			
			if (source.getX()<target.getX()) {	
				xi	= (int)(source.getX() + Math.cos(angle) * Vertex.RADIUS);
				xf	= (int)(source.getX() + Math.cos(angle) * (hypotenuse - Vertex.RADIUS-3));
			}
			else {			
				xi	= (int)(source.getX() - Math.cos(angle) * Vertex.RADIUS);
				xf	= (int)(source.getX() - Math.cos(angle) * (hypotenuse - Vertex.RADIUS-3));
			}
			
			if (source.getY()<target.getY()) {	
				yi	= (int)(source.getY() + Math.sin(angle) * Vertex.RADIUS);
				yf	= (int)(source.getY() + Math.sin(angle) * (hypotenuse - Vertex.RADIUS-3));
			}
			else {			
				yi	= (int)(source.getY() - Math.sin(angle) * Vertex.RADIUS);
				yf	= (int)(source.getY() - Math.sin(angle) * (hypotenuse - Vertex.RADIUS-3));
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
			
			if (Math.abs(angle - otherangle) > 0.1) return false;
			return true;
		}
		else {
			return (isInCurve1 && !isInCurve2);
		}
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

	public void setType(Type type) {
		this.type	= type;
	}
	
	//-------------------------------------------------------------

	public void setDistance(int distance){
		this.distance = distance;
	}
	//-------------------------------------------------------------

	public void setAmountDistance(int dif){
		if (source.equals(target) && distance + dif < Vertex.RADIUS) return;
		distance	+=	dif;
	}

	//-------------------------------------------------------------

	public void setValue(String value) {
		this.value = value;
	}

	public void setActive(boolean act){
		this.active = act;
	}

	public void setActive(boolean act,boolean propagate){
		if (act == this.active) return;
		this.active = act;
	}

	public void setAmountRotation(double dif){
		rotation	+=	dif;
		if (rotation>2*Math.PI){
			rotation -= 2*Math.PI;
		}			
	}

	public void setRotation(int mx, int my){
		int a = my - source.getY();
		int b = mx - source.getX();
		double h = Math.sqrt(Math.pow(a,2)+Math.pow(b, 2));
		if (b<0) 
			rotation = Math.PI + Math.asin(a/h);
		else
			rotation = 0-Math.asin(a/h);
	}
	//-------------------------------------------------------------

	public Vertex getSource(){
		return source;
	}
	
	//-------------------------------------------------------------

	public Vertex getTarget(){
		return target;
	}
	
	//-------------------------------------------------------------

	public Type getType(){
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

	public String getValue() {
		return value;
	}
	
	//-------------------------------------------------------------

	public boolean isActive() {
		return active;
	}
}