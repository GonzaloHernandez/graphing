package main;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

public class GrapherMain extends JFrame{

	//-------------------------------------------------------------------------------------
	final int	family			= 1;
	final int	version			= 1;
	final int	construcction	= 4;
	
	//-------------------------------------------------------------------------------------

	protected	JDesktopPane	desktop;
	
	private	JSplitPane		split;
	private	JMenuBar		menuBar;
	private	JMenu			system,help,relatedTopics,samples;
	private	JMenuItem		newSession,open,exit,contents,about,credits;
	
	protected	String			messageReturn;
	protected	MenuOptions		menuOptions;
	protected	GrapherSession	currentSession;
	protected	PropertiesView	properties;
	protected	JMenu			recent;
	
	//-------------------------------------------------------------------------------------

	public GrapherMain() {
		super("Graphing");
	    int w = Toolkit.getDefaultToolkit().getScreenSize().width;
	    int h = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		setSize(800,550);
		setLocation(w/2-400,h/2-275);
		initElements();
		progListeners();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		new About(this);
	}

	//-------------------------------------------------------------------------------------

	private void initElements(){
		Font defaultFont	= new Font("Arial",Font.PLAIN,10);
		messageReturn		= null;
						
		Image icon = Toolkit.getDefaultToolkit().getImage("icons/grapher.png");
	    setIconImage(icon);
	    
		menuOptions	= new MenuOptions(this);
		
		menuBar			= new JMenuBar();
		desktop			= new JDesktopPane();
		system			= new GrapherMenu("System",defaultFont,"system.png");
		newSession		= new GrapherItem("New session",defaultFont,"new.png");
		open			= new GrapherItem("Open session",defaultFont,"open.png");
		recent			= new GrapherMenu("Resent session",defaultFont,"recent.png");
		exit			= new GrapherItem("Exit",defaultFont,"exit.png");
		
		help			= new GrapherMenu("Help",defaultFont,"help.png");
		contents		= new GrapherItem("Content",defaultFont,"contents.png");
		relatedTopics	= new GrapherMenu("Related topics",defaultFont,"related_topics.png");
		samples			= new GrapherMenu("Samples",defaultFont,"samples.png");
		about			= new GrapherItem("About of Graphing'",defaultFont,"about.png");
		credits			= new GrapherItem("Copyright",defaultFont,"credits.png");
		
		properties		= new PropertiesView(this);
		split			= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,desktop,properties); 
		
		currentSession	= null;
		setJMenuBar(menuBar);
		
		menuBar.add(system);
		system.add(newSession);
		system.add(open);
		system.addSeparator();
		system.add(recent);
		system.addSeparator();
		system.add(exit);
		
		menuBar.add(help);
		help.add(contents);
		help.add(relatedTopics);
		help.add(samples);
		help.addSeparator();
		help.add(about);
		help.add(credits);
		
		addRelatedTopics();
		addSamples();
		
