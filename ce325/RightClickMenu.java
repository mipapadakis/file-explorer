/*
Description:
Creates all the right-click menus:
	-for buttons/icons representing a file (flag=BUTTON)
	-for textAreas representing a file's name (flag=TEXTAREA)
	-for whole tabs (flag=TAB)
	-for the tree (flag=TREE)
	-for ListPanels representing a file that was matched after a sub-Search (flag=SEARCH)
*/

package ce325;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.plaf.TabbedPaneUI;

/**
 *
 * @author minas
 */
public class RightClickMenu extends JComponent {
	protected static final int BUTTON = 1;
	protected static final int TAB = 2;
	protected static final int TEXTAREA = 3;
	protected static final int TREE = 4;
	protected static final int SEARCH = 5;
	private static MouseEvent event;
	private static FileNode file;
	private static JComponent extra;
	
	public RightClickMenu(){
		super();
	}
	
	public static void create(MouseEvent e, FileNode f, JComponent c, int flag){
		event = e;
		file = f;
		extra = c; //Used when I right-click a name in order to rename it. Param <c> is the textArea clicked.
		if(flag==BUTTON)
			createMenuForButton();
		else if(flag==TAB)
			createMenuForTab();
		else if(flag==TEXTAREA)
			createMenuForTextArea();
		else if(flag==TREE)
			createMenuForTree();
		else if(flag==SEARCH)
			createMenuForSearch();
	}
	
