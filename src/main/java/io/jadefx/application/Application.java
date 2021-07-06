package io.jadefx.application;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import io.jadefx.scene.Scene;
import io.jadefx.scene.Window;
import io.jadefx.scene.layout.Pane;

public abstract class Application {
	
	public static void launch(String[] args) {
		
		// Get the class name that called launch method.
		String callingClassName = getCallingClass("launch");
		if (callingClassName == null) {
			throw new RuntimeException("Error: unable to determine main class (2)");
		}
		
		// Create new instance of calling class
		Application object = null;
		try {
			Class<?> theClass = Class.forName(callingClassName);
			object = (Application) theClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Initialize application
		Application application = (Application)object;
		
		// Initialize GLFW
		if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		long handle = GLFW.glfwCreateWindow(300, 300, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL);
		if ( handle == MemoryUtil.NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		// NanoVG
		GLFW.glfwMakeContextCurrent(handle);
		GLFW.glfwSwapInterval(1);
		GL.createCapabilities();
		long vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
		
		// Create scene
		Window window = new Window(handle, vg);
		Pane root = new Pane();
		root.setBackgroundLegacy(null);
		window.setScene(new Scene(root));
		application.start(window.getScene());
		
		// Loop
		window.setVisible(true);
		loop(window);
		
		// Cleanup
		GLFW.glfwTerminate();
	}
	
	private static void loop(Window window) {
		
		while ( !GLFW.glfwWindowShouldClose(window.getHandle()) ) {
			GL11.glClearColor(0.9741f, 0.9741f, 0.9741f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

			window.render();
			
			GLFW.glfwSwapBuffers(window.getHandle());
			GLFW.glfwPollEvents();
		}
	}

	private static String getCallingClass(String methodName) {
		// Figure out the right class to call
		boolean foundThisMethod = false;
		String callingClassName = null;
		for (StackTraceElement se : Thread.currentThread().getStackTrace()) {
			// Skip entries until we get to the entry for this class
			String className = se.getClassName().replace("/", ".");
			String cmethodName = se.getMethodName();
			if (foundThisMethod) {
				callingClassName = className;
				break;
			} else if (Application.class.getName().equals(className) && methodName.equals(cmethodName)) {
				foundThisMethod = true;
			}
		}
		
		return callingClassName;
	}
	
	public abstract void start(Scene scene);
}
