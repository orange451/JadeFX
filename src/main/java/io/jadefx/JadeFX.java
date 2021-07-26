package io.jadefx;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

import io.jadefx.geometry.Pos;
import io.jadefx.scene.Scene;
import io.jadefx.scene.layout.Pane;
import io.jadefx.stage.Stage;
import io.jadefx.stage.Window;
import io.jadefx.util.JadeFXUtil;

public class JadeFX {
	private static Map<Long, Window> activeWindows = new HashMap<>();
	
	/**
	 * Attach JadeFX to an already established GLFW window handle. NanoVG context will be automatically created based on system specifications.
	 */
	public static Window create(long glfwHandle) {
		return create(glfwHandle, JadeFXUtil.makeNanoVGContext(true));
	}
	
	/**
	 * Attach JadeFX to an already established GLFW window handle. NanoVG context is supplied by user.
	 */
	public static Window create(long glfwHandle, long nanovg) {
		Window window = new Stage(glfwHandle, nanovg);
		Pane root = new Pane();
		root.setAlignment(Pos.ANCESTOR);
		root.setBackgroundLegacy(null);
		window.setScene(new Scene(root));
		
		activeWindows.put(glfwHandle, window);
		
		return window;
	}
	
	/**
	 * Cleanup JadeFX window.
	 */
	public static void cleanup(Window window) {
		activeWindows.remove(window.getHandle());
		nvgDelete(window.getContext().getNVG());
        glfwFreeCallbacks(window.getHandle());
	}
	
	/**
	 * Render every JadeFX window under the same thread.
	 * Will set the GLFW context before each window drawing.
	 * Will call glfwPollEvents.
	 */
	public static void render() {
		for (Window window : activeWindows.values()) {
			GLFW.glfwMakeContextCurrent(window.getHandle());
			render(window);
		}
		
		GLFW.glfwPollEvents();
	}
	
	/**
	 * Render a specific JadeFX window in the current thread.
	 */
	public static void render(Window window) {
		window = activeWindows.get(window.getHandle());
		if ( window == null )
			return;
		
		renderWindow(window);
		GLFW.glfwSwapBuffers(window.getHandle());
	}
	
	private static void renderWindow(Window window) {
		if ( !window.isFlushed() )
			return;
		
		GL11.glClearColor(0.9741f, 0.9741f, 0.9741f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		
		NanoVG.nvgBeginFrame(window.getContext().getNVG(), window.getWidth(), window.getHeight(), window.getPixelRatio());
		{
			NanoVG.nvgReset(window.getContext().getNVG());
			NanoVG.nvgResetScissor(window.getContext().getNVG());
			window.render();
		}
		NanoVG.nvgEndFrame(window.getContext().getNVG());
	}
}
