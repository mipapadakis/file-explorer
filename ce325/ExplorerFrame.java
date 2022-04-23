/*
Description:
Creates the main frame of the explorer.
The frame consists of:
	-The menu bar		(created here)
	-The toolbar		(class ToolbarPanel)
	-The tree			(class TreePanel)
	-The tabbed panel	(class TabbedPanel)
	-The bottom label	(class BottomPanel)
*/

package ce325;

import java.awt.*; 
import java.awt.event.ActionListener;
import javax.swing.*; 

public class ExplorerFrame extends JFrame {
	protected static int NEW_FOLDER = 1;
	protected static int NEW_FILE = 2;
	protected static int width = FileExplorer.INITIAL_WIDTH;
	protected static int height = FileExplorer.INITIAL_HEIGHT;
	protected static JSplitPane splitPane;
	protected static ToolbarPanel toolbar;
	protected static BottomPanel bottom;
	protected static JMenuBar menuBar;
	protected static JMenu FileMenu;
	protected static JMenu NewMenu;
	
	public ExplorerFrame(String path){
		super();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
        toolbar = new ToolbarPanel();
		bottom = new BottomPanel();
		
        //Create a splitpane so the user can resize the tree panel
        splitPane = new JSplitPane(SwingConstants.VERTICAL, new TreePanel(), new TabbedPanel(path));
		
		add(toolbar, BorderLayout.PAGE_START);
		add(splitPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.PAGE_END);
		addMenuBar();
		
		setTitle("File Explorer");
		setIconImage(((ImageIcon) (new FileNode(System.getProperty("user.dir"))).getSystemIcon()).getImage());
		setSize(width, height);
		show();
	}
	
	public ExplorerFrame(){
		super();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
        toolbar = new ToolbarPanel();
		bottom = new BottomPanel();
		
        //Create a splitpane so the user can resize the tree panel
        splitPane = new JSplitPane(SwingConstants.VERTICAL, new TreePanel(), new TabbedPanel());
		
		add(toolbar, BorderLayout.PAGE_START);
		add(splitPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.PAGE_END);
		addMenuBar();
		
		setTitle("File Explorer");
		setIconImage(((ImageIcon) (new FileNode(System.getProperty("user.dir"))).getSystemIcon()).getImage());
		setSize(width, height);
		show();
	}
	
	protected static void setNeutralFocus(){
		bottom.requestFocus();
		TabbedPanel.setSelectedIcon(null);
	}
	
	protected static void updateFileMenuTip(String tip){
		 FileMenu.setToolTipText(tip);
	}
	
