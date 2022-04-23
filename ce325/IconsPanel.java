/*
Description:
Used when the mode (view) is MainPanel.MODE_ICONS.
This panel contains a file's icon and name. One IconPanel is created for each
file, and they are all then added on the MainPanel.
It consists of:
	-iconPanel, containing the icon in the form of a JButton (<button>)
	-namePanel, containing the name inside a JTextArea (<textArea>)
	-Handles the file's rename.
*/

package ce325;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author minas
 */
public class IconsPanel extends JPanel{
	private static final int TEXT_AREA_WIDTH = 12;
	private static final Color LINE_BORDER = new Color(100,100,240);
	private static final Color CLICKED = new Color(200,200,240);
	private boolean showFileExtensions = true;
	private int clickCount = 0;
	private FileNode file;
	private String buttonTip;
	private JButton button;
	private JTextArea textArea;
	private JPanel iconPanel;
	private JPanel namePanel;
	
	public IconsPanel(FileNode file, boolean showFileExtensions){
		super(new BorderLayout(50,5));
		this.showFileExtensions = showFileExtensions;
		setBackground(Color.white);
		setMouseListenerForBorder((JPanel) this);
		if(file==null)
			return;
		buttonTip=file.path();
		this.file = file;
		
		if(!file.exists()){
			System.err.println("ERROR!");
			return;
		}
		
		//Initialize the button which shows the file's icon
		button = new JButton(new ImageIcon(file.getIcon()));
		InitializeButton();
		
		//Add the button to a panel
		iconPanel = new JPanel(new GridLayout(1,1));
		iconPanel.setBackground(Color.white);
		iconPanel.add(button);
		
		//Initialize the textArea which shows the file's name
		textArea = new JTextArea(2,TEXT_AREA_WIDTH);
		InitializeTextArea();
		
		//Add the textArea to a panel
		namePanel = new JPanel(new BorderLayout(8,0));
		namePanel.setBackground(Color.white);
		namePanel.add(textArea, BorderLayout.CENTER);
		namePanel.add(new JLabel(" "), BorderLayout.WEST);
		namePanel.add(new JLabel(" "), BorderLayout.EAST);
		
		//Add both panels to the IconsPanel (the parent)
		add(iconPanel, BorderLayout.CENTER);
		add(namePanel, BorderLayout.PAGE_END);
	}
	
	protected FileNode getFileNode(){return file;}
	
