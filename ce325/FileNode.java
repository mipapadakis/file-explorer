/*
Description:
Class that extends java.io.File and handles most file-related jobs.
For example:
	-Finds proper icons to show on the main panel
	-Calculates file sizes
	-Creates files-directories
	-Creates lists of the file's children that the other classes use
	-Handles the "root of roots":
Root of roots is a specific fileNode I created, that does not refer to any
existing file. Its children are the system's drives, and the file explorer more 
or less behaves as if it is a drive, with path equal to <TabbedPanel.ROOT>.
*/

package ce325;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

public class FileNode extends File{
	protected static final int DEFAULT = -1; //Used in properties: Choose best fitted unit(B,KB,MB,GB) for the file's size. 
	protected static final int BYTES = 1;
	protected static final int KILOBYTES = 2;
	protected static final int MEGABYTES = 3;
	protected static final int GIGABYTES = 4;
    private BufferedImage icon;
	private Icon systemIcon = null;
	private boolean rootOfRoots = false; //Indicates that this node is the father of all the system's drives
	private boolean isDrive = false;
	private String format, dateCreated = null, dateModified = null, dateAccessed = null;
    
	//This constructor is used only when we reach the "root of roots".
	public FileNode(){
		super(calculateRoot(null));
		rootOfRoots = true;
		InitializeIcon();
    }
	
    public FileNode(String pathname){
		super(pathname);
		if(pathname.equals(TabbedPanel.ROOT))
			rootOfRoots=true;
		else if((new File(pathname)).getParent()==null)
			isDrive=true;
		InitializeIcon();
    }
    
    public FileNode(File file){
		super(file.getAbsolutePath());
		if(getParent()==null)
			isDrive=true;
		InitializeIcon();
    }
	
	private void InitializeDates(){
		try{
			BasicFileAttributes attr;
			attr = Files.readAttributes(toPath(), BasicFileAttributes.class);
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy,  HH:mm");
			dateCreated = "" + df.format(attr.creationTime().toMillis());
			dateModified = "" + df.format(attr.lastModifiedTime().toMillis());
			dateAccessed = "" + df.format(attr.lastAccessTime().toMillis());
		}
		catch(Exception e){}
	}
	
	//Returns the paths of all the system's drives (eg C:, E:, ...)
	protected static String[] getRootPaths(){
		File roots[] = listRoots();
		String paths[] = new String[roots.length];
		for(int i=0; i<paths.length; i++)
			paths[i] = roots[i].getAbsolutePath();
		return paths;
	}
	
	//Returns all the system's drives
	protected static FileNode[] getRoots(){
		File roots[] = listRoots();
		FileNode fn[] = new FileNode[roots.length];
		for(int i=0; i<fn.length; i++){
			fn[i] = new FileNode(roots[i].getAbsolutePath());
		}
		return fn;
	}
	
	//Calculate the root of a specific path. If the parameter is null, return the root path of the (user.dir)'s drive
	protected static String calculateRoot(String path){
		File cur;
		if(path==null || path.equals(""))
			//Starting from current dir, go to parent dir until parent==null (meaning that cur=root).
			cur = new File(Paths.get("").toAbsolutePath().toString());
		else
			cur = new File(path);
		
		while(cur.getParentFile()!=null){
			cur = cur.getParentFile();
		}
		return(cur.getAbsolutePath());
    }
    
    @Override
    public String toString(){
		if(isRootOfRoots())
			return "Drives";
		else if(isDrive())
			//return "Drive " + getAbsolutePath().replace("\\","").replace("/",""); //Show "Drive C:" instead of "C:/"
			return FileSystemView.getFileSystemView().getSystemDisplayName(this); //Example: Shows "Local Disk (C:)" instead of just "C:/".
		return getName();
    }
	
	//I use this instead of getAbsolutePath() because the root of roots is not an actual file.
    protected String path(){
		if(isRootOfRoots()){
			return TabbedPanel.ROOT;
		}
		return getAbsolutePath();
    }
	
	//I use this instead of getName() because the root of roots is not an actual file.
	protected String name(){
		return toString();
    }
	
	//I use this instead of isHidden() because the root of roots is not an actual file.
	protected boolean hidden(){
		if(isRootOfRoots())
			return false;
		else if(isDrive())
			return false;
		return (isHidden() || name().charAt(0)=='.');
    }
	
