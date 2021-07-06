package io.jadefx.scene.text;

public class Font {
	private String name;
	private double size;
	
	public Font(double size) {
		this("Roboto", size);
	}
	
	public Font(String name, double size) {
		this.name = name;
		this.size = size;
	}
	
	public String getFamily() {
		return this.name;
	}
	
	public double getSize() {
		return this.size;
	}
}
