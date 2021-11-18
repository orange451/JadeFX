package io.jadefx.application;

import org.joml.Vector2i;
import org.lwjgl.glfm.GLFM;
import org.mini.gui.GApplicationDisplay;
import org.mini.gui.GCallBack;
import org.mini.gui.GLFMApplicationDisplay;

import io.jadefx.geometry.ScreenOrientation;
import io.jadefx.stage.Stage;
import mobile.jadefx.stage.MobileStage;

public abstract class MobileApplication extends Application {
	
	private static final GApplicationDisplay appDisplay = new GLFMApplicationDisplay();
	
	protected Vector2i getDefaultWindowSize() {
		return new Vector2i(375, 667); // This is not used by mobile devices, only when ran on desktop machines.
	}
	
	@Override
	protected Stage newStage(long handle, long vg) {
		return new MobileStage(handle, vg);
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
    
    public static void setMultitouchEnabled(boolean multitouchEnabled) {
    	if ( GCallBack.getInstance() == null || appDisplay == null )
    		return;
    	
    	appDisplay.setMultitouchEnabled(GCallBack.getInstance().getDisplay(), multitouchEnabled);
    }
}