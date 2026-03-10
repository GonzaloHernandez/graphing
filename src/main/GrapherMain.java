package main;
import java.awt.Desktop;
import java.awt.Font;
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
import java.net.URI;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

public class GrapherMain extends JFrame{

	//-------------------------------------------------------------------------------------
	final int	family			= 1;
	final int	version			= 3;
	final int	construction	= 6;
	
	//-------------------------------------------------------------------------------------

	protected	JDesktopPane	desktop;
	protected	Persistence		persistence;
	
	private	JSplitPane		split;
	private	JMenuBar		menuBar;
	private	JMenu			file,help,relatedTopics,samples;
	private	JMenuItem		newSession,openSession,saveSession,saveSessionAs,importGame;
	private	JMenuItem		exit,contents,shortcuts,about,credits;
	
	protected	String			dialogReturn;
	protected	MenuOptions		menuOptions;
	protected	GrapherSession	currentSession;
	protected	ViewProperties	properties;
	protected	JMenu			recent;
	
	protected	boolean			showAbout;
	protected	String			curdir;

	//-------------------------------------------------------------------------------------

	public GrapherMain() {
		super("Graphing");
		persistence = new Persistence(this);		
		setSize(1200,800);
		initElements();
		progListeners();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		if (showAbout) new About(this);
	}

	//-------------------------------------------------------------------------------------

	private void initElements(){
		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());
		dialogReturn		= null;
						
		Image icon = Toolkit.getDefaultToolkit().getImage("icons/grapher.png");
	    setIconImage(icon);
	    
		menuOptions	= new MenuOptions(this);
		
		menuBar			= new JMenuBar();
		desktop			= new JDesktopPane();
		file			= new GrapherMenu("File",defaultFont,"system.png");
		newSession		= new GrapherItem("New Session",defaultFont,"new.png");
		openSession		= new GrapherItem("Open Session",defaultFont,"open.png");
		recent			= new GrapherMenu("Recent Sessions",defaultFont,"recent.png");
		saveSession		= new GrapherItem("Save Session",defaultFont,"save.png");
		saveSessionAs	= new GrapherItem("Save Session as ...",defaultFont,"saveas.png");
		importGame		= new GrapherItem("Import Game (GM)",defaultFont,"open.png");
		exit			= new GrapherItem("Exit",defaultFont,"exit.png");
		
		help			= new GrapherMenu("Help",defaultFont,"help.png");
		contents		= new GrapherItem("Content",defaultFont,"contents.png");
		shortcuts		= new GrapherItem("Short cuts",defaultFont,"shortcut.png");
		relatedTopics	= new GrapherMenu("Related Topics",defaultFont,"related_topics.png");
		samples			= new GrapherMenu("Samples",defaultFont,"samples.png");
		about			= new GrapherItem("About of Graphing",defaultFont,"about.png");
		credits			= new GrapherItem("Copyright",defaultFont,"credits.png");
		
