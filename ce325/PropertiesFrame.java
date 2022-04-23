/*
Description:
Creates a JFrame that presents the file's properties (path, size, date Modified, etc)
*/

package ce325;

import java.awt.GridLayout;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PropertiesFrame extends JFrame {
	
	public PropertiesFrame(String path){
		super();
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		FileNode file = new FileNode(path);
		
		if(file.isRootOfRoots()){
			setLayout(new GridLayout(9,1));
			StringBuilder temp = new StringBuilder("");
			add(new JLabel(" Name: Root of Roots "));
			add(new JLabel(" Type: Directory (Virtual) "));
			temp.append(" Contains all the system's drives (");
			String paths[] = FileNode.getRootPaths();
			for(int i=0; i<paths.length-1; i++)
				temp.append(paths[i].replace("\\","")).append(",  ");
			temp.append(paths[paths.length-1].replace("\\","")).append(") ");
			add(new JLabel(temp.toString())); //eg: "Contains all the system's drives (C:, D:, E:)"
			add(new JLabel("                                                                          "));
			add(new JLabel(" Size:"));
			add(new JLabel( String.format(" Total Space: %.1fGB (%s bytes) ", (double) file.totalSpace(FileNode.GIGABYTES),NumberFormat.getInstance().format(file.totalSpace(FileNode.BYTES))) ));
			add(new JLabel( String.format(" Used Space: %.1fGB (%s bytes) ", (double) file.usedSpace(FileNode.GIGABYTES),NumberFormat.getInstance().format(file.usedSpace(FileNode.BYTES))) ));
			add(new JLabel( String.format(" Free Space: %.1fGB (%s bytes) ", (double) file.freeSpace(FileNode.GIGABYTES),NumberFormat.getInstance().format(file.freeSpace(FileNode.BYTES))) ));
			setIconImage(((ImageIcon) ( FileNode.getRoots()[0]).getSystemIcon()).getImage());
		}
		else if(file.isDrive()){
			setLayout(new GridLayout(14,1));
			add(new JLabel(" Name: " + file.name() + " "));
			add(new JLabel(" Path: " + file.path() + " "));
			add(new JLabel(" Type: Directory"));
			add(new JLabel(contentsString(file))); //eg: "Contents: 18 items (1 folder, 17 files)"
			add(new JLabel("                                                                          "));
			add(new JLabel(" Size:"));
			add(new JLabel( String.format(" Total Space: %.1fGB (%s bytes) ", (double) file.totalSpace(FileNode.GIGABYTES),NumberFormat.getInstance().format(file.totalSpace(FileNode.BYTES))) ));
			add(new JLabel( String.format(" Used Space: %.1fGB (%s bytes) ", (double) file.usedSpace(FileNode.GIGABYTES),NumberFormat.getInstance().format(file.usedSpace(FileNode.BYTES))) ));
			add(new JLabel( String.format(" Free Space: %.1fGB (%s bytes) ", (double) file.freeSpace(FileNode.GIGABYTES),NumberFormat.getInstance().format(file.freeSpace(FileNode.BYTES))) ));
			add(new JLabel(" "));
			add(new JLabel(" Date Modified: "+file.getDateModified() + " ") );
			add(new JLabel(" Date Created: "+file.getDateCreated() + " ") );
			add(new JLabel(" Last Access: "+file.getDateAccessed() + " ") );
			setIconImage(((ImageIcon) ( FileNode.getRoots()[0]).getSystemIcon()).getImage());
		}
		else if(file.isDirectory()){
			setLayout(new GridLayout(8,1));
			add(new JLabel(" Name: " + file.name() + " "));
			add(new JLabel(" Path: " + file.path() + " "));
			add(new JLabel(" Type: Directory "));
			add(new JLabel(contentsString(file) + " ")); //eg: "Contents: 18 items (1 folder, 17 files)"
			add(new JLabel(sizeString(file, FileNode.DEFAULT) + " "));
			add(new JLabel("                                                                          "));
			add(new JLabel(" Date Modified: "+file.getDateModified() + " ") );
			//add(new JLabel(" Date Created: "+file.getDateCreated() + " ") );
			//add(new JLabel(" Last Access: "+file.getDateAccessed() + " ") );
			setIconImage(((ImageIcon) file.getSystemIcon()).getImage());
		}
		else if(file.isFile()){
			setLayout(new GridLayout(7,1));
			add(new JLabel(" Name: " + file.name() + " "));
			add(new JLabel(" Path: " + file.path() + " "));
			add(new JLabel(formatString(file) + " "));
			add(new JLabel(sizeString(file, FileNode.DEFAULT) + " "));
			add(new JLabel("                                                                          "));
			add(new JLabel(" Date Modified: "+file.getDateModified() + " ") );
			//add(new JLabel(" Date Created: "+file.getDateCreated() + " ") );
			//add(new JLabel(" Last Access: "+file.getDateAccessed() + " ") );
			setIconImage(((ImageIcon) file.getSystemIcon()).getImage());
		}
		else{
			setLayout(new GridLayout(2,1));
			add(new JLabel("Error! "));
		}
		
		//setSize(FileExplorer.PROPERTIES_WIDTH, FileExplorer.PROPERTIES_HEIGHT);
		setLocationRelativeTo(TabbedPanel.getTabbedPane());
		setTitle("Properties");
		pack();
		show();
	}
	
	//Returns: " Contents: 18 items (1 folder, 17 files)"
	protected String contentsString(FileNode fn){
		int folders, files; //<folders> = number of children folders, <files> = number of children files.
		String itemsStr = " items (", foldersStr = " folders, ", filesStr = " files)";
		
		if(fn.isDirectory()){
			folders = fn.NumberOfChildren(1);
			files = fn.NumberOfChildren(2);
			
			if(folders==1)
				foldersStr = " folder, ";
			if(files==1)
				filesStr = " file)";
			if(files+folders==1)
				itemsStr = " item (";
			return " Contents: "+ (folders+files) + itemsStr + folders + foldersStr + files + filesStr ;
		}
		return null;
	}
	
	//Returns: " Type: music (1 folder, 17 files)"
	protected String formatString(FileNode fn){
		if(!fn.getFileExtension().equals("") && !fn.isDirectory())
				return " Type: " + fn.getFormat() + " (."+fn.getFileExtension()+")";
		return " Type: " + fn.getFormat();
	}
	
	//Returns: " Size: 18.25GB"
	//Call with unit = FileNode.DEFAULT to find the suitable unit(B,KB,MB,GB) automatically.
	protected String sizeString(FileNode fn, int unit){
		double size; //Size of fn
		String unitStr="B";
		if(unit==FileNode.DEFAULT){
			size = fn.getFileSize(FileNode.BYTES);
			//Find proper unit:
			if(size>=1024){
				size = size/1024;
				unitStr = "KB";
				if(size>=1024){
					size = size/1024;
					unitStr = "MB";
					if(size>=1024){
						size = size/1024;
						unitStr = "GB";
					}
				}
			}
		}
		else{
			size = fn.getFileSize(unit);
			if(unit==FileNode.BYTES)
				unitStr="B";
			else if(unit==FileNode.KILOBYTES)
				unitStr="KB";
			else if(unit==FileNode.MEGABYTES)
				unitStr="MB";
			else if(unit==FileNode.GIGABYTES)
				unitStr="GB";
			else{
				size = fn.getFileSize(FileNode.MEGABYTES);
				unitStr="MB";
			}
		}
		return String.format(" Size: %.2f" + unitStr, size);
	}
}
