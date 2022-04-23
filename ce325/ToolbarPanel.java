/*
Description:
Creates the toolbar, which contains many components like Back, Forward, search etc.
*/

package ce325;

import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 *
 * @author minas
 */
public class ToolbarPanel extends javax.swing.JPanel{
	protected static int BACK_BUTTON = 3;
	protected static int FORWARD_BUTTON = 4;
	protected static int UP_BUTTON = 5;
	protected static int CHANGE_VIEW_TO_ICONS = 6;
	protected static int CHANGE_VIEW_TO_LIST = 7;
	protected static int PATH = 8;
	protected static int REFRESH = 9;
	protected static int SEARCH = 10;
	protected static int SEARCH_BUTTON = 11;
	protected static final String BACK_TEXT = " ◄ ";
	protected static final String FORWARD_TEXT = " ► ";
	protected static final String UP_TEXT = " ▲ ";
	protected static final String EMPTY_TEXT = "   ";
	protected static JButton BackButton;
    protected static JButton ForwardButton;
    protected static JFormattedTextField PathTextField;
    protected static JButton RefreshButton;
    protected static JButton SearchButton;
    protected static JFormattedTextField SearchTextField;
    protected static JToolBar Toolbar;
    protected static JButton UpButton;
    protected static JComboBox ViewComboBox;
	
	public ToolbarPanel(){
        super(new java.awt.GridLayout(1, 1));
		
		Toolbar = new JToolBar();
        BackButton = new JButton();
        ForwardButton = new JButton();
        UpButton = new JButton();
        ViewComboBox = new JComboBox();
        PathTextField = new JFormattedTextField();
        RefreshButton = new JButton();
        SearchTextField = new JFormattedTextField();
        SearchButton = new JButton();
        Toolbar.setRollover(true);
		
        BackButton.setText(BACK_TEXT);
        BackButton.setToolTipText("Back");
        BackButton.setFocusable(false);
        BackButton.setHorizontalTextPosition(SwingConstants.CENTER);
        BackButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        BackButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TabbedPanel.newAction(null, BACK_BUTTON);
            }
        });
        Toolbar.add(BackButton);

        ForwardButton.setText(FORWARD_TEXT);
        ForwardButton.setToolTipText("Forward");
        ForwardButton.setFocusable(false);
        ForwardButton.setHorizontalTextPosition(SwingConstants.CENTER);
        ForwardButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        ForwardButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(null, FORWARD_BUTTON);
            }
        });
        Toolbar.add(ForwardButton);
		
        UpButton.setText(UP_TEXT);
        UpButton.setToolTipText("Up");
        UpButton.setFocusable(false);
        UpButton.setHorizontalTextPosition(SwingConstants.CENTER);
        UpButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        UpButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(null, UP_BUTTON);
            }
        });
        Toolbar.add(UpButton);
        Toolbar.add(new JToolBar.Separator());

        ViewComboBox.setModel(new DefaultComboBoxModel(new String[] { "Icons", "List" }));
        ViewComboBox.setToolTipText("Change View");
        BackButton.setFocusable(false);
        ViewComboBox.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
				if(ViewComboBox.getSelectedIndex()==0){
					TabbedPanel.newAction(null, CHANGE_VIEW_TO_ICONS);
				}
				else{
					TabbedPanel.newAction(null, CHANGE_VIEW_TO_LIST);
				}
            }
        });
        Toolbar.add(ViewComboBox);

        PathTextField.setText("");
        PathTextField.setToolTipText("Path");
        //PathTextField.setPreferredSize(new java.awt.Dimension(100,10));
        PathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
				if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER){
					TabbedPanel.newAction(PathTextField.getText(), REFRESH);
					ExplorerFrame.setNeutralFocus();
				}
				else
					TabbedPanel.newAction(PathTextField.getText(), PATH);
            }
        });
        Toolbar.add(PathTextField);

        RefreshButton.setText("↻");
        RefreshButton.setToolTipText("Refresh");
        RefreshButton.setFocusable(false);
        RefreshButton.setHorizontalTextPosition(SwingConstants.CENTER);
        RefreshButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        RefreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabbedPanel.newAction(PathTextField.getText(), REFRESH);
            }
        });
        Toolbar.add(RefreshButton);
        Toolbar.add(new JToolBar.Separator());

        SearchTextField.setText("Search");
        SearchTextField.setToolTipText("Search in current directory");
        SearchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER)
					TabbedPanel.newAction(SearchTextField.getText(), SEARCH_BUTTON);
				else
					TabbedPanel.newAction(SearchTextField.getText(), SEARCH);
            }
        });
		SearchTextField.addFocusListener( new FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent e) {
				SearchTextField.selectAll();
			}
		});
        Toolbar.add(SearchTextField);

        SearchButton.setText("⌕");
        SearchButton.setFocusable(false);
        SearchButton.setToolTipText("Search in sub-folders as well");
        SearchButton.setHorizontalTextPosition(SwingConstants.CENTER);
        SearchButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		SearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
				String input = SearchTextField.getText();
				if(!input.equals(""))
					TabbedPanel.newAction(input, SEARCH_BUTTON);
				else
					TabbedPanel.newAction(null, REFRESH);
            }
        });
        Toolbar.add(SearchButton);
		
		add(Toolbar);
	}
	
	protected static void setComboboxView(int i){ ViewComboBox.setSelectedIndex(i); } //0 for icons, 1 for list
	protected static int getComboboxView(){ return ViewComboBox.getSelectedIndex(); }
}
