package main;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;

public class About extends JDialog implements Runnable{

	//-------------------------------------------------------------------------------------

	private	GrapherMain	main;
	private	Image		img;
	private	Thread		thread;

	//-------------------------------------------------------------------------------------

	public About(GrapherMain main){
		super(main,"About Graphing",true);
		this.main	= main;
		initElements();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		int w = 420;
		int h = 200;

		setSize(w,h);
		setLocation(main.getX()+main.getWidth()/2-w/2,main.getY()+main.getHeight()/2-h/2);
		setResizable(false);
		setUndecorated(true);

		progListeners();
		start();
		
		setVisible(true);
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeners(){
		addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				if (e.getX()>315 && e.getY()>8 && e.getX()<315+12 && e.getY()<8+13) {
					main.showAbout = !main.showAbout;
					repaint(316,9,10,13);
				} 
				else {
					dispose();
				}
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}
			
		});
	}
	
	public void paint(Graphics g1){
		Graphics2D g = (Graphics2D) g1;

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.drawString("Wait...",100,100);
		g.drawImage(img,0,0,main);
		g.setColor(Color.BLACK);
		g.drawRect(0,0,getWidth()-1,getHeight()-1);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial",Font.BOLD,13));
		g.drawString("Version "+main.family+"."+main.version+" construction "+main.construcction,10,20);
		g.setColor(Color.WHITE);
		g.fillRect(315, 8, 12, 13);
		g.setColor(new Color(50,100,255));
		g.drawRect(315, 8, 12, 13);
		if (main.showAbout) {
			g.drawString("x",317,19);
		}
		g.drawString("Show again",330,20);
	}

	//-------------------------------------------------------------------------------------

	public void run() {
		img = Toolkit.getDefaultToolkit().getImage("icons/press.png");
		for (int i=0;i<3;i++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}		
			
			// repaint();
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			
		}	
		dispose();
	}
	
	//-------------------------------------------------------------------------------------

	public void start(){
		thread=new Thread(this);
		thread.start();
	}
}
