/*
Description:
Creates the tabbed pane.
I added a "x" button on each tab, and a "+" tab that is used to create more tabs.
Handles most actions of the user, like delete/open file, change view (mode), etc
*/

package ce325;

import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;

public class TabbedPanel extends JPanel{
	protected final static String NEW_TAB_PATH = FileExplorer.NEW_TAB_DIRECTORY;
	private final static String PLUS = "+  ";
	protected final static String ROOT = "Root of roots"; //Create a FileNode with path=ROOT that contains all the system's drives (root of roots).
	private static JTabbedPane tabbedPane;
	private static IconsPanel selectedIcon = null;	 //Contains the IconPanel of the file that the user has clicked on (selected). Used in the menu bar.
	private static ListPanel selectedListIcon = null;//Contains the ListPanel of the file that the user has clicked on (selected). Used in the menu bar.
	protected static TabHistory tabHistory;
	protected static ArrayList<String> directory; //Contains each tab's path
	private static ArrayList<MainPanel> mainPanelList; //Contains each tab's MainPanel
	
	public TabbedPanel(String path){
        super(new GridLayout(1, 1));
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		
		//Create the tabbedPane
        tabbedPane = new JTabbedPane();
		tabbedPane.removeAll();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		
		//Create a new tab
		FileNode file = new FileNode(path);
		directory = new ArrayList(); //directory is a List<String> in which I save the path of the shown directory for each tab.
		directory.add(path);
		mainPanelList = new ArrayList(); //MainPanelComponent handles the way files are represented on the TabbedPanel.
		mainPanelList.add(0, new MainPanel(path)); //Each tab has its own settings for MainPanel.
		tabbedPane.addTab(file.name(),null, mainPanelList.get(0), path);
		tabbedPane.setTabComponentAt(0, tabComponents(path)); //Initialize the tab's title and icon, as well as the "X" button.
		
		//Show the current path to the ToolBar's path textField.
		if(!path.equals(ROOT))
			ToolbarPanel.PathTextField.setText(path);
		else
			ToolbarPanel.PathTextField.setText("");
		
		tabHistory = new TabHistory(path); //TabHistory is used for the Back and Front buttons of the ToolBar.
		BottomPanel.showFileProperties(new FileNode(path)); //The JLabel at the bottom is used to show some info on the current Directory OR the selected file.
		setAddButton(); //Adds a tab labelled "+", used for adding new tabs.
		createTabListener(); //Handle the "+" tab and mouse clicks
        add(tabbedPane); //Add the tabbed pane to this panel.
		
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		UIManager.put("swing.boldMetal", Boolean.FALSE); //Turn off metal's use of bold fonts
		setVisible (true);
    }
	
	public TabbedPanel(){
        super(new GridLayout(1, 1));
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		
		//Create the tabbedPane
        tabbedPane = new JTabbedPane();
		tabbedPane.removeAll();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		
		//Create a new tab
		FileNode file = new FileNode();
		directory = new ArrayList(); //directory is a List<String> in which I save the path of the shown directory for each tab.
		directory.add(TabbedPanel.ROOT);
		mainPanelList = new ArrayList(); //MainPanelComponent handles the way files are represented on the TabbedPanel.
		mainPanelList.add(0, new MainPanel(TabbedPanel.ROOT)); //Each tab has its own settings for MainPanel.
		tabbedPane.addTab(file.name(),null, mainPanelList.get(0), TabbedPanel.ROOT);
		tabbedPane.setTabComponentAt(0, tabComponents(TabbedPanel.ROOT)); //Initialize the tab's title and icon, as well as the "X" button.
		
		ToolbarPanel.PathTextField.setText(""); //Show the current path to the ToolBar's path textField.
		tabHistory = new TabHistory(TabbedPanel.ROOT); //TabHistory is used for the Back and Front buttons of the ToolBar.
		BottomPanel.showFileProperties(file); //The JLabel at the bottom is used to show some info on the current Directory OR the selected file.
		setAddButton(); //Adds a tab labelled "+", used for adding new tabs.
		createTabListener(); //Handle the "+" tab and mouse clicks
        add(tabbedPane); //Add the tabbed pane to this panel.
		
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		UIManager.put("swing.boldMetal", Boolean.FALSE); //Turn off bold fonts
		setVisible (true);
    }
	