		add(split);
		load();
	}
	
	//-------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		new  GrapherMain();
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeners(){
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if (verify()){
					save();
					dispose();
					System.exit(0);
				}
			}
			public void windowActivated(WindowEvent e){
				split.setDividerLocation(getWidth()-222);
			}
		});
		
		addComponentListener(new ComponentListener(){

			public void componentResized(ComponentEvent arg0) {
				split.setDividerLocation(getWidth()-222);
			}

			public void componentMoved(ComponentEvent arg0) {
			}

			public void componentShown(ComponentEvent arg0) {
			}

			public void componentHidden(ComponentEvent arg0) {
			}
			
		});
		
		newSession.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addSession();
			}
		});
		
		open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				openSession(null);
			}
		});
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openAbout();
			}
		});
		
		credits.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openCredits();
			}
		});
		
		contents.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openHelpContents();
			}
		});
	}

	//-------------------------------------------------------------------------------------
	
	private void addSession(){
		GrapherSession session = new GrapherSession(this);
		session.setSessionNumber(findLastSessionNumber()+1);
		desktop.add(session);
		JInternalFrame frames[] = desktop.getAllFrames(); 
		if (frames.length>1){
			int xa = 0;
			int ya = 0;
			for (int i=0;i<frames.length;i++){
				if (frames[i].getClass().getName().equals("GrapherSession")){
					if (frames[i].getX()>xa) xa = frames[i].getX();
					if (frames[i].getY()>ya) ya = frames[i].getY();
				}
			}
			session.setLocation(xa+25,ya+25);
		}
		
		try {
			session.setSelected(true);
		} catch (PropertyVetoException e) {
		}
	}
	
	//-------------------------------------------------------------------------------------

	private int findLastSessionNumber(){
		int sessionNumber=0;
		JInternalFrame iframes[] = desktop.getAllFrames();
		for (int i=0;i<iframes.length;i++){
			if (iframes[i].getClass().getName().equals("GrapherSession")){
				GrapherSession	session = (GrapherSession)iframes[i];
				if (session.getSessionNumber()>sessionNumber){
					sessionNumber = session.getSessionNumber();
				}
			}
		}
		return sessionNumber;
	}
	
	//-------------------------------------------------------------------------------------

	private void openSession(String fileName){
		if (fileName == null) {
			FileDialog dialog = new FileDialog(this,"Select a file",FileDialog.LOAD);
			dialog.setFile("*.aut");
			dialog.setVisible(true);
		
			if (dialog.getFile()==null) return;
			fileName = dialog.getDirectory()+dialog.getFile(); 
		}
		
		JInternalFrame iframes[] = desktop.getAllFrames();
		for (int i=0;i<iframes.length;i++){
			if (iframes[i].getClass().getName().equals("GrapherSession")){
				GrapherSession	session = (GrapherSession)iframes[i];
				if (session.getTitle().equals(fileName)){
					messageBox("This session is opened|It is no possible to open again.","Warning","Accept");
					try {
						session.setSelected(true);
					} catch (PropertyVetoException e) {
					}
					return;
				}
			}
		}
		
		addSession();
		currentSession.board.load(fileName);
	}
	
	//-------------------------------------------------------------------------------------

	public void openCompiler(Board b){
		Compiler compilador = new Compiler(b);
		desktop.add(compilador);
		if (currentSession!=null){
			compilador.setLocation(currentSession.getX()+25,currentSession.getY()+25);
		}
		try {
			compilador.setSelected(true);
		} catch (PropertyVetoException e) {
		}
	}
	
	//-------------------------------------------------------------------------------------

	public String messageBox(String info,String title,String buttons){
		messageReturn = "";
		new MessageBox(this,info,title,buttons);
		return messageReturn;		
	}

	//-------------------------------------------------------------------------------------

	public void save() {
		String fileName	= "graphing.cnf";
		try {
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "rw");
	        
			file.writeInt(recent.getItemCount());
			
			for (int i=0;i<recent.getItemCount();i++){
				JMenuItem item = recent.getItem(i);
				file.writeUTF(item.getText());
			}
	        
	        file.close();
	        
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}

	//-------------------------------------------------------------------------------------

	public void load() {
		String fileName	= "graphing.cnf";
		try {
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
	        
			int count = file.readInt();
			
			for (int i=0;i<count;i++){
				addRecentSession(file.readUTF());
			}
	        
	        file.close();
	        
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}

	//-------------------------------------------------------------------------------------

	public void addRecentSession(String fileName){
		
		for (int i=0;i<recent.getItemCount();i++){
			JMenuItem item = recent.getItem(i);
			if (item.getText().equals(fileName)) return;
		}
		
		if (recent.getItemCount()==5) {
			recent.remove(0);
		}
		
		final JMenuItem item = new GrapherItem(fileName,new Font("Arial",Font.PLAIN,10),"session.png");
		recent.add(item);

		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				openSession(item.getText());
			}
		});
	}
	
	//-------------------------------------------------------------------------------------

	public void openAbout(){
		new About(this);
	}

	//	-------------------------------------------------------------------------------------

	public void openCredits(){
		new Credits(this);
	}

	//-------------------------------------------------------------------------------------
	
	public void openHelpContents(){
		if (Desktop.isDesktopSupported()) {
			try {
				File myFile = new File("docs/help.pdf");
				Desktop.getDesktop().open(myFile);
			} catch (IOException ex) {
				// no application registered for PDFs
			}
		}
	}

	//-------------------------------------------------------------------------------------
	
	public void addRelatedTopics(){
		final String links[][] = {	
			{"http://es.wikipedia.org/wiki/Lenguaje_de_programaci%C3%B3n","Lenguaje de programacion"},
			{"http://es.wikipedia.org/wiki/Compilador","Compilador"},
			{"http://es.wikipedia.org/wiki/Grafo","Grafo"},
			{"http://es.wikipedia.org/wiki/Aut%C3%B3mata_finito","Automata"},
			{"http://es.wikipedia.org/wiki/Aut%C3%B3mata_finito","AFD"}	
		};
		
		for (int i=0;i<links.length;i++){
			JMenuItem item = new GrapherItem(links[i][1],new Font("Arial",Font.PLAIN,10),"link.png");
			item.setActionCommand(links[i][0]);
			relatedTopics.add(item);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(new URI(e.getActionCommand()));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	public void addSamples(){
		final String links[] = {"name","polynomial",};

		for (int i=0;i<links.length;i++){
			JMenuItem item = new GrapherItem(links[i],new Font("Arial",Font.PLAIN,10),"sample.png");
			samples.add(item);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openSession("samples/"+((JMenuItem)e.getSource()).getText()+".aut");
				}
			});
		}
	}

	//-------------------------------------------------------------------------------------
	
	private boolean verify(){
		JInternalFrame frames[] = desktop.getAllFrames();
		for (int i=0;i<frames.length;i++){
			if (frames[i].getClass().toString().equals("class GrapherSession")){
				GrapherSession session = (GrapherSession)frames[i];
				if (session.modified) {
					if (!session.save()) return false;
				}
			}
		}
		return true;
	}

	//-------------------------------------------------------------------------------------
	// Free functions
	//-------------------------------------------------------------------------------------

	public static void drawCenterString(Graphics g,String s,int x,int y){
		FontMetrics metrics			= g.getFontMetrics();
		int			widthString		= metrics.stringWidth(s);//charsWidth(s.toCharArray(),0,s.length());
		int			heightString	= metrics.getAscent();
		
		g.drawString(s,x-widthString/2,y+heightString/2);		
	}
}
