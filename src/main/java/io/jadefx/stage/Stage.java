package io.jadefx.stage;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import io.jadefx.JadeFX;
import io.jadefx.gl.Renderer;
import io.jadefx.scene.Scene;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.util.JadeFXUtil;

public class Stage extends Window {
	
	private Scene scene;
	
	private Context context;
	
	private Renderer renderCallback;

	private static final int NO_FLUSH = 0;
	private static final int FLUSH = 4;
	private static Map<Long,Integer> flushMap = new HashMap<>();

	public Stage() {
		this(JadeFXUtil.createWindowGLFW(300, 300, "Window"), JadeFXUtil.makeNanoVGContext(true));
		JadeFX.create(this.getHandle(), this.getContext().getNVG());
	}
	
	public Stage(long handle, long nvgContext) {
		super(handle, nvgContext);
		this.context = new Context(this, nvgContext);
		
		Pane root = new StackPane();
		root.setBackgroundLegacy(null);
		this.setScene(new Scene(root));

		flushMap.put(handle, 10);
		
		this.getWindowSizeCallback().addCallback((window, width, height) -> {
			scene.dirty();
			JadeFX.render(Stage.this);
		});
	}

	public Context getContext() {
		return this.context;
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	@Override
	public void setSize(double width, double height) {
		super.setSize(width, height);
		scene.setPrefSize(width, height);
		scene.setMaxSize(width, height);
		scene.setMinSize(width, height);
	}
	
	public boolean isFlushed() {
		return this.canDraw() || this.getContext().isFlushed();
	}

	private boolean canDraw() {
		return this.getFlush() > NO_FLUSH;
	}
	
	private int getFlush() {
		return flushMap.get(this.getHandle());
	}
	
	private void setFlush(int flush) {
		flushMap.put(this.getHandle(), flush);
	}

	/**
	 * Sets the rendering callback for this window. By default there is no rendering
	 * callback.<br>
	 * A rendering callback runs directly before the window renders its UI.
	 * 
	 * @param callback
	 */
	public void setRenderingCallback(Renderer callback) {
		this.renderCallback = callback;
	}
	
	protected void recomputeWindowSize() {
		double oldWidth = this.width;
		double oldHeight = this.height;
		
		this.width = 20000;
		this.height = 20000;
		
		for (int i = 0; i < 10; i++) {
			this.getScene().render(this.getContext());
		}

		double width = this.getScene().getPrefWidth() > 0 ? this.getScene().getPrefWidth() : this.getScene().getRoot().getWidth();
		double height = this.getScene().getPrefHeight() > 0 ? this.getScene().getPrefHeight() : this.getScene().getRoot().getHeight();
		System.out.println(this.getScene().getPrefWidth() + " / " + this.getScene().getPrefHeight());
		
		if ( width < 4 || height < 4 ) {
			width = (int)oldWidth;
			height = (int)oldHeight;
		}
		
		this.setSize(width, height);
	}

	@Override
	public void render() {
		if ( this.getScene() == null )
			return;
		
		// If context hasent finished initializing, we cannot draw.
		if ( !this.getContext().isLoaded() )
			return;
		
		// iIf context has been flushed, make window flush.
		if ( this.getContext().isFlushed() ) {
			this.getContext().setFlushed(false);
			this.setFlush(FLUSH);	// We need to flush for a few frames because
		}							// GLFM doesnt have a swapbuffers implementation.
		
		// If we can not draw, stop
		if ( !this.canDraw() )
			return;

		// Decrement flush
		this.setFlush(Math.max(this.getFlush()-1, 0));
		
		// Prepare context for rendering
		this.getContext().refresh();
		
		// Render callback
		if ( this.renderCallback != null )
			this.renderCallback.render(this.getContext(), this.getWidth(), this.getHeight());
		
		// Render stage
		this.getScene().render(this.getContext());
	}
}