	protected String getNameAt(int index){return tabbedPane.getTitleAt(index);}
	protected static JTabbedPane getTabbedPane(){ return tabbedPane;}
	protected static int getSelected(){return tabbedPane.getSelectedIndex();}
	protected static Icon getIconAt(int index){return tabbedPane.getIconAt(index);}
	protected static Component getComponentAt(int index){return tabbedPane.getComponentAt(index);}
	protected static String getSelectedPath(){return directory.get(getSelected());}
	protected static String getPathAt(int index){return directory.get(index);}
	protected static File getFileAt(int index){return new File(getPathAt(index));}
	protected static TabHistory getTabHistory(){return tabHistory;}
	protected static ArrayList<MainPanel> getMainPanelComponent(){return mainPanelList;}
	protected static int getSelectedTabMode(){return mainPanelList.get(getSelected()).getMode();}
	protected static IconsPanel getSelectedIcon(){return selectedIcon;}
	protected static ListPanel getSelectedListIcon(){return selectedListIcon;}
	protected static FileNode getFileNodeAt(int index){
		if(!getPathAt(index).equals(PLUS))
			return new FileNode(getFileAt(index));
		else
			return new FileNode( FileNode.calculateRoot(null));
	}
	
	//Note that the ToolTip for each tab will show the tab's directory path.
	protected static void setNameAt(int index, String name){tabbedPane.setTitleAt(index, name);}
	protected static void setTipAt(int index, String tip){tabbedPane.setToolTipTextAt(index, fixedPath(tip));}
	protected static void setIconAt(int index, Icon icon){tabbedPane.setIconAt(index, icon);}
	protected static void setComponentAt(int index, Component component){tabbedPane.setComponentAt(index, component);}
	protected static void setBackgroundAt(int index, Color color){tabbedPane.setBackgroundAt(index, color);}
	protected static void setForegroundAt(int index, Color color){tabbedPane.setForegroundAt(index, color);}
	protected static void setSelectedIcon(IconsPanel icon){
		selectedListIcon=null;
		if(icon==null){
			ExplorerFrame.updateFileMenuTip("Select a file to enable this menu");
			ExplorerFrame.FileMenu.setEnabled(false);
		}
		else{
			selectedIcon = icon;
			ExplorerFrame.updateFileMenuTip("Choose action for \""+icon.getFileNode().name()+"\"");
			ExplorerFrame.FileMenu.setEnabled(true);
		}
	}
	protected static void setSelectedListIcon(ListPanel listIcon){
		selectedIcon = null;
		if(listIcon==null){
			ExplorerFrame.updateFileMenuTip("Select a file to enable this menu");
			ExplorerFrame.FileMenu.setEnabled(false);
		}
		else{
			selectedListIcon = listIcon;
			ExplorerFrame.updateFileMenuTip("Choose action for \""+listIcon.getFileNode().name()+"\"");
			ExplorerFrame.FileMenu.setEnabled(true);
		}
	}
	
	//Sets a specific tab as selected.
	protected static void setSelected(int index){
		if(index==tabbedPane.getTabCount()-1){
			setComponentAt(index, mainPanelList.get(index-1));
			tabbedPane.setSelectedIndex(index-1);
			TreePanel.setPath(getPathAt(index-1));
		}
		else{
			setComponentAt(index, mainPanelList.get(index));
			tabbedPane.setSelectedIndex(index);
			TreePanel.setPath(getPathAt(index));
		}
	}
	
    //Sets a path on a specific tab.
	protected static void setTab(int index, String path){
		int selected = getSelected();
		
		if(path.equals(ROOT)){ //User has reached a root. View all the drives.
			if(index>=tabbedPane.getTabCount()-1){
				addNewTab(ROOT);
				return;
			}
			setNameAt(index, "Drives:");
			setTipAt(index, ROOT);
			setIconAt(index, javax.swing.filechooser.FileSystemView.getFileSystemView().getSystemIcon( new File(FileNode.calculateRoot(null)) ));
			directory.remove(index);
			directory.add(index, ROOT);
			mainPanelList.get(index).newPath(ROOT);
			//setComponentAt(index, mainPanelList.get(index));
			tabbedPane.setTabComponentAt(index, tabComponents(ROOT));
			ToolbarPanel.PathTextField.setText("");
			BottomPanel.showFileProperties(new FileNode(ROOT));
		}
		else{
			FileNode file = new FileNode( path );
			if(index>=tabbedPane.getTabCount()-1){
				addNewTab(path);
				return;
			}
			setNameAt(index, file.name());
			setTipAt(index, file.path());
			setIconAt(index, file.getSystemIcon());
			directory.remove(index);
			directory.add(index, path);
			mainPanelList.get(index).newPath(path);
			//setComponentAt(index, mainPanelList.get(index));
			tabbedPane.setTabComponentAt(index, tabComponents(path));
			ToolbarPanel.PathTextField.setText(path);
			BottomPanel.showFileProperties(file);
			setSelected(selected);
		}
		setSelected(selected);
	}
	
