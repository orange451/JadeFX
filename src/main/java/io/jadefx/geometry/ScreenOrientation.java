package io.jadefx.geometry;

import org.mini.glfm.Glfm;

public enum ScreenOrientation {
	LANDSCAPE(Glfm.GLFMUserInterfaceOrientationLandscape),
	PORTRAIT(Glfm.GLFMUserInterfaceOrientationPortrait),
	ANY(Glfm.GLFMUserInterfaceOrientationAny);

	private int val;

	ScreenOrientation(int val) {
		this.val = val;
	}

	public int getValue() {
		return this.val;
	}
}
