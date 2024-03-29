package io.jadefx.application;

import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.joml.Vector2i;
import org.lwjgl.glfm.GLFM;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.mini.gui.GCallBack;

import io.jadefx.JadeFX;
import io.jadefx.stage.Stage;
import io.jadefx.util.JadeFXUtil;

public abstract class Application {
	
	public static void launch(String[] args) {
		// Get the class name that called launch method.
		String callingClassName = getCallingClass(Application.class.getName(), "launch");
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
		launch(application, args);
	}
	
	protected static void launch(Application application, String... args) {
		// Check if we're running on MiniJVM implementation
		GCallBack callback = null;
		boolean isGLFM = false;
		try {
			Class<?> miniGlfw = Class.forName("org.mini.glfw.Glfw");
			if ( miniGlfw == null )
				return;
			
			callback = GCallBack.getInstance();
			isGLFM = "org.mini.glfm.GlfmCallBackImpl".equals(System.getProperty("gui.driver"));
		} catch (ClassNotFoundException e) {
			//
		}
		
		long handle;
		
		// Initialize GLFM
		if ( isGLFM ) {
			handle = callback.getDisplay();
			if ( handle == MemoryUtil.NULL )
				throw new RuntimeException("Failed to create the GLFW window");
			
	        JadeFXUtil.setupDefaultDisplayGLFM(handle);
		} else { // Initialize GLFW
			if (!GLFW.glfwInit())
				throw new IllegalStateException("Unable to initialize GLFW");
			
			handle = JadeFXUtil.createWindowGLFW(application.getDefaultWindowSize().x, application.getDefaultWindowSize().y, "Window");
			if ( handle == MemoryUtil.NULL )
				throw new RuntimeException("Failed to create the GLFW window");

			GLFW.glfwMakeContextCurrent(handle);
			GLFW.glfwSwapInterval(1);
			GL.createCapabilities();
		}
		
		long vg = JadeFXUtil.makeNanoVGContext(true);
		
		// Create scene
        final Stage stage = JadeFX.create(application.newStage(handle, vg));
		if ( application instanceof MobileApplication ) {
			stage.setVisible(true);
		}
		application.preStart(stage, args);
		application.start(stage, args);
		
		// Loop
		if ( isGLFM ) {
			launchLoopGLFM(stage, handle);
		} else {
			launchLoopGLFW(stage, handle);
		}
	}
	
	private static void launchLoopGLFW(Stage stage, long handle) {
		while ( !GLFW.glfwWindowShouldClose(stage.getHandle()) ) {
			JadeFX.render();
		}
        cleanup(stage);
	}
	
	private static void launchLoopGLFM(Stage stage, long handle) {
        GLFM.glfmSetRenderFuncCallback(handle, (display, frameTime) -> JadeFX.render(stage));
	}
	
	protected Stage newStage(long handle, long vg) {
		return new Stage(handle, vg);
	}
	
	protected Vector2i getDefaultWindowSize() {
		return new Vector2i(800, 600);
	}
	
	private static void cleanup(Stage window) {
		JadeFX.cleanup(window);
        GL.setCapabilities(null);
        glfwTerminate();
	}

	private static String getCallingClass(String className, String methodName) {
		// Figure out the right class to call
		boolean foundThisMethod = false;
		String callingClassName = null;
		for (StackTraceElement se : Thread.currentThread().getStackTrace()) {
			String clazzName = se.getClassName().replace("/", ".");
			String cmethodName = se.getMethodName();
			
			if (foundThisMethod) {
				callingClassName = clazzName;
				break;
			} else if (className.equals(clazzName) && methodName.equals(cmethodName)) {
				foundThisMethod = true;
			}
		}
		
		return callingClassName;
	}
	
	protected void preStart(Stage window, String[] args) {
		// Override this if you want.
	}
	
	public abstract void start(Stage scene, String[] args);
}
