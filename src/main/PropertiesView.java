package main;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PropertiesView extends JPanel{
	
	protected	JTabbedPane		tab;
	protected	GrapherMain		main;
	protected	GeneralView		generalView;
	protected	TypesView		typesView;
	protected	PrintView		printView;
	protected	int				selectedTab;
	
	//--------------------------------------------------------------------------

	public PropertiesView(GrapherMain main) {
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//--------------------------------------------------------------------------

	private void initElements(){
		setLayout(new BorderLayout());
		tab			= new JTabbedPane();
		generalView	= new GeneralView(main);
		typesView	= new TypesView(main);
		printView	= new PrintView(main);
		add(tab,"Center");
	}
	
	//--------------------------------------------------------------------------
	
	public void refresh(){
		if (main.currentSession!=null){
			int aux = selectedTab;
			tab.addTab("General",generalView);
			tab.addTab("Types",typesView);
			tab.addTab("Printing",printView);
			selectedTab = aux;
			generalView.refresh();
			typesView.refresh();
			printView.refresh();
			tab.setSelectedIndex(selectedTab);
		}
		else {
			int aux = selectedTab;
			tab.removeAll();
			selectedTab = aux;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void progListeners(){
		tab.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				selectedTab = tab.getSelectedIndex();
			}
			
		});
	}
	
}
