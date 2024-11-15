package main;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessageBox extends JDialog{

	private	String	message;
	private	String	buttons;
	
	private	JLabel		info[];
	private	JButton		controls[];
	private	GrapherMain	main;
	
	public MessageBox(GrapherMain main,String message,String title,String buttons) {
		super(main,true);
		this.main		= main;
		this.message	= message;
		this.buttons	= buttons;
		setTitle(title);
		setSize(300,100);
		initElements();
		setResizable(false);
		center();
		setVisible(true);
	}
	
	private void initElements(){
		StringTokenizer s; 
		String token;
		int buttonsCount,labelsCount;
		
		s = new StringTokenizer(message,"|");
		for (labelsCount=0;s.hasMoreTokens();labelsCount++) s.nextToken();
		s = new StringTokenizer(buttons,"|");
		for (buttonsCount=0;s.hasMoreTokens();buttonsCount++) s.nextToken();
		
		controls	= new JButton[buttonsCount];
		JPanel ps	= new JPanel(new GridLayout(labelsCount,1));
		JPanel pb	= new JPanel(new GridLayout(1,buttonsCount));
		
		info		= new JLabel[labelsCount];
		
		s = new StringTokenizer(message,"|");
		
		for (int i=0;i<labelsCount;i++){
			token = s.nextToken();
			info[i] = new JLabel(token,JLabel.CENTER);
			ps.add(info[i]);
		}
		
		s = new StringTokenizer(buttons,"|");
		
		for (int i=0;i<buttonsCount;i++){
			token = s.nextToken();
			controls[i] = new JButton(token);
			pb.add(controls[i]);
			controls[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					main.messageReturn  = ((JButton)e.getSource()).getText();
					dispose();
				}
			});
		}
		
		Font defaultFont	= new Font("Cantarell",Font.PLAIN,11);
		for (JLabel component : info) {
			component.setFont(defaultFont);			
		}
		for (JButton component : controls) {
			component.setFont(defaultFont);			
		}
		
		add(ps,"Center");
		add(pb,"South");
	}

	private void center(){
		int x,y;
		x = main.getX()+(main.getWidth()/2)-getWidth()/2;
		y = main.getY()+(main.getHeight()/2)-getHeight()/2;
		setLocation(x,y);
	}
}
