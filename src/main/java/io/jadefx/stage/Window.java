package io.jadefx.stage;

import static io.jadefx.event.listener.EventListener.EventListenerType.CURSOR_POS_LISTENER;
import static io.jadefx.event.listener.EventListener.EventListenerType.KEY_LISTENER;
import static io.jadefx.event.listener.EventListener.EventListenerType.MOUSE_BUTTON_LISTENER;
import static io.jadefx.event.listener.EventListener.EventListenerType.MOUSE_WHEEL_LISTENER;
import static io.jadefx.event.listener.EventListener.EventListenerType.WINDOW_CLOSE_LISTENER;
import static io.jadefx.event.listener.EventListener.EventListenerType.WINDOW_SIZE_LISTENER;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfm.GLFM;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import io.jadefx.JadeFX;
import io.jadefx.event.EventHelper;
import io.jadefx.event.KeyEvent;
import io.jadefx.event.MouseEvent;
import io.jadefx.event.ScrollEvent;
import io.jadefx.event.TypeEvent;
import io.jadefx.event.listener.CursorPositionListener;
import io.jadefx.event.listener.EventListener;
import io.jadefx.event.listener.KeyListener;
import io.jadefx.event.listener.MouseButtonListener;
import io.jadefx.event.listener.MouseWheelListener;
import io.jadefx.event.listener.WindowCloseListener;
import io.jadefx.event.listener.WindowFocusListener;
import io.jadefx.event.listener.WindowSizeListener;
import io.jadefx.event.listener.EventListener.EventListenerType;
import io.jadefx.glfw.Callbacks.CharCallback;
import io.jadefx.glfw.Callbacks.CharModsCallback;
import io.jadefx.glfw.Callbacks.CursorEnterCallback;
import io.jadefx.glfw.Callbacks.CursorPosCallback;
import io.jadefx.glfw.Callbacks.FramebufferSizeCallback;
import io.jadefx.glfw.Callbacks.KeyCallback;
import io.jadefx.glfw.Callbacks.MouseButtonCallback;
import io.jadefx.glfw.Callbacks.ScrollCallback;
import io.jadefx.glfw.Callbacks.WindowCloseCallback;
import io.jadefx.glfw.Callbacks.WindowFocusCallback;
import io.jadefx.glfw.Callbacks.WindowIconifyCallback;
import io.jadefx.glfw.Callbacks.WindowMaximizeCallback;
import io.jadefx.glfw.Callbacks.WindowPosCallback;
import io.jadefx.glfw.Callbacks.WindowRefreshCallback;
import io.jadefx.glfw.Callbacks.WindowSizeCallback;
import io.jadefx.glfw.input.KeyboardHandler;
import io.jadefx.glfw.input.MouseHandler;
import io.jadefx.scene.Scene;

public abstract class Window {
	
	private long handle;
	
	protected int width = 0;
	
	protected int height = 0;
	
	protected int framebufferWidth = 0;
	
	protected int framebufferHeight = 0;
	
	protected float pixelRatio = 1;

	private WindowSizeCallback windowSizeCallback;
	private WindowCloseCallback windowCloseCallback;
	private WindowFocusCallback windowFocusCallback;
	private KeyCallback keyCallback;
	private CharCallback charCallback;
	private MouseButtonCallback mouseButtonCallback;
	private CursorPosCallback cursorPosCallback;
	private ScrollCallback scrollCallback;
	private WindowIconifyCallback windowIconifyCallback;
	private FramebufferSizeCallback framebufferSizeCallback;
	private CursorEnterCallback cursorEnterCallback;
	private CharModsCallback charModsCallback;
	private WindowPosCallback windowPosCallback;
	private WindowMaximizeCallback windowMaximizeCallback;
	private WindowRefreshCallback windowRefreshCallback;

	private MouseHandler mouseHandler;
	private KeyboardHandler keyboardHandler;

	private boolean firstShow = true;
	private boolean visible = false;
	
	private Map<EventListenerType, List<EventListener>> eventListeners = new HashMap<>();
	
	public Window(long handle, long nvgContext) {
		this.handle = handle;
		this.setCallbacks();
	}
	
