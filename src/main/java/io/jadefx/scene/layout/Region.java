package io.jadefx.scene.layout;

import io.jadefx.geometry.Insets;
import io.jadefx.scene.Node;
import io.jadefx.scene.Parent;

public abstract class Region extends Parent {
	protected Insets border = Insets.EMPTY;
	
	protected Insets padding = Insets.EMPTY;
	
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
	protected double computePrefWidth() {
		float maxWidthInside = (float) (getMaxElementWidth()+getPadding().getWidth()+getBorder().getWidth());
		return Math.max(super.computePrefWidth(), maxWidthInside);
	}
	
	@Override
	protected double computePrefHeight() {
		float maxHeightInside = (float) (getMaxElementHeight()+getPadding().getHeight()+getBorder().getHeight());
		return Math.max(super.computePrefHeight(), maxHeightInside);
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
