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
	
	@Override
	public boolean equals(Object object) {
		if ( object == null )
			return false;
		
		if ( !(object instanceof Font) )
			return false;
		
		Font font = (Font)object;
		if ( !font.name.equals(name) )
			return false;
		
		if ( font.size != size )
			return false;
			
		return true;
	}
}