	//Create a new tab showing the contents of the <path>
	protected static void addNewTab(String path){
		File temp = new File(path);
		if((temp.exists() && temp.isFile())){
			System.err.println("This is not a directory!");
			return;
		}
		
		FileNode file;
		int tabCount = tabbedPane.getTabCount();
		//Note: You cannot have more than 10 tabs (not including the add tab)
		if(tabCount<=10){
			setSelected(0);
			if(tabCount>1)
				tabbedPane.removeTabAt(tabCount-1);
			
			//Create a new tab
			file = new FileNode(path);
			directory.add(tabCount-1, path);
			mainPanelList.add(tabCount-1, new MainPanel(path));
			tabbedPane.addTab(file.name(), file.getSystemIcon(), mainPanelList.get(tabCount-1), path);
			tabbedPane.setTabComponentAt(tabCount-1, tabComponents(path));
			tabHistory.addTab(path);
			TreePanel.setPath(path);
			
			if(tabCount>1)
				setAddButton();
		}
		if(!path.equals(ROOT))
			ToolbarPanel.PathTextField.setText(path);
		else
			ToolbarPanel.PathTextField.setText("");
		BottomPanel.showFileProperties(new FileNode(path));
		setSelected(tabbedPane.getTabCount()-2);
	}
	
	//If index==-1, delete the currently selected tab.
	protected static void removeTabAt(int index){
		int tabCount = tabbedPane.getTabCount(), selected = getSelected(), delete;
				
		//At position tabCount we have the  "+" tab => don't delete it! Also, make sure there is always at least one tab.
		if(index>=tabCount-1 || (index==0 && tabCount==2))
			return;
		
		if(index<0)
			delete = selected;
		else //Else, delete the clicked tab.
			delete = index; // 0<=delete<=tabCount-2

		if(tabCount>2){
			if(delete>0){
				if(delete == selected){
					setSelected(delete-1);
				}
			}
			else{
				if(delete == selected){
					setSelected(1);
				}
			}
			directory.remove(delete);
			mainPanelList.remove(delete);
			tabbedPane.remove(delete);
			tabHistory.removeTab(delete);
		}
	}
	
	//Create the "+" tab that when clicked, creates a new tab.
	private static void setAddButton(){
		int tabCount = tabbedPane.getTabCount();
		
		tabbedPane.addTab(PLUS, makeTextPanel("Too many tabs!"));
		tabCount++;
		mainPanelList.add(tabCount-1, null);
		directory.add(tabCount-1, PLUS);
		tabbedPane.setToolTipTextAt(tabCount-1,"Create new tab at home directory");
		setSelected(tabCount-2);
	}
	
