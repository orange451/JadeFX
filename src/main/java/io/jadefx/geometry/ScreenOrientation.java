package io.jadefx.geometry;

public enum ScreenOrientation {
	LANDSCAPE(2),
	PORTRAIT(1),
	ANY(0);

	private int val;

	ScreenOrientation(int val) {
		this.val = val;
	}

	public int getValue() {
		return this.val;
	}
}
