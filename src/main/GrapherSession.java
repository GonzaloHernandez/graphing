package main;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class GrapherSession extends JInternalFrame{
	
	//-------------------------------------------------------------------------------------

	protected	Board		board;
	protected	GrapherMain	main;
	protected	boolean		modified;
	private		int			sessionNumber;

	boolean		manualResizing;

	int			deltaWidth	= 12;	// Difference between JInternalFrame and Jcomponent size
	int			deltaHeight	= 36;
	
	//-------------------------------------------------------------------------------------

	public GrapherSession(GrapherMain main) {
		super("",true,true,true,true);
		this.main 		=  main;
		this.modified	= false;
		this.sessionNumber	= 0;
		this.manualResizing = false; 
		setSize(500,400);
		setPreferredSize(new Dimension(500-deltaWidth,400-deltaHeight));
		initElements();
		progListeners();
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}

	//-------------------------------------------------------------------------------------

	private void initElements() {		
		board	= new Board(this);
		getContentPane().add(board,"Center");
	}

	//-------------------------------------------------------------------------------------

	private void progListeners() {
		
		addInternalFrameListener(new InternalFrameListener(){

			public void internalFrameOpened(InternalFrameEvent arg0) {
				return;
			}

			public void internalFrameClosing(InternalFrameEvent arg0) {
				save();
			}

			public void internalFrameClosed(InternalFrameEvent arg0) {
			}

			public void internalFrameIconified(InternalFrameEvent arg0) {
			}

			public void internalFrameDeiconified(InternalFrameEvent arg0) {
			}

			public void internalFrameActivated(InternalFrameEvent arg0) {
				setActived();
				boolean aux = modified;
				main.properties.refresh();
				setModified(aux);
				if (getName() == null) return;
				if (getName().startsWith(main.curdir)) {
					setTitle(getName().substring(main.curdir.length()));
				} else {
					setTitle(getName());
				}				
			}

			public void internalFrameDeactivated(InternalFrameEvent arg0) {
				main.currentSession = null;
				main.properties.refresh();
			}
			
		});

		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (!manualResizing) {
					Dimension nd = new Dimension((int)(board.getWidth()/board.scaleFactor),(int)(board.getHeight()/board.scaleFactor));
					board.setPreferredSize(new Dimension(nd.width, nd.height));
				}
				manualResizing = false;
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}
			
		});
	}

	//-------------------------------------------------------------------------------------
	
	public boolean save(){
		if (board.compiler!=null){
			main.messageBox("Simulation running, Do not close","Warning","Accept");
			return false;
		}
		if (!modified) {
			dispose();
			return true;
		}
		else {
			String messageReturn;
			messageReturn = main.messageBox("Session no saved.|Do you want to save it?","Warning","Yes|No Save|Cancel");
			if (messageReturn.equals("No Save")){
				dispose();
				return true;
			}
			else if (messageReturn.equals("Yes")){
				if (!board.save(false)) return false;
				dispose();
				return true;
			}
		}
		return false;
	}

	//-------------------------------------------------------------------------------------

	private void setActived(){
		main.currentSession = this;
	}
	
	//-------------------------------------------------------------------------------------

	public void setModified(boolean modified){
		this.modified = modified;
		if (board.compiler!=null) board.compiler.updateAutomata();
	}

	//-------------------------------------------------------------------------------------

	public void setSessionNumber(int sessionNumber){
		this.sessionNumber	= sessionNumber;
		setTitle("Session "+sessionNumber);
	}
	
	//-------------------------------------------------------------------------------------

	public int getSessionNumber(){
		return sessionNumber;
	}

	//-------------------------------------------------------------------------------------

	public boolean isModified(){
		return modified;
	}
}