	//Listens to the user's selection of tab. If "+" tab is selected, create new tab.
	private static void createTabListener(){
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int tabCount = tabbedPane.getTabCount();
				if(!getSelectedPath().equals(ROOT))
					ToolbarPanel.PathTextField.setText(getSelectedPath());
				else
					ToolbarPanel.PathTextField.setText("");
				
				//Note: You cannot have more than 10 tabs (not including the add tab)
				if(getSelected()==tabCount-1 && tabCount<=10) //Check if "+" tab was pressed
					addNewTab(NEW_TAB_PATH);
				else{
					if(mainPanelList.get(getSelected()).getMode()==MainPanel.MODE_lIST)
						ToolbarPanel.setComboboxView(1);
					else if(mainPanelList.get(getSelected()).getMode()==MainPanel.MODE_ICONS)
						ToolbarPanel.setComboboxView(0);
					ExplorerFrame.setNeutralFocus();
				}
			}
		});
		addBackgroundClickListener(tabbedPane, true);
	}
	
	//addBackgroundClickListener: Listener for clicks on the tab's empty body.
	//Parameter <clickLosesFocus> defines whether the click calls the neutral focus (so anything that was focused loses focus)
	protected static void addBackgroundClickListener(JComponent component, boolean clickLosesFocus){
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Get clicked tab. Note: when I click on a tab's main body, tabNr==-1.
				int tabNr = ((TabbedPaneUI)tabbedPane.getUI()).tabForCoordinate(tabbedPane, e.getX(), e.getY());
				
				if(clickLosesFocus)
					setSelectedIcon(null);
				
				if(e.getButton() == 1){ //Left Click
					if(tabNr<0 && clickLosesFocus){
						ExplorerFrame.setNeutralFocus();
						BottomPanel.showFileProperties(new FileNode(getSelectedPath()));
					}
				}
				else if(e.getButton() == 2){ //MouseWheel click
					if(clickLosesFocus)
						removeTabAt(tabNr);
				}
				else if(e.getButton() == 3){ //Right Click
					RightClickMenu.create(e, new FileNode(getSelectedPath()), null, RightClickMenu.TAB);
				}
			}
		});
	}

	//Replace all "/" of the path with "\", and return it.
	private static String fixedPath(String path){
		if(path.contains("/")){
			StringBuilder sb = new StringBuilder();
			String[] splitted = path.split("/");
			for(String s: splitted)
				sb.append(s).append("\\");
			return sb.toString();
		}
		return(path);
	}
	
	//Shows just a text. Not used unless the user tries to open more than 10 tabs
	private static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(label);
		panel.setBackground(Color.white);
		addBackgroundClickListener(panel, false);
        return panel;
    }
	
	//Handles the tab's title, icon, "x" button, etc.
	private static JPanel tabComponents(String path){
		
		//First component of the tab is the icon of its directory.//Create a new tab
		FileNode file = new FileNode(path);
		String title = file.toString();
		ImageIcon icon = (ImageIcon) file.getSystemIcon();
		
		//Second, a JLabel with the title.
		JLabel label = new JLabel(title);
		Font font = label.getFont();
		label.setFont( font.deriveFont(font.getSize() + 2.4f) ); //Increase tab title's font size
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 3, 10));//(up, left, bottom, right)
		
		//Third, a button for closing the tab.
		JButton b;
		File image = new File(System.getProperty("user.dir")+ "\\src\\ce325\\icons\\Xbutton_small.png");
		if(image.exists()){
			b = new JButton(new ImageIcon(image.getAbsolutePath()));
		}
		else{
			b = new JButton("  x  ");
		}
		b.setSize(new Dimension(12, 12));
		b.setBackground(Color.white);
		b.setToolTipText("close this tab");
        b.setFocusable(false);
		b.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.GRAY));
		b.setBorderPainted(false);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = tabbedPane.indexOfTabComponent(b.getParent());
				removeTabAt(i);
			}
		});
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				int tabNr = ((TabbedPaneUI)tabbedPane.getUI()).tabForCoordinate(tabbedPane, e.getX(), e.getY());
				if (e.getButton() == 2){ //MouseWheel click
					removeTabAt(tabNr);
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				b.setBorderPainted(true);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				b.setBorderPainted(false);
			}
		});
		
		//Add everything to a panel:
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p.setOpaque(false);
		p.add(new JLabel(icon));
        p.add(label);
		p.add(b);
		p.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		return p;
	}
	
	protected static void openFile(File file){
		if(!Desktop.isDesktopSupported() || !file.exists()){
			System.err.println("Cannot open file.");
			return;
		}
		try{
			java.awt.Desktop desktop = Desktop.getDesktop();
			desktop.open(file);
		}
		catch(IOException e){
			System.err.println("Cannot open file.");
		}
	}
	
	//Used when other classes want to change the current tab's directory to a new one.
	protected static void changeCurrentTabEvent(FileNode directory){
		int selected = tabbedPane.getSelectedIndex();
		String path = directory.path();
		setTab(selected, path);
		tabHistory.addPathToTab(selected, path);
		ExplorerFrame.setNeutralFocus();
	}
	
	//Used when other classes want to do specific actions like go back, create
	//new files, search, refresh, etc. These actions are determined by <flag>.
	protected static void newAction(String input, int flag){
		int selected = tabbedPane.getSelectedIndex();
		String path = getPathAt(selected);
		
		if(flag==ToolbarPanel.BACK_BUTTON){
			path = tabHistory.getPathOf(selected, ToolbarPanel.BACK_BUTTON);
			if(!path.equals(TabHistory.END_OF_LIST))
				setTab(selected, path);
			else
				System.err.println("Can't go back!");
		}
		else if(flag==ToolbarPanel.FORWARD_BUTTON){
			path = tabHistory.getPathOf(selected, ToolbarPanel.FORWARD_BUTTON);
			if(!path.equals(TabHistory.END_OF_LIST))
				setTab(selected, path);
			else
				System.err.println("Can't go forward!");
		}
		else if(flag==ToolbarPanel.UP_BUTTON){
			FileNode up = new FileNode(path);
			if(up.getParentFile()!=null){
				setTab(selected, up.getParent());
				tabHistory.addPathToTab(selected, up.getParent());
			}
			else{
				System.err.println(ROOT); //Reached the root!
				setTab(selected, ROOT);
				tabHistory.addPathToTab(selected, ROOT);
			}
		}
		else if(flag==ExplorerFrame.NEW_FOLDER){
			if(getFileNodeAt(getSelected()).isRootOfRoots()){
				System.err.println("You cannot create a folder here.");
			}
			else{
				FileNode curDir = new FileNode(path);
				curDir.createDir("New Folder");
				newAction(null, ToolbarPanel.REFRESH);
			}
		}
		else if(flag==ExplorerFrame.NEW_FILE){
			if(getFileNodeAt(getSelected()).isRootOfRoots()){
				System.err.println("You cannot create a file here.");
			}
			else{
				FileNode curDir = new FileNode(new File(path));
				curDir.createFile("New File");
				newAction(null, ToolbarPanel.REFRESH);
			}
		}
		else if(flag==ToolbarPanel.CHANGE_VIEW_TO_ICONS){
			mainPanelList.get(selected).setMode(MainPanel.MODE_ICONS);
			mainPanelList.get(selected).newPath(path);
		}
		else if(flag==ToolbarPanel.CHANGE_VIEW_TO_LIST){
			mainPanelList.get(selected).setMode(MainPanel.MODE_lIST);
			mainPanelList.get(selected).newPath(path);
		}
		else if(flag==ToolbarPanel.PATH){
			FileNode file = new FileNode(input); //The file described from the path the user typed
			if(!file.exists()){
				System.err.println("Wrong path!");
				return;
			}
			if( path.equals(file.path()) )
				return; //Already there
			
			if(file.isDirectory()){
				path = file.path();
				setTab(selected, path);
				tabHistory.addPathToTab(selected, path);
			}
			else{
				//View the parent directory of the file.
				path = file.getParent();
				setTab(selected, path);
				tabHistory.addPathToTab(selected, path);
				openFile(file);
			}
			
		}
		else if(flag==ToolbarPanel.REFRESH){
			if(input!=null)
				path = input;
			setTab(selected, path);
			ToolbarPanel.SearchTextField.setText("");
			if(!path.equals(ROOT))
				ToolbarPanel.PathTextField.setText(path);
			else
				ToolbarPanel.PathTextField.setText("");
			ExplorerFrame.setNeutralFocus();
		}
		else if(flag==ToolbarPanel.SEARCH){
			mainPanelList.get(selected).newSimpleSearch(path, input);
		}
		else if(flag==ToolbarPanel.SEARCH_BUTTON){
			mainPanelList.get(selected).newSubSearch(path, input);
		}
	}
	
	//Either am IconsPanel or a ListPanel was double-clicked.
	//Open the file/directory passed as a parameter.
	protected static void iconClickedEvent(FileNode file){
		int selected = tabbedPane.getSelectedIndex();
		
		if(file.isDirectory()){ //is Directory?
			setTab(selected, file.path());
			tabHistory.addPathToTab(selected, file.path());
		}
		else
			openFile(file);
	}
	
	protected static void deleteFile(File file){
		int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete \""+file.getName()+"\"?",
				"Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(input==0){
			System.out.println("Deleting file...");
			file.delete();
			newAction(null, ToolbarPanel.REFRESH);
		}
	}
}
