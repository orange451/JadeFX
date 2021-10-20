package io.jadefx;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

import io.jadefx.scene.Scene;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.stage.Stage;
import io.jadefx.transition.TransitionManager;
import io.jadefx.util.JadeFXUtil;

public class JadeFX {
	private static Map<Long, Stage> activeWindows = new HashMap<>();
	
	/**
	 * Attach JadeFX to an already established GLFW window handle. NanoVG context will be automatically created based on system specifications.
	 */
	public static Stage create(long glfwHandle) {
		return create(glfwHandle, JadeFXUtil.makeNanoVGContext(true));
	}
	
	/**
	 * Attach JadeFX to an already established GLFW window handle. NanoVG context is supplied by user.
	 */
	public static Stage create(long glfwHandle, long nanovg) {
		return create(new Stage(glfwHandle, nanovg));
	}
	
	/**
	 * Attach JadeFX to an already established Stage.
	 */
	public static Stage create(Stage stage) {
		Pane root = new StackPane();
		root.setBackgroundLegacy(null);
		stage.setScene(new Scene(root));
		
		activeWindows.put(stage.getHandle(), stage);
		
		return stage;
	}
	
	/**
	 * Cleanup JadeFX window.
	 */
	public static void cleanup(Stage window) {
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
		for (Stage window : activeWindows.values()) {
			GLFW.glfwMakeContextCurrent(window.getHandle());
			render(window);
		}
		
		GLFW.glfwPollEvents();
	}
	
	/**
	 * Render a specific JadeFX window in the current thread.
	 */
	public static void render(Stage window) {
		TransitionManager.tick();
		
		window = activeWindows.get(window.getHandle());
		if ( window == null )
			return;
		
		renderWindow(window);
		GLFW.glfwSwapBuffers(window.getHandle());
	}
	
	private static void renderWindow(Stage window) {
		window.getContext().updateContext();
		
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
