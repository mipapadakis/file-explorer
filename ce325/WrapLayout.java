/*
Description:
Used to represent all the IconsPanel panels correctly. Without this, they would
get out of the frame's boundaries.
*/

package ce325;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class WrapLayout extends FlowLayout{
	public WrapLayout(){
		super();
	}
	public WrapLayout(int align){
		super(align);
	}
	
	@Override
	public Dimension preferredLayoutSize(Container target){
		return layoutSize(target, true);
	}
	
	@Override
	public Dimension minimumLayoutSize(Container target){
		Dimension minimum = layoutSize(target, false);
		minimum.width -= (getHgap() + 1);
		return minimum;
	}
	private Dimension layoutSize(Container target, boolean preferred){
		synchronized (target.getTreeLock()){
			//Each row must fit with the width allocated to the containter.
			//When the container width = 0, the preferred width of the container
			//has not yet been calculated so lets ask for the maximum.

			//int targetWidth = target.getSize().width;
			Container container = target;

			while (container.getSize().width == 0 && container.getParent() != null){
				container = container.getParent();
			}

			int targetWidth = container.getSize().width;

			if (targetWidth == 0)
				targetWidth = Integer.MAX_VALUE;

			int hgap = getHgap();
			int vgap = getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
			int maxWidth = targetWidth - horizontalInsetsAndGap;

			//Fit components into the allowed width
			Dimension dim = new Dimension(0, 0);
			int rowWidth = 0;
			int rowHeight = 0;

			int nmembers = target.getComponentCount();
			for (int i = 0; i < nmembers; i++){
				Component m = target.getComponent(i);

				if (m.isVisible()){
					Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

					//Can't add the component to current row. Start a new row.
					if (rowWidth + d.width > maxWidth){
						addRow(dim, rowWidth, rowHeight);
						rowWidth = 0;
						rowHeight = 0;
					}

					//Add a horizontal gap for all components after the first
					if (rowWidth != 0)
						rowWidth += hgap;

					rowWidth += d.width;
					rowHeight = Math.max(rowHeight, d.height);
				}
			}

			addRow(dim, rowWidth, rowHeight);

			dim.width += horizontalInsetsAndGap;
			dim.height += insets.top + insets.bottom + vgap * 2;

			//When using a scroll pane or the DecoratedLookAndFeel we need to
			//make sure the preferred size is less than the size of the
			//target containter so shrinking the container size works
			//correctly. Removing the horizontal gap is an easy way to do this.
			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

			if (scrollPane != null && target.isValid()){
				dim.width -= (hgap + 1);
			}

			return dim;
		}
	}
	private void addRow(Dimension dim, int rowWidth, int rowHeight){
		dim.width = Math.max(dim.width, rowWidth);

		if (dim.height > 0)
			dim.height += getVgap();
		dim.height += rowHeight;
	}
}
