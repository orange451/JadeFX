package io.jadefx.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Vector2d;

import io.jadefx.collections.ObservableList;
import io.jadefx.event.Event;
import io.jadefx.event.EventHandler;
import io.jadefx.event.EventHelper;
import io.jadefx.event.MouseEvent;
import io.jadefx.geometry.Orientation;
import io.jadefx.geometry.Pos;
import io.jadefx.stage.Context;
import io.jadefx.style.Percentage;
import io.jadefx.style.PercentageCalc;
import io.jadefx.style.StyleTransition;
import io.jadefx.style.Stylesheet;
import io.jadefx.style.StylesheetCompileError;
import io.jadefx.transition.Transition;

public abstract class Node {
	protected Node parent;
	protected ObservableList<Node> children = new ObservableList<>();
	
	/*
	 * Positioning settings
	 */
	protected Vector2d absolutePosition = new Vector2d(); // ONLY USED INTERNALLY DONT TOUCH
	protected Vector2d translation = new Vector2d();
	protected Vector2d size = new Vector2d();
	protected Vector2d prefsize = new Vector2d();
	protected Percentage prefwidthRatio;
	protected Percentage prefheightRatio;
	protected LayoutBounds layoutBounds = new LayoutBounds(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
	protected Percentage minwidthRatio;
	protected Percentage minheightRatio;
	protected Percentage maxwidthRatio;
	protected Percentage maxheightRatio;
	protected Pos alignment;
	
	/**
	 * Flags
	 */
	private int flags = 0;
	public final int FLAG_CSS_DIRTY			= 1;
	public final int FLAG_LAYOUT_DIRTY		= 2;
	public final int FLAG_SIZE_DIRTY		= 4;
	
	/**
	 * Styling
	 */
	private String id;
	private Stylesheet stylesheet;
	private String localStyle;
	private Stylesheet localStylesheet;
	private Map<String, StyleTransition> styleTransitions = new HashMap<>();
	private Map<String, Node> idToNode = new HashMap<>();
	private List<Node> descendents = new ObservableList<Node>();
	private ObservableList<String> classList = new ObservableList<String>();
	private Scene scene;
	
	/**
	 * Events
	 */
	protected EventHandler<MouseEvent> mousePressedEvent;
	protected EventHandler<MouseEvent> mousePressedEventInternal;
	
	protected EventHandler<MouseEvent> mouseReleasedEvent;
	protected EventHandler<MouseEvent> mouseReleasedEventInternal;
	
	protected EventHandler<MouseEvent> mouseClickedEvent;
	protected EventHandler<MouseEvent> mouseClickedEventInternal;
	
	protected EventHandler<Event> mouseEnteredEvent;
	protected EventHandler<Event> mouseEnteredEventInternal;
	
	protected EventHandler<Event> mouseExitedEvent;
	protected EventHandler<Event> mouseExitedEventInternal;
	
	/**
	 * Other
	 */
	private boolean mouseTransparent = false;
	private Vector2d cachedAvailableSize;
	private LayoutBounds innerBounds;
	
	public Node() {
		this.setAlignment(Pos.ANCESTOR);
		
		children.setAddCallback((e)->{
			this.dirty();
			e.setParent(this);
		});
		
		children.setRemoveCallback((e)->{
			this.dirty();
		});
		
		classList.setAddCallback((e)->{
			this.setFlag(FLAG_CSS_DIRTY);
		});
		
		classList.setRemoveCallback((e)->{
			this.setFlag(FLAG_CSS_DIRTY);
		});
	}
	
	protected void dirty() {
		this.setFlag(FLAG_CSS_DIRTY | FLAG_LAYOUT_DIRTY | FLAG_SIZE_DIRTY);
		//this.setFlag(FLAG_LAYOUT_DIRTY);
		//this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Return the modifiable list of children.
	 * @return
	 */
	protected ObservableList<Node> getChildren() {
		return this.children;
	}
	
	/**
	 * Code ran to position a node based on supplied parent
	 */
	Vector2d lastSize = new Vector2d();
	protected void position() {
		stylePush();
		{
			lastSize.set(this.size);
			
			size();
			
			if ( hasFlag(FLAG_SIZE_DIRTY) )
				sizeChildren();

			if ( !hasFlag(FLAG_LAYOUT_DIRTY) )
				layoutChildren();
			
			this.resetFlag(FLAG_LAYOUT_DIRTY);
			this.resetFlag(FLAG_SIZE_DIRTY);
			this.resetFlag(FLAG_CSS_DIRTY);
			
			int buffer = this.getScene().getContext().isFlushed() ? 2 : 1; // TODO find a way to not require double buffering
			for (int k = 0; k < buffer; k++) {
				
				for (int i = 0; i < this.children.size(); i++) {
					Node node = this.children.get(i);
					node.position();
				}
			}
		}
		stylePop();
	}

	/**
	 * Position the children in this node.
	 */
	protected void layoutChildren() {
		for (int i = 0; i < this.children.size(); i++) {
			Node node = this.children.get(i);

			node.setLocalPosition(getTranslateX(), getTranslateY());
		}
	}
	
	/**
	 * Generic size routine. Sizes based on preferred size. Afterwards sizes based on min/max.
	 * If the size changes at the end of this, the size dirty flag is marked as true.
	 */
	protected void size() {
		this.sizePreferred();
		this.sizeMinMax();
		
		if (!this.size.equals(this.lastSize)) {
			this.setFlag(FLAG_SIZE_DIRTY);
			this.setFlag(FLAG_LAYOUT_DIRTY);
			this.cachedAvailableSize = null;
			
			if ( this.parent != null ) {
				this.parent.setFlag(FLAG_LAYOUT_DIRTY);
				this.parent.setFlag(FLAG_SIZE_DIRTY);
				this.parent.cachedAvailableSize = null;
			}
		}
	}
	
	/**
	 * Logic to size the node based on its preferred sizing. Also limits based on what sizing is available.
	 */
	protected void sizePreferred() {
		double prefWidth = this.computePrefWidth();
		double prefHeight = this.computePrefHeight();
		
		Vector2d available = this.getAvailableSize();
		double availableWidth = available.x;
		double availableHeight = available.y;

		// We dont care about what is available if it's bigger than we /want/ to be
		availableWidth = Math.min(prefWidth, availableWidth);
		availableHeight = Math.min(prefHeight, availableHeight);
		
		// Clamp to pref size and available max size
		sizeClamp(prefWidth, prefHeight, availableWidth, availableHeight);
	}
	
	/**
	 * Get the actual preferred width with respect to fill ratios.
	 */
	protected double computePrefWidth() {
		return computeMinSize(this.getPrefWidth(), this.getPrefWidthRatio(), Orientation.HORIZONTAL);
	}
	
	/**
	 * Get the actual preferred height with respect to fill ratios.
	 */
	protected double computePrefHeight() {
		return computeMinSize(this.getPrefHeight(), this.getPrefHeightRatio(), Orientation.VERTICAL);
	}
	
	/**
	 * Get the min size based on a pre-defined pixel amount or a percentage of the parent size.
	 */
	protected double computeMinSize(double desiredSizePixels, Percentage desiredParentPercentage, Orientation orientation) {
		double minSize = desiredSizePixels;
		
		if ( desiredParentPercentage != null ) {
			Vector2d available = this.getAvailableSize();			
			double availableSize = orientation == Orientation.HORIZONTAL ? available.x : available.y;
			
			if ( desiredParentPercentage instanceof PercentageCalc )
				((PercentageCalc)desiredParentPercentage).setReferenceSize(availableSize);
			
			minSize = Math.max(minSize, availableSize * desiredParentPercentage.getValueClamped());
		}
		
		return minSize;
	}

	/**
	 * Get the max size based on a pre-defined pixel amount or a percentage of the parent size.
	 */
	protected double computeMaxSize(double desiredSizePixels, Percentage desiredParentPercentage, Orientation orientation) {
		double maxSize = desiredSizePixels;
		
		if ( desiredParentPercentage != null ) {
			Vector2d available = this.getAvailableSize();
			double availableSize = orientation == Orientation.HORIZONTAL ? available.x : available.y;
			
			if ( desiredParentPercentage instanceof PercentageCalc )
				((PercentageCalc)desiredParentPercentage).setReferenceSize(availableSize);
			
			maxSize = Math.min(maxSize, availableSize * desiredParentPercentage.getValueClamped());
		}
		
		return maxSize;
	}
	
	/**
	 * Size the node based on its min/max
	 */
	protected void sizeMinMax() {
		double minWidth = computeMinSize(this.getMinWidth(), this.getMinWidthRatio(), Orientation.HORIZONTAL);
		double minHeight = computeMinSize(this.getMinHeight(), this.getMinHeightRatio(), Orientation.VERTICAL);
		double maxWidth = computeMaxSize(this.getMaxWidth(), this.getMaxWidthRatio(), Orientation.HORIZONTAL);
		double maxHeight = computeMaxSize(this.getMaxHeight(), this.getMaxHeightRatio(), Orientation.VERTICAL);
		
		boolean dirty = sizeClamp(minWidth, minHeight, maxWidth, maxHeight);
		
		if ( dirty )
			this.setFlag(FLAG_SIZE_DIRTY);
	}

	/**
	 * Size all direct child nodes.
	 */
	private void sizeChildren() {
		for (int i = 0; i < this.children.size(); i++) {
			Node node = this.children.get(i);
			node.size();
		}
	}
	
	/**
	 * Clamp size between specified bounds. 
	 * Returns true if size was modified.
	 */
	private boolean sizeClamp(double minWidth, double minHeight, double maxWidth, double maxHeight) {
		boolean resized = false;
		
		// Cap size to min size
		if ( size.x < minWidth ) {
			size.x = minWidth;
			resized = true;
		}
		
		if ( size.y < minHeight ) {
			size.y = minHeight;
			resized = true;
		}

		// Cap size to max size
		if ( size.x > maxWidth ) {
			size.x = maxWidth;
			resized = true;
		}
		
		if ( size.y > maxHeight ) {
			size.y = maxHeight;
			resized = true;
		}
		
		return resized;
	}
	
	protected void setParent(Node parent) {
		Node oldParent = this.parent;
		this.parent = parent;
		
		// If parents change...
		if ( oldParent != parent ) {
			
			// Remove us from old parent
			if ( oldParent != null )
				unregisterFromParent(this, oldParent);
			
			// Add us to new parent
			registerToParent(this, parent);
			
			// Mark dirty
			this.dirty();
			
			// Find new scene
			Scene sc = null;
			Node p = this.parent;
			int t = 0;
			while(p != null && t < 64) {
				if ( p instanceof Scene ) {
					sc = (Scene) p;
					break;
				}
				p = p.parent;
				t++;
			}
			
			this.scene = sc;
			
			// Update descendants
			for (Node node : descendents) {
				node.scene = scene;
			}
		}
		
		if ( parent instanceof Scene ) {
			parent.scene = (Scene) parent;
		}
		
		// Register our element ID
		registerElementIDToParent(this, parent);
		
		// Reset scene
		if ( parent == null ) {
			this.scene = null;
			this.dirty();
		}
	}
	
	protected void forceSize(double width, double height) {
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
		this.setPrefSize(width, height);
	}
	
	/**
	 * Return a vector containing the available size derived from the parent node.
	 * @return Vector2d
	 */
	public Vector2d getAvailableSize() {
		//if ( cachedAvailableSize != null )
			//return cachedAvailableSize;
		
		cachedAvailableSize = new Vector2d(getMaxPotentialWidth(), getMaxPotentialHeight());
		return cachedAvailableSize;
	}
	
	protected double getMaxPotentialWidth() {
		double max = Integer.MAX_VALUE;
		Node p = this.parent;
		
		if ( p != null ) {
			double use = p.getMaxWidth();
			if ( use > Integer.MAX_VALUE*0.5 )
				use = p.getWidth();
			
			max = Math.min(max, use);
		}
		
		max = Math.max(max, getMinWidth());
		
		return max;
	}
	
	protected double getMaxPotentialHeight() {
		double max = Integer.MAX_VALUE;
		Node p = this.parent;
		
		if ( p != null ) {
			double use = p.getMaxHeight();
			if ( use > Integer.MAX_VALUE*0.5 )
				use = p.getHeight();
			
			max = Math.min(max, use);
		}
		
		max = Math.max(max, getMinHeight());
		
		return max;
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

			double tempSize = Math.max(child.getWidth(), Math.min(child.computePrefWidth(), this.getMaxWidth()));
			if ( child.getPrefWidthRatio() != null && child.getPrefWidthRatio().getValue() > 0)
				tempSize = Math.min(this.getMaxWidth(), Math.max(child.getPrefWidth(), child.getMinWidth()));
			
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

			double tempSize = Math.max(child.getHeight(), Math.min(child.computePrefHeight(), this.getMaxHeight()));
			if ( child.getPrefHeightRatio() != null && child.getPrefHeightRatio().getValue() > 0)
				tempSize = Math.min(this.getMaxHeight(), Math.max(child.getPrefHeight(), child.getMinHeight()));

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
			
			boolean hasRatioFill = child.getPrefWidthRatio() != null;
			
			double tempSize;
			if ( hasRatioFill )
				tempSize = Math.max(child.getPrefWidth(), child.getMinWidth());
			else
				tempSize = Math.max(child.getWidth(), Math.min(child.computePrefWidth(), this.getMaxWidth()));
			
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
			
			boolean hasRatioFill = child.getPrefHeightRatio() != null;
					
			double tempSize;
			if ( hasRatioFill )
				tempSize = Math.max(child.getPrefHeight(), child.getMinHeight());
			else
				tempSize = Math.max(child.getHeight(), Math.min(child.computePrefHeight(), this.getMaxHeight()));
			
			totalSize += tempSize;
		}
		
		return totalSize;
	}
	
	/**
	 * Force the absolute position of a node.
	 */
	private void setAbsolutePosition(double x, double y) {
		if ( getX() == x && getY() == y )
			return;
		
		this.absolutePosition.x = x;
		this.absolutePosition.y = y;
		this.setFlag(FLAG_LAYOUT_DIRTY);
	}
	
	/**
	 * Positions the node at a given x/y offset from its parent node.
	 */
	public void setLocalPosition(double x, double y) {
		LayoutBounds bounds = parent.getInnerBounds();
		
		float topLeftX = (float) (parent.getAbsolutePosition().x + bounds.minX);
		float topLeftY = (float) (parent.getAbsolutePosition().y + bounds.minY);
		
		double changex = (topLeftX + x)-getX();
		double changey = (topLeftY + y)-getY();
		
		setAbsolutePosition( this.getX()+changex, this.getY()+changey);
	}

	protected Vector2d getAbsolutePosition() {
		if ( this.absolutePosition == null )
			return new Vector2d();
		return this.absolutePosition;
	}

	/**
	 * Return the current width of the node.
	 * @return
	 */
	public double getWidth() {
		return size.x;
	}
	
	/**
	 * Return the current height of the node.
	 * @return
	 */
	public double getHeight() {
		return size.y;
	}
	
	/**
	 * Return the parent node.
	 * @return
	 */
	public Node getParent() {
		return this.parent;
	}
	
	/**
	 * Set the layout alignment.
	 * @param pos
	 */
	protected void setAlignment(Pos pos) {
		this.alignment = pos;
	}
	
	/**
	 * Return a bounds fit to the size of the node.
	 * @return
	 */
	public LayoutBounds getInnerBounds() {
		if ( innerBounds == null )
			innerBounds = new LayoutBounds(0,0,0,0);
		
		innerBounds.minX = 0;
		innerBounds.minY = 0;
		innerBounds.maxX = getWidth();
		innerBounds.maxY = getHeight();
		
		return innerBounds;
	}
	
	/**
	 * Return the layout alignment used to position this node.
	 */
	public Pos usingAlignment() {
		Pos useAlignment = null;
		Node p = this.getParent();
		int t = 0;
		while ( p != null && (useAlignment == null || useAlignment == Pos.ANCESTOR ) && t < 32 ) {
			useAlignment = p.getAlignment();
			p = p.parent;
			t++;
		}
		
		if ( useAlignment == null || useAlignment == Pos.ANCESTOR )
			return Pos.CENTER;
		
		return useAlignment;
	}
	
	/**
	 * Return the current layout alignment.
	 * @return
	 */
	protected Pos getAlignment() {
		return this.alignment;
	}
	
	/**
	 * Return the absolute x position of this node.
	 * @return
	 */
	public double getX() {
		if ( absolutePosition == null )
			absolutePosition = new Vector2d();
		
		return absolutePosition.x;
	}
	
	/**
	 * Return the absolute y position of this node.
	 * @return
	 */
	public double getY() {
		if ( absolutePosition == null )
			absolutePosition = new Vector2d();
		
		return absolutePosition.y;
	}
	
	/**
	 * Defines the x coordinate of the translation that is added to this Node's transform.
	 */
	public double getTranslateX() {
		_ensureTranslation();
		return translation.x;
	}

	/**
	 * Defines the x coordinate of the translation that is added to this Node's transform.
	 */
	public void setTranslateX(double value) {
		_ensureTranslation();
		if ( value == translation.x )
			return;
		
		translation.x = value;
		this.setFlag(FLAG_LAYOUT_DIRTY);
	}
	
	/**
	 * Defines the y coordinate of the translation that is added to this Node's transform.
	 */
	public double getTranslateY() {
		_ensureTranslation();
		return translation.y;
	}
	
	/**
	 * Defines the y coordinate of the translation that is added to this Node's transform.
	 */
	public void setTranslateY(double value) {
		_ensureTranslation();
		if ( value == translation.y )
			return;
		
		translation.y = value;
		this.setFlag(FLAG_LAYOUT_DIRTY);
	}
	
	private void _ensureTranslation() {
		if ( translation == null )
			translation = new Vector2d();
	}
	
	private boolean isRatioSimilar(Percentage p1, Percentage p2) {
		if ( p1 == null && p2 != null )
			return false;
		
		if ( p1 != null && p2 == null )
			return false;
		
		if ( p1.getValue() == p2.getValue() )
			return true;
		
		if ( p1.equals(p2) )
			return true;
		
		double x = Math.abs(p1.getValue()-p2.getValue());
		return x < 0.01;
	}
	
	/**
	 * Return the minimum width of this node.
	 * @return
	 */
	public double getMinWidth() {
		return layoutBounds.minX;
	}
	
	/**
	 * Returns the min width ratio of this node as a percentage.
	 * @return
	 */
	public Percentage getMinWidthRatio() {
		return this.minwidthRatio;
	}
	
	/**
	 * Return the minimum height of this node.
	 * @return
	 */
	public double getMinHeight() {
		return layoutBounds.minY;
	}
	
	/**
	 * Returns the min height ratio of this node as a percentage.
	 * @return
	 */
	public Percentage getMinHeightRatio() {
		return this.minheightRatio;
	}
	
	/**
	 * Return the maximum width of this node.
	 * @return
	 */
	public double getMaxWidth() {
		return layoutBounds.maxX;
	}
	
	/**
	 * Returns the max width ratio of this node as a percentage.
	 * @return
	 */
	public Percentage getMaxWidthRatio() {
		return this.maxwidthRatio;
	}
	
	/**
	 * Return the maximum height of this node.
	 * @return
	 */
	public double getMaxHeight() {
		return layoutBounds.maxY;
	}
	
	/**
	 * Returns the max height ratio of this node as a percentage.
	 * @return
	 */
	public Percentage getMaxHeightRatio() {
		return this.maxheightRatio;
	}
	
	/**
	 * Set the preferred size of this node.
	 * <br>
	 * This size is not guaranteed.
	 * @param width
	 * @param height
	 */
	public void setPrefSize( double width, double height ) {
		setPrefWidth( width );
		setPrefHeight( height );
	}
	
	/**
	 * Set the absolute minimum size of this node.
	 * @param width
	 * @param height
	 */
	public void setMinSize( double width, double height ) {
		setMinWidth(width);
		setMinHeight(height);
	}
	
	/**
	 * Set the absolute maximum size of this node.
	 * @param width
	 * @param height
	 */
	public void setMaxSize( double width, double height ) {
		setMaxWidth(width);
		setMaxHeight(height);
	}
	
	/**
	 * Set the preferred width of this node.
	 * <br>
	 * This size is not guaranteed.
	 * @param width
	 */
	public void setPrefWidth( double width ) {
		if ( this.prefsize == null )
			return;
		
		if (this.prefsize.x == width)
			return;
		
		this.prefsize.x = width;
		if (this.getMinWidth() > 0) {
			width = Math.max(layoutBounds.minX, Math.min(layoutBounds.maxX, width));
		}
		this.size.x = width;
		this.setFlag(FLAG_SIZE_DIRTY);
		
		if ( this.parent != null )
			this.parent.setFlag(FLAG_LAYOUT_DIRTY);
	}
	
	/**
	 * Set the preferred width ratio of this node.
	 * 1.0 will be the size of the parent nodes width.
	 * 0.5 will be half the size of the parent nodes width.
	 * @param ratio
	 */
	public void setPrefWidthRatio( Percentage ratio ) {
		if ( isRatioSimilar(prefwidthRatio, ratio) )
			return;
		
		this.prefwidthRatio = ratio;
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the preferred height of this node.
	 * <br>
	 * This size is not guaranteed.
	 * @param width
	 */
	public void setPrefHeight( double height ) {
		if ( this.prefsize == null )
			return;
		
		if (this.prefsize.y == height)
			return;
		
		this.prefsize.y = height;
		if ( this.getMinHeight() > 0 ) {
			height = Math.max(layoutBounds.minY, Math.min(layoutBounds.maxY, height));
		}
		this.size.y = height;
		this.setFlag(FLAG_SIZE_DIRTY);
		
		if ( this.parent != null )
			this.parent.setFlag(FLAG_LAYOUT_DIRTY);
	}
	
	/**
	 * Set the preferred height ratio of this node.
	 * 1.0 will be the size of the parent nodes height.
	 * 0.5 will be half the size of the parent nodes height.
	 * @param ratio
	 */
	public void setPrefHeightRatio( Percentage ratio ) {
		if ( isRatioSimilar(prefheightRatio, ratio) )
			return;
		
		this.prefheightRatio = ratio;
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Returns the preferred width of this node.
	 * @return
	 */
	public double getPrefWidth() {
		return prefsize.x;
	}
	
	/**
	 * Returns the preferred width ratio of this node as a percentage.
	 * @return
	 */
	public Percentage getPrefWidthRatio() {
		return this.prefwidthRatio;
	}
	
	/**
	 * Returns the preferred height of this node.
	 * @return
	 */
	public double getPrefHeight() {
		return prefsize.y;
	}
	
	/**
	 * Returns the preferred height ratio of this node as a percentage.
	 * @return
	 */
	public Percentage getPrefHeightRatio() {
		return this.prefheightRatio;
	}
	
	/**
	 * Set the minimum width of this node.
	 * @param width
	 */
	public void setMinWidth( double width ) {
		if ( width < 0 )
			width = 0;
		
		if (layoutBounds.minX == width)
			return;
		
		layoutBounds.minX = width;
		if ( size.x < width )
			size.x = width;
		
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the preferred width ratio of this node.
	 * 1.0 will be the size of the parent nodes width.
	 * 0.5 will be half the size of the parent nodes width.
	 * @param ratio
	 */
	public void setMinWidthRatio( Percentage ratio ) {
		if ( isRatioSimilar(minwidthRatio, ratio) )
			return;
		
		this.minwidthRatio = ratio;
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the minimum height of this node.
	 * @param height
	 */
	public void setMinHeight( double height ) {
		if ( height < 0 )
			height = 0;
		
		if (layoutBounds.minY == (int)height)
			return;
		
		layoutBounds.minY = (int)height;
		if ( size.y < (int)height )
			size.y = (int)height;
		
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the preferred width ratio of this node.
	 * 1.0 will be the size of the parent nodes width.
	 * 0.5 will be half the size of the parent nodes width.
	 * @param ratio
	 */
	public void setMinHeightRatio( Percentage ratio ) {
		if ( isRatioSimilar(minheightRatio, ratio) )
			return;
		
		this.minheightRatio = ratio;
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the maximum width of this node.
	 * @param width
	 */
	public void setMaxWidth( double width ) {
		if ( width < 0 )
			width = 0;
		
		if (layoutBounds.maxX == width)
			return;
		
		layoutBounds.maxX = width;
		if ( size.x > width )
			size.x = width;
		
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the preferred width ratio of this node.
	 * 1.0 will be the size of the parent nodes width.
	 * 0.5 will be half the size of the parent nodes width.
	 * @param ratio
	 */
	public void setMaxWidthRatio( Percentage ratio ) {
		if ( isRatioSimilar(maxwidthRatio, ratio) )
			return;
		
		this.maxwidthRatio = ratio;
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the maxmimum height of this node.
	 * @param height
	 */
	public void setMaxHeight( double height ) {
		if ( height < 0 )
			height = 0;
		
		if (layoutBounds.maxY == height)
			return;
		
		layoutBounds.maxY = height;
		if ( size.y > height )
			size.y = height;
		
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Set the preferred width ratio of this node.
	 * 1.0 will be the size of the parent nodes width.
	 * 0.5 will be half the size of the parent nodes width.
	 * @param ratio
	 */
	public void setMaxHeightRatio( Percentage ratio ) {
		if ( isRatioSimilar(maxheightRatio, ratio) )
			return;
		
		this.maxheightRatio = ratio;
		this.setFlag(FLAG_SIZE_DIRTY);
	}
	
	/**
	 * Resets a flag if it is enabled.
	 */
	protected void resetFlag(int flag) {
		if ( hasFlag(flag) )
			flags &= ~flag;
	}
	
	/**
	 * Sets a flag to true, and updates all child nodes.
	 */
	protected void setFlag(int flag) {
		flags |= flag;
		
		for (int i = 0; i < this.children.size(); i++) {
			if ( i >= this.children.size() )
				continue;
			
			Node child = this.children.get(i);
			if ( child == null )
				continue;
			
			child.setFlag(flag);
		}
		
		// If a flag changes, force a re-draw in the scene
		if ( this.getScene() != null && this.getScene().getContext() != null )
			this.getScene().getContext().flush();
	}
	
	/**
	 * Check if node contains flag.
	 */
	public boolean hasFlag(int flag) {
		return (flags & flag) > 0;
	}
	
	/**
	 * Get the scene this node belongs to.
	 * @return
	 */
	public Scene getScene() {
		return this.scene;
	}
	
	/**
	 * Apply our style to the current stack
	 */
	List<Transition> tranniesToRemove = new ArrayList<>();
	protected void stylePush() {
		if ( this.scene == null )
			return;
		
		Context context = this.scene.getContext();
		if ( context != null ) {
			
			for (Entry<String, StyleTransition> entry : styleTransitions.entrySet()) {
				StyleTransition transition = entry.getValue();
				List<Transition> innerTrannies = transition.getTransitions();
				tranniesToRemove.clear();
				
				for (int i = 0; i < innerTrannies.size(); i++) {
					if ( i >= innerTrannies.size() )
						continue;
					
					Transition innerTrans = innerTrannies.get(i);
					if ( innerTrans == null )
						continue;
					
					if ( innerTrans.isFinished() ) {
						tranniesToRemove.add(innerTrans);
						continue;
					}
					
					this.dirty();
				}
				
				for (Transition tranny : tranniesToRemove) {
					innerTrannies.remove(tranny);
				}
			}
			
			// Add our sheet to the stack
			if ( this.getStylesheet() != null )
				context.getCurrentStyling().add(this.getStylesheet());
			
			// Apply styling!
			if (this.hasFlag(FLAG_CSS_DIRTY))
				for (int i = 0; i < context.getCurrentStyling().size(); i++)
					context.getCurrentStyling().get(i).applyStyling(this);
			
			// Apply our local style if it exists
			if (this.hasFlag(FLAG_CSS_DIRTY)) {
				if (this.getStyleLocal() != null) {
					this.getStyleLocal().applyStyling(this, "NODESTYLE");
				}
			}
		}
	}
	
	/**
	 * Remove out style from the current stack
	 */
	protected void stylePop() {
		if ( this.scene == null )
			return;
		
		Context context = this.scene.getContext();
		// Remove our sheet from the stack
		if ( context != null ) {
			if ( this.getStylesheet() != null )
				context.getCurrentStyling().remove(this.getStylesheet());
		}
		
		resetFlag(FLAG_CSS_DIRTY);
	}
	
	/**
	 * Set the stylesheet used to style this node and all descendent nodes to this node.
	 * @param css
	 */
	public void setStylesheet(String css) {
		this.setFlag(FLAG_CSS_DIRTY);
		try {
			Stylesheet style = new Stylesheet(css);
			style.compile();
			this.stylesheet = style;
		} catch(StylesheetCompileError e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the stylesheet used to style this node and all descendent nodes to this node.
	 * @param css
	 */
	public void setStylesheet(Stylesheet css) {
		if ( css != null ) {
			try {
				if ( !css.isCompiled() )
					css.compile();
				this.stylesheet = css;
			} catch(StylesheetCompileError e) {
				e.printStackTrace();
			}
		}
		this.stylesheet = css;
		this.setFlag(FLAG_CSS_DIRTY);
	}
	
	/**
	 * Returns the stylesheet object used to style this node and all descendent nodes to this node.
	 * @return
	 */
	public Stylesheet getStylesheet() {
		return this.stylesheet;
	}
	
	/**
	 * Set the local style used to directly style this node when drawing.
	 * @param localStyle
	 */
	public void setStyle(String localStyle) {
		this.localStyle = localStyle;
		this.setFlag(FLAG_CSS_DIRTY);
		try {
			Stylesheet local = new Stylesheet("NODESTYLE { " + this.localStyle + " }");
			local.compile();
			this.localStylesheet = local;
		} catch(StylesheetCompileError e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the local style used to directly style this node when drawing.
	 * @return
	 */
	public String getStyle() {
		return this.localStyle;
	}
	
	/**
	 * Returns the local stylesheet used to directly style this node when drawing.
	 * @return
	 */
	protected Stylesheet getStyleLocal() {
		return this.localStylesheet;
	}
	
	/**
	 * Returns a specific style transition attached to a property in this node. NULL if no transition is specified.
	 * @param name
	 * @return
	 */
	public StyleTransition getStyleTransition(String property) {
		return this.styleTransitions.get(property);
	}

	/**
	 * Sets the specific style transition attached to a property in this node.
	 * @param property
	 * @param styleTransition
	 */
	public void setStyleTransition(String property, StyleTransition styleTransition) {
		this.styleTransitions.put(property, styleTransition);
		this.setFlag(FLAG_CSS_DIRTY);
	}
	
	/**
	 * Returns the first descendant element that has the matching id.
	 * @param id
	 * @return
	 */
	public Node getElementById(String id) {
		return idToNode.get(id);
	}
	
	/**
	 * Returns a list of all descendant elements with the matching tag (Element Type)
	 * @param tag
	 * @return
	 */
	public ArrayList<Node> getElementsByTag(String tag) {
		ArrayList<Node> ret = new ArrayList<Node>();
		
		for (int i = 0; i < descendents.size(); i++) {
			if ( i >= descendents.size() )
				continue;
			
			Node t = descendents.get(i);
			if ( t == null )
				continue;
			
			if ( t.getElementType().equals(tag) )
				ret.add(t);
		}
		
		return ret;
	}
	
	/**
	 * returns a list of all descendant elements with the matching class name in its class list.
	 * @param className
	 * @return
	 */
	public ArrayList<Node> getElementsByClassName(String className) {
		ArrayList<Node> ret = new ArrayList<Node>();
		
		for (int i = 0; i < descendents.size(); i++) {
			if ( i >= descendents.size() )
				continue;
			
			Node t = descendents.get(i);
			if ( t == null )
				continue;
			
			if ( t.getClassList().contains(className) )
				ret.add(t);
		}
		
		return ret;
	}
	
	/**
	 * Returns this elements class list. Used primarily for styling.
	 * @return
	 */
	public List<String> getClassList() {
		return this.classList;
	}
	
	/**
	 * Set the unique ID for this Node. Can be querried via context#getNodeById()
	 * @param id
	 */
	public void setElementId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the unique ID for this node.
	 * @param id
	 */
	public String getElementId() {
		return this.id;
	}
	
	private void registerToParent(Node node, Node parent) {
		if ( parent == null )
			return;
		
		parent.descendents.add(node);
		for (Node descendent : node.descendents)
			parent.descendents.add(descendent);
		
		registerToParent(node, parent.getParent());
	}
	
	private void registerElementIDToParent(Node node, Node parent) {
		if ( parent == null )
			return;
		
		if ( node.getElementId() != null && node.getElementId().length() > 0 )
			parent.idToNode.put(node.getElementId(), node);
		
		registerElementIDToParent(node, parent.getParent());
	}
	
	private void unregisterFromParent(Node node, Node parent) {
		if ( parent == null )
			return;
		
		parent.idToNode.remove(node.getElementId());
		parent.descendents.remove(node);
		unregisterFromParent(node, parent.getParent());
	}
	
	/**
	 * The element tag used to express this node for styling purposes.
	 */
	public abstract String getElementType();
	
	/**
	 * Render the node.
	 */
	public abstract void render(Context context);
	
	@Override
	public String toString() {
		return name();
	}
	
	public String name() {
		return this.getClass() + "[id=" + this.getElementId() + " style=" + this.getStyle() + "]";
	}
	
	public static void resizeRelocate(Node node, double x, double y, double width, double height) {
		node.setAbsolutePosition(x, y);
		node.forceSize(width, height);
		node.dirty();
	}
	
	public class LayoutBounds {
		protected double minX;
		protected double minY;
		protected double maxX;
		protected double maxY;
		
		public LayoutBounds(double minX, double minY, double maxX, double maxY) {
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}

		/**
		 * Returns the height of the padding of this node.
		 * @return
		 */
		public double getPadHeight() {
			return minY + (Node.this.getHeight()-maxY);
		}

		/**
		 * Returns the width of the padding of this node.
		 * @return
		 */
		public double getPadWidth() {
			return minX + (Node.this.getWidth()-maxX);
		}

		/**
		 * Returns the internal width of this node.
		 * @return
		 */
		public double getWidth() {
			return maxX - minX;
		}
		
		/**
		 * Returns the internal height of this node.
		 * @return
		 */
		public double getHeight() {
			return maxY - minY;
		}

		public double getX() {
			return minX;
		}

		public double getY() {
			return minY;
		}
	}

	/**
	 * Returns whether a point in scene-space is within the rectangle-bounds of this node.
	 */
	public boolean contains(float x, float y) {
		return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
	}

	/**
	 * Returns whether this node will ignore all mouse events.
	 */
	public boolean isMouseTransparent() {
		return this.mouseTransparent;
	}
	
	/**
	 * Sets the mouse transparency property. See {@link #isMouseTransparent()}.
	 */
	public void setMouseTransparent(boolean mouseTransparent) {
		this.mouseTransparent = mouseTransparent;
	}

	/**
	 * Returns whether this node is hovered by mouse or touch gesture.
	 */
	public boolean isHovered() {
		if ( this.getScene() == null || this.getScene().getContext() == null )
			return false;
		
		return this.getScene().getContext().isNodeHovered(this);
	}

	/**
	 * Returns whether this node is currently being clicked down by mouse or touch gesture.
	 * Generally, before a node is clicked it will be hovered. See {@link #isHovered()}.
	 */
	public boolean isClicked() {
		if ( this.getScene() == null || this.getScene().getContext() == null )
			return false;
		
		return this.getScene().getContext().isNodeClicked(this);
	}

	/**
	 * Returns whether this node is currently selected as a result of mouse or touch gesture.
	 * Generally, before a node is selected it will be clicked. See {@link #isClicked()}.
	 */
	public boolean isSelected() {
		if ( this.getScene() == null || this.getScene().getContext() == null )
			return false;
		
		return this.getScene().getContext().isNodeSelected(this);
	}

	public void onMouseExited(Event event) {
		this.setFlag(FLAG_CSS_DIRTY);
		
		if (mouseExitedEventInternal != null)
			EventHelper.fireEvent(mouseExitedEventInternal, event);
		if (mouseExitedEvent != null)
			EventHelper.fireEvent(mouseExitedEvent, event);
	}

	public void onMouseEntered(Event event) {
		this.setFlag(FLAG_CSS_DIRTY);
		
		if (mouseEnteredEventInternal != null)
			EventHelper.fireEvent(mouseEnteredEventInternal, event);
		if (mouseEnteredEvent != null)
			EventHelper.fireEvent(mouseEnteredEvent, event);
	}

	public void onMousePress(MouseEvent event) {
		this.setFlag(FLAG_CSS_DIRTY);

		if (mousePressedEventInternal != null)
			EventHelper.fireEvent(mousePressedEventInternal, event);
		if (mousePressedEvent != null)
			EventHelper.fireEvent(mousePressedEvent, event);
	}

	public void onMouseRelease(MouseEvent event) {
		this.setFlag(FLAG_CSS_DIRTY);
		
		// Click logic
		long time = System.currentTimeMillis()-_lastClick;
		if ( time > 300 )
			_flag_clicks = 0;
		_flag_clicks++;
		_lastClick = System.currentTimeMillis();
		MouseEvent clickEvent = new MouseEvent(event.getMouseX(), event.getMouseY(), event.getButton(), _flag_clicks);
		
		// Click event
		if (mouseClickedEventInternal != null)
			EventHelper.fireEvent(mouseClickedEventInternal, clickEvent);
		if (mouseClickedEvent != null)
			EventHelper.fireEvent(mouseClickedEvent, clickEvent);
		
		// Releaseevent
		if (mouseReleasedEventInternal != null)
			EventHelper.fireEvent(mouseReleasedEventInternal, event);
		if (mouseReleasedEvent != null)
			EventHelper.fireEvent(mouseReleasedEvent, event);
	}
	private long _lastClick = 0;
	private int _flag_clicks = 0;
	
	protected EventHandler<MouseEvent> getMousePressedEventInternal() {
		return mousePressedEventInternal;
	}
	
	protected void setOnMousePressedInternal(EventHandler<MouseEvent> mousePressedEventInternal) {
		this.mousePressedEventInternal = mousePressedEventInternal;
	}
	
	public EventHandler<MouseEvent> getMousePressedEvent() {
		return this.mousePressedEvent;
	}
	
	public void setOnMousePressed( EventHandler<MouseEvent> event ) {
		this.mousePressedEvent = event;
	}

	
	protected EventHandler<MouseEvent> getMouseReleasedEventInternal() {
		return mouseReleasedEventInternal;
	}

	protected void setOnMouseReleasedInternal(EventHandler<MouseEvent> mouseReleasedEventInternal) {
		this.mouseReleasedEventInternal = mouseReleasedEventInternal;
	}
	
	public EventHandler<MouseEvent> getMouseReleasedEvent() {
		return this.mouseReleasedEvent;
	}
	
	public void setOnMouseReleased( EventHandler<MouseEvent> event ) {
		this.mouseReleasedEvent = event;
	}
	

	protected EventHandler<MouseEvent> getMouseClickedEventInternal() {
		return mouseClickedEventInternal;
	}

	protected void setOnMouseClickedInternal(EventHandler<MouseEvent> mouseClickedEventInternal) {
		this.mouseClickedEventInternal = mouseClickedEventInternal;
	}
	
	public EventHandler<MouseEvent> getOnMouseClicked() {
		return this.mouseClickedEvent;
	}
	
	public void setOnMouseClicked( EventHandler<MouseEvent> event) {
		this.mouseClickedEvent = event;
	}
	

	protected EventHandler<Event> getMouseEnteredEventInternal() {
		return mouseEnteredEventInternal;
	}

	protected void setOnMouseEnteredInternal(EventHandler<Event> mouseEnteredEventInternal) {
		this.mouseEnteredEventInternal = mouseEnteredEventInternal;
	}
	
	public EventHandler<Event> getMouseEnteredEvent() {
		return mouseEnteredEvent;
	}
	
	public void setOnMouseEntered( EventHandler<Event> event ) {
		this.mouseEnteredEvent = event;
	}
	

	protected EventHandler<Event> getMouseExitedEventInternal() {
		return mouseExitedEventInternal;
	}

	protected void setOnMouseExitedInternal(EventHandler<Event> mouseExitedEventInternal) {
		this.mouseExitedEventInternal = mouseExitedEventInternal;
	}
	public EventHandler<Event> getMouseExitedEvent() {
		return this.mouseExitedEvent;
	}
	
	public void setOnMouseExited( EventHandler<Event> event ) {
		this.mouseExitedEvent = event;
	}
}
