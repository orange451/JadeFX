package mobile.jadefx.glfm.callbacks;

import org.lwjgl.glfm.GLFMTouchCallbackI;

import io.jadefx.glfw.Callback;

public class TouchCallback extends Callback<GLFMTouchCallbackI> implements GLFMTouchCallbackI {
	@Override
	public void invoke(long window, int touchId, int action, double x, double y) {
		for (GLFMTouchCallbackI callback : callbacks)
			callback.invoke(window, touchId, action, x, y);
	}
}
