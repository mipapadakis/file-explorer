/*
Description:
Each tab of the TabbedPanel contains a MainPanel. It is the main panel that
contains the contents of the tab's current directory.It handles the way and the
order the files are shown. It uses either IconsPanel or ListPanel to represent
each file, depending on the <mode> chosen.
*/

package ce325;

import static ce325.TabbedPanel.addBackgroundClickListener;
import java.util.ArrayList;
import javax.swing.JPanel;

public class MainPanel extends javax.swing.JScrollPane {
	//ORIENTATION
	protected static final int DESCENDING = 101;
	protected static final int ASCENDING = 102; //default
	//MODE
	protected static final int MODE_ICONS = 103; //default
	protected static final int MODE_lIST = 104;
	//SORTING TYPES:
	protected static final int SORT_NAME = 105; //default
	protected static final int SORT_TYPE = 106;
	protected static final int SORT_SIZE= 107;
	protected static final int SORT_COUNT= 108;
	//SHOW EXTRA DATA
	protected static final int SHOW_HIDDEN = 109;
	protected static final int SHOW_EXTENSION = 110;
	//SCROLLBAR SPEED
	protected static final int VERY_SLOW = 6;
	protected static final int SLOW = 10;
	protected static final int NORMAL = 16;
	protected static final int FAST = 22;
	protected static final int VERY_FAST = 26;
	
	//VARIABLES
	private JPanel parentPanel = new JPanel(new WrapLayout(java.awt.FlowLayout.LEFT));
	private int mode = FileExplorer.MODE;
	private int sort = FileExplorer.SORT;
	private int orientation = FileExplorer.ORIENTATION;
	private boolean showHiddenFiles = FileExplorer.MAIN_PANEL_SHOWS_HIDDEN_FILES;
	private boolean showFileExtensions = FileExplorer.MAIN_PANEL_SHOWS_HIDDEN_FILES;
	private static int scrollbarSpeed = FileExplorer.SCROLLBAR_SPEED;
	
	public MainPanel(String path){
		super();
		setVerticalScrollBar(verticalScrollBar);
		setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getVerticalScrollBar().setUnitIncrement(scrollbarSpeed);
		setBackground(java.awt.Color.white);
		setViewportView(parentPanel);
		parentPanel.setBackground(java.awt.Color.white);
		TabbedPanel.addBackgroundClickListener(parentPanel, true);
		TabbedPanel.addBackgroundClickListener(this, true);
		updateComponents(path, null, false);
	}
	
	public MainPanel(String path, String input, int mode, int orientation, int sort, boolean showHiddenFiles, boolean showFileExtensions){
		super();
		setVerticalScrollBar(verticalScrollBar);
		setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getVerticalScrollBar().setUnitIncrement(scrollbarSpeed);
		setBackground(java.awt.Color.white);
		setViewportView(parentPanel);
		parentPanel.setBackground(java.awt.Color.white);
		TabbedPanel.addBackgroundClickListener(parentPanel, true);
		TabbedPanel.addBackgroundClickListener(this, true);
		setShowHiddenFiles(showHiddenFiles);
		setShowFileExtensions(showFileExtensions);
		//If the constructor has zero on any of the int parameters, use the default value.
		if(mode!=0)
			setMode(mode);
		if(orientation!=0)
			setOrientation(orientation);
		if(sort!=0)
			setSort(sort);
		updateComponents(path, input, false);
	}
	
	protected int getMode(){return mode;}
	protected boolean showsIcons(){return mode==MODE_ICONS;}
	protected boolean showsList(){return mode==MODE_lIST;}
	protected int getOrientation(){return orientation;}
	protected int getSort(){return sort;}
	protected int getSpeed(){return scrollbarSpeed;}
	protected boolean showsHiddenFiles(){return showHiddenFiles;}
	protected boolean showsFileExtensions(){return showFileExtensions;}
	
