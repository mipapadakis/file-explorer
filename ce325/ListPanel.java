/*
Description:
Used when the mode (view) is MainPanel.MODE_LIST and also when we sub-Search.
This panel contains a file's small icon and name. One ListPanel is created for 
each file, and they are all then added on the MainPanel.
It consists of:
	-iconPanel, containing the system's icon inside a JLabel
	-namePanel, containing the name inside a JTextArea (<textArea>), and the
		emptyLabel which I use so that there is only one column of ListPanels in
		the MainPanel. While sub-Searching, the namePanel also contains the 
		matched file's path.
	-Handles the file's rename.
*/

package ce325;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ListPanel extends JPanel{
	private static final int EMPTY_LABEL_WIDTH = 2000;
	private static final Color LINE_BORDER = new Color(100,100,240);
	private static final Color GRAY_FONT = new Color(150,150,150);
	private static final Color CLICKED = new Color(200,200,240);
	private boolean showFileExtensions = true;
	private FileNode searchedDirectory = null;
	private int clickCount = 0;
	private String tip = null;
    private JLabel systemIcon;
	private FileNode file;
	private JTextArea textArea; //when clicked, call RightClickMenu with BUTTON flag.
	private final JLabel emptyLabel; //Used for the Rightclick menu: when clicked, call RightClickMenu with TAB flag.
	private final JLabel pathLabel; //Used for showing the path after a sub-search.
	private JPanel iconPanel;
	private JPanel namePanel;
	
	//Default constructor.
	public ListPanel(FileNode file, boolean showFileExtensions){
		super(new FlowLayout(FlowLayout.LEFT, 2, 0)); //(align, hgap, vgap)
		setBackground(Color.white);
		setMouseListenerForBorder((JPanel) this);
		this.showFileExtensions = showFileExtensions;
		pathLabel=null;
		emptyLabel = createEmptyLabel(EMPTY_LABEL_WIDTH);
		if(file==null)
			return;
		tip=file.path();
		this.file = file;
		if(!file.exists()){
			System.err.println("ERROR!");
			return;
		}
		
		//Initialize the icon
		systemIcon = new JLabel((ImageIcon) file.getSystemIcon());
		
		//Add the icon to a panel
		iconPanel = new JPanel(new GridLayout(1,1));
		iconPanel.setBackground(Color.white);
		iconPanel.add(systemIcon);
		InitializeIcon();
		
		//Initialize the textArea which shows the file's name
		textArea = new JTextArea(1, file.name().length()+1);
		InitializeTextArea();
		InitializeEmptyLabel(emptyLabel);
		
		//Add the textArea to a panel
		namePanel = new JPanel(new BorderLayout(0,0));
		namePanel.setBackground(Color.white);
		namePanel.add(textArea, BorderLayout.WEST);
		namePanel.add(emptyLabel, BorderLayout.EAST);
		
		//Add both panels to the ListPanel (the parent)
		add(iconPanel);
		add(namePanel);
		InitializePanels();
	}
	
	//Constructor used when the search button was pressed (sub-Search).
	//The search took place on the <searchDirectory> and the <file> parameter
	//is one of the matched files that were found during the search.
	//Note: all the subDirectories of the <searchDirectory> were searched,
	//not only its immediate children.
	public ListPanel(FileNode file, FileNode searchDirectory, boolean showFileExtensions){
		super(new FlowLayout(FlowLayout.LEFT, 2, 0)); //(align, hgap, vgap)
		setBackground(Color.white);
		setMouseListenerForBorder((JPanel) this);
		this.showFileExtensions = showFileExtensions;
		emptyLabel = createEmptyLabel(EMPTY_LABEL_WIDTH);
		if(file==null || searchDirectory==null){
			pathLabel=null;
			return;
		}
		pathLabel = new JLabel(" Path: " + file.getParent());
		pathLabel.setForeground(GRAY_FONT);
		tip=file.path();
		this.file = file;
		this.searchedDirectory = searchDirectory;
		if(!file.exists()){
			System.err.println("ERROR!");
			return;
		}
		
		//Initialize the icon
		systemIcon = new JLabel((ImageIcon) file.getSystemIcon());
		
		//Add the icon to a panel
		iconPanel = new JPanel(new GridLayout(1,1));
		iconPanel.setBackground(Color.white);
		iconPanel.add(systemIcon);
		InitializeIcon();
		
		//Initialize the textArea which shows the file's name
		textArea = new JTextArea(1, file.name().length()+1);
		InitializeTextArea();
		InitializeEmptyLabel(emptyLabel);
		InitializeEmptyLabel(pathLabel);
		
		//Add the textArea to a panel
		namePanel = new JPanel(new BorderLayout(0,0));
		namePanel.setBackground(Color.white);
		namePanel.add(textArea, BorderLayout.WEST);
		namePanel.add(emptyLabel, BorderLayout.EAST);
		namePanel.add(pathLabel, BorderLayout.PAGE_END);
		
		//Add both panels to the ListPanel (the parent)
		add(iconPanel);
		add(namePanel);
		InitializePanels();
	}
	
	protected FileNode getFileNode(){return file;}
	
	private void InitializeIcon(){
		if(!file.isDrive() && !file.isRootOfRoots())
			iconPanel.setToolTipText(tip);
		setMouseListenerForBorder(systemIcon);
		systemIcon.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1){
					textArea.requestFocus();
				}
				if(e.getButton()==1 && e.getClickCount() == 2){
					TabbedPanel.iconClickedEvent(file);
				}
				else if(e.getButton()==2 && file.isDirectory()){ //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				}
				else if(e.getButton()==2 && searchedDirectory!=null){ //Mousewheel click on a searched file opens its parent directory in a new tab
					TabbedPanel.addNewTab(file.getParent());
				}
				else if(e.getButton()==3){
					if(searchedDirectory==null)
						RightClickMenu.create(e, file, null, RightClickMenu.BUTTON);
					else
						RightClickMenu.create(e, file, null, RightClickMenu.SEARCH);
				}
			}
		});
		systemIcon.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				TabbedPanel.setSelectedListIcon(ListPanel.this);
			}
			@Override
			public void focusLost(FocusEvent e) {
				TabbedPanel.setSelectedListIcon(null);
			}
		});
	}
	
	private void InitializeTextArea(){
		setMouseListenerForBorder(textArea);
		if(!file.isDrive() && !file.isRootOfRoots())
			textArea.setToolTipText(tip);
		showName();
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.getCaret().setVisible(false);
		textArea.setBackground(Color.white);
		setMouseListenerForBorder(textArea);
		//java.awt.Font font = textArea.getFont();
		//float size = font.getSize() + 3.5f;
		//textArea.setFont( font.deriveFont(size) );
		
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				BottomPanel.showFileProperties(file);
				TabbedPanel.setSelectedListIcon(ListPanel.this);
			}
			@Override
			public void focusLost(FocusEvent e) {
				TabbedPanel.setSelectedListIcon(null);
				requestFocus();
				if(!file.isDrive() && !file.isRootOfRoots()){ //You can't rename drives.
					textAreaUpdated();
				}
			}
		});
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER && !file.isDrive() && !file.isRootOfRoots()){
					textAreaUpdated();
				}
				textArea.setColumns(textArea.getText().length()+1);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER && !file.isDrive() && !file.isRootOfRoots()){
					textAreaUpdated();
				}
				textArea.setColumns(textArea.getText().length()+1);
			}
		});
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1 && e.getClickCount() == 1 && searchedDirectory==null){
					clickCount++;
					if(clickCount>1 && !file.isDrive()){
						setRenamable(true);
						iconPanel.setBackground(Color.WHITE);
						namePanel.setBackground(Color.WHITE);
						setBackground(Color.WHITE);
						clickCount=0;
					}
				}
				if(e.getButton()==1 && e.getClickCount() == 2){
					TabbedPanel.iconClickedEvent(file);
					clickCount=0;
				}
				else if(e.getButton()==2 && file.isDirectory()){ //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				}
				else if(e.getButton()==3){
					if(searchedDirectory==null)
						RightClickMenu.create(e, file, null, RightClickMenu.BUTTON);
					else
						RightClickMenu.create(e, file, null, RightClickMenu.SEARCH);
				}
			}
		});
	}
	
	private void InitializeEmptyLabel(JLabel label){
		TabbedPanel.addBackgroundClickListener(label, true);
		setMouseListenerForBorder(label);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 1){ //Left Click
					BottomPanel.showFileProperties(file);
					textArea.requestFocus();
					if(e.getClickCount()>=2)
						TabbedPanel.iconClickedEvent(file);
				}
				else if(e.getButton() == 2){ //MouseWheel click
					TabbedPanel.addNewTab(file.path());
				}
				else if(e.getButton() == 3){ //Right Click
					RightClickMenu.create(e, file, null, RightClickMenu.TAB);
				}
			}
		});
		label.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				TabbedPanel.setSelectedListIcon(null);
			}
			@Override
			public void focusLost(FocusEvent e) {
				TabbedPanel.setSelectedListIcon(null);
			}
		});
	}
	
	//Called when the file's name may have been changed by the user. Handles the rename.
	protected void textAreaUpdated(){
		boolean changeIcon = false;
		String name = fixed(textArea.getText());
		if(textArea.isEditable()){ //isEditable==true means that the user may have changed the file name. Rename the file:
			if(showFileExtensions && file.path().lastIndexOf(".")!=-1 && !file.isDirectory()){ //Ignore any changes to the file extension
				String format = file.path().substring(file.path().lastIndexOf("."));
				if(name.lastIndexOf(".")==-1){
					name += format;
				}
				else if(!name.substring(name.lastIndexOf(".")).equals(format)){
					name = name.substring(0,name.lastIndexOf("."));
					name += format;
				}
			}
			else if(!file.isDirectory()){
				changeIcon = true;
			}
			System.out.printf("old path:	%s\nnew path:	%s\n", file.path(), file.getParent()+ "\\" + name);
			file.renameTo(new File(file.getParent()+ "\\" +name));
			file = new FileNode(new File(file.getParent()+ "\\" +name));
			if(changeIcon){
				systemIcon.setIcon(new ImageIcon(file.getIcon()));
			}
			textArea.setBorder(BorderFactory.createEmptyBorder());
			setRenamable(false);
			textArea.select(0,0);
		}
	}
	
	private String fixed(String name){
		name = name.replaceAll("\\t", "");
		name = name.replaceAll("\\n", "");
		name = name.replace("\\", "");
		name = name.replaceAll("/", "");
		name = name.trim().replaceAll(" +", " "); //Replace 2 or more spaces with one space
		return name;
	}
	
	private void setMouseListenerForBorder(JComponent comp){
		comp.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				setBorder(BorderFactory.createLineBorder(LINE_BORDER));
				TabbedPanel.setSelectedListIcon(ListPanel.this);
				iconPanel.setBackground(CLICKED);
				namePanel.setBackground(CLICKED);
				setBackground(CLICKED);
				BottomPanel.showFileProperties(file);
			}
			@Override
			public void focusLost(FocusEvent e) {
				setBorder(BorderFactory.createEmptyBorder());
				TabbedPanel.setSelectedListIcon(null);
				iconPanel.setBackground(Color.WHITE);
				namePanel.setBackground(Color.WHITE);
				setBackground(Color.WHITE);
				if(searchedDirectory==null){
					if(file.getParentFile()==null)
						BottomPanel.showFileProperties(new FileNode());
					else
						BottomPanel.showFileProperties(new FileNode(file.getParentFile()));
					setRenamable(false);
					clickCount=0;
				}
			}
		});
		
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1){
					textArea.requestFocus();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(LINE_BORDER));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if(!comp.hasFocus())
					setBorder(BorderFactory.createEmptyBorder());
			}
		});
	}
	
	private void InitializePanels(){
		setMouseListenerForBorder(iconPanel);
		setMouseListenerForBorder(namePanel);
		iconPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1 && e.getClickCount() == 1)
					textArea.requestFocus();
				else if(e.getButton()==1 && e.getClickCount() == 2)
					TabbedPanel.iconClickedEvent(file);
				else if(e.getButton()==2 && file.isDirectory()) //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				else if(e.getButton()==3)
					RightClickMenu.create(e, file, null, RightClickMenu.TAB);
			}
		});
		namePanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1 && e.getClickCount() == 1)
					textArea.requestFocus();
				else if(e.getButton()==1 && e.getClickCount() == 2)
					TabbedPanel.iconClickedEvent(file);
				else if(e.getButton()==2 && file.isDirectory()) //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				else if(e.getButton()==3)
					RightClickMenu.create(e, file, null, RightClickMenu.TAB);
			}
		});
	}
	
	private void showName(){
		String name = file.name();
		if(!showFileExtensions && !file.getFileExtension().equals("")){
			name = name.substring(0,name.lastIndexOf("."));
			if(name.length()==0)
				name = file.name();
		}
		textArea.setText(name);
	}
	
	protected void setRenamable(boolean renamable){
		if(renamable){
			if(file.isRootOfRoots() || file.isDrive())
				return;
			if(!textArea.hasFocus())
				textArea.requestFocus();
			textArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
			textArea.setEditable(true);
			textArea.getCaret().setVisible(true);
			//textArea.setB
			if(textArea.getText().contains(".") && !file.isDirectory())
				textArea.select(0, textArea.getText().lastIndexOf("."));
			else
				textArea.selectAll();
		}
		else{
			TabbedPanel.newAction(null, ToolbarPanel.REFRESH);
			textArea.setEditable(false);
			textArea.getCaret().setVisible(false);
			textArea.setBorder(BorderFactory.createEmptyBorder());
		}
	}
	
	private JLabel createEmptyLabel(int width){
		StringBuilder sb = new StringBuilder("");
		for(int i=0; i<width; i++){
			sb.append(" ");
		}
		return new JLabel(sb.toString());
	}
}