	private void addMenuBar(){
		menuBar = new JMenuBar();
		menuBar.setBorder(BorderFactory.createLineBorder(new Color(240,240,240)));
		//menuBar.setBackground( new Color(240,240,240));
		FileMenu = new JMenu("File");
        FileMenu.setToolTipText("Select a file to enable this menu");
        FileMenu.setFocusable(false);
		FileMenu.setEnabled(false);
        //FileMenu.setHorizontalTextPosition(SwingConstants.CENTER);
        FileMenu.setVerticalTextPosition(SwingConstants.BOTTOM);
		
		// open, open in new tab/window, rename, delete, properties
		JMenuItem openMI = new JMenuItem("Open");
		JMenuItem openInNewTabMI = new JMenuItem("Open in a new tab");
		JMenuItem openInNewWindowMI = new JMenuItem("Open in a new window");
		JMenuItem renameMI = new JMenuItem("Rename");
		JMenuItem deleteMI = new JMenuItem("Delete");
		JMenuItem propertiesMI = new JMenuItem("Properties");
        openMI.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt){
				FileNode file = null;
                if(TabbedPanel.getSelectedTabMode()==MainPanel.MODE_ICONS){
					try{file = TabbedPanel.getSelectedIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				else{
					try{file = TabbedPanel.getSelectedListIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				
				if(file==null)
					System.err.println("Error!");
				else if(file.isDirectory())
					TabbedPanel.changeCurrentTabEvent(file);
				else
					TabbedPanel.openFile(file);
            }
        });
		openInNewTabMI.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
				FileNode file = null;
                if(TabbedPanel.getSelectedTabMode()==MainPanel.MODE_ICONS){
					try{file = TabbedPanel.getSelectedIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				else{
					try{file = TabbedPanel.getSelectedListIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				
				if(file==null)
					System.err.println("Error!");
				else if(file.isFile())
					System.err.println("\""+file.name()+"\" is not a directory!");
				else
					TabbedPanel.addNewTab(file.path());
            }
        });
		openInNewWindowMI.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileNode file = null;
                if(TabbedPanel.getSelectedTabMode()==MainPanel.MODE_ICONS){
					try{file = TabbedPanel.getSelectedIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				else{
					try{file = TabbedPanel.getSelectedListIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				
				if(file==null)
					System.err.println("Error!");
				else if(file.isFile())
					System.err.println("\""+file.name()+"\" is not a directory!");
				else
					FileExplorer.createWindow(file.path());
            }
        });
		renameMI.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt){
                if(TabbedPanel.getSelectedTabMode()==MainPanel.MODE_ICONS){
					try{TabbedPanel.getSelectedIcon().setRenamable(true);}
					catch(NullPointerException n){}
				}
				else{
					try{TabbedPanel.getSelectedListIcon().setRenamable(true);}
					catch(NullPointerException n){}
				}
            }
        });
		deleteMI.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt){
                FileNode file = null;
                if(TabbedPanel.getSelectedTabMode()==MainPanel.MODE_ICONS){
					try{file = TabbedPanel.getSelectedIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				else{
					try{file = TabbedPanel.getSelectedListIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				
				if(file==null)
					System.err.println("Error!");
				else if(file.isRootOfRoots() || file.isDrive())
					System.err.println("You can't delete this!");
				else
					TabbedPanel.deleteFile(file);
            }
        });
		propertiesMI.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt){
				FileNode file = null;
                if(TabbedPanel.getSelectedTabMode()==MainPanel.MODE_ICONS){
					try{file = TabbedPanel.getSelectedIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				else{
					try{file = TabbedPanel.getSelectedListIcon().getFileNode();}
					catch(NullPointerException n){}
				}
				
				if(file==null)
					System.err.println("Error!");
				else
					new PropertiesFrame(file.path());
            }
        });
		FileMenu.add(openMI);			//Open
		FileMenu.add(openInNewTabMI);	//Open in a new tab
		FileMenu.add(openInNewWindowMI);//Open in a new window
		FileMenu.add(new JSeparator());	//---------------------
		FileMenu.add(renameMI);			//Rename
		FileMenu.add(deleteMI);			//Delete
		FileMenu.add(new JSeparator());	//---------------------
		FileMenu.add(propertiesMI);		//Properties
		menuBar.add(FileMenu);
		
		NewMenu = new JMenu("New");
        NewMenu.setToolTipText("Create a new Folder or File");
        NewMenu.setFocusable(false);
		NewMenu.setEnabled(true);
        //FileMenu.setHorizontalTextPosition(SwingConstants.CENTER);
        NewMenu.setVerticalTextPosition(SwingConstants.BOTTOM);
        JMenuItem FolderMI = new JMenuItem();
        JMenuItem FileMI = new JMenuItem();
		
        FolderMI.setText("Folder");
        FolderMI.setToolTipText("Create a new folder");
        FolderMI.setHorizontalTextPosition(SwingConstants.CENTER);
        FolderMI.setVerticalTextPosition(SwingConstants.BOTTOM);
        FolderMI.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(null, ExplorerFrame.NEW_FOLDER);
            }
        });
        NewMenu.add(FolderMI);
		
		FileMI.setText("File");
        FileMI.setToolTipText("Create a new text file");
        FileMI.setHorizontalTextPosition(SwingConstants.CENTER);
        FileMI.setVerticalTextPosition(SwingConstants.BOTTOM);
        FileMI.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(null, ExplorerFrame.NEW_FILE);
            }
        });
        NewMenu.add(FileMI);
		menuBar.add(NewMenu);
		setJMenuBar(menuBar);
	}
}