	protected boolean isDrive(){return isDrive; }
	protected boolean isRootOfRoots(){return rootOfRoots;}
    protected BufferedImage getIcon(){return icon;}
	protected String getFormat(){ return format; }
	protected String getDateModified(){
		if(dateModified==null || dateCreated==null || dateAccessed==null)
			InitializeDates();
		return dateModified;
	}
	protected String getDateCreated(){
		if(dateModified==null || dateCreated==null || dateAccessed==null)
			InitializeDates();
		return dateCreated;
	}
	protected String getDateAccessed(){
		if(dateModified==null || dateCreated==null || dateAccessed==null)
			InitializeDates();
		return dateAccessed;
	}
    protected Icon getSystemIcon(){
		if(systemIcon==null){
			if(isRootOfRoots() || path().equals(TabbedPanel.ROOT) || isDrive() || getParent()==null)
				systemIcon = FileSystemView.getFileSystemView().getSystemIcon(new File(calculateRoot(null)));
			else
				systemIcon = FileSystemView.getFileSystemView().getSystemIcon(this);
		}
		return systemIcon;
	}
	
	//Calculate number of children.
    protected int NumberOfChildren(int option){
		//option=0 for all children
		//option=1 for number of directories
		//option=2 for all files that are not directories
		//option=3 for all non-hidden files that are not directories
		
		if(isRootOfRoots())
			return getRootPaths().length;
		
		int count=0;
		File files[] = listFiles();
		if(files==null){
			return 0;
		}
		for(File f: files){
			if(option==0)
				return(files.length);
			else if(option==1 && f.isDirectory())
				count++;
			else if(option==2 && !f.isDirectory())
				count++;
			else if(option==3 && !f.isDirectory() && !(new FileNode(f)).hidden() )
				count++;
		}
		return(count);
    }
    
    //Returns an Arraylist of a FileNode's children files.
    protected ArrayList<FileNode> getChildren(int option){
		//Use option=0 to return list of all children.
		//Use option=1 to return list of children that are directories
		//Use option=2 to return list of children that are NOT directories
		ArrayList list = new ArrayList<>();
		
		if(isRootOfRoots()){
			//The "root of roots" contains all the system's roots (drives):
			if(option==2)
				return null;
			for(FileNode fn: getRoots()){
				list.add(fn);
			}
		}
		else{
			if(!this.isDirectory()){
				return null;
			}
			File files[] = this.listFiles();
			if(files==null){
				return null;
			}

			for(File f: files){
				if(option==0)
					list.add(new FileNode(f));
				else if(option==1 && f.isDirectory())
					list.add(new FileNode(f));
				else if(option==2 && f.isFile())
					list.add(new FileNode(f));
			}
		}
		return list;
    }
	
	protected boolean hasSameFormatWith(FileNode f){
		return getFileExtension().equals(f.getFileExtension());
	}
    
    protected String getFileExtension(){
		String name = name();
		int dot = name.lastIndexOf(".");
		if(dot==-1 || isDirectory()){
			return "";
		}
		return name.substring(dot+1); //return extension without the dot.
    }
	
	protected long getFileSize(int unit){
		//unit is either BYTES = 1, or KILOBYTES = 2, or MEGABYTES = 3, or GIGABYTES = 4.
		long size = 0;
		if(isRootOfRoots()){
			for(FileNode drive: getRoots())
				size += drive.getTotalSpace() - drive.getFreeSpace(); //Get the sum of all the disks' sizes in bytes.
		}
		else if(!isDirectory())
			size = (new File(getAbsolutePath())).length(); //Get the size of the file (not the FileNode!) in bytes.
		else{
			try{
				size = subSize(this);
			}
			catch(NullPointerException e){
				System.err.println("NullPointerException: Couldn't calculate size of folder " + name());
			}
		}
		for(int i=1; i<unit; i++)
			size = size/1024;
		return size;
	}
	
	//If isDrive, return the drive's total space
	//For any other fileNode, return the total space of all drives (sum)
	protected long totalSpace(int unit){
		long size=0;
		if(isDrive)
			size = getTotalSpace();
		else{
			for(FileNode drive: getRoots())
				size += drive.getTotalSpace();
		}
		
		for(int i=1; i<unit; i++)
			size = size/1024;
		return size;
	}
	
	//If isDrive, return the drive's free space
	//For any other fileNode, return the free space of all drives (sum)
	protected long freeSpace(int unit){
		long size=0;
		if(isDrive)
			size = getFreeSpace();
		else{
			for(FileNode drive: getRoots())
				size += drive.getFreeSpace();
		}
		
		for(int i=1; i<unit; i++)
			size = size/1024;
		return size;
	}
	
	//If isDrive, return the drive's used space
	//For any other fileNode, return the used space of all drives (sum)
	protected long usedSpace(int unit){
		return totalSpace(unit) - freeSpace(unit);
	}
	
