package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ViewProperties extends JPanel{
	
	protected	JTabbedPane		tab;
	protected	GrapherMain		main;
	protected	ViewGeneral		generalView;
	protected	ViewTypes		typesView;
	// protected	ViewPrint		printView;
	protected	ViewElements	elementsView;
	protected	ViewDictionary	dictionaryView;
	protected	int				selectedTab;
	
	//--------------------------------------------------------------------------

	public ViewProperties(GrapherMain main) {
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//--------------------------------------------------------------------------

	private void initElements(){
		setLayout(new BorderLayout());
		tab				= new JTabbedPane();
		generalView		= new ViewGeneral(main);
		typesView		= new ViewTypes(main);
		// printView		= new ViewPrint(main);
		elementsView	= new ViewElements(main);
		dictionaryView	= new ViewDictionary(main);
		add(tab,"Center");
		
		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());
		for (Component component : getComponents()) {
			component.setFont(defaultFont);			
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void refresh(){
		if (main.currentSession!=null){
			int aux = selectedTab;
			tab.addTab("General",generalView);
			tab.addTab("Types",typesView);
			// tab.addTab("Printing",printView);
			tab.addTab("Elements",elementsView);
			tab.addTab("Dictionary",dictionaryView);
			selectedTab = aux;
			generalView.refresh();
			typesView.refresh();
			// printView.refresh();
			elementsView.refresh();
			dictionaryView.refresh();
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
