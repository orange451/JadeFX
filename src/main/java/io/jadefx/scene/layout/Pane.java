package io.jadefx.scene.layout;

import io.jadefx.collections.ObservableList;
import io.jadefx.paint.Color;
import io.jadefx.scene.Context;
import io.jadefx.scene.Node;
import io.jadefx.style.Background;
import io.jadefx.style.BackgroundSolid;
import io.jadefx.style.BlockPaneRenderer;
import io.jadefx.style.BorderStyle;
import io.jadefx.style.BoxShadow;

/**
 * Base class for layout panes which need to expose the children list as public
 * so that users of the subclass can freely add/remove children.
 */
public class Pane extends Region implements BlockPaneRenderer {
	
	private Color borderColor;
	private float[] borderRadii;
	private BorderStyle borderStyle;
	private ObservableList<Background> backgrounds = new ObservableList<>();
	private ObservableList<BoxShadow> boxShadows = new ObservableList<>();
	
    /**
     * Creates a Pane layout.
     */
    public Pane() {
        super();

        backgrounds.setAddCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        backgrounds.setRemoveCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        boxShadows.setAddCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        boxShadows.setRemoveCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        
        this.setBorderRadii(0);
        this.setBackgroundLegacy(Color.TRANSPARENT);
    }

    /**
     * Creates a Pane layout.
     * @param children The initial set of children for this pane.
     */
    public Pane(Node... children) {
    	this();
    	
        getChildren().addAll(children);
    }

    /**
     * Gets the list of children of this {@code Pane}.
     * @return the list of children of this {@code Pane}.
     */
    @Override public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
	
	@Override
	public String getElementType() {
		return "pane";
	}

	@Override
	public void render(Context context) {
		//if ( !isVisible() )
			//return;
		
		// Apply CSS
		//this.stylePush();
		{
			// Render standard pane
			BlockPaneRenderer.render(context, this);
			
			// Draw children
			for (int i = 0; i < getChildren().size(); i++) {
				// Draw child
				Node child = getChildren().get(i);
				if ( child == null )
					continue;
				
				child.render(context);
			}
		}
		//this.stylePop();
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */
	public void setBackgroundLegacy(Color color) {
		setBackground( new BackgroundSolid(color) );
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */	
	public void setBackground(Background color) {
		for (int i = 0; i < backgrounds.size(); i++) {
			if ( backgrounds.get(i) instanceof BackgroundSolid ) {
				backgrounds.remove(i--);
			}
		}
		
		this.setFlag(FLAG_CSS_DIRTY);
		
		if ( color != null )
			getBackgrounds().add(0, color);
	}
	
	/**
	 * Get the current background color of this node.
	 * @return
	 */
	public Background getBackground() {
		if ( this.backgrounds.size() == 0 )
			return null;
		
		return this.backgrounds.get(0);
	}
	
	/**
	 * Get list of backgrounds used for drawing.
	 */
	public ObservableList<Background> getBackgrounds() {
		return this.backgrounds;
	}
	
	@Override
	public void setBorderStyle(BorderStyle style) {
		this.borderStyle = style;
		this.setFlag(FLAG_CSS_DIRTY);
	}

	@Override
	public BorderStyle getBorderStyle() {
		return this.borderStyle;
	}

	@Override
	public float[] getBorderRadii() {
		return borderRadii;
	}

	@Override
	public void setBorderRadii(float radius) {
		this.setBorderRadii(radius, radius, radius, radius);
	}

	@Override
	public void setBorderRadii(float[] radius) {
		this.setBorderRadii(radius[0], radius[1], radius[2], radius[3]);
	}

	@Override
	public void setBorderRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft) {
		this.borderRadii = new float[] {cornerTopLeft, cornerTopRight, cornerBottomRight, cornerBottomLeft};
		this.setFlag(FLAG_CSS_DIRTY);
	}

	@Override
	public void setBorderColor(Color color) {
		this.borderColor = color;
		this.setFlag(FLAG_CSS_DIRTY);
	}

	@Override
	public Color getBorderColor() {
		return this.borderColor;
	}

	@Override
	public ObservableList<BoxShadow> getBoxShadowList() {
		return this.boxShadows;
	}
}