	private void InitializeButton(){
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		button.setToolTipText(buttonTip);
		button.setBackground(Color.white);
		setMouseListenerForBorder(button);
		button.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1){
					button.requestFocus();
				}
				if(e.getButton()==1 && e.getClickCount() == 2){
					TabbedPanel.iconClickedEvent(file);
				}
				else if(e.getButton()==2 && file.isDirectory()){ //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				}
				else if(e.getButton()==3){
					RightClickMenu.create(e, file, textArea, RightClickMenu.BUTTON);
				}
			}
		});
		button.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				TabbedPanel.setSelectedIcon(IconsPanel.this);
			}
			@Override
			public void focusLost(FocusEvent e) {
				TabbedPanel.setSelectedIcon(null);
			}
		});
	}
	
	private void InitializeTextArea(){
		showFullName(false);
		if(!file.isDrive() && !file.isRootOfRoots())
			textArea.setToolTipText("Double-click to rename");
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.getCaret().setVisible(false);
		textArea.setWrapStyleWord(true); //Avoid splitting words when changing line.
		textArea.setBackground(Color.white);
		
		/*
		//Increase/decrease font size:
		java.awt.Font font = textArea.getFont();
		float size = font.getSize() + 0.5f;
		textArea.setFont( font.deriveFont(size) );
		*/
		
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				showFullName(true);
				BottomPanel.showFileProperties(file);
				TabbedPanel.setSelectedIcon(IconsPanel.this);
				button.setBorder(BorderFactory.createLineBorder(LINE_BORDER));
			}
			@Override
			public void focusLost(FocusEvent e) {
				TabbedPanel.setSelectedIcon(null);
				textArea.setBorder(BorderFactory.createEmptyBorder());
				setRenamable(false);
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
			}
		});
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1){
					textArea.requestFocus();
					if(e.getButton()==1 && e.getClickCount() == 1){
						clickCount++;
						if(clickCount>1 && !file.isDrive()){
							setRenamable(true);
							iconPanel.setBackground(Color.WHITE);
							namePanel.setBackground(Color.WHITE);
							setBackground(Color.WHITE);
							clickCount=0;
						}
					}
				}
				if(e.getButton()==1 && e.getClickCount() == 2){
					if(file.isDrive() || file.isRootOfRoots()){
						System.out.println("Drives cannot be renamed.");
						return;
					}
					setRenamable(true);
				}
				else if(e.getButton()==3){
					if(!file.isDrive() && !file.isRootOfRoots()){
						return;
					}
					RightClickMenu.create(e, file, iconPanel, RightClickMenu.TEXTAREA);
				}
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
			else if(!file.isDirectory())
				changeIcon = true;
			System.out.printf("Name eddited! Old path:	%s\nnew path:	%s\n", file.path(), file.getParent()+ "\\" + name);
			file.renameTo(new File(file.getParent()+ "\\" +name));
			file = new FileNode(file.getParent()+ "\\" +name);
			if(changeIcon){
				button.setIcon(new ImageIcon(file.getIcon()));
			}
			textArea.setBorder(BorderFactory.createEmptyBorder());
			setRenamable(false);
			textArea.select(0,0);
		}
		showFullName(false);
	}
	
	private String fixed(String name){
		name = name.replaceAll("\\t", "");
		name = name.replaceAll("\\n", "");
		name = name.replace("\\", "");
		name = name.replaceAll("/", "");
		name = name.trim().replaceAll(" +", " "); //Replace 2 or more spaces with one space
		return name;
	}
	
	private void setMouseListenerForBorder( JComponent comp){
		comp.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				button.setBorder(BorderFactory.createLineBorder(LINE_BORDER));
				TabbedPanel.setSelectedIcon(IconsPanel.this);
				if(comp instanceof JButton){ //ICON SELECTED
					iconPanel.setBackground(CLICKED);
					BottomPanel.showFileProperties(file);
					showFullName(true);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				button.setBorder(BorderFactory.createEmptyBorder());
				TabbedPanel.setSelectedIcon(null);
				if(comp instanceof JButton){ //ICON DESELECTED
					iconPanel.setBackground(Color.WHITE);
					if(file.getParentFile()==null)
						BottomPanel.showFileProperties(new FileNode());
					else
						BottomPanel.showFileProperties(new FileNode(file.getParentFile()));
					showFullName(false);
					clickCount=0;
				}
			}
		});
		
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBorder(BorderFactory.createLineBorder(LINE_BORDER));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button.setBorder(BorderFactory.createEmptyBorder());
			}
		});
	}
	
	private void InitializePanels(){
		iconPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1){
					button.requestFocus();
					TabbedPanel.setSelectedIcon(IconsPanel.this);
				}
				if(e.getButton()==1 && e.getClickCount() == 2){
					TabbedPanel.iconClickedEvent(file);
				}
				else if(e.getButton()==2 && file.isDirectory()){ //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				}
				else if(e.getButton()==3){
					RightClickMenu.create(e, file, textArea, RightClickMenu.BUTTON);
				}
			}
		});
		namePanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1){
					textArea.requestFocus();
					TabbedPanel.setSelectedIcon(IconsPanel.this);
				}
				if(e.getButton()==1 && e.getClickCount() == 2){
					TabbedPanel.iconClickedEvent(file);
				}
				else if(e.getButton()==2 && file.isDirectory()){ //Mousewheel click on a directory opens it in a new tab
					TabbedPanel.addNewTab(file.path());
				}
				else if(e.getButton()==3){
					RightClickMenu.create(e, file, textArea, RightClickMenu.BUTTON);
				}
			}
		});
	}
	
	//In order to avoid large textAreas due to large file names, this method
	//partially hides the file's name while it is not on focus.
	private void showFullName(boolean b){
		String name = file.name();	
		if(b){
			if(name.length()>2*TEXT_AREA_WIDTH){
				if(!showFileExtensions && !file.getFileExtension().equals("")){
					name = name.substring(0,name.lastIndexOf("."));
					if(name.length()==0)
						name = file.name();
				}
				textArea.setText(name);
			}
		}
		else{
			if(!showFileExtensions && !file.getFileExtension().equals("")){
				name = name.substring(0,name.lastIndexOf("."));
				if(name.length()==0)
					name = file.name();
			}
			if(name.length()>2*TEXT_AREA_WIDTH)
				textArea.setText(name.substring(0, 2*TEXT_AREA_WIDTH-3) + "...");
			else
				textArea.setText(name);
		}
	}
	
	protected void setRenamable(boolean renamable){
		if(renamable){
			if(file.isRootOfRoots() || file.isDrive())
				return;
			
			if(!textArea.hasFocus())
				textArea.requestFocus();
			showFullName(true);
			textArea.setEditable(true);
			textArea.getCaret().setVisible(true);
			textArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
			//textArea.setB
			if(textArea.getText().contains(".") && !file.isDirectory())
				textArea.select(0, textArea.getText().lastIndexOf("."));
			else
				textArea.selectAll();
		}
		else{
			textArea.setEditable(false);
			textArea.getCaret().setVisible(false);
		}
	}
}
