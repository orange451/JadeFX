package io.jadefx.application;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfm.GLFM;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.mini.gui.GCallBack;

import io.jadefx.scene.Scene;
import io.jadefx.scene.Window;
import io.jadefx.scene.layout.Pane;

public abstract class Application {
	
	private static Map<Long,Integer> flushMap = new HashMap<>();
	
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
		launch(application);
	}
	
	protected static void launch(Application application) {
		GCallBack callback = GCallBack.getInstance();
		boolean isGLFM = "org.mini.glfm.GlfmCallBackImpl".equals(System.getProperty("gui.driver"));
		
		long handle;
		
		// Initialize GLFW
		if ( isGLFM ) {
			//callback.init(800, 600);
			handle = callback.getDisplay();
			if ( handle == MemoryUtil.NULL )
				throw new RuntimeException("Failed to create the GLFW window");
			
	        GLFM.glfmSetDisplayConfig(handle,
	        		GLFM.GLFMRenderingAPIOpenGLES3,
	        		GLFM.GLFMColorFormatRGBA8888,
	        		GLFM.GLFMDepthFormat16,
	        		GLFM.GLFMStencilFormat8,
	        		GLFM.GLFMMultisampleNone);
		} else {
			if (!GLFW.glfwInit())
				throw new IllegalStateException("Unable to initialize GLFW");
			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
			handle = GLFW.glfwCreateWindow(300, 300, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL);
			if ( handle == MemoryUtil.NULL )
				throw new RuntimeException("Failed to create the GLFW window");
			GLFW.glfwMakeContextCurrent(handle);
			GLFW.glfwSwapInterval(1);
			GL.createCapabilities();
		}
		
		// NanoVG
		long vg;
		boolean modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
				|| (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);
		if (modernOpenGL) {
			int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
			vg = NanoVGGL3.nvgCreate(flags);
		} else {
			int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
			vg = NanoVGGL2.nvgCreate(flags);
		}
		
		flushMap.put(handle, 10);
		
		// Create scene
		Window window = new Window(handle, vg);
		Pane root = new Pane();
		root.setBackgroundLegacy(null);
		window.setScene(new Scene(root));
		application.start(window.getScene());
		
		// Loop
		window.setVisible(true);
		if ( isGLFM ) {
	        GLFM.glfmSetRenderFuncCallback(handle, (display, frameTime) -> render(window));
		} else {
			loop(window);
	        cleanup(window);
		}
	}
	
	private static void loop(Window window) {
		while ( !GLFW.glfwWindowShouldClose(window.getHandle()) ) {
			
			render(window);
			
			GLFW.glfwSwapBuffers(window.getHandle());
			GLFW.glfwPollEvents();
		}
	}
	
	private static void render(Window window) {
		if ( window.getContext().isFlushed() )
			flushMap.put(window.getHandle(), 3);
			
		if (flushMap.get(window.getHandle()) < 0)
			return;
		
		flushMap.put(window.getHandle(), flushMap.get(window.getHandle())-1);
		
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
	
	private static void cleanup(Window window) {
		nvgDelete(window.getContext().getNVG());
        GL.setCapabilities(null);
        glfwFreeCallbacks(window.getHandle());
        glfwTerminate();
	}

	protected static String getCallingClass(String className, String methodName) {
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
	
	public abstract void start(Scene scene);
}
