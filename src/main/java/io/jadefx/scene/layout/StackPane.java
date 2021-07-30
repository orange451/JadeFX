package io.jadefx.scene.layout;

import io.jadefx.geometry.HPos;
import io.jadefx.geometry.Pos;
import io.jadefx.geometry.VPos;
import io.jadefx.scene.Node;

/**
*
* StackPane lays out its children in a back-to-front stack.
* <p>
* The z-order of the children is defined by the order of the children list
* with the 0th child being the bottom and last child on top.  If a border and/or
* padding have been set, the children will be layed out within those insets.
*/
public class StackPane extends Pane {

    /**
     * Creates a StackPane layout with default CENTER alignment.
     */
    public StackPane() {
        super();
        
        this.setAlignment(Pos.CENTER);
    }

    /**
     * Creates a StackPane layout with default CENTER alignment.
     * @param children The initial set of children for this pane.
     */
    public StackPane(Node... children) {
        super();
        getChildren().addAll(children);
    }
    
    @Override
    protected void layoutChildren() {
		for (int i = 0; i < this.children.size(); i++) {
			Node node = this.children.get(i);
			Pos useAlignment = node.usingAlignment();

			double xMult = 0;
			if ( useAlignment.getHpos() == HPos.CENTER)
				xMult = 0.5f;
			if ( useAlignment.getHpos() == HPos.RIGHT)
				xMult = 1;

			double yMult = 0;
			if ( useAlignment.getVpos() == VPos.CENTER)
				yMult = 0.5f;
			if ( useAlignment.getVpos() == VPos.BOTTOM)
				yMult = 1f;

			LayoutBounds bounds = this.getInnerBounds();
			double layoutX = (bounds.getWidth()-node.getWidth())*xMult;
			double layoutY = (bounds.getHeight()-node.getHeight())*yMult;
			double offsetX = layoutX + this.getTranslateX();
			double offsetY = layoutY + this.getTranslateY();
			
			node.setLocalPosition(offsetX, offsetY);
		}
    }
    
    public Pos getAlignment() {
    	return super.getAlignment();
    }
    
	/**
	 * Set the layout alignment.
	 * @param pos
	 */
	public void setAlignment(Pos pos) {
		super.setAlignment(pos);
	}
    
	@Override
	public String getElementType() {
		return "stackpane";
	}
}
