package mobile.jadefx;

public class Touch {
	protected double x;
	protected double y;
	protected int action;
	protected int touchId;
	
	public Touch(int touchId, int action, double x, double y) {
		this.touchId = touchId;
		this.action = action;
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public int getAction() {
		return this.action;
	}
	
	public int getTouchId() {
		return this.touchId;
	}
}
