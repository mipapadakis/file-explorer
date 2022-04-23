/*
Description:
Records path history for when the Back or Forward buttons of the ToolbarPanel
were pressed.
*/

package ce325;

import java.util.ArrayList;

public class TabHistory {
	protected static String END_OF_LIST = "Reached the end of the history list!";
	//I need a way to save the path history of every open tab.
	//To do that, I will create a list of lists (one path-list for each tab).
	private ArrayList tabs; //Contains ArrayList items.
	private ArrayList pathList; //Contains Strings representing each path.
	private int current[];
	/*<--------------------------current[] EXPLANATION------------------------->
	current.length is equal to number of tabs.
	current[k] = index to the current path at tab k.
	All these paths are accessible by the list ((ArrayList) tabs).get(k), so:
	String path =(String)((ArrayList)((ArrayList)tabs).get(k)).get(current[k])
	contains the path of the directory shown in tab k.
	
	<---------------------------------ALGORITHM:------------------------------->
	Say that in a specific tab (eg tab 4), we access the below paths in
	chronological order, pathCurrent being the most recent:
	{ pathCurrent, path1, path2, path3 } => current[4] = 0
	
	/////////////////////////Press BACK button ONCE:////////////////////////////
	{ pathCurrent, path1, path2, path3 } => current[4] = current[4]+1 = 1
	
	///////////////////Go to a new path (eg. press UP button)://////////////////
	Steps:
	1)Delete all paths that have (index < current[4]).
	2)Add pathUP to beginning of list.
	3)Set current[4] to 0.
	Result:
	{ pathUP, path1, path2, path3 } => current[4]=0
	Notes for this case:
	-Forward Key now leads to nothing.
	-This case also appears after changing directory using the tree or toolbar.
	-Implemented in method "addPathToTab".
	
	///////////////////////Press BACK button THREE TIMES:///////////////////////
	{ pathUP, path1, path2, path3 } => current[4] = current[4]+3 = 3
	Note: Back key now leads to nothing.
	
	/////////////////////////Press FORWARD button once://///////////////////////
	{ pathX, path1, path2, path3 } => current[4] = current[4]-1 = 2
	
	<------------------------------------------------------------------------>*/
	
	public TabHistory(String path){
		tabs = new ArrayList();
		pathList = new ArrayList();
		pathList.add(path);
		tabs.add(pathList);// { {path} }
		current = new int[]{0};
	}
	
	protected String getPathOf(int index, int flag){ //Flag represents the BACK or FORWARD button.
		String path;
		if(index>=((ArrayList) tabs).size()){
			printLists("Error in getPathOf! Lists:");
			return null;
		}
		
		if(flag==ToolbarPanel.BACK_BUTTON){
			pathList = (ArrayList) tabs.get(index);
			if(current[index]+1<pathList.size())
				path = (String) pathList.get(++current[index]);
			else
				path = END_OF_LIST;
		}
		else if(flag==ToolbarPanel.FORWARD_BUTTON){
			pathList = (ArrayList) tabs.get(index);
			if(current[index]-1>=0)
				path = (String) pathList.get(--current[index]);
			else
				path = END_OF_LIST;
		}
		else{
			return null;
		}
		//printLists("getPathOf called. Lists:");
		return path;
	}
	
	//Case when a new path is added to the history list.
	protected void addPathToTab(int index, String path){
		if(index>=((ArrayList) tabs).size()){
			addTab(path);
		}
		else{
			pathList = (ArrayList) tabs.get(index);
			for(int i=0; i<current[index]; i++){
				pathList.remove(0); //1)Delete all paths that have (i < current[index]).
			}
			pathList.add(0,path); //2)Add pathUP to beginning of list.
			current[index]=0; //3)Set current[index] to 0.
			//printLists("addPathToTab called. Lists:");
		}
	}
	
	//Add a new path to the tab[index] at the start of their pathList.
	protected void addTab(String path){
		pathList = new ArrayList();
		pathList.add(path);
		tabs.add(pathList);
		
		//Current table must increase in size:
		int temp[] = new int[current.length+1];
		for(int i=0; i<current.length; i++){
			temp[i] = current[i];
		}
		temp[current.length]=0;
		current = temp;
		//printLists("addTab called. Lists:");
	}
	
	protected void removeTab(int index){
		if(index >= ((ArrayList) tabs).size()){
			printLists("Error in removeTab! Lists:");
			return;
		}
		tabs.remove(index);
		
		//Remove the tab from the current table as well:
		int temp[] = new int[current.length-1];
		for(int i=0; i<temp.length; i++){
			if(i<index)
				temp[i] = current[i];
			else
				temp[i] = current[i+1];
		}
		current = temp;
		//printLists("removeTab called. Lists:");
	}
	
	private void printLists(String extra){
		System.out.println(extra);
		for(int i=0; i<((ArrayList) tabs).size(); i++){
			pathList = (ArrayList) ((ArrayList) tabs).get(i);
			System.out.printf("{");
			for(int j=0; j<pathList.size(); j++){
				System.out.printf((String)pathList.get(j));
				if(j<pathList.size()-1){
					System.out.printf(", ");
				}
			}
			System.out.printf("}\n");
		}
	}
}
