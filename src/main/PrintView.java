package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class PrintView extends JPanel{

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;
	protected	JComboBox<String>	measure,paperSize,orientation;
	protected	JLabel				measurePaperSize;
	protected	JTextField			left,top,bottom,right;
	protected	String				paperSizeInfo[]	= {"Letter","A4"};
	protected	double 				x,y,width,height,paperWidth,paperHeight,east,south;
	protected	boolean				listenersActived;
	
	//-------------------------------------------------------------------------------------

	public PrintView(GrapherMain main){
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		String aux1[] = {"Centimeters","Inch"};
		String aux2[] = {"Portrait","Landscape"};
		
		measure				= new JComboBox<>(aux1);
		paperSize			= new JComboBox<>(paperSizeInfo);
		measurePaperSize	= new JLabel("",JLabel.CENTER);
		orientation			= new JComboBox<>(aux2);

		left				= new JTextField();
		top					= new JTextField();
		bottom				= new JTextField();
		right				= new JTextField();
		
		setLayout(new BorderLayout());
		
		GridBagLayout		grid	= new GridBagLayout(); 
		GridBagConstraints	gbc		= new GridBagConstraints();
		JPanel				objects	= new JPanel(grid);
		
		gbc.fill		= GridBagConstraints.HORIZONTAL;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		gbc.weightx		= 0.1;
		
		objects.add(new JLabel("Metter"),gbc);
		objects.add(measure,gbc);
		
		objects.add(new JLabel("Paper size"),gbc);
		gbc.gridwidth	= 2;
		objects.add(paperSize,gbc);
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		objects.add(measurePaperSize,gbc);
	
		gbc.gridwidth	= 2;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		
		objects.add(new JLabel("Orientation"),gbc);
		
		objects.add(orientation,gbc);
		
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		
		objects.add(new JLabel("Margins"),gbc);

		gbc.gridwidth	= 2;
		objects.add(new JLabel("Left"),gbc);
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		objects.add(left,gbc);
		
		gbc.gridwidth	= 2;
		objects.add(new JLabel("Right"),gbc);
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		objects.add(right,gbc);
		
		gbc.gridwidth	= 2;
		objects.add(new JLabel("Up"),gbc);
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		objects.add(top,gbc);
		
		gbc.gridwidth	= 2;
		objects.add(new JLabel("Botton"),gbc);
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		objects.add(bottom,gbc);
		
		add(objects,"North");
		Font defaultFont	= new Font("Cantarell",Font.PLAIN,11);
		for (Component component : objects.getComponents()) {
			component.setFont(defaultFont);			
		}
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeners(){
		measure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!listenersActived) return;
				main.currentSession.board.settings.measure = measure.getSelectedIndex();
				double factor=0;
				x			= main.currentSession.board.pageFormat.getPaper().getImageableX();
				y			= main.currentSession.board.pageFormat.getPaper().getImageableY();
				paperWidth	= main.currentSession.board.pageFormat.getPaper().getWidth();
				paperHeight	= main.currentSession.board.pageFormat.getPaper().getHeight();
				
				if (measure.getSelectedIndex()==0) {
					factor = 2.54;
				}
				else{
					factor = 1;
				}
				
				measurePaperSize.setText(""+paperWidth*factor/72+" x "+paperHeight*factor/72);
				
				listenersActived = false;
				
				left	.setText(""+x*factor/72);
				right	.setText(""+east*factor/72);
				top		.setText(""+y*factor/72);
				bottom	.setText(""+south*factor/72);
				
				listenersActived = true;
				
			}
		});

		paperSize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!listenersActived) return;
				Paper p = main.currentSession.board.pageFormat.getPaper();
				switch (paperSize.getSelectedIndex()){
					case 0: p.setSize(8.5*72,11*72);
							p.setImageableArea(p.getImageableX(),p.getImageableY(),p.getWidth()-x-east,p.getHeight()-y-south);
							break;
							
					case 1: p.setSize(8.5*72,13*72);
							p.setImageableArea(p.getImageableX(),p.getImageableY(),p.getWidth()-x-east,p.getHeight()-y-south);
							break;
					
					case 2: p.setSize(8.5*72,14*72);
							p.setImageableArea(p.getImageableX(),p.getImageableY(),p.getWidth()-x-east,p.getHeight()-y-south);
							break;
							
				}
				main.currentSession.board.pageFormat.setPaper(p);
				paperWidth	= main.currentSession.board.pageFormat.getPaper().getWidth();
				paperHeight	= main.currentSession.board.pageFormat.getPaper().getHeight();
				double factor=0;
				if (measure.getSelectedIndex()==0) {
					factor = 2.54;
				}
				else{
					factor = 1;
				}
				
				measurePaperSize.setText(""+paperWidth*factor/72+" x "+paperHeight*factor/72);
			}
		});

		orientation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!listenersActived) return;
				PageFormat f = main.currentSession.board.pageFormat;
				switch (orientation.getSelectedIndex()){
					case 0:	f.setOrientation(PageFormat.PORTRAIT);
							break;
					case 1:	f.setOrientation(PageFormat.LANDSCAPE);
							break;
				}
			}
		});
		
		left.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e) {
				if (!listenersActived) return;
				Paper p = main.currentSession.board.pageFormat.getPaper();
				try{
					switch (measure.getSelectedIndex()){
						case 0:	x = Double.parseDouble(left.getText())/2.54*72;	break;
						case 1:	x = Double.parseDouble(left.getText())*72;		break;
					}
					
					p.setImageableArea(x,p.getImageableY(),p.getWidth()-x-east,p.getHeight()-y-south);
					main.currentSession.board.pageFormat.setPaper(p);
				}
				catch(NumberFormatException ex){
					
				}
				
			}
		});
		
		left.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				left.selectAll();
			}

			public void focusLost(FocusEvent arg0) {}
			
		});

		top.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent arg0) {
				if (!listenersActived) return;
				Paper p  = main.currentSession.board.pageFormat.getPaper();
				try{
					switch (measure.getSelectedIndex()){
						case 0:	y = Double.parseDouble(top.getText())/2.54*72;	break;
						case 1:	y = Double.parseDouble(top.getText())*72;		break;
					}
					
					p.setImageableArea(p.getImageableX(),y,p.getWidth()-x-east,p.getHeight()-y-south);
					main.currentSession.board.pageFormat.setPaper(p);
				}
				catch(NumberFormatException ex){
					
				}
				
			}
		});

		top.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				top.selectAll();
			}

			public void focusLost(FocusEvent arg0) {}
			
		});
		
		right.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent arg0) {
				if (!listenersActived) return;
				Paper p  = main.currentSession.board.pageFormat.getPaper();
				try{
					switch (measure.getSelectedIndex()){
						case 0:	east = Double.parseDouble(right.getText())/2.54*72;	break;
						case 1:	east = Double.parseDouble(right.getText())*72;			break;
					}
					
					p.setImageableArea(x,p.getImageableY(),p.getWidth()-x-east,p.getHeight()-y-south);
					main.currentSession.board.pageFormat.setPaper(p);
				}
				catch(NumberFormatException ex){
					
				}
				
			}
		});

		right.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				right.selectAll();
			}

			public void focusLost(FocusEvent arg0) {}
			
		});
		
		bottom.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent arg0) {
				if (!listenersActived) return;
				Paper p  = main.currentSession.board.pageFormat.getPaper();
				try{
					switch (measure.getSelectedIndex()){
						case 0:	south = Double.parseDouble(bottom.getText())/2.54*72;	break;
						case 1:	south = Double.parseDouble(bottom.getText())*72;		break;
					}
					
					p.setImageableArea(x,p.getImageableY(),p.getWidth()-x-east,p.getHeight()-y-south);
					main.currentSession.board.pageFormat.setPaper(p);
				}
				catch(NumberFormatException ex){
					
				}
				
			}
		});

		bottom.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				bottom.selectAll();
			}

			public void focusLost(FocusEvent arg0) {}
			
		});
		
		listenersActived	= true;
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		listenersActived	= false;
		measure.setSelectedIndex(main.currentSession.board.settings.measure);
		
		if (main.currentSession.board.pageFormat.getPaper().getHeight()==11*72){
			paperSize.setSelectedIndex(0);
		}
		else{
			paperSize.setSelectedIndex(1);
		}
		
		if (main.currentSession.board.pageFormat.getOrientation()==PageFormat.PORTRAIT){
			orientation.setSelectedIndex(0);
		}
		else{
			orientation.setSelectedIndex(1);
		}
	
		double factor;
		
		x			= main.currentSession.board.pageFormat.getPaper().getImageableX();
		y			= main.currentSession.board.pageFormat.getPaper().getImageableY();
		width		= main.currentSession.board.pageFormat.getPaper().getImageableWidth();
		height		= main.currentSession.board.pageFormat.getPaper().getImageableHeight();
		paperWidth	= main.currentSession.board.pageFormat.getPaper().getWidth();
		paperHeight	= main.currentSession.board.pageFormat.getPaper().getHeight();
		east		= paperWidth-x-width;
		south		= paperHeight-y-height;
		
		if (measure.getSelectedIndex()==0) {
			factor = 2.54;
		}
		else{
			factor = 1;
		}
		
		measurePaperSize.setText(""+paperWidth*factor/72+" x "+paperHeight*factor/72);
		
		if (paperWidth==8.5*72 && paperHeight==11*72)		paperSize.setSelectedIndex(0);
		else if (paperWidth==8.5*72 && paperHeight==13*72)	paperSize.setSelectedIndex(1);
		
		left	.setText(""+x*factor/72);
		right	.setText(""+east*factor/72);
		top		.setText(""+y*factor/72);
		bottom	.setText(""+south*factor/72);	
		listenersActived	= true;
	}

}
