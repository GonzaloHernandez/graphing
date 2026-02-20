package main;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

public class GrapherDialog extends JDialog{

	private	JPanel	panelControls;
	private	String	buttons;
	
	private	JButton		controls[];
	private	GrapherMain	main;
	
	public GrapherDialog(GrapherMain main,String title,JPanel panel,String buttons) {
		super(main,true);
		this.main			= main;
		this.panelControls	= panel;
		this.buttons		= buttons;
		setTitle(title);
		// setSize(300,100);
		initElements();
		setResizable(false);
		center();
		pack();
		setVisible(true);
	}
	
	private void initElements(){
		StringTokenizer s; 
		String token;
		int buttonsCount;
		
		s = new StringTokenizer(buttons,"|");
		for (buttonsCount=0;s.hasMoreTokens();buttonsCount++) s.nextToken();
		
		controls	= new JButton[buttonsCount];
		JPanel panelButtons	= new JPanel(new GridLayout(1,buttonsCount));
		
		s = new StringTokenizer(buttons,"|");
		
		for (int i=0;i<buttonsCount;i++){
			token = s.nextToken();
			controls[i] = new JButton(token);
			panelButtons.add(controls[i]);
			controls[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					main.dialogReturn = ((JButton)e.getSource()).getText();;
					dispose();
				}
			});
		}
		getRootPane().setDefaultButton(controls[0]);

		Border marginControls = BorderFactory.createEmptyBorder(15,15,15,15);
		panelControls.setBorder(marginControls);
		
		Border marginButtons = BorderFactory.createEmptyBorder(0,15,15,15);
		panelButtons.setBorder(marginButtons);

		Font defaultFont	= new Font("Cantarell",Font.PLAIN,11);
		for (JButton component : controls) {
			component.setFont(defaultFont);			
		}
		
		add(panelControls,"Center");
		add(panelButtons,"South");

		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                          .put(escapeKeyStroke, "dispose");
        this.getRootPane().getActionMap().put("dispose", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
	}

	private void center(){
		int x,y;
		x = main.getX()+(main.getWidth()/2)-getWidth()/2;
		y = main.getY()+(main.getHeight()/2)-getHeight()/2;
		setLocation(x,y);
	}


}
