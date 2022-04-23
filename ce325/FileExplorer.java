/*
Description:
The executable file for the file explorer.
Contains the main method.
You can change the settings of the program.
Handles the "open in new window" commands.
*/

package ce325;

import javax.swing.*; 



public class FileExplorer{
	//You can change the below constants to adjust the settings of the program.
	protected static final int INITIAL_WIDTH = 800;
	protected static final int INITIAL_HEIGHT = 500;
	protected static final int TREE_MIN_WIDTH = 130;
	protected static final int TREE_MIN_HEIGHT = 40;
	protected static final boolean TREE_SHOWS_ROOT = false;
	protected static final boolean TREE_SHOWS_HIDDEN_FILES = false;
	protected static final boolean TREE_SHOWS_FILES = false;
	protected static final int CLICKS_TO_EXPAND_FOLDERS = 1; //{1, 2}
	protected static final String NEW_TAB_DIRECTORY = System.getProperty("user.home"); //use System.getProperty("user.dir") for current directory
	protected static int MODE = MainPanel.MODE_ICONS;		//{MainPanel.MODE_ICONS, MainPanel.MODE_lIST}
	protected static int SORT = MainPanel.SORT_NAME;		//{MainPanel.SORT_NAME, MainPanel.SORT_SIZE, MainPanel.SORT_TYPE, MainPanel.SORT_COUNT}
	protected static int ORIENTATION = MainPanel.ASCENDING;	//{MainPanel.ASCENDING, MainPanel.DESCENDING}
	protected static int SCROLLBAR_SPEED = MainPanel.NORMAL;//{MainPanel.VERY_SLOW, MainPanel.SLOW, MainPanel.NORMAL, MainPanel.FAST, MainPanel.VERY_FAST} 
	protected static boolean MAIN_PANEL_SHOWS_HIDDEN_FILES = false;
	protected static boolean MAIN_PANEL_SHOWS_FILE_EXTENSIONS = true;
	protected static boolean SETTINGS_SAME_FOR_ALL_TABS = false;
	
	public FileExplorer(String path){
		new ExplorerFrame(path);
	}
	public FileExplorer(){
		new ExplorerFrame();
	}
	
    public static void main(String[] args){
		//Use Windows 10 theme:
		try{ UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch(Exception e){}
		new FileExplorer();
    }
	
	protected static void createWindow(String path){
		java.io.File file = new java.io.File(path);
		if(!file.exists() && !path.equals(TabbedPanel.ROOT)){
			System.err.println("Path \""+path+"\" is not valid");
			return;
		}
		if(file.isDirectory() || path.equals(TabbedPanel.ROOT)){
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new FileExplorer(path);
				}
			});
		}
		else
			System.err.println("\""+file.getName()+"\" is not a directory!");
	}
}