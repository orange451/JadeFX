package io.jadefx.scene.control;

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
