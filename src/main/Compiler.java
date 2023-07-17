package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

class Linea	extends Object {
	//---------------------------------------------------------------------------
	private	String		mensaje;
	private	int			pointer;
	private	int			tipo;
	//---------------------------------------------------------------------------
	public Linea(String mensaje) {
		this.mensaje	= mensaje;
		pointer		= -1;
		tipo			= 1;
	}
	//---------------------------------------------------------------------------
	public Linea(String mensaje,int tipo) {
		this.mensaje	= mensaje;
		pointer		= -1;
		this.tipo		= tipo;
	}
	//---------------------------------------------------------------------------
	public Linea(int pointer) {
		this.pointer	= pointer;
		mensaje			= null;
		tipo			= 3;
	}
	//---------------------------------------------------------------------------
	public int getTipo() {
		return tipo;
	}
	//---------------------------------------------------------------------------
	public String getMensaje() {
		return mensaje;
	}
	//---------------------------------------------------------------------------
	public int getPointer() {
		return pointer;
	}
}

class Tablero extends JComponent {
	//---------------------------------------------------------------------------
	private	int 		inicio,cantidad,interlineado;
	private	Vector		<Linea>historial;
	private	JScrollBar	vertical;
	//---------------------------------------------------------------------------
	public Tablero(JScrollBar vertical) {
		this.vertical	= vertical;
		historial		= new Vector<Linea>();
		inicio			= 0;
		cantidad		= 0;
		interlineado	= 20;
		adiEscuchadores();
	}
	//---------------------------------------------------------------------------
	public void paint(Graphics g) {
		Font	fmen,flin;
		Color	cmen,cpoi,clin;
		int		smen;
		
		g.setColor(Color.WHITE);
		g.fillRect(0,0,getWidth(),getHeight());
		cantidad	= getHeight() / interlineado;
		fmen		= new Font("Courier",Font.PLAIN,14);
		flin		= new Font("Courier",Font.BOLD,14);
		
		clin		= new Color(0,128,0);
		cmen		= Color.GRAY;
		cpoi		= Color.RED;
				
		smen		= 8;
		
		for (int i=inicio,y=15 ; i<inicio+cantidad&&i<historial.size() ; i++,y+=interlineado) {
			switch(((Linea)historial.elementAt(i)).getTipo()) {
				case 1:	String men = ((Linea)historial.elementAt(i)).getMensaje();
						if (men.length()==0) break;
						g.setColor(clin);
						g.setFont(flin);
						g.drawString(men,20,y);
						break;
				case 2:	men = ((Linea)historial.elementAt(i)).getMensaje();
						if (men.length()==0) break;
						g.setColor(cmen);
						g.setFont(fmen);
						g.drawString(men,20,y);
						g.drawRect(18,y-12,men.length()*smen+5,16);
						break;
				case 3:	int pos = 24+((Linea)historial.elementAt(i)).getPointer()*smen;
						g.setColor(cpoi);
						g.drawLine(pos,y-12,pos,y-4);
						g.drawLine(10,y-4,pos,y-4);
						g.drawLine(10,y-4,10,y+15);
						g.drawLine(10,y+15,18,y+15);
						g.drawLine(pos,y-15,pos+2,y-12);
						g.drawLine(pos+2,y-12,pos-2,y-12);
						g.drawLine(pos-2,y-12,pos,y-15);
						break;
			}
			
		}
	}
	//---------------------------------------------------------------------------
	public void actualizarPosicion(int inicio) {
		this.inicio	= inicio;
	}
	//---------------------------------------------------------------------------
	public void adicionarLinea(Linea linea) {
		historial.add(linea);
		if (historial.size()-cantidad>0)	inicio	= historial.size() - cantidad;
		else								inicio	= 0;
	}
	//---------------------------------------------------------------------------
	public void adiEscuchadores() {
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (inicio + e.getWheelRotation() >=0 && inicio + e.getWheelRotation()<=totalLineas()-cantidad) {
					inicio += e.getWheelRotation();
					repaint();
					vertical.setValue(inicio+1);
				}
			}
		});
	}
	//---------------------------------------------------------------------------
	public int totalLineas() {	return historial.size(); }
	//---------------------------------------------------------------------------
	public int getCantidad() { return cantidad; }
	//---------------------------------------------------------------------------
	public void limpiar() {
		historial.clear();
		repaint();
	}
}