	protected void setCallbacks() {
		cursorPosCallback = new CursorPosCallback();
		cursorPosCallback.addCallback(glfwSetCursorPosCallback(handle, cursorPosCallback));
		cursorPosCallback.addCallback(this::cursorPosCallback);

		charCallback = new CharCallback();
		charCallback.addCallback(glfwSetCharCallback(handle, charCallback));
		charCallback.addCallback(this::charCallback);

		keyCallback = new KeyCallback();
		keyCallback.addCallback(glfwSetKeyCallback(handle, keyCallback));
		keyCallback.addCallback(this::keyCallback);

		mouseButtonCallback = new MouseButtonCallback();
		mouseButtonCallback.addCallback(glfwSetMouseButtonCallback(handle, mouseButtonCallback));
		mouseButtonCallback.addCallback(this::mouseButtonCallback);

		windowFocusCallback = new WindowFocusCallback();
		windowFocusCallback.addCallback(glfwSetWindowFocusCallback(handle, windowFocusCallback));
		windowFocusCallback.addCallback(this::focusCallback);

		windowCloseCallback = new WindowCloseCallback();
		windowCloseCallback.addCallback(glfwSetWindowCloseCallback(handle, windowCloseCallback));
		windowCloseCallback.addCallback(this::closeCallback);

		windowSizeCallback = new WindowSizeCallback();
		windowSizeCallback.addCallback(glfwSetWindowSizeCallback(handle, windowSizeCallback));
		windowSizeCallback.addCallback(this::sizeCallback);
		windowSizeCallback.addCallback((window, width, height) -> {
			if (width == 0 || height == 0)
				return;
			
			this.width = width;
			this.height = height;
			
			if ( this.framebufferWidth > 0 ) {
				pixelRatio = (this.framebufferWidth <= this.width) ? 1 : this.framebufferWidth / this.width;
			}
		});

		scrollCallback = new ScrollCallback();
		scrollCallback.addCallback(glfwSetScrollCallback(handle, scrollCallback));
		scrollCallback.addCallback(this::scrollCallback);

		windowIconifyCallback = new WindowIconifyCallback();
		windowIconifyCallback.addCallback(glfwSetWindowIconifyCallback(handle, windowIconifyCallback));

		framebufferSizeCallback = new FramebufferSizeCallback();
		framebufferSizeCallback.addCallback(glfwSetFramebufferSizeCallback(handle, framebufferSizeCallback));
		framebufferSizeCallback.addCallback((window, width, height) -> {
			if (width == 0 || height == 0)
				return;
			
			framebufferWidth = width;
			framebufferHeight = height;
			
			if ( this.width > 0 ) {
				pixelRatio = (framebufferWidth <= this.width) ? 1 : framebufferWidth / this.width;
			}
		});

		cursorEnterCallback = new CursorEnterCallback();
		cursorEnterCallback.addCallback(glfwSetCursorEnterCallback(handle, cursorEnterCallback));

		charModsCallback = new CharModsCallback();
		charModsCallback.addCallback(glfwSetCharModsCallback(handle, charModsCallback));

		windowPosCallback = new WindowPosCallback();
		windowPosCallback.addCallback(glfwSetWindowPosCallback(handle, windowPosCallback));

		windowMaximizeCallback = new WindowMaximizeCallback();
		windowMaximizeCallback.addCallback(glfwSetWindowMaximizeCallback(handle, windowMaximizeCallback));

		windowRefreshCallback = new WindowRefreshCallback();
		windowRefreshCallback.addCallback(glfwSetWindowRefreshCallback(handle, windowRefreshCallback));

		mouseHandler = new MouseHandler(this);
		keyboardHandler = new KeyboardHandler(this);
	}
	
	public abstract boolean isFlushed();
	
	public abstract void render();
	
	public void setSize(double width, double height) {
		GLFW.glfwSetWindowSize(this.getHandle(), (int)width, (int)height);
		this.width = (int)width;
		this.height = (int)height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getFrameBufferWidth() {
		return this.framebufferWidth;
	}

	public int getFrameBufferHeight() {
		return this.framebufferHeight;
	}

	public float getPixelRatio() {
		return this.pixelRatio;
	}

	/**
	 * Add the given EventListener to the Window.
	 * 
	 * @param listener
	 */
	public void addEventListener(EventListener listener) {
		EventListenerType key = listener.getEventListenerType();

		if (eventListeners.containsKey(key)) {
			eventListeners.get(key).add(listener);
		} else {
			eventListeners.put(key, new ArrayList<EventListener>());
			addEventListener(listener);
		}
	}

	/**
	 * Remove the given EventListener.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeEventListener(EventListener listener) {
		EventListenerType key = listener.getEventListenerType();

		if (eventListeners.containsKey(key)) {
			return eventListeners.get(key).remove(listener);
		} else {
			return false;
		}
	}

	/**
	 * Remove all EventListeners of the given type.
	 * 
	 * @param type
	 */
	public void removeAllEventListeners(EventListenerType type) {
		eventListeners.put(type, new ArrayList<EventListener>());
	}

	/**
	 * Get all EventListeners of the given type.
	 * 
	 * @param type
	 * @return
	 */
	public List<EventListener> getEventListenersForType(EventListenerType type) {
		if (eventListeners.containsKey(type)) {
			return eventListeners.get(type);
		} else {
			eventListeners.put(type, new ArrayList<EventListener>());
			return getEventListenersForType(type);
		}
	}

	/**
	 * Attempts to show this Window by setting visibility to true
	 */
	public void show() {
		if ( firstShow ) {
			recomputeWindowSize();
		}
		setVisible(true);
		//focus();
		//focusHack();
	}
	
	protected abstract void recomputeWindowSize();
	
	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean flag) {
		// Show window
		visible = flag;
		firstShow = false;
		this.setSize(this.getWidth(), this.getHeight());
		GLFW.glfwShowWindow(this.getHandle());

		// Update framebuffer callback
		int[] fWidth = new int[1];
		int[] fHeight = new int[1];
		GLFW.glfwGetFramebufferSize(getHandle(), fWidth, fHeight);
		if ( this.getFramebufferSizeCallback() != null )
			this.getFramebufferSizeCallback().invoke(this.getHandle(), fWidth[0], fHeight[0]);
		
		// Update window size callback
		int[] pWidth = new int[1];
		int[] pHeight = new int[1];
		GLFW.glfwGetWindowSize(getHandle(), pWidth, pHeight);
		if ( this.getWindowSizeCallback() != null )
			this.getWindowSizeCallback().invoke(this.getHandle(), pWidth[0], pHeight[0]);
	}

