package main;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;

public class Board extends JComponent implements Printable{
	
	//-------------------------------------------------------------------------------------
	
	protected	int				mousex,mousey;
	protected	boolean			controled;
	protected	String			fileName;
	protected	GrapherSettings	settings;
	protected	Vector			<State>states;
	protected	GrapherSession	session;
	protected	Vector			<ConectionType>types;
	protected	Connection		currentConnection;
	protected	State			stateSource,stateTarget;
	protected	boolean			menuBlock;
	protected	Compiler		compiler;
	protected	PageFormat		pageFormat;
		
	//-------------------------------------------------------------------------------------
	
	public Board(GrapherSession session) {
		this.session	= session;
		this.menuBlock	= false;
		this.compiler	= null;
		getInputMap().put(KeyStroke.getKeyStroke("A"), "actionName");
		initElements();
		progListeners();
	}

	//-------------------------------------------------------------------------------------

	private void initElements() {
		this.fileName	= "";
		this.compiler	= null;
		
		types	= new Vector<ConectionType>();
		types.add(new ConectionType(1,"number","0123456789"));
		types.add(new ConectionType(2,"point","."));
		types.add(new ConectionType(3,"uppercase","ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		types.add(new ConectionType(4,"lowercase","abcdefghijklmnopqrstuvwxyz"));
		
		settings	= new GrapherSettings(true,true,"",types);
		states		= new Vector<State>();
		stateSource	= null;
		stateTarget	= null;
		controled	= false;		
		pageFormat	= new PageFormat();
		Paper		paper	= new Paper();
		paper.setSize(8.5*72,11*72); //letter size
		paper.setImageableArea(72,72,paper.getWidth()-72*2,paper.getHeight()-72*2);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);
	}

	//-------------------------------------------------------------------------------------

	public void paint(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0,0,getWidth(),getHeight());
		if (controled) {
			g.setColor(Color.BLACK);
			if (stateTarget!=null) {
				g.drawLine(stateSource.getX(),stateSource.getY(),stateTarget.getX(),stateTarget.getY());
			}
			else {
				g.drawLine(stateSource.getX(),stateSource.getY(),mousex,mousey);
			}
		}
		for (int i=0 ; i<states.size() ; i++) {
			states.elementAt(i).draw(g,settings);
		}

		export();
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addState(int x,int y){
		states.add(new State(states.size(),x,y));
		repaint();
		session.setModified(true);
	}

	//-------------------------------------------------------------------------------------

	public void progListeners() {
		addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent arg0) {
				
			}

			public void keyReleased(KeyEvent arg0) {
				
			}

			public void keyTyped(KeyEvent e) {
				
				if (currentConnection != null){
					if (!e.isControlDown()){
						if (e.getKeyChar()=='a') {
							currentConnection.setAmountDistance(2);
						}
						else if (e.getKeyChar()=='z'){
							currentConnection.setAmountDistance(-2);
						}
					}
					else{
						if ((int)e.getKeyChar()==1) {
							currentConnection.setAmountRotation(-(Math.PI/16));
						}
						else if ((int)e.getKeyChar()==26){
							currentConnection.setAmountRotation((Math.PI/16));
						}
						
					}
					session.setModified(true);
				}
				repaint();
			}
			
		});
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (currentConnection!=null && e.getModifiersEx()== InputEvent.SHIFT_DOWN_MASK) { //Shift + Click 
					session.main.menuOptions.showTypes(false);
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (!e.isControlDown()) {
						if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount() == 2) {
							addState(e.getX(),e.getY());
						}
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3) {
					if (stateTarget!=null && currentConnection!=null) {
						session.main.menuOptions.show(true,true,true);
					}else if (stateTarget!=null) {
						session.main.menuOptions.show(true,false,true);
					}
					else if (currentConnection!=null) {
						session.main.menuOptions.show(false,true,true);				
					} 
					else {
						session.main.menuOptions.show(false,false,true);
					}
				}
			}
			public void mousePressed(MouseEvent e) {
				for (int i=0 ; i<states.size() ; i++) {
					State state = (State)states.elementAt(i);
				
					if (state.isArea(e.getX(),e.getY())) {
						
						if (!e.isControlDown()) {
							stateTarget = state;
							stateTarget.setMouseDiference(e.getX(),e.getY());
						}
						else {
							controled	= true;
							stateSource	= state;
							stateSource.setStatus(State.MARKED);
						}
						break;						
					}
					repaint();
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (controled) {
					if (stateTarget==null) {
						controled = false;
						return;
					}
					if (settings.allowStateZero || stateTarget.getNumber() > 0) {
						for (int i=0 ; i<states.size() ; i++) {
							State state = (State)states.elementAt(i);
							if (state.isArea(e.getX(),e.getY())) {
								stateSource.addConnection(stateTarget);
								session.setModified(true);
								repaint();
							}
						}
					}
					controled = false;
				}
				stateSource = null;
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() { 
			public void mouseDragged(MouseEvent e) {
				mousex = e.getX();
				mousey = e.getY();
				if (!e.isControlDown()) {
					if (!controled && stateTarget!=null) {
						stateTarget.setLocation(e.getX(),e.getY());
						session.setModified(true);
					}
				}
				else {
					stateTarget = null;
					for (int i=0 ; i<states.size() ; i++) {
						State state = (State)states.elementAt(i);
						if (state.isArea(e.getX(),e.getY())) {
							stateTarget = state;
							if (state != stateSource) {
								state.setStatus(State.FOCUSED);
							}
						}
						else if (state != stateSource) {
							state.setStatus(State.STILL);
						}
					}
				}
				repaint();
			}
			
			public void mouseMoved(MouseEvent e) {
				if (menuBlock) return;
				if (!e.isControlDown()) stateSource = null;
				
				stateTarget = null;
				currentConnection = null;
				for (int i=0 ; i<states.size() ; i++) {
					State state = (State)states.elementAt(i);
					if (state.isArea(e.getX(),e.getY())) {
						stateTarget	= state;
						if (state != stateSource) {
							state.setStatus(State.FOCUSED);
						}
					}
					else if (state != stateSource) {
						state.setStatus(State.STILL);
					}
					
					Vector<Connection> connections = state.getConnections();
					for (int j=0 ; j<connections.size() ; j++) {
						Connection connection = (Connection)connections.elementAt(j); 
						if (connection.isArea(e.getX(),e.getY())) {
							currentConnection = connection;
							connection.setStatus(Connection.FOCUSED);
						}
						else {
							connection.setStatus(Connection.STILL);
						}
					}
				}
				repaint();
			}
		});

		addMouseWheelListener(new MouseWheelListener(){

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (stateTarget!=null && e.isShiftDown()) {
					stateTarget.setValue(stateTarget.getValue()-e.getWheelRotation());
				} 
				else if (currentConnection != null){
					if (!e.isControlDown()){
						currentConnection.setAmountDistance(-e.getWheelRotation()*2);
					}
					else{
						currentConnection.setAmountRotation((Math.PI/16)*-e.getWheelRotation());
					}
					session.setModified(true);
				}
				repaint();
			}
			
		});
	}

	//-------------------------------------------------------------------------------------
	
	public void restart(){
		states.removeAllElements();
		repaint();
	}

	//-------------------------------------------------------------------------------------

	public void load(){
		if (session.isModified()) {
			String messageReturn = session.main.messageBox("This session was no saved.|Do you want to close anyway?","Warning","Yes|No");
			if (messageReturn.equals("No")||messageReturn.equals("")) return;
		}
		FileDialog dialog = new FileDialog(session.main,"Select a file",FileDialog.LOAD);
		dialog.setFile("*.aut");
		dialog.setVisible(true);
		
		if (dialog.getFile()==null) return;
		
		load(dialog.getDirectory()+dialog.getFile());
	}

	//-------------------------------------------------------------------------------------

	public boolean load(String fileName){
		try {
	        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
	        this.fileName = fileName;
	        session.setTitle(fileName);
	        
	        short	n,number,x,y, value,owner, numberSource,numberTarget,numberType,distance;
	        double	rotation;
	        boolean	accepted;
	        State	source=null,target=null;
	        ConectionType	type=null;
	        String	name,symbols;
	        
	        states.removeAllElements();
	        types.removeAllElements();
	        
	        if (	file.readShort()	!= 7 ||
	        		file.readShort()	!= 4 ||
	        		!file.readUTF().equals("GRAPHER")	) {
	        	file.close();
	        	session.dispose();
	        	session.main.messageBox("Invalid file","Warning","Accept");
	        	return false;
	        }
	        
	        file.readShort();
	        file.readShort();
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	number	= file.readShort();
	        	name 	= file.readUTF();
	        	symbols	= file.readUTF();
	        	types.add(new ConectionType(number,name,symbols));
	        }
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	number		= file.readShort();
	        	x 			= file.readShort();
	        	y			= file.readShort();
	        	accepted	= file.readBoolean();

	        	value		= file.readShort();
				owner		= file.readShort();

				states.add(new State(number,x,y,State.STILL,accepted,value,owner));
	        }
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	numberSource	= file.readShort();
	        	numberTarget	= file.readShort();
	        	numberType		= file.readShort();
	        	distance		= file.readShort();
	        	rotation		= file.readDouble();
	        	
	        	for (int s=0;s<states.size();s++){
	        		if (states.elementAt(s).getNumber()==numberSource){
	        			source = states.elementAt(s);
	        			break;
	        		}
	        	}
	        	for (int t=0;t<states.size();t++){
	        		if (states.elementAt(t).getNumber()==numberTarget){
	        			target = states.elementAt(t);
	        			break;
	        		}
	        	}
	        	
	        	if (numberType>=0) {
		        	for (int p=0;p<types.size();p++){
		        		if (types.elementAt(p).getNumber()==numberType) {
		        			type = types.elementAt(p);
		        			break;
		        		}
		        	}
		        	source.addConnection(target,type,distance,rotation);	        		
	        	}
	        	else {
	        		source.addConnection(target,null,distance,rotation);
	        	}
	        }
	        
	        settings.showTypeNames		= file.readBoolean();
	        settings.showStateNumbers	= file.readBoolean();
	        settings.comment			= file.readUTF();
	        
	        session.setSize(file.readShort(),file.readShort());
	        
	        file.close(); 
	        repaint();
	        session.main.properties.refresh();
	        session.setModified(false);
	        
	        session.main.addRecentSession(fileName);
	        return true;	        
	    } catch (IOException e) {
	    	session.main.messageBox("The file ["+fileName+"] does not exists","File error","Accept");
	    	return false;
	    }
	}
	
	//-------------------------------------------------------------------------------------
	
	public boolean save(boolean saveAs) {
		try {
			if (saveAs || fileName.equals("")) {
				FileDialog dialog = new FileDialog(session.main,"Select a file name",FileDialog.SAVE);
				dialog.setFile("*.aut");
				dialog.setVisible(true);
				if (dialog.getFile()==null) return false;
				fileName = dialog.getDirectory()+dialog.getFile();
			}
			
			JInternalFrame iframes[] =  session.main.desktop.getAllFrames();
			for (int i=0;i<iframes.length;i++){
				if (iframes[i].getClass().getName().equals("GrapherSession")){
					GrapherSession	session = (GrapherSession)iframes[i];
					if (session.equals(this.session)) continue;
					if (session.getTitle().equals(fileName)){
						session.main.messageBox("Name invalid.|There exists a session opened with the same indentifier","Warning","Accept");
						fileName = "";
						return false;
					}
				}
			}
			
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "rw");
	        
	        session.setTitle(fileName);
	        
	        file.writeShort(7);
	        file.writeShort(4);
	        file.writeUTF("GRAPHER");
	        file.writeShort(session.main.family);
	        file.writeShort(session.main.version);
	        
	        short	connectionsCount = 0;
	        
	        file.writeShort(types.size());
	        for (int i=0;i<types.size();i++){
	        	file.writeShort(types.elementAt(i).getNumber());
	        	file.writeUTF(types.elementAt(i).getName());
	        	file.writeUTF(types.elementAt(i).getSymbols());
	        }
	        
	        file.writeShort(states.size());
	        for (int i=0;i<states.size();i++){
	        	file.writeShort(states.elementAt(i).getNumber());
	        	file.writeShort(states.elementAt(i).getX());
	        	file.writeShort(states.elementAt(i).getY());
	        	file.writeBoolean(states.elementAt(i).isAccepted());

	        	file.writeShort(states.elementAt(i).getValue());
	        	file.writeShort(states.elementAt(i).getOwner());

				connectionsCount += states.elementAt(i).getConnections().size();
	        }
	        
	        file.writeShort(connectionsCount);
	        for (int s=0;s<states.size();s++){
	        	for (int i=0;i<states.elementAt(s).getConnections().size();i++){
		        	file.writeShort(states.elementAt(s).getConnections().elementAt(i).getSource().getNumber());
		        	file.writeShort(states.elementAt(s).getConnections().elementAt(i).getTarget().getNumber());
		        	if (states.elementAt(s).getConnections().elementAt(i).getType()!=null) {
		        		file.writeShort(states.elementAt(s).getConnections().elementAt(i).getType().getNumber());
		        	}
		        	else {
		        		file.writeShort(-1);
		        	}
		        	file.writeShort(states.elementAt(s).getConnections().elementAt(i).getDistance());
		        	file.writeDouble(states.elementAt(s).getConnections().elementAt(i).getRotation());
		        }
	        }
	        
	        file.writeBoolean(settings.showTypeNames);
	        file.writeBoolean(settings.showStateNumbers);
	        file.writeUTF(settings.comment);	        
	        
	        file.writeShort(session.getWidth());
	        file.writeShort(session.getHeight());
	        
	        file.setLength(file.getFilePointer());
	        file.close();
	        session.setModified(false);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return true;
	}

	//-------------------------------------------------------------------------------------

	public boolean export() {
		int v = session.main.currentSession.board.settings.programmingView;

		//----------------------------------------------------------

		String matrix = "[";
		int size = states.size();
		for (int s=0;s<states.size();s++){
			matrix += v==1?"[":"|";
			for (int t=0;t<states.size();t++){
				boolean found = false;
				for (int i=0;i<states.elementAt(s).getConnections().size();i++){
					if (states.elementAt(s).getConnections().elementAt(i).getTarget().getNumber()==t) {
						found = true;
					}
		        }
				matrix += found?"1":"0";
				if (t<states.size()-1) matrix += ","; 
				else 
					matrix += v==1?"]":"";
	        }
			if (s<states.size()-1) matrix += "\n"; 
			else 
				matrix += v==1?"]\n":"|]\n";
		}

		//----------------------------------------------------------

		String from = "[";
		String to	= "[";
		int nConections = 0;

		for (State s : states) {
			for (Connection c : s.getConnections()) {
				from	+= c.getSource().getNumber() + ",";
				to		+= c.getTarget().getNumber() + ",";
				nConections ++;
			}
		}

		from	= from	.substring(0, from.length()-1) 	+ "]\n";
		to		= to	.substring(0, to.length()-1) 	+ "]\n";

		//----------------------------------------------------------

		String values = "[";
		String owners = "[";

		for (int i=0;i<states.size();i++){
			values += states.elementAt(i).getValue() + ",";
			owners += states.elementAt(i).getOwner() + ",";
		}

		values = values.substring(0, values.length()-1) + "]\n";
		owners = owners.substring(0, owners.length()-1) + "]\n";

		//----------------------------------------------------------

		session.main.properties.exportView.info.setText(
			"Adjacency matrix ("+size+"x"+size+")\n"+
			matrix + "\n" + 
			"List of edges ("+nConections+")\n" +
			from +
			to +
			"\nValue of states ("+size+")\n"  +
			values + "\n" + 
			"Owners ("+size+")\n" +
			owners);
		return true;
	}

	//-------------------------------------------------------------------------------------
	public String getFileName(){
		return fileName;
	}
	
	//-------------------------------------------------------------------------------------

	public void deleteState(State state){
		for (int s=0;s<states.size();s++) {
			for (int c=0;c<states.elementAt(s).getConnections().size();c++){
				Connection connection = states.elementAt(s).getConnections().elementAt(c);
				if (connection.getTarget().equals(state)){
					states.elementAt(s).getConnections().remove(connection);
				}
			}
		}
		for (int i=state.getNumber()+1;i<states.size();i++){
			states.elementAt(i).setNumber(states.elementAt(i).getNumber()-1);
		}
		
		states.removeElement(state);
	}

	//-------------------------------------------------------------------------------------
	
	public State getState(int number){
		if (number>=states.size()) return null;
		return states.elementAt(number);
	}

	//-------------------------------------------------------------------------------------

	public void keyPressed(KeyEvent e){
		
	}
	
	//-------------------------------------------------------------------------------------

	public int[][] getMatrix(){
		
		int gra[][] = new int[states.size()][types.size()];
		
		for (int s=0;s<states.size();s++){
			for (int t=0;t<types.size();t++){
				gra[s][t] = 0;
				ConectionType type = types.elementAt(t);
				for (int c=0;c<states.elementAt(s).getConnections().size();c++){
					Connection connection = states.elementAt(s).getConnections().elementAt(c);
					if (connection.getType().equals(type)){
						gra[s][t] = connection.getTarget().getNumber();
						break;
					}
				}
			}
		}
		
		return gra;
	}
	
	//-------------------------------------------------------------------------------------

	public String[] getVocabulary(){
		String voc[] = new String[types.size()];
		for (int i=0;i<types.size();i++){
			voc[i] = types.elementAt(i).getSymbols();
		}
		return voc;
	}
	
	//-------------------------------------------------------------------------------------

	public int[] getAcceptedStates(){
		int count = 0,i=0;
		for (int s=0;s<states.size();s++) if (states.elementAt(s).isAccepted()) count++;
		int ace[] = new int[count];
		for (int s=0;s<states.size();s++) {
			if (states.elementAt(s).isAccepted()) {
				ace[i]= states.elementAt(s).getNumber();
				i++;
			}
		}
		return ace;
	}

	//-------------------------------------------------------------------------------------

	public State isDFD(){
		
		for (int t=0;t<states.size();t++){
			State target = states.elementAt(t);
			String type=null;
			for (int s=0;s<states.size();s++){
				State source = states.elementAt(s);
				for (int c=0;c<source.getConnections().size();c++){
					if (source.getConnections().elementAt(c).getType()==null) return source;
					if (source.getConnections().elementAt(c).getTarget().equals(target)){
						if (source.getConnections().elementAt(c).getType()==null) continue;
						if (type==null){
							type = source.getConnections().elementAt(c).getType().getName();
						}
						else{
							if (!source.getConnections().elementAt(c).getType().getName().equals(type)){
								return target;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	//-------------------------------------------------------------------------------------

	public void print(){
		PrinterJob printJob = PrinterJob.getPrinterJob();
		
		printJob.setPrintable(this,pageFormat);
        if (printJob.printDialog()) {
            try {
            	printJob.setJobName("Grapher "+session.getTitle());
            	printJob.print();  
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
	}

	public int print(Graphics g1, PageFormat pf, int pi) throws PrinterException {

		Graphics2D g = (Graphics2D) g1;

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if (pi >= 1) return Printable.NO_SUCH_PAGE;
		g.setColor(Color.WHITE);
		g.fillRect(0,0,getWidth(),getHeight());
		if (controled) {
			g.setColor(Color.BLACK);
			if (stateTarget!=null) {
				g.drawLine(stateSource.getX(),stateSource.getY(),stateTarget.getX(),stateTarget.getY());
			}
			else {
				g.drawLine(stateSource.getX(),stateSource.getY(),mousex,mousey);
			}
		}
		for (int i=0 ; i<states.size() ; i++) {
			states.elementAt(i).draw(g,settings);
		}
		return 0;
	}
}