class Compiler extends JInternalFrame {
	//---------------------------------------------------------------------------
	private	Tablero		tablero;
	private	JTextField	campo;
	private	JPanel		vista;
	private	JScrollBar	vertical;
	private	Automata	automata;
	//---------------------------------------------------------------------------
	private	JPopupMenu	flotante;
	private	JMenu		vistas;
	private	JMenuItem	limpiar,textual,grafica;
	private	Board		board;

	//---------------------------------------------------------------------------
	public Compiler(){
		super("Compilador",true,true,true,true);
		setSize(400,200);
		adiControls();
		adiListeners();
		setVisible(true);
	}
	//---------------------------------------------------------------------------
	public Compiler(Board board){
		super("Compiler",true,true,true,true);
		this.board			= board;
		this.board.compiler	= this;
		setTitle(board.session.getTitle());
		setSize(400,200);
		adiControls();
		updateAutomata();
		adiListeners();
		setVisible(true);
	}
	//---------------------------------------------------------------------------
	public void updateAutomata(){
		automata.setMatrix(board.getMatrix(),board.getVocabulary(),board.getAcceptedStates());
	}
	//---------------------------------------------------------------------------
	private void adiControls() {
		vertical	= new JScrollBar(JScrollBar.VERTICAL,1,1,1,2);
		tablero		= new Tablero(vertical);
		vista		= new JPanel(new BorderLayout());
		campo		= new JTextField("");
		automata	= new Automata();
		
		flotante	= new JPopupMenu();
		vistas		= new JMenu("Views");
		limpiar		= new JMenuItem("Clear history");
		textual		= new JMenuItem("Text view");
		grafica		= new JMenuItem("Graph view");
		
		vistas		.setFont(new Font("Verdana",Font.PLAIN,11));
		limpiar		.setFont(new Font("Verdana",Font.PLAIN,11));
		textual		.setFont(new Font("Verdana",Font.PLAIN,11));
		grafica		.setFont(new Font("Verdana",Font.PLAIN,11));
		
		flotante	.add(limpiar);
		flotante	.addSeparator();
		flotante	.add(vistas);
		vistas		.add(textual);
		vistas		.add(grafica);
		
		vista	.add(tablero,"Center");
		vista	.add(vertical,"East");
		campo	.setFont(new Font("Courier",Font.PLAIN,13));
		
		tablero	.add(flotante);
	
		getContentPane().add(vista,"Center");
		getContentPane().add(campo,"South");
	}
	//---------------------------------------------------------------------------
	private void adiListeners() {
		campo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!campo.getText().equals("")){
					evaluarExpresion();
				}
			}
		});
		vertical.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				tablero.actualizarPosicion(vertical.getValue()-1);
				tablero.repaint();
			}
		});
		tablero.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==3) {
					flotante.show(tablero,e.getX(),e.getY());
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
		limpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tablero.limpiar();
			}
		});
		addInternalFrameListener(new InternalFrameListener(){

			public void internalFrameOpened(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void internalFrameClosing(InternalFrameEvent arg0) {
				board.compiler	= null;
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
				// TODO Auto-generated method stub
				
			}

			public void internalFrameDeactivated(InternalFrameEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	//---------------------------------------------------------------------------
	private void evaluarExpresion(){
		Info inf = automata.validate(campo.getText());
		
		if (!inf.getMessage().equals("")) {
			tablero.adicionarLinea(new Linea(""));
			tablero.adicionarLinea(new Linea(campo.getText(),2));
			tablero.adicionarLinea(new Linea(inf.getPosition()));
			tablero.adicionarLinea(new Linea(inf.getMessage()+" ["+inf.getCharacter()+"]",2));			
			tablero.adicionarLinea(new Linea(""));
		}
		else if (!inf.execute()) {
			tablero.adicionarLinea(new Linea(campo.getText()));
		}

		int	cantidad = tablero.totalLineas()-tablero.getCantidad();
		if (cantidad<=0)	
			vertical.setMaximum(1);
		else {
			vertical.setMaximum(cantidad+1);
			vertical.setValue(tablero.totalLineas()-tablero.getCantidad()+1);
		}
		
		tablero.repaint();
	}
}