	public void setResizible(boolean resizable) {
		glfwSetWindowAttrib(handle, GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
	}

	public WindowSizeCallback getWindowSizeCallback() {
		return windowSizeCallback;
	}

	public WindowCloseCallback getWindowCloseCallback() {
		return windowCloseCallback;
	}

	public WindowFocusCallback getWindowFocusCallback() {
		return windowFocusCallback;
	}

	public WindowIconifyCallback getWindowIconifyCallback() {
		return windowIconifyCallback;
	}

	public KeyCallback getKeyCallback() {
		return keyCallback;
	}

	public CharCallback getCharCallback() {
		return charCallback;
	}

	public MouseButtonCallback getMouseButtonCallback() {
		return mouseButtonCallback;
	}

	public CursorPosCallback getCursorPosCallback() {
		return cursorPosCallback;
	}

	public ScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	public CursorEnterCallback getCursorEnterCallback() {
		return cursorEnterCallback;
	}

	public CharModsCallback getCharModsCallback() {
		return charModsCallback;
	}

	public FramebufferSizeCallback getFramebufferSizeCallback() {
		return framebufferSizeCallback;
	}

	public WindowMaximizeCallback getWindowMaximizeCallback() {
		return windowMaximizeCallback;
	}

	public WindowPosCallback getWindowPosCallback() {
		return windowPosCallback;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

	public KeyboardHandler getKeyboardHandler() {
		return keyboardHandler;
	}

	public WindowRefreshCallback getWindowRefreshCallback() {
		return windowRefreshCallback;
	}

	public long getHandle() {
		return this.handle;
	}
	
	private void closeCallback(long window) {
		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(WINDOW_CLOSE_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((WindowCloseListener) listeners.get(i)).invoke(window);
		}
	}

	private void sizeCallback(long window, int width, int height) {
		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(WINDOW_SIZE_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((WindowSizeListener) listeners.get(i)).invoke(window, width, height);
		}
	}

	private void focusCallback(long window, boolean focus) {
		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(MOUSE_WHEEL_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((WindowFocusListener) listeners.get(i)).invoke(window, focus);
		}

	}

	private void keyCallback(long handle, int key, int scancode, int action, int mods) {
		boolean isCtrlDown = (mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL || (mods & GLFW_MOD_SUPER) == GLFW_MOD_SUPER;
		boolean isAltDown = (mods & GLFW_MOD_ALT) == GLFW_MOD_ALT;
		boolean isShiftDown = (mods & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT;

		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(KEY_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((KeyListener) listeners.get(i)).invoke(handle, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
		}
		
		//
	}
	
	private void charCallback(long window, int codepoint) {
		//
	}

	protected void mouseButtonCallback(long window, int button, int downup, int modifier) {

		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(MOUSE_BUTTON_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((MouseButtonListener) listeners.get(i)).invoke(window, button, downup, modifier);
		}
		
		//
	}

	protected void cursorPosCallback(long window, double x, double y) {

		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(CURSOR_POS_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((CursorPositionListener) listeners.get(i)).invoke(window, x, y);
		}
		
		//
	}

	protected void scrollCallback(long window, double dx, double dy) {
		/*
		 * Call window event listeners
		 */
		List<EventListener> listeners = getEventListenersForType(MOUSE_WHEEL_LISTENER);

		// Scale scrolling down
		if (dx != 1 && dx != -1 && dy != -1 && dy != 1) {
			if (Math.abs(dx) < 1 || Math.abs(dy) < 1) {
				dx = Math.signum(dx) * (dx * dx);
				dy = Math.signum(dy) * (dy * dy);
			} else {
				dx = (dx - 1) * 0.5 + 1;
				dy = (dy - 1) * 0.5 + 1;
			}
		}

		for (int i = 0; i < listeners.size(); i++) {
			((MouseWheelListener) listeners.get(i)).invoke(window, dx, dy);
		}

		/*
		 * Call scene node listeners
		 */

		double[] values = new double[2];
		values[0] = dx;
		values[1] = dy;
		//
	}
}