	//Calculates the size of a directory by recursively accessing all its children
	protected long subSize(File file) throws NullPointerException{
		long size = 0;
		if(file.isDirectory()){
			System.out.println("Calculating size of directory \""+file.getName()+"\"...");
			for(File child: file.listFiles()){
				if(child.isDirectory())
					size += subSize(child);
				else
					size += child.length();
			}
		}
		return size;
	}
    
    //Create a file in <this>, with name equal to <name>.
    protected FileNode createFile(String name){
		if(isRootOfRoots()){
			System.err.println("You can't create a file here.");
			return null;
		}
		
		if(!isDirectory()){
			System.err.println(name()+" is not a directory!");
			return null;
		}
		FileNode newNode = new FileNode(getAbsolutePath() + "\\" + name);

		try{
			if(!newNode.createNewFile()){
				//File already exists.
				JOptionPane.showMessageDialog(null, "File already exists!");
				return null;
			}
		}catch(IOException e){
			System.err.println("Couldn't create file.");
			return null;
		}
		return newNode;
    }
    
    //Create a directory in <this>, with name equal to <name>.
    protected FileNode createDir(String name){
		if(isRootOfRoots()){
			System.err.println("You can't create a folder here.");
			return null;
		}
		
		if(!isDirectory()){
			System.err.println(name()+" is not a directory!");
			return null;
		}
		FileNode newNode = new FileNode(getAbsolutePath() + "\\" + name);

		if(!newNode.mkdir()){
			//File already exists.
			JOptionPane.showMessageDialog(null, "File already exists!");
			return null;
		}
		return newNode;
    }
	
	//Return true if there is a regex match between <this>.name() and the <input>
	protected Boolean regexMatch(String input){
		Boolean b;
		try{ b = java.util.regex.Pattern.compile(input).matcher(name()).find(); }
		catch(java.util.regex.PatternSyntaxException e){ b=false; }
		return b;
	}
    
    protected void InitializeIcon(){
		try{
			String iconPath;
			if(this.isDirectory() || isRootOfRoots()){
				format = "Directory";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\folder.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isAudio()){
				format = "Audio";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\audio.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isImage()){
				format = "Image";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\image.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isVideo()){
				format = "Video";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\video.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isCompressed()){
				format = "Zip";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\zip.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isHtml()){
				format = "Html";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\html.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isTxt()){
				format = "Text";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\txt.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isXml()){
				format = "Xml";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\xml.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isPdf()){
				format = "Pdf";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\pdf.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isWord()){
				format = "Word Document";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\doc.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else if(this.isExcel()){
				format = "Excel Document";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\xlsx.png";
				icon = ImageIO.read(new File(iconPath));
			}
			else{
				format = "Unknown";
				iconPath = System.getProperty("user.dir") + "\\src\\ce325\\icons\\question.png";
				icon = ImageIO.read(new File(iconPath));
			}
		}catch(IOException e) {
			System.err.println("Error creating image");
		}
    }
    
    protected boolean isAudio(){
		String extension = getFileExtension();
		if(extension.equals("mp3") || extension.equals("ogg") || extension.equals("wav") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isImage(){
		String extension = getFileExtension();
		if(extension.equals("png") || extension.equals("ppm") || extension.equals("giff") || extension.equals("jpeg") || extension.equals("jpg") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isVideo(){
		String extension = getFileExtension();
		if(extension.equals("avi") || extension.equals("flv") || extension.equals("wmv") || extension.equals("mp4") || extension.equals("mov") || extension.equals("mkv") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isCompressed(){
		String extension = getFileExtension();
		if(extension.equals("zip") || extension.equals("tar") || extension.equals("tgz") || extension.equals("gz") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isHtml(){
		String extension = getFileExtension();
		if(extension.equals("html") || extension.equals("htm") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isTxt(){
		String extension = getFileExtension();
		if(extension.equals("txt") || extension.equals("c") || extension.equals("java") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isXml(){
		String extension = getFileExtension();
		if(extension.equals("xml") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isPdf(){
		String extension = getFileExtension();
		if(extension.equals("pdf") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isWord(){
		String extension = getFileExtension();
		if(extension.equals("doc") || extension.equals("docx") || extension.equals("odt") ){
			return(true);
		}
		return(false);
    }
    
    protected boolean isExcel(){
		String extension = getFileExtension();
		if(extension.equals("ods") || extension.equals("xlsx") || extension.equals("xlx")|| extension.equals("jpeg")|| extension.equals("jpg") ){
			return(true);
		}
		return(false);
    }
}
