/*
Description:
Panel at the bottom of the frame.
Shows info about the selected/current directory.
*/

package ce325;

public class BottomPanel extends javax.swing.JPanel{
    private static javax.swing.JLabel bottomTextPane;
	
	public BottomPanel(){
        super(new java.awt.GridLayout(1, 1));
        bottomTextPane = new javax.swing.JLabel();
		bottomTextPane.setText("Hello!");
		add(bottomTextPane);
	}
	
	protected static void showFileProperties(FileNode fn){
		
		if(fn.isRootOfRoots()){
			int drives = fn.NumberOfChildren(1);
			if(drives==1)
				bottomTextPane.setText(" Total Space: "+
						fn.totalSpace(FileNode.GIGABYTES)+"GB, Free Space: "+
						fn.freeSpace(FileNode.GIGABYTES)+"GB, Used Space: "+
						fn.usedSpace(FileNode.GIGABYTES)+"GB");
			else
				bottomTextPane.setText(" "+drives+" drives." + " Total space: "+
						fn.totalSpace(FileNode.GIGABYTES)+"GB, Free Space: "+
						fn.freeSpace(FileNode.GIGABYTES)+"GB, Used Space: "+
						fn.usedSpace(FileNode.GIGABYTES)+"GB");
			return;
		}
		
		double size = 0; //Size of fn
		String unit = "B", format = fn.getFormat();
		int folders, files; //<folders> = number of children folders, <files> = number of children files.
		String itemsStr = " items (", foldersStr = " folders, ", filesStr = " files)";
		String lastModified = fn.getDateModified();
		if(!fn.isDirectory())
			size = fn.getFileSize(1);
		
		if(lastModified!=null)
			lastModified = ",    Last Modified: " + lastModified;
		else
			lastModified = "";
		
		//Find proper unit:
		if(size>=1024){
			size = size/1024;
			unit = "KB";
			if(size>=1024){
				size = size/1024;
				unit = "MB";
				if(size>=1024){
					size = size/1024;
					unit = "GB";
				}
			}
		}
		
		if(fn.isDirectory()){
			folders = fn.NumberOfChildren(1);
			if(TabbedPanel.getMainPanelComponent().get(TabbedPanel.getSelected()).showsHiddenFiles())
				files = fn.NumberOfChildren(2);
			else
				files = fn.NumberOfChildren(3); //if !showHiddenFiles => remove hidden files from the sum!
			
			if(folders==1)
				foldersStr = " folder, ";
			if(files==1)
				filesStr = " file)";
			if(files+folders==1)
				itemsStr = " item (";
			bottomTextPane.setText(" Contents: "+ (folders+files) + itemsStr + folders + foldersStr + files + filesStr);
		}
		else{
			if(!fn.getFileExtension().equals("") && !fn.isDirectory())
				format = format + " (."+fn.getFileExtension()+")";
			bottomTextPane.setText(String.format(" Size: %.2f", size) + unit + ",    Format: " + format + lastModified);
		}
	}
}
