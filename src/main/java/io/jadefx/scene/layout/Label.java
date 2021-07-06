package io.jadefx.scene.layout;

import io.jadefx.scene.control.Labeled;

public class Label extends Labeled {
	
	public Label() {
		super();
	}
	
	public Label(String string) {
		super(string);
	}

	@Override
	public String getElementType() {
		return "label";
	}
}
