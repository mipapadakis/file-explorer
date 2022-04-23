/*
Description:
Used to sub-Search.
Searches recursively all the sub-directories of a directory and returns a list of
fileNodes containing the matched files.
Note: Uses regular expressions to compare the input and the file names.
*/

package ce325;

import java.io.File;
import java.util.ArrayList;

public class SearchRegex {
	private final ArrayList<FileNode> list;
	private final FileNode file;
	private final String regex;
	
	public SearchRegex(String path, String input){
		file = new FileNode(path);
		regex = input;
		list = new ArrayList<>();
		
		//Initialize list:
		subSearch(file);
		System.out.println("\nSearch finished! List:");
		for(FileNode fn: list)
			System.out.println("-Path: "+fn.path());
		System.out.println(list.size()+" matches.");
	}
	
	protected ArrayList<FileNode> getList(){ return list; }
	
	//Recursively search all sub-Directories of the param <file>. Add to the list the files whose names match the regex field:
	protected void subSearch(File file){
		if(file.isDirectory()){
			System.out.println("Searching in "+file.getAbsolutePath());
			if(!file.canRead()){
				System.out.println("Permission Denied!");
				return;
			}
			try{
				//Add matched directories:
				Boolean b;
				try{ b = java.util.regex.Pattern.compile(regex).matcher(file.getName()).find(); }
				catch(java.util.regex.PatternSyntaxException e){ b=false; }
				if(b){
					list.add(new FileNode(file));
					System.out.println("Found Directory: "+file.getName());
				}
				
				//Add matched files:
				for(File child: file.listFiles()){
					if(child.isDirectory())
						subSearch(child);
					else{
						try{ b = java.util.regex.Pattern.compile(regex).matcher(child.getName()).find(); }
						catch(java.util.regex.PatternSyntaxException e){ b=false; }
						if(b){
							list.add(new FileNode(child));
							System.out.println("Found: "+child.getName());
						}
					}
				}
			}
			catch(NullPointerException e){System.err.println("[ERROR]");}
		}
	}
	
}
