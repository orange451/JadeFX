package io.jadefx.scene;

/**
 * Every window has a Scene that contains various nodes that add functionality to the program. 
 *
 */
public class Scene extends Node {
	private Node root;
	
	private Context context;

	public Scene(Node root) {
		this(root, root.getPrefWidth(), root.getPrefHeight());
	}
	
	public Scene(Node root, double prefWidth, double prefHeight) {
		setRoot(root);
		this.setPrefSize(prefWidth, prefHeight);
	}

	@Override
	public String getElementType() {
		return "scene";
	}

	@Override
	public double getX() {
		return 0;
	}
	
	@Override
	public double getY() {
		return 0;
	}
	
	/**
	 * Sets the base node of the scene, essentially becoming the "container" for everything else.
	 * 
	 * @param node
	 */
	public void setRoot(Node node) {
		this.children.clear();
		this.children.add(node);
		this.root = node;
	}

	public Node getRoot() {
		return this.root;
	}
	
	@Override
	public void render(Context context) {
		if ( root == null )
			return;
		
		this.context = context;
		
		// Reset stylesheet stack
		context.getCurrentStyling().clear();

		// Stretch to match screen
		this.absolutePosition.x = 0;
		this.absolutePosition.y = 0;
		root.forceSize(getWidth(), getHeight());
		
		// Position elements
		position();
		
		// Render normal
		root.render(context);
	}

	public Context getContext() {
		return this.context;
	}
	
	public void dirty() {
		super.dirty();
		
		if ( this.root != null )
			root.dirty();
	}
}
