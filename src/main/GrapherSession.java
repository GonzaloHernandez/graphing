package main;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class GrapherSession extends JInternalFrame{
	
	//-------------------------------------------------------------------------------------

	protected	Board		board;
	protected	GrapherMain	main;
	protected	boolean		modified;
	private		int			sessionNumber;
	
	//-------------------------------------------------------------------------------------

	public GrapherSession(GrapherMain main) {
		super("",true,true,true,true);
		this.main 		=  main;
		this.modified	= false;
		this.sessionNumber	= 0; 
		setSize(500,400);
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
				// TODO Auto-generated method stub
				
			}

			public void internalFrameClosing(InternalFrameEvent arg0) {
				save();
			}

			public void internalFrameClosed(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void internalFrameIconified(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void internalFrameDeiconified(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void internalFrameActivated(InternalFrameEvent arg0) {
				setActived();
				boolean aux = modified;
				main.properties.refresh();
				setModified(aux);
			}

			public void internalFrameDeactivated(InternalFrameEvent arg0) {
				main.currentSession = null;
				main.properties.refresh();
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
