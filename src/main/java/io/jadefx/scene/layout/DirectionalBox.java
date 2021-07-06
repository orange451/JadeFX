package io.jadefx.scene.layout;

import io.jadefx.geometry.Pos;

public abstract class DirectionalBox extends Pane implements Spacable {
	protected float spacing;
	
	public DirectionalBox() {
		this.setAlignment(Pos.TOP_LEFT);
	}
	
	public void setSpacing(double d) {
		this.spacing = (float) d;
	}
	
	public double getSpacing() {
		return this.spacing;
	}
}
