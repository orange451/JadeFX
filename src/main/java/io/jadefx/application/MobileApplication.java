package io.jadefx.application;

import org.lwjgl.glfm.GLFM;
import org.mini.gui.GApplicationDisplay;
import org.mini.gui.GCallBack;
import org.mini.gui.GLFWApplicationDisplay;

import io.jadefx.geometry.ScreenOrientation;
import io.jadefx.scene.Scene;

public abstract class MobileApplication extends Application {
	
	static {
		String callingClassName = getCallingClass(MobileApplication.class.getName(), "<clinit>");

		// Force inject application to launcher
		if (callingClassName != null) {
			MobileApplication object = null;
			try {
				Class<?> theClass = Class.forName(callingClassName);
				System.out.println("Found mobile application: " + theClass);
				object = (MobileApplication) theClass.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			// Initialize application
			Application application = (Application)object;
			launch(application);
		} else {
			System.out.println("Could not find calling class...");
		}
	}
	
	private static GApplicationDisplay appDisplay = new GLFWApplicationDisplay();
	
	public static void setOrientation(ScreenOrientation orientation) {
		if ( orientation == null )
			orientation = ScreenOrientation.ANY;
		
		appDisplay.setUserInterfaceOrientation(GCallBack.getInstance().getDisplay(), orientation.getValue());
	}
    
    public static void showStatusBar() {
    	appDisplay.setDisplayChrome(GCallBack.getInstance().getDisplay(), GLFM.GLFMUserInterfaceChromeNavigationAndStatusBar);
    }
    
    public static void hideStatusBar() {
    	appDisplay.setDisplayChrome(GCallBack.getInstance().getDisplay(), GLFM.GLFMUserInterfaceChromeFullscreen);
    }
    
    public static void showKeyboard() {
    	appDisplay.setKeyboardVisible(GCallBack.getInstance().getDisplay(), true);
    }
    
    public static void hideKeyboard() {
    	appDisplay.setKeyboardVisible(GCallBack.getInstance().getDisplay(), false);
    }
	
	public abstract void start(Scene scene);
}