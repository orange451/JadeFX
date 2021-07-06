package mobile.jadefx;

import org.mini.gui.GApplicationDisplay;
import org.mini.gui.GCallBack;
import org.mini.gui.GForm;
import org.mini.gui.GLFWApplicationDisplay;

import io.jadefx.geometry.ScreenOrientation;
import io.jadefx.scene.Scene;

public abstract class MobileApplication {
	
	static {
		String callingClassName = getCallingClass("<clinit>");

		// Force inject application to launcher (MiniJVM hack)
		if (callingClassName != null) {
			MobileApplication object = null;
			try {
				Class<?> theClass = Class.forName(callingClassName);
				object = (MobileApplication) theClass.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			
            GCallBack.getInstance().setApplication(new GJadeFXApplication(object));
		}
	}
	
	public static void launch(String[] args) {
		
		// Get the class name that called launch method.
		String callingClassName = getCallingClass("launch");
		if (callingClassName == null) {
			throw new RuntimeException("Error: unable to determine main class (2)");
		}
		
		// Create new instance of calling class
		MobileApplication object = null;
		try {
			Class<?> theClass = Class.forName(callingClassName);
			object = (MobileApplication) theClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Initialize mobile application
		MobileApplication application = (MobileApplication)object;
        GCallBack ccb = GCallBack.getInstance();
        ccb.init(800, 600);
        ccb.setApplication(new GJadeFXApplication(application));
        ccb.mainLoop();
        ccb.destroy();
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
			} else if (MobileApplication.class.getName().equals(className) && methodName.equals(cmethodName)) {
				foundThisMethod = true;
			}
		}
		
		return callingClassName;
	}
	
	private static GApplicationDisplay appDisplay = new GLFWApplicationDisplay();

	protected GForm createForm() {
		System.out.println("Creating form for: " + System.getProperty("gui.driver"));
		try {
			if ( "org.mini.glfm.GlfmCallBackImpl".equals(System.getProperty("gui.driver")) ) {
				GCallBack.getInstance();
				appDisplay = new org.mini.gui.GLFMApplicationDisplay();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		setOrientation(ScreenOrientation.ANY);

		GJadeFXForm form = new GJadeFXForm();
		Scene scene = form.getWindow().getScene();
		start(scene);

		return form;
	}
	
	public static void setOrientation(ScreenOrientation orientation) {
		if ( orientation == null )
			orientation = ScreenOrientation.ANY;
		
		appDisplay.setUserInterfaceOrientation(GCallBack.getInstance().getDisplay(), orientation.getValue());
	}
    
    public static void showStatusBar() {
    	appDisplay.setDisplayChrome(GCallBack.getInstance().getDisplay(), org.mini.glfm.Glfm.GLFMUserInterfaceChromeNavigationAndStatusBar);
    }
    
    public static void hideStatusBar() {
    	appDisplay.setDisplayChrome(GCallBack.getInstance().getDisplay(), org.mini.glfm.Glfm.GLFMUserInterfaceChromeFullscreen);
    }
    
    public static void showKeyboard() {
    	appDisplay.setKeyboardVisible(GCallBack.getInstance().getDisplay(), true);
    }
    
    public static void hideKeyboard() {
    	appDisplay.setKeyboardVisible(GCallBack.getInstance().getDisplay(), false);
    }
	
	public abstract void start(Scene scene);
}