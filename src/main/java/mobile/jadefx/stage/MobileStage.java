package mobile.jadefx.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfm.GLFM;

import io.jadefx.paint.Color;
import io.jadefx.stage.Stage;
import io.jadefx.util.JadeFXUtil;
import mobile.jadefx.Touch;
import mobile.jadefx.glfm.callbacks.TouchCallback;

public class MobileStage extends Stage {
	private TouchCallback touchCallback;
	
	private Map<Integer, Touch> activeTouches;

	public MobileStage(long handle, long vg) {
		super(handle, vg);
		
		this.activeTouches = new HashMap<>();
	}

	@Override
	protected void setCallbacks() {
		super.setCallbacks();
		
		touchCallback = new TouchCallback();
		this.getTouchCallback().addCallback(GLFM.glfmSetTouchCallback(this.getHandle(), touchCallback));
		this.getTouchCallback().addCallback((display, touchId, action, x, y) -> {
			MobileTouch touch = (MobileTouch) activeTouches.get(touchId);
			if ( touch == null ) {
				touch = new MobileTouch(touchId, action, x, x);
				activeTouches.put(touchId, touch);
			}
			
			touch.update(action, x, y);
			
			if ( action == GLFM.GLFMTouchPhaseEnded || action == GLFM.GLFMTouchPhaseCancelled )
				activeTouches.remove(touchId);
		});
	}
	
	public List<Touch> getActiveTouches() {
		List<Touch> touches = new ArrayList<>();
		for (Entry<Integer, Touch> touchSet : activeTouches.entrySet()) {
			touches.add(touchSet.getValue());
		}
		
		return touches;
	}

	public TouchCallback getTouchCallback() {
		return touchCallback;
	}

	private class MobileTouch extends Touch {
		public MobileTouch(int touchId, int action, double x, double y) {
			super(touchId, action, x, y);
		}
		
		public void update(int action, double x, double y) {
			this.action = action;
			this.x = x;
			this.y = y;
		}
	}
	
	@Override
	public void render() {
		super.render();
		
		for (Touch touch : getActiveTouches() ) {
			JadeFXUtil.fillRect(this.getContext(), touch.getX() - 8, touch.getY() - 8, 16, 16, Color.BLACK);
			this.getContext().flush();
		}
		
		//if ( this.getContext().getHoveredNodes().size() > 0 )
			//System.out.println(this.getContext().getHoveredNodes().get(0));
	}
}
