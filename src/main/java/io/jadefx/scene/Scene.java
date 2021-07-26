package io.jadefx.scene;

import io.jadefx.collections.ObservableList;
import io.jadefx.paint.Color;
import io.jadefx.stage.Context;
import io.jadefx.stage.Window;
import io.jadefx.style.Background;
import io.jadefx.style.BackgroundSolid;
import io.jadefx.style.StyleBackground;

/**
 * Every window has a Scene that contains various nodes that add functionality to the program. 
 *
 */
public class Scene extends Node implements StyleBackground {
	private Node root;
	
	private Context context;
	
	private ObservableList<Background> backgrounds;

	public Scene(Node root) {
		this(root, root.getPrefWidth(), root.getPrefHeight());
	}
	
	public Scene(Node root, double prefWidth, double prefHeight) {
		setRoot(root);
		this.setPrefSize(prefWidth, prefHeight);
		
		this.backgrounds = new ObservableList<>();
		this.setBackground(new BackgroundSolid(Color.WHITE_SMOKE));
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
	
	private boolean firstFrame = true;
	
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
		this.forceSize(getWindow().getWidth(), getWindow().getHeight());
		root.forceSize(getWidth(), getHeight());
		
		// Position elements
		int repeat = firstFrame ? 8 : 1;
		for (int i = 0; i < repeat; i++)
			position();
		firstFrame = false;
		
		// Backgrounds
		for (Background background : backgrounds)
			background.render(context, getX(), getY(), getWidth(), getHeight(), new float[4]);
		
		// Render normal
		root.render(context);
	}
	
	public Window getWindow() {
		return this.getContext().getWindow();
	}

	public Context getContext() {
		return this.context;
	}
	
	public void dirty() {
		super.dirty();
		
		if ( this.root != null )
			root.dirty();
	}

	@Override
	public Background getBackground() {
		if ( backgrounds.size() == 0 )
			return null;
		
		return backgrounds.get(0);
	}

	@Override
	public void setBackground(Background color) {
		this.backgrounds.clear();
		this.backgrounds.add(color);
	}

	@Override
	public ObservableList<Background> getBackgrounds() {
		return this.backgrounds;
	}
}
