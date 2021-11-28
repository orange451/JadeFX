package io.jadefx.stage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import io.jadefx.collections.ObservableList;
import io.jadefx.event.Event;
import io.jadefx.event.MouseEvent;
import io.jadefx.glfw.input.MouseHandler;
import io.jadefx.scene.Node;
import io.jadefx.scene.Parent;
import io.jadefx.scene.Scene;
import io.jadefx.style.Stylesheet;
import io.jadefx.util.IOUtil;

public class Context {
	
	private static Context currentContext;

	private final Stage window;

	private long nvgContext;

	private boolean modernOpenGL;
	private boolean isCore = true;
	
	private boolean flush;

	private List<Stylesheet> currentSheets = new ArrayList<>();
	
	private List<Node> hoveredNodes = new ArrayList<>();
	
	private List<Node> clickedNodes = new ArrayList<>();
	
	private List<Node> selectedNodes = new ArrayList<>();
	
	private DefaultFonts fonts;

	private boolean loaded = false;
	
	public Context(Stage window) {
		this(window, -1);
	}
	
	public Context(Stage window, long nvgContext) {
		this.window = window;
		this.nvgContext = nvgContext;
		try {
			this.fonts = new DefaultFonts(nvgContext);
			NanoVG.nvgFontFace(nvgContext, fonts.ROBOTO.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.loaded = true;
		
		window.getMouseButtonCallback().addCallback((handle, button, action, mods)->{
			if ( action == GLFW.GLFW_PRESS )
				mouseHover();

			MouseEvent event = new MouseEvent(window.getMouseHandler().getX(), window.getMouseHandler().getY(), button);
			
			if ( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
				if ( action == GLFW.GLFW_PRESS ) {
					List<Node> removedNodes = new ArrayList<>();
					List<Node> newNodes = new ArrayList<>();
					for (Node node : clickedNodes)
						if ( !hoveredNodes.contains(node) )
							removedNodes.add(node); // This node is now no longer clicked
					for (Node node : hoveredNodes)
						if ( !clickedNodes.contains(node) )
							newNodes.add(node); // This node was just clicked for first time
					
					clickedNodes.removeAll(removedNodes);
					clickedNodes.addAll(newNodes);
					
					for (Node node : newNodes)
						node.onMousePress(event);
				}
				
				if ( action == GLFW.GLFW_RELEASE ) {
					for (Node node : clickedNodes)
						node.onMouseRelease(event);
					
					// Move clicked nodes in to selected nodes
					selectedNodes.clear();
					selectedNodes.addAll(clickedNodes);
					clickedNodes.clear();
				}
			}
		});
	}

	public void init() {
		if ( nvgContext > -1 )
			return;
		
		this.modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
				|| (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);

		if (this.isModernOpenGL()) {
			int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
			nvgContext = NanoVGGL3.nvgCreate(flags);
		} else {
			int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
			nvgContext = NanoVGGL2.nvgCreate(flags);
		}

		isCore = true;
	}

	public void dispose() {
		if (this.isModernOpenGL()) {
			NanoVGGL3.nvgDelete(nvgContext);
		} else {
			NanoVGGL2.nvgDelete(nvgContext);
		}
	}

	/**
	 * Returns the internal NanoVG pointer.
	 * 
	 * @return
	 */
	public long getNVG() {
		return nvgContext;
	}

	/**
	 * Force resets the OpenGL Viewport to fit the window.
	 */
	public void refresh() {
		currentContext = this;
		GL11.glViewport(0, 0, (int) (window.getWidth() * window.getPixelRatio()), (int) (window.getHeight() * window.getPixelRatio()));
	}
	
	/**
	 * Force the context into a flush state. When flushed, the next frame update will be forced to re-render the scene.
	 */
	public void flush() {
		flush = true;
	}
	
	/**
	 * Returns whether the context is in flushed state. See {@link #flush()}.
	 */
	public boolean isFlushed() {
		return flush;
	}
	
	/**
	 * Returns whether the context is loaded and ready to use.
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Hidden method to set the flush state of the context.
	 */
	protected void setFlushed(boolean flush) {
		this.flush = flush;
	}

	/**
	 * Returns whether the internal renderer is using modern opengl (OpenGL 3.2+)
	 * 
	 * @return
	 */
	public boolean isModernOpenGL() {
		return this.modernOpenGL;
	}

	/**
	 * Returns whether the window was created with a core OpenGL profile or not.
	 * 
	 * @return
	 */
	public boolean isCoreOpenGL() {
		return this.isCore;
	}

	/**
	 * Get the window associated with this context
	 */
	public Window getWindow() {
		return this.window;
	}

	/**
	 * Returns the current list of stylesheets used for rendering the node.
	 * 
	 * @return
	 */
	public List<Stylesheet> getCurrentStyling() {
		return currentSheets;
	}

	public void updateContext() {
		mouseHover();
	}

	protected boolean hoveringOverPopup;

	private void mouseHover() {
		// Get scene
		Scene scene = window.getScene();

		// Calculate current hover
		hoveringOverPopup = false;
		Node hovered = calculateHoverRecursive(null, scene);
		Node last = hovered;
		//hovered = calculateHoverPopups(scene);

		// Check if hovering over a popup
		if (last != null && !last.equals(hovered)) {
			hoveringOverPopup = true;
		}

		// Not hovering over popups
		/*if (last != null && last.equals(hovered)) {
			for (int i = 0; i < scene.getPopups().size(); i++) {
				PopupWindow popup = scene.getPopups().get(i);
				popup.weakClose();
			}
		}*/

		// Check all hovered nodes to see if no longer hovered
		for (int i = 0; i < hoveredNodes.size(); i++) {
			if ( i >= hoveredNodes.size() )
				continue;
			
			Node node = hoveredNodes.get(i);
			if ( node == null )
				continue;
			
			MouseHandler mh = window.getMouseHandler();
			float mouseX = mh.getX();
			float mouseY = mh.getY();
			
			if ( !node.contains(mouseX, mouseY) ) {
				hoveredNodes.remove(i--);
				node.onMouseExited(new Event());
			}
		}
	}

	/*private Node calculateHoverPopups(Scene scene) {
		MouseHandler mh = window.getMouseHandler();
		ObservableList<PopupWindow> popups = scene.getPopups();
		for (int i = 0; i < popups.size(); i++) {
			PopupWindow popup = popups.get(i);
			if (popup.contains(mh.getX(), mh.getY())) {
				return calculateHoverRecursive(null, popup);
			}
		}

		return hovered;
	}*/
	
	public List<Node> getHoveredNodes() {
		return new ArrayList<>(this.hoveredNodes);
	}

	protected Node calculateHoverRecursive(Node parent, Node root) {
		// Use scene as an entry point into nodes
		if (parent == null && root instanceof Scene)
			root = ((Scene) root).getRoot();

		// If there's no root. then there's nothing to hover
		if (root == null)
			return null;

		// Ignore if unclickable
		if (root.isMouseTransparent())
			return parent;

		MouseHandler mh = window.getMouseHandler();
		float mouseX = mh.getX();
		float mouseY = mh.getY();

		// If mouse is out of our bounds, ignore.
		if ( !root.contains(mouseX, mouseY) ) {
			return parent;
		}

		// Handle mouse enter logic
		if (!hoveredNodes.contains(root)) {
			hoveredNodes.add(root);
			root.onMouseEntered(new Event());
		}

		// Check children
		if ( root instanceof Parent ) {
			ObservableList<Node> children = ((Parent)root).getChildrenUnmodifyable();
			for (int i = children.size() - 1; i >= 0; i--) {
				Node ret = calculateHoverRecursive(root, children.get(i));
				if (ret != null && !ret.equals(root)) {
					return ret;
				}
			}
		}
		return root;
	}

	public static Context getCurrent() {
		return currentContext;
	}

	public boolean isNodeHovered(Node node) {
		return this.hoveredNodes.contains(node);
	}

	public boolean isNodeSelected(Node node) {
		return this.selectedNodes.contains(node);
	}

	public boolean isNodeClicked(Node node) {
		return this.clickedNodes.contains(node);
	}
}

class DefaultFonts {
	public final FontData ROBOTO;
	
	private Map<String, FontData> loadedFonts = new HashMap<>();
	
	protected static Map<String, ByteBuffer> fontData = new HashMap<>();
	
	public DefaultFonts(long vg) throws IOException {
		add(ROBOTO = new FontData(vg, "Roboto", "jadefx/font/Roboto-Regular.ttf"));

		//fallback(vg, new FontData(vg, "NotoColorEmoji", "jadefx/font/NotoColorEmoji.ttf"));
		//fallback(vg, new FontData(vg, "NotoSans", "jadefx/font/NotoSansCJKsc-Medium.otf"));
		fallback(vg, new FontData(vg, "Entypo", "jadefx/font/entypo.ttf"));
	}

	private void add(FontData fontData) {
		loadedFonts.put(fontData.getName(), fontData);
	}
	
	private void fallback(long vg, FontData fallback) {
		for (Entry<String, FontData> entry : loadedFonts.entrySet()) {
			NanoVG.nvgAddFallbackFontId(vg, entry.getValue().getHandle(), fallback.getHandle());
		}
	}
}

class FontData {
	private String name;
	private int handle;
	
	public FontData(long vg, String name, String resourcePath) throws IOException {
		this.name = name;
		
		ByteBuffer data = DefaultFonts.fontData.get(name);
		if ( data == null ) {
			data = IOUtil.ioResourceToByteBuffer(resourcePath);
			DefaultFonts.fontData.put(name, data);
		}
		
		this.handle = NanoVG.nvgCreateFontMem(vg, name, data, 0);
	}
	
	public int getHandle() {
		return this.handle;
	}
	
	public String getName() {
		return this.name;
	}
}
