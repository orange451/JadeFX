package io.jadefx.scene.layout;

import io.jadefx.geometry.Insets;
import io.jadefx.scene.Node;
import io.jadefx.scene.Parent;

public abstract class Region extends Parent {
	protected Insets border = Insets.EMPTY;
	
	/**
	 * Set the padding insets of this node. All child nodes will be offset based on the insets.
	 * @param value
	 */
    public final void setPadding(Insets value) { padding = value; }
    
    /**
     * 
     * @return Return the padding insets of this node.
     */
    public final Insets getPadding() { return padding; }
    
    /**
     * Set the border insets of this node. All child nodes will be offset based on the insets.
     * @param value
     */
    public final void setBorder(Insets value) { border = value; }
    
    /**
     * 
     * @return Return the padding insets of this node.
     */
    public final Insets getBorder() { return border; }

    /**
     * Convenience method to set the 4 borders based on 1 value.
     * @param width
     */
	public void setBorderWidth(float width) {
		this.setBorder(new Insets(width));
	}
	
	/**
	 * Returns the average of the 4 border widths.
	 * @return
	 */
	public float getBorderWidth() {
		return (float) (this.getBorder().getLeft() + this.getBorder().getRight() + this.getBorder().getTop() + this.getBorder().getBottom())/4f;
	}
	
    /**
     * Convenience method to set the 4 borders based on 2 values.
     * @param width
     */
	public void setBorderWidth(float width, float height) {
		this.setBorder(new Insets(height, width, height, width));
		this.setFlag(FLAG_CSS_DIRTY);
	}
	
	@Override
	public LayoutBounds getInnerBounds() {
		LayoutBounds original = super.getInnerBounds();
		LayoutBounds bounds = new LayoutBounds(
				original.getX() + (padding.getLeft()+border.getLeft()),
				original.getY() + (padding.getTop()+border.getTop()),
				original.getX() + original.getWidth() - (padding.getRight()+border.getRight()),
				original.getY() + original.getHeight() - (padding.getBottom()+border.getBottom()));
		
		return bounds;
	}
	
	
	@Override
	protected void sizePreferred() {
		super.sizePreferred();
		
		float maxWidthInside = (float) (getMaxElementWidth()+getPadding().getWidth()+getBorder().getWidth());
		maxWidthInside = (float) Math.max(maxWidthInside, this.computePrefWidth());
		size.x = maxWidthInside;
		
		// Fit this pane to the height of its elements.
		float maxHeightInside = (float) (getMaxElementHeight()+getPadding().getHeight()+getBorder().getHeight());
		maxHeightInside = (float) Math.max(maxHeightInside, this.computePrefHeight());
		size.y = maxHeightInside;
	}

	
	/**
	 * Get the width of the widest element inside this node.
	 * @return
	 */
	protected double getMaxElementWidth() {
		double runningX = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child == null )
				continue;

			double tempSize = child.getWidth();
			if ( child.getPrefWidthRatio() != null && child.getPrefWidthRatio().getValue() > 0)
				tempSize = Math.max(child.getPrefWidth(), child.getMinWidth());
			
			runningX = Math.max(runningX, tempSize);
		}
		
		return runningX;
	}
	
	/**
	 * Get the height of the highest element inside this node.
	 * @return
	 */
	protected double getMaxElementHeight() {
		double runningY = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child == null )
				continue;
			
			double tempSize = child.getHeight();
			if ( child.getPrefHeightRatio() != null && child.getPrefHeightRatio().getValue() > 0)
				tempSize = Math.max(child.getPrefHeight(), child.getMinHeight());

			runningY = Math.max(runningY, tempSize);
		}
		
		return runningY;
	}
	
	/**
	 * Returns the widths of all direct children added together.
	 * Treats fillable regions that stretch as size 0.
	 * @return
	 */
	protected double getCombinedElementWidth() {
		double totalSize = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double tempSize = child.getWidth();
			
			if ( child.getPrefWidthRatio() != null && child.getPrefWidthRatio().getValue() > 0)
				tempSize = Math.max(child.getPrefWidth(), child.getMinWidth());
		
			totalSize += tempSize;
		}
		
		return totalSize;
	}
	
	/**
	 * Returns the heights of all direct children added together.
	 * Treats fillable regions that stretch as size 0.
	 * @return
	 */
	protected double getCombinedElementHeight() {
		double totalSize = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double tempSize = child.getHeight();
			
			if ( child.getPrefHeightRatio() != null && child.getPrefHeightRatio().getValue() > 0)
				tempSize = Math.max(child.getPrefHeight(), child.getMinHeight());
		
			totalSize += tempSize;
		}
		
		return totalSize;
	}
}