	protected void newPath(String path){updateComponents(path, null, false);}
	protected void newSimpleSearch(String path, String input){updateComponents(path, input, false);}
	protected void newSubSearch(String path, String input){updateComponents(path, input, true);}
	protected void setMode(int mode){this.mode=mode; TabbedPanel.newAction(null, ToolbarPanel.REFRESH);}
	protected void setOrientation(int orientation){this.orientation=orientation; TabbedPanel.newAction(null, ToolbarPanel.REFRESH);}
	protected void setShowHiddenFiles(boolean showHiddenFiles){this.showHiddenFiles=showHiddenFiles; TabbedPanel.newAction(null, ToolbarPanel.REFRESH);}
	protected void setShowFileExtensions(boolean showFileExtensions){this.showFileExtensions=showFileExtensions; TabbedPanel.newAction(null, ToolbarPanel.REFRESH);}
	protected void setSort(int sort){this.sort=sort; TabbedPanel.newAction(null, ToolbarPanel.REFRESH);}
	protected void setSpeed(int speed){
		scrollbarSpeed = speed;
		getVerticalScrollBar().setUnitIncrement(scrollbarSpeed);
		TabbedPanel.newAction(null, ToolbarPanel.REFRESH);
	}
	
	//Updates MainPanel after a sub-Search, or a change that has been made (eg. sort=Sort by Size)
	protected void updateComponents(String path, String input, boolean searchResults){
		JPanel p;
		FileNode file = new FileNode(path);
		//WrapLayout.java: Found it online. Couldn't use FlowLayout because it used only one row to display the icons.
		ArrayList<FileNode> sortedList = new ArrayList<>(); //Contains ALL children, sorted.
		parentPanel = new JPanel(new WrapLayout(java.awt.FlowLayout.LEFT));
		parentPanel.setBackground(java.awt.Color.white);
		if(searchResults){
			SearchRegex search = new SearchRegex(path, input); //Create the search-list, containing the search results. Also, print the list (at System.out).
			sortedList = search.getList(); //Initialize the sortedList with the search-list
		}
		
		/* E X A M P L E:
		BY NAME:		BY SIZE:		BY TYPE:		BY COUNT:
		a.txt-3KB		i.mkv-900MB		c.mkv-700MB		a.txt-3KB
		b.mp3-1.2MB		c.mkv-700MB		i.mkv-900MB		d.txt-10B
		c.mkv-700MB		h.png-55MB		j.mkv-22MB		e.txt-20KB
		d.txt-10B		j.mkv-22MB		g.png-1MB		c.mkv-700MB
		e.txt-20KB		f.mp3-2MB		h.png-55MB		i.mkv-900MB	
		f.mp3-2MB		b.mp3-1.2MB		b.mp3-1.2MB		j.mkv-22MB
		g.png-1MB		g.png-1MB		f.mp3-2MB		g.png-1MB
		h.png-55MB		e.txt-20KB		a.txt-3KB		h.png-55MB
		i.mkv-900MB		a.txt-3KB		d.txt-10B		b.mp3-1.2MB
		j.mkv-22MB		d.txt-10B		e.txt-20KB		f.mp3-2MB
		*/
		
		if(mode==MODE_ICONS && file.getChildren(0)==null){  //Has children?
			System.err.println("(Empty folder)");
			parentPanel.add(new IconsPanel(null, false));
			setViewportView(parentPanel);
			BottomPanel.showFileProperties(file);
			return;
		}
		if(mode==MODE_ICONS && file.getChildren(0)==null){ //Has children?
			System.err.println("(Empty folder)");
			parentPanel.add(new ListPanel(null, false));
			setViewportView(parentPanel);
			BottomPanel.showFileProperties(file);
			return;
		}
			
		if(sort==SORT_NAME && !searchResults){ //If searchResults==true, the sorted list is already initialized correctly.
			for(FileNode fn: file.getChildren(1)){ //Show directories first:
				sortedList.add(fn);
			}
			if(!file.isRootOfRoots())
				for(FileNode fn: file.getChildren(2)) //Show non-directory files:
					sortedList.add(fn);
		}
		else if(sort==SORT_SIZE && !file.isRootOfRoots()){
			if(searchResults)
				sortedList = SortBySize(sortedList);
			else{
				sortedList = SortBySize(file.getChildren(1)); //Add folders (sorted by size)
				for(FileNode fn: SortBySize(file.getChildren(2))) //Add files
					sortedList.add(fn);
			}
		}
		else if(sort==SORT_TYPE && !file.isRootOfRoots()){
			/*How the files will be displayed:
			1)Display all directories
			2)Below the directories, pick the largest file in size and display all files with same format as this (sorted by name).
			3)Pick the second largest that has different format than the first, display all files with same format as this (sorted by name).
			4)...*/
			if(searchResults)
				sortedList = SortByType(sortedList);
			else{
				sortedList = file.getChildren(1); //Add folders (sorted by name)
				for(FileNode fn: SortByType(file.getChildren(2))) //Add files
					sortedList.add(fn);
			}
		}
		else if(sort==SORT_COUNT && !file.isRootOfRoots()){ //Sort based on frequency of file types
			if(searchResults)
				sortedList = SortByCount(sortedList);
			else{
				sortedList = file.getChildren(1); //Add folders (sorted by name)
				for(FileNode fn: SortByCount(file.getChildren(2))) //Add files
					sortedList.add(fn);
			}
		}
		
		if(orientation==ASCENDING){
			//From sorted children, remove all that we must not show (eg hidden files) and add the remaining to te iconsPanel/ListPanel
			for(FileNode fn: sortedList){
				if(searchResults)
					p = addToMainPanel(fn, path, true);
				else
					p = addToMainPanel(fn, input, false);
				
				if(p!=null){
					parentPanel.add(p);
				}
			}
		}
		else{
			//From sorted children, remove all that we must not show (eg hidden files) and add the remaining to te iconsPanel/ListPanel
			for(int i=sortedList.size()-1; i>=0; i--){
				if(searchResults)
					p = addToMainPanel(sortedList.get(i), path, true);
				else
					p = addToMainPanel(sortedList.get(i), input, false);
				
				if(p!=null){
					parentPanel.add(p);
				}
			}
		}
		setViewportView(parentPanel);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				getVerticalScrollBar().setValue(0);
			}
		 });
	}
	
	//Create the IconsPanel (or ListPanel depending on <mode>) and return it.
	private JPanel addToMainPanel(FileNode fn, String input, boolean searchResults){
		JPanel p;
		if(searchResults){ //If searchResults==true, the input contains the path of the directory in which we started the subSearch.
			p = new ListPanel(fn, new FileNode(input), showFileExtensions);
			addBackgroundClickListener(p, false);
			if(showHiddenFiles==false && !fn.isDrive() && (fn.isHidden() || (fn.name().length()>0 && fn.name().charAt(0)=='.'))){
				p = null; //Don't show files that are hidden
			}
		}
		else if(mode==MODE_ICONS){
			p = new IconsPanel(fn, showFileExtensions);
			addBackgroundClickListener(p, false);
			
			if(showHiddenFiles==false && fn.hidden()){
				p = null; //Don't show files that are hidden
			}
			if(input!=null && !fn.regexMatch(input)) //!fn.name().toLowerCase().contains(input.toLowerCase())
				p = null; //don't show files that do not match the regex <input>.
		}
		else if(mode==MODE_lIST){
			p = new ListPanel(fn, showFileExtensions);
			addBackgroundClickListener(p, false);
			if(showHiddenFiles==false && fn.hidden()){
				p = null; //Don't show files that are hidden
			}
			if(input!=null && !fn.regexMatch(input)) //!fn.name().toLowerCase().contains(input.toLowerCase())
				p = null; //don't show files that do not match the regex <input>.
		}
		else{
			System.err.println("Error!");
			p=null;
		}
		
		return p;
	}
	
	//Receives a list of fileNodes and returns the list sorted by size (list.get(0) = file with max size).
	private ArrayList<FileNode> SortBySize(ArrayList<FileNode> byName){ //option=1 for directories, 2 for non-dir and 0 for both.
		ArrayList<FileNode> list = new ArrayList();
		//Create a copy of the parameter, because I will need to remove the nodes of it below.
		//If I used the byName instead of the copy, I would empty it, thus causing unpredictable problems elsewhere.
		for(FileNode fn: byName)
			list.add(new FileNode(fn.path()));
		int maxIndex=0;
		double size, max=0;
		ArrayList<FileNode> sorted = new ArrayList();
		
		//Selection-sort:
		while(!list.isEmpty()){
			//Find max size
			for(int i=0; i<list.size(); i++){
				size = ((FileNode)list.get(i)).getFileSize(FileNode.BYTES);
				if(size>max){
					max=size;
					maxIndex=i;
				}
			}
			sorted.add( list.get(maxIndex) );
			//System.out.println("Added size:" + max + "\t\tname:"+list.get(maxIndex).name());
			list.remove(maxIndex);
			max=0;
			maxIndex=0;
		}
		return sorted;
	}
	
	//Receives a list of fileNodes and returns the list sorted by type (The first type group contains files of the same format as the largest in size file's).
	private ArrayList<FileNode> SortByType(ArrayList<FileNode> byName){
		ArrayList<FileNode> bySize = SortBySize(byName);
		ArrayList<FileNode> byType = new ArrayList();
		int end = bySize.size();
		FileNode temp;
		String format;
		
		while(bySize.size()>0){
			format = bySize.get(0).getFileExtension(); //bySize.get(0) always has the largest file (in size) comparing to the rest of the nodes.
			for(int i=0; i<end; i++){
				temp = bySize.get(i);
				if(temp.getFileExtension().equals(format)){
					bySize.remove(i);
					i--;
					end--;
				}
			}
			for(FileNode fn: byName){
				if(fn.getFileExtension().equals(format)){
					byType.add(fn);
				}
			}
		}
		return byType;
	}
	
	//Receives a list of fileNodes and returns the list sorted by count (list.get(0) = file with most frequent format).
	private ArrayList<FileNode> SortByCount(ArrayList<FileNode> ByName){
		ArrayList<FileNode> bySize = SortBySize(ByName);
		ArrayList<FileNode> byCount = new ArrayList();
		ArrayList<FileNode> formatsBySize = new ArrayList(); //Includes the largest fileNodes of each format (starting from the largest). 
		ArrayList<FileNode> sameFormatList, maxList;
		int count, maxIndex, max, end = bySize.size();
		FileNode temp;
		FileNode largest;
		
		//Create formatsBySize:
		while(bySize.size()>0){
			largest = bySize.get(0); //bySize.get(0) always has the largest file (in size) comparing to the rest of the nodes.
			
			//Remove all fileNodes with same format as <largest>'s format
			for(int i=0; i<end; i++){
				temp = bySize.get(i);
				if(temp.getFileExtension().equals(largest.getFileExtension())){
					bySize.remove(i);
					i--;
					end--;
				}
			}
			
			//Add to the formatsBySize list
			formatsBySize.add(largest);
			//Note: formatsBySize contains only ONE fileNode (the largest in size) for each format.
		}
		
		System.out.println("formatsBySize:");
		for(FileNode fn: formatsBySize)
			System.out.println(fn.name());
		
		//Create byCount:
		while(formatsBySize.size()>0){
			maxList = getSameFormatCount(formatsBySize.get(0));
			maxIndex=0;
			max=0;
			//Find format with max count
			for(int i=0; i<formatsBySize.size(); i++){
				sameFormatList = getSameFormatCount(formatsBySize.get(i));
				count = sameFormatList.size(); //This represents the count of files with the same format as <formatsBySize.get(i)>'s
				if(count>max){
					max = count;
					maxIndex=i;
					maxList = sameFormatList;
				}
			}
			//Remove the fileNode with most frequent format:
			formatsBySize.remove(maxIndex);
			
			for(FileNode fn: maxList)
				byCount.add(fn);
		}
		return byCount;
	}
	
	//Returns a list of the <child>'s siblings that have same format with the <child>. The list is sorted by name.
	private ArrayList<FileNode> getSameFormatCount(FileNode child){
		ArrayList<FileNode> list = new ArrayList();
		FileNode parent = new FileNode(child.getParent());
		
		for(FileNode fn: parent.getChildren(0)){
			if(fn.hasSameFormatWith(child)){
				list.add(fn);
			}
		}
		return list;
	}
}