	private static void createMenuForButton(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		
		JMenuItem openMI = new JMenuItem("Open");
		openMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(file.isDirectory()){
					TabbedPanel.changeCurrentTabEvent(file);
				}
				else{
					TabbedPanel.openFile(file);
				}
			}
		});
		JMenuItem openNewTabMI = new JMenuItem("Open in a new tab");
		openNewTabMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(file.isDirectory())
					TabbedPanel.addNewTab(file.path());
			}
		});
		JMenuItem openNewWindowMI = new JMenuItem("Open in a new window");
		openNewWindowMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileExplorer.createWindow(file.path());
			}
		});
		JMenuItem renameMI = new JMenuItem("Rename");
		renameMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(extra!=null){
					JTextArea textArea = (JTextArea) extra;
					textArea.requestFocus();
					textArea.setEditable(true);
					textArea.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black));
					if(textArea.getText().contains(".") && !file.isDirectory())
						textArea.select(0, textArea.getText().indexOf("."));
					else
						textArea.selectAll();
				}
			}
		});
		JMenuItem deleteMI = new JMenuItem("Delete");
		deleteMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(file.isRootOfRoots() || file.isDrive())
					System.err.println("You can't delete this!");
				else
					TabbedPanel.deleteFile(file);
			}
		});
		JMenuItem propertiesMI = new JMenuItem("Properties");
		propertiesMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PropertiesFrame(file.path());
			}
		});
		
		jPopupMenu.add(openMI);				//Open
		jPopupMenu.add(openNewTabMI);		//Open in new tab (dir)
		jPopupMenu.add(openNewWindowMI);	//Open in new window (dir)
		if(!file.isRootOfRoots()){
			jPopupMenu.addSeparator();		//----------------------
			jPopupMenu.add(renameMI);		//Rename
			jPopupMenu.add(deleteMI);		//Delete
		}
		jPopupMenu.addSeparator();			//----------------------
		jPopupMenu.add(propertiesMI);		//Properties			
		jPopupMenu.show(event.getComponent(), event.getX(), event.getY());
	}
	
	private static void createMenuForTab(){
		JTabbedPane tabbedPane = TabbedPanel.getTabbedPane();
		int selected = tabbedPane.getSelectedIndex();
		int tabNr = ((TabbedPaneUI)tabbedPane.getUI()).tabForCoordinate(tabbedPane, event.getX(), event.getY());
					
		JPopupMenu jPopupMenu = new JPopupMenu();

		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenuItem newTabMI = new JMenuItem("New tab");
		newTabMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tabCount = tabbedPane.getTabCount();
				//Note: You cannot have more than 10 tabs (not including the add tab)
				if(tabCount<=10){
					//Create a new tab showing the home directory
					TabbedPanel.addNewTab(TabbedPanel.NEW_TAB_PATH);
				}
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenuItem newWindowMI = new JMenuItem("Open this in a new window");
		newWindowMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileExplorer.createWindow(file.path());
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenuItem closeTabMI = new JMenuItem("Close tab");
		closeTabMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.removeTabAt(tabNr);
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenuItem closeOthersMI = new JMenuItem("Close other tabs");
		closeOthersMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int survivorTab, temp=0;
				if(tabNr<0){ //When I click inside a tab, tabNr==-1, so set the selected tab as survivorTab.
					survivorTab = tabbedPane.getSelectedIndex();
				}
				else{ //Else, set the clicked tab as survivorTab.
					survivorTab = tabNr;
				}

				while(tabbedPane.getTabCount()>2){
					//Delete first tab until tab==survivorTab.
					//Then delete second tab until there are 2 tabs, the survivor and the "+" tab.
					if(temp!=survivorTab){
						TabbedPanel.removeTabAt(temp);
						survivorTab--;
					}
					else{ //When the survivorTab becomes first tab, delete all the rest
						temp++;
					}
				}
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenu viewMI = new JMenu("View"); //File extensions, Hidden files, Icons View, Detailed View
		JCheckBoxMenuItem hiddenFilesCB = new JCheckBoxMenuItem("Hidden files");
		hiddenFilesCB.setState(TabbedPanel.getMainPanelComponent().get(selected).showsHiddenFiles());
		hiddenFilesCB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == 1){
					Boolean state = hiddenFilesCB.getState();
					TabbedPanel.getMainPanelComponent().get(selected).setShowHiddenFiles(state);
					hiddenFilesCB.setState(!state);
				}
			}
		});
		JCheckBoxMenuItem fileExtensionsCB = new JCheckBoxMenuItem("File extensions");
		fileExtensionsCB.setState(TabbedPanel.getMainPanelComponent().get(selected).showsFileExtensions());
		fileExtensionsCB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == 1){
					Boolean state = fileExtensionsCB.getState();
					TabbedPanel.getMainPanelComponent().get(selected).setShowFileExtensions(state);
					fileExtensionsCB.setState(!state);
				}
			}
		});
		JRadioButtonMenuItem modeIcons = new JRadioButtonMenuItem("Icons View");
		modeIcons.setSelected(TabbedPanel.getMainPanelComponent().get(selected).showsIcons());
		modeIcons.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setMode(MainPanel.MODE_ICONS);
				ToolbarPanel.setComboboxView(0);
			}
		});
		JRadioButtonMenuItem modeList = new JRadioButtonMenuItem("List View");
		if(modeIcons.isSelected())
			modeList.setSelected(false);
		else
			modeList.setSelected(true);
		modeList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setMode(MainPanel.MODE_lIST);
				ToolbarPanel.setComboboxView(1);
			}
		});
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(modeIcons);
		modeGroup.add(modeList);
		
		viewMI.add(modeIcons);
		viewMI.add(modeList);
		viewMI.add(new JSeparator());
		if(!file.isRootOfRoots()){
			viewMI.add(hiddenFilesCB);
			viewMI.add(fileExtensionsCB);
		}
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenu sortMI = new JMenu("Sort"); //Ascending/Descending | Name/Size/Type/Count/Date(created/modified)
		JRadioButtonMenuItem AscendingMI = new JRadioButtonMenuItem("Ascending");
		AscendingMI.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getOrientation()==MainPanel.ASCENDING);
		AscendingMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setOrientation(MainPanel.ASCENDING);
			}
		});
		
		JRadioButtonMenuItem DescendingMI = new JRadioButtonMenuItem("Descending");
		DescendingMI.setSelected(!AscendingMI.isSelected());
		DescendingMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setOrientation(MainPanel.DESCENDING);
			}
		});
		ButtonGroup orientationGroup = new ButtonGroup();
		orientationGroup.add(AscendingMI);
		orientationGroup.add(DescendingMI);
		
		JRadioButtonMenuItem sortNameMI = new JRadioButtonMenuItem("Name");
		sortNameMI.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSort()==MainPanel.SORT_NAME);
		sortNameMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSort(MainPanel.SORT_NAME);
			}
		});
		JRadioButtonMenuItem sortTypeMI = new JRadioButtonMenuItem("Type");
		sortTypeMI.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSort()==MainPanel.SORT_TYPE);
		sortTypeMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSort(MainPanel.SORT_TYPE);
			}
		});
		JRadioButtonMenuItem sortSizeMI = new JRadioButtonMenuItem("Size");
		sortSizeMI.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSort()==MainPanel.SORT_SIZE);
		sortSizeMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSort(MainPanel.SORT_SIZE);
			}
		});
		JRadioButtonMenuItem sortCountMI = new JRadioButtonMenuItem("Count");
		sortCountMI.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSort()==MainPanel.SORT_COUNT);
		sortCountMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSort(MainPanel.SORT_COUNT);
			}
		});
		ButtonGroup sortGroup = new ButtonGroup();
		sortGroup.add(sortNameMI);
		sortGroup.add(sortTypeMI);
		sortGroup.add(sortSizeMI);
		sortGroup.add(sortCountMI);
		
		sortMI.add(AscendingMI);
		sortMI.add(DescendingMI);
		sortMI.add(new JSeparator());
		sortMI.add(sortNameMI);
		sortMI.add(sortTypeMI);
		sortMI.add(sortSizeMI);
		sortMI.add(sortCountMI);
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenu speedMI = new JMenu("Scroll speed"); //File extensions, Hidden files, Icons View, Detailed View
		JRadioButtonMenuItem verySlowRB = new JRadioButtonMenuItem("Very Slow");
		verySlowRB.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSpeed()==MainPanel.VERY_SLOW);
		verySlowRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSpeed(MainPanel.VERY_SLOW);
			}
		});
		JRadioButtonMenuItem slowRB = new JRadioButtonMenuItem("Slow");
		slowRB.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSpeed()==MainPanel.SLOW);
		slowRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSpeed(MainPanel.SLOW);
			}
		});
		JRadioButtonMenuItem normalRB = new JRadioButtonMenuItem("Normal");
		normalRB.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSpeed()==MainPanel.NORMAL);
		normalRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSpeed(MainPanel.NORMAL);
			}
		});
		JRadioButtonMenuItem fastRB = new JRadioButtonMenuItem("Fast");
		fastRB.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSpeed()==MainPanel.FAST);
		fastRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSpeed(MainPanel.FAST);
			}
		});
		JRadioButtonMenuItem veryFastRB = new JRadioButtonMenuItem("Very Fast");
		veryFastRB.setSelected(TabbedPanel.getMainPanelComponent().get(selected).getSpeed()==MainPanel.VERY_FAST);
		veryFastRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.getMainPanelComponent().get(selected).setSpeed(MainPanel.VERY_FAST);
			}
		});
		ButtonGroup speedGroup = new ButtonGroup();
		speedGroup.add(verySlowRB);
		speedGroup.add(slowRB);
		speedGroup.add(normalRB);
		speedGroup.add(fastRB);
		speedGroup.add(veryFastRB);
		
		speedMI.add(verySlowRB);
		speedMI.add(slowRB);
		speedMI.add(normalRB);
		speedMI.add(fastRB);
		speedMI.add(veryFastRB);
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenuItem refreshMI = new JMenuItem("Refresh");
		refreshMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.newAction(null, ToolbarPanel.REFRESH);
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenu NewMenu = new JMenu("New");
        JMenuItem FolderMI = new JMenuItem();
        JMenuItem FileMI = new JMenuItem();
		
        FolderMI.setText("Folder");
        FolderMI.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(null, ExplorerFrame.NEW_FOLDER);
            }
        });
        NewMenu.add(FolderMI);
		
		FileMI.setText("File");
        FileMI.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(null, ExplorerFrame.NEW_FILE);
            }
        });
        NewMenu.add(FileMI);
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenuItem propertiesMI = new JMenuItem("Properties");
		propertiesMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PropertiesFrame(file.path());
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		//JMenuItem settingsMI = new JMenuItem("Settings");
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

		if(tabbedPane.getTabCount()>2)
			jPopupMenu.add(newWindowMI);	//Open in a new window	
		jPopupMenu.add(newTabMI);			//New tab				
		jPopupMenu.add(refreshMI);			//Refresh				
		jPopupMenu.addSeparator();			//----------------------
		jPopupMenu.add(viewMI);				//View					>File extensions, Hidden files, Icons View, List View
		if(!file.isRootOfRoots())
			jPopupMenu.add(sortMI);			//Sort					>Ascending/Descending, Name, Size, Type, Count
		jPopupMenu.add(speedMI);			//Scroll Speed			>Very Slow, Slow, Normal, Fast, Very Fast
		jPopupMenu.addSeparator();			//----------------------
		if(tabbedPane.getTabCount()>2){
			jPopupMenu.add(closeTabMI);		//Close tab				
			jPopupMenu.add(closeOthersMI);	//Close other tabs		
			jPopupMenu.addSeparator();		//----------------------
		}
		if(!file.isRootOfRoots())
			jPopupMenu.add(NewMenu);		//New					>Folder, File
		jPopupMenu.add(propertiesMI);		//Properties			

		//Show menu,except if the "+" tab was clicked
		if(tabNr!=tabbedPane.getTabCount()-1){
			jPopupMenu.show(event.getComponent(), event.getX(), event.getY());
		}
	}
	
	private static void createMenuForTextArea(){
		JTextArea textArea = (JTextArea) event.getComponent();
		
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem renameMI = new JMenuItem("Rename");
		renameMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(extra!=null)
					extra.setBackground(new java.awt.Color(100,100,255));
				textArea.requestFocus();
				textArea.setEditable(true);
				textArea.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black));
				if(textArea.getText().contains(".") && !file.isDirectory())
					textArea.select(0, textArea.getText().indexOf("."));
				else
					textArea.selectAll();
			}
		});
		jPopupMenu.add(renameMI);
		jPopupMenu.show(textArea, event.getX(), event.getY());
	}
	
	private static void createMenuForTree(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		
		JMenuItem hideExpandedMI = new JMenuItem("Hide Expanded Folders");
		hideExpandedMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePanel.collapsePath(null);
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JCheckBoxMenuItem showRootCB = new JCheckBoxMenuItem("Show Root");
		showRootCB.setSelected(TreePanel.showsRoot());
		showRootCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean state = showRootCB.getState();
				TreePanel.setShowsRoot(state);
				showRootCB.setState(!state);
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JCheckBoxMenuItem showFilesCB = new JCheckBoxMenuItem("Show Files");
		showFilesCB.setSelected(TreePanel.showsFiles());
		showFilesCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean state = showFilesCB.getState();
				TreePanel.setShowsFiles(!TreePanel.showsFiles());
				showFilesCB.setState(!state);
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JCheckBoxMenuItem showHiddenFoldersCB = new JCheckBoxMenuItem("Show Hidden Folders");
		showHiddenFoldersCB.setSelected(TreePanel.showsHiddenFiles());
		showHiddenFoldersCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean state = showHiddenFoldersCB.getState();
				TreePanel.setShowsHiddenFiles(!TreePanel.showsHiddenFiles());
				showHiddenFoldersCB.setState(!state);
			}
		});
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		JMenu clickNumberMenu = new JMenu("Clicks needed to expand folders");
		JRadioButtonMenuItem oneRB = new JRadioButtonMenuItem("1");
		JRadioButtonMenuItem twoRB = new JRadioButtonMenuItem("2");
		oneRB.setSelected(FileExplorer.CLICKS_TO_EXPAND_FOLDERS==1);
		twoRB.setSelected(!oneRB.isSelected());
		ButtonGroup clickCountGroup = new ButtonGroup();
		clickCountGroup.add(oneRB);
		clickCountGroup.add(twoRB);
		
		oneRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePanel.setClickCount(1);
			}
		});
		twoRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePanel.setClickCount(2);
			}
		});
		
		clickNumberMenu.add(oneRB);
		clickNumberMenu.add(twoRB);
		//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
		
		jPopupMenu.add(hideExpandedMI);			//Hide Expanded Folders			
		jPopupMenu.add(showRootCB);				//Show Root						
		
		//jPopupMenu.add(showFilesCB);			//Show Files								//BUG. The tree doesn't update when I check this.
		//jPopupMenu.add(showHiddenFoldersCB);	//Show Hidden Files							//BUG. The tree doesn't update when I check this.
		//jPopupMenu.add(clickNumberMenu);		//Clicks needed to expand folders >  1,2	//BUG. For some reason the radiobutton doesn't change status

		//Show menu
		jPopupMenu.show(event.getComponent(), event.getX(), event.getY());
	}
	
	private static void createMenuForSearch(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		
		JMenuItem openMI = new JMenuItem("Open");
		openMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(file.isDirectory())
					TabbedPanel.changeCurrentTabEvent(file);
				else
					TabbedPanel.openFile(file);
			}
		});
		JMenuItem openNewTabMI = new JMenuItem("Open file location in a new tab");
		openNewTabMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TabbedPanel.addNewTab(file.getParent());
			}
		});
		JMenuItem openNewWindowMI = new JMenuItem("Open file location in a new window");
		openNewWindowMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileExplorer.createWindow(file.getParent());
			}
		});
		JMenuItem deleteMI = new JMenuItem("Delete");
		deleteMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(file.isRootOfRoots() || file.isDrive())
					System.err.println("You can't delete this!");
				else
					TabbedPanel.deleteFile(file);
			}
		});
		JMenuItem propertiesMI = new JMenuItem("Properties");
		propertiesMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PropertiesFrame(file.path());
			}
		});
		
		jPopupMenu.add(openMI);				//Open
		jPopupMenu.add(openNewTabMI);		//Open path in new tab (dir)
		jPopupMenu.add(openNewWindowMI);	//Open path in new window (dir)
		jPopupMenu.addSeparator();			//----------------------
		if(!file.isRootOfRoots()){
			jPopupMenu.add(deleteMI);		//Delete
			jPopupMenu.addSeparator();		//----------------------
		}
		jPopupMenu.add(propertiesMI);		//Properties			
		jPopupMenu.show(event.getComponent(), event.getX(), event.getY());
	}
}