		properties		= new ViewProperties(this);
		split			= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,desktop,properties); 
		
		currentSession	= null;
		setJMenuBar(menuBar);
		
		menuBar.add(file);
		file.add(newSession);
		file.add(openSession);
		file.add(recent);
		file.add(saveSession);
		file.add(saveSessionAs);
		file.addSeparator();
		file.add(importGame);
		file.addSeparator();
		file.add(exit);
		
		menuBar.add(help);
		help.add(contents);
		help.add(shortcuts);
		help.add(relatedTopics);
		// help.add(samples);
		help.addSeparator();
		help.add(about);
		help.add(credits);
		
		addRelatedTopics();
		addSamples();
		
		add(split);
		
		showAbout = false;

		UIManager.put("InternalFrame.titleFont",defaultFont);
		curdir = System.getProperty("user.dir");
		persistence.loadGrapher();
	}
	
	//-------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		new  GrapherMain();
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeners(){
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				while (currentSession!=null) {
					if (!currentSession.safeClosing()) return;
				}
				persistence.saveGrapher();
				dispose();
				System.exit(0);
			}
			public void windowActivated(WindowEvent e){
				split.setDividerLocation(getWidth()-320);
			}
		});
		
		addComponentListener(new ComponentListener(){

			public void componentResized(ComponentEvent arg0) {
				split.setDividerLocation(getWidth()-320);
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
		
		openSession.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				persistence.loadSession("",true);
			}
		});
		
		saveSession.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				persistence.saveSession(false);
			}
		});

		saveSessionAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				persistence.saveSession(true);
			}
		});

		importGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				persistence.importGame();
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

		JFrame frame = this;
		shortcuts.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				String message = "<html>" +
					"<body style='width: 300px; padding: 5px;'>" +
					"<b>General:</b>" +
					"<ul>" +
					"  <li><b>Ctrl + S:</b> Save session</li>" +
					"  <li><b>Ctrl +Shift + S:</b> Save session as</li>" +
					"  <li><b>Ctrl +Shift + E:</b> Export as Image (PNG)</li>" +
					"  <li><b>G:</b> Toggle grid</li>" +
					"  <li><b>H (Hold):</b> Hide elements</li>" +
					"  <li><b>Ctrl + (+/-):</b> Zoom in/out</li>" +
					"</ul>" +
					"<b>When a Vertex is selected:</b>" +
					"<ul>" +
					"  <li><b>Delete:</b> Remove vertex</li>" +
					"  <li><b>0-9:</b> Set value directly</li>" +
					"  <li><b>Drag:</b> Move vertex</li>" +
					"</ul>" +
					"<b>When an Edge is selected:</b>" +
					"<ul>" +
					"  <li><b>Delete:</b> Remove edge</li>" +
					"  <li><b>0-9:</b> Set value directly</li>" +
					"  <li><b>Drag:</b> Move self-loop edge</li>" +
					"  <li><b>A / Z:</b> Adjust distance</li>" +
					"  <li><b>Ctrl + A / Z:</b> Rotate edge</li>" +
					"  <li><b>Scroll:</b> Adjust distance</li>" +
					"  <li><b>Ctrl + Scroll:</b> Rotate edge</li>" +
					"</ul>" +
					"<b>Other Mouse Controls:</b>" +
					"<ul>" +
					"  <li><b>Double-Click:</b> Add vertex / Edit properties</li>" +
					"  <li><b>Ctrl + Drag:</b> Create new edge</li>" +
					"  <li><b>Shift + Click:</b> Toggle active</li>" +
					"  <li><b>Shift + Right-Click:</b> Toggle active propagating</li>" +
					"  <li><b>Right-Click:</b> Open context menu</li>" +
					"  <li><b>Alt + Right-Click:</b> Open context type menu</li>" +
					"</ul>" +
					"</body></html>";


		        JOptionPane.showMessageDialog(frame, message, "Shortcuts", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	//-------------------------------------------------------------------------------------
	
	protected void addSession(){
		GrapherSession session = new GrapherSession(this);
		session.setSessionNumber(findLastSessionNumber()+1);
		desktop.add(session);
		JInternalFrame frames[] = desktop.getAllFrames(); 
		if (frames.length>1){
			int xa = 0;
			int ya = 0;
			for (int i=0;i<frames.length;i++){
				if (frames[i].getClass().getName().equals("main.GrapherSession")){
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
			if (iframes[i].getClass().getName().equals("main.GrapherSession")){
				GrapherSession	session = (GrapherSession)iframes[i];
				if (session.getSessionNumber()>sessionNumber){
					sessionNumber = session.getSessionNumber();
				}
			}
		}
		return sessionNumber;
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
		dialogReturn = "";
		new MessageBox(this,info,title,buttons);
		return dialogReturn;
	}

	public String grapherDialog(String info,JPanel panel,String buttons){
		dialogReturn = "";
		new GrapherDialog(this,info,panel,buttons);
		return dialogReturn;
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

		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());

		final JMenuItem item = new GrapherItem(fileName, defaultFont,"session.png");
		recent.add(item);

		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				persistence.loadSession(item.getText(),true);
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
			Font currentFont	= UIManager.getFont("Label.font");
			Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());
			JMenuItem item = new GrapherItem(links[i][1],defaultFont,"link.png");
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
		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());

		for (int i=0;i<links.length;i++){
			JMenuItem item = new GrapherItem(links[i],defaultFont,"sample.png");
			samples.add(item);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					persistence.loadSession("samples/"+((JMenuItem)e.getSource()).getText()+".aut",true);
				}
			});
		}
	}
}
