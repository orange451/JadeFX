package io.jadefx.scene.layout;

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
	public String getElementType() {
		return "stackpane";
	}
}
