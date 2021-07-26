package io.jadefx.application;

import org.joml.Vector2i;
import org.lwjgl.glfm.GLFM;
import org.mini.gui.GApplicationDisplay;
import org.mini.gui.GCallBack;
import org.mini.gui.GLFWApplicationDisplay;

import io.jadefx.geometry.ScreenOrientation;

public abstract class MobileApplication extends Application {
	
	private static GApplicationDisplay appDisplay;
	
	static {
		appDisplay = new GLFWApplicationDisplay();
		
		/*String callingClassName = getCallingClass(MobileApplication.class.getName(), "<clinit>");

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
			System.out.println("Could not hook in to MiniJVM.");
		}*/
	}
	
	protected Vector2i getDefaultWindowSize() {
		return new Vector2i(375, 667);
	}
	
	public static void setOrientation(ScreenOrientation orientation) {
		if ( orientation == null || appDisplay == null )
			orientation = ScreenOrientation.ANY;
		
		appDisplay.setUserInterfaceOrientation(GCallBack.getInstance().getDisplay(), orientation.getValue());
	}
    
    public static void showStatusBar() {
    	if ( GCallBack.getInstance() == null || appDisplay == null )
    		return;
    	
    	appDisplay.setDisplayChrome(GCallBack.getInstance().getDisplay(), GLFM.GLFMUserInterfaceChromeNavigationAndStatusBar);
    }
    
    public static void hideStatusBar() {
    	if ( GCallBack.getInstance() == null || appDisplay == null )
    		return;
    	
    	appDisplay.setDisplayChrome(GCallBack.getInstance().getDisplay(), GLFM.GLFMUserInterfaceChromeFullscreen);
    }
    
    public static void showKeyboard() {
    	if ( GCallBack.getInstance() == null || appDisplay == null )
    		return;
    	
    	appDisplay.setKeyboardVisible(GCallBack.getInstance().getDisplay(), true);
    }
    
    public static void hideKeyboard() {
    	if ( GCallBack.getInstance() == null || appDisplay == null )
    		return;
    	
    	appDisplay.setKeyboardVisible(GCallBack.getInstance().getDisplay(), false);
    }
}