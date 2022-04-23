/*
Description:
Creates the path tree.
->When a directory of the tree is clicked, view it in the current tab's MainPanel.
->When a directory in MainPanel is accessed, view its path tree.
*/

package ce325;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreePanel extends JPanel{
	private static final int MIN_WIDTH = FileExplorer.TREE_MIN_WIDTH;
	private static final int MIN_HEIGHT = FileExplorer.TREE_MIN_HEIGHT;
	private static boolean showsRoot = FileExplorer.TREE_SHOWS_ROOT;
	private static boolean showsHiddenFiles = FileExplorer.TREE_SHOWS_HIDDEN_FILES;
	private static boolean showsFiles = FileExplorer.TREE_SHOWS_FILES;
	private static int clickCount = FileExplorer.CLICKS_TO_EXPAND_FOLDERS;
	private static DefaultTreeModel TreeModel;
	private static DefaultMutableTreeNode rootTreeNode;
    private static FileNode rootFileNode;
    private static JTree Tree;
    private static JScrollPane TreeScrollPane;
	
	public TreePanel(){
        super(new GridLayout(1, 1));
		setMinimumSize(new java.awt.Dimension(MIN_WIDTH, MIN_HEIGHT));
		setMouseListener(this);
        TreeScrollPane = new JScrollPane();
		setMouseListener(TreeScrollPane);
        Tree = new JTree();
        TreeScrollPane.setViewportView(Tree);
		rootFileNode = new FileNode();
		initTree();
		removeAll();
		add(TreeScrollPane);
	}
	
	public TreePanel(String path){
        super(new GridLayout(1, 1));
		setMinimumSize(new java.awt.Dimension(MIN_WIDTH, MIN_HEIGHT));
		//setMouseListener(this);
        TreeScrollPane = new JScrollPane();
		//setMouseListener(TreeScrollPane);
        Tree = new JTree();
        TreeScrollPane.setViewportView(Tree);
		rootFileNode = new FileNode();
		initTree();
		removeAll();
		add(TreeScrollPane);
	}
	
	protected static JScrollPane getTreeScrollPane(){return(TreeScrollPane);}
	protected static JTree getTree(){return(Tree);}
	protected static int getClickCount(){return Tree.getToggleClickCount();}
	protected static boolean showsRoot(){return showsRoot;}
	protected static boolean showsHiddenFiles(){return showsHiddenFiles;}
	protected static boolean showsFiles(){return showsFiles;}
	protected static void setClickCount(int value){
        Tree.setRootVisible(!showsRoot);
		Tree.setRootVisible(showsRoot);
		clickCount = value;
		Tree.setToggleClickCount(value);
	}
	protected static void setShowsRoot(boolean value){
		showsRoot=value;
        Tree.setRootVisible(value);
	}
	protected static void setShowsHiddenFiles(boolean value){
		showsHiddenFiles=value;
	}
	protected static void setShowsFiles(boolean value){
		showsFiles=value;
	}
	
	//Start the tree from the system's drives.
	private static void initTree(){
        Tree.setRootVisible(showsRoot);
        Tree.setToggleClickCount(clickCount);
		setMouseListener(Tree);
		TreeModel  = (DefaultTreeModel) Tree.getModel();
		rootTreeNode = new DefaultMutableTreeNode(rootFileNode);
		TreeModel.setRoot(rootTreeNode);
		UpdateNode(rootTreeNode);
		TreeModel.reload();

		Tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) Tree.getLastSelectedPathComponent();
				try{
					UpdateNode(selectedNode);
					//Update the currently open tab:
					TabbedPanel.changeCurrentTabEvent( (FileNode) selectedNode.getUserObject());
				}
				catch(NullPointerException ex){}
			}
		});
		
		//SET TREE ICONS:
		Tree.setCellRenderer(new DefaultTreeCellRenderer() {
		@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
				Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
				setIcon(((FileNode)((DefaultMutableTreeNode)value).getUserObject()).getSystemIcon());
				return c;
			}
		});
	}
	
	//This is called when the directory of the MainPanel changes. It expands all
	//the tree's paths until the selected directory, which is represented by the
	//<path> parameter.
	public static void setPath(String path){
		FileNode dir = new FileNode(path);
		if(!dir.isDirectory())
			return;
		
		/*Generally, if I expand the tree in order to view the folder "example" 
		using expandPath, the tree will expand untill all the siblings of the 
		"example" are shown. BUT, the "example"'s children, if any, will not be
		shown. I want to view the children as well, so instead of "exapmle"'s
		path, I will use the path of one of its children.*/
		try{path = dir.getChildren(1).get(0).path();} //Without this, the tree would show only the dir's siblings, the dir would not be expanded.
		catch(Exception e){}
		
		DefaultMutableTreeNode current = rootTreeNode, child;
		String paths[] = treePathNames(path); //if <path>=="A/B/C" then paths[0]="A", paths[1]="A/B", paths[2]="A/B/C"
		TreePath treePath;
		//collapsePaths(new javax.swing.tree.TreePath(TreeModel.getPathToRoot(rootTreeNode))); //Unused because it slows down the explorer.
		
		for(int i=0; i<paths.length; i++){
			try{UpdateNode(current);}
			catch(NullPointerException ex){}
			
			if(current.getChildCount()==0){
				System.err.println("No Children!!!!!!!!!!!!!!");
				return;
			}
			
			for(int j=0; j<TreeModel.getChildCount(current); j++){
				child = (DefaultMutableTreeNode) TreeModel.getChild(current, j);
				if(child==null){
					System.err.println("ERRORRRRRRRR");
					return;
				}
				if( ((FileNode) child.getUserObject()).path().equals(paths[i])){
					current = child;
					break;
				}
				if(j==TreeModel.getChildCount(current)){
					System.err.println("ERROR: Tree child not found");
				}
			}
			treePath = new javax.swing.tree.TreePath(TreeModel.getPathToRoot(current));
			Tree.expandPath(treePath);
		}
	}
    
	//Create the tree nodes for all the <parent>'s children folders.
    private static void UpdateNode(DefaultMutableTreeNode parent) throws NullPointerException{
		DefaultMutableTreeNode c;
		if(parent==null)
			return;

		FileNode fileNode = (FileNode) parent.getUserObject();
		if(fileNode==null || !fileNode.isDirectory())
			return;
		
		if(fileNode.getChildren(1)!=null){ //Does <parent> have children that are directories?
			for(FileNode dirChild: fileNode.getChildren(1)){
				if(showsHiddenFiles==false && dirChild.hidden())
					continue;
				c = new DefaultMutableTreeNode(dirChild);
				parent.add(c);
			}
		}
    }
	
	//Return an array of Strings containing the names of folders that lead to the final <path>
	private static String[] treePathNames(String path){
		String names[] = path.replace("/", "\\").split("\\\\");
		
		if(names.length>1){
			names[0]+="\\";
			names[1]=names[0]+names[1];
		}
		for(int i=0; i<names.length; i++){
			if(i>1){
				names[i] = names[i-1]+"\\"+names[i];
			}
		}
		return names;
	}
	
	//Create right click menu
	private static void setMouseListener(javax.swing.JComponent comp){
		comp.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if(e.getButton()==3)
					RightClickMenu.create(e, null, null, RightClickMenu.TREE);
			}
		});
	}
	
	//Collapse all paths until we reach <parent>
	protected static void collapsePath(TreePath parent) {
		if(parent == null){ //if parent==null, view all the drives.
			FileNode drives[] = FileNode.getRoots();
			Tree.setRootVisible(showsRoot);
			for(int i=0; i<drives.length; i++){
				parent = new javax.swing.tree.TreePath(TreeModel.getPathToRoot(rootTreeNode.getChildAt(i)));
				TreeNode node = (TreeNode) parent.getLastPathComponent();
				if (node.getChildCount() >= 0) {
					for(java.util.Enumeration e=node.children(); e.hasMoreElements();) {
						TreeNode n = (TreeNode) e.nextElement();
						TreePath path = parent.pathByAddingChild(n);
						collapsePath(path);
					}
				}
				Tree.collapsePath(parent);
			}
			return;
		}
		
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for(java.util.Enumeration e=node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				collapsePath(path);
			}
		}
		Tree.collapsePath(parent);
	}
}
