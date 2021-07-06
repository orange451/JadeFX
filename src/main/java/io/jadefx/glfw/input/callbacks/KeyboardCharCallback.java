package io.jadefx.glfw.input.callbacks;

import java.util.ArrayList;
import java.util.List;

import io.jadefx.glfw.Callbacks.CharCallback;

public class KeyboardCharCallback extends CharCallback {
	private List<String> queue;
	private String lastChar = "";
	private long lastPress;
	private boolean enabled = false;

	public void setEnabled(boolean flag) {
		this.enabled = flag;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean hasData() {
		return (this.enabled && this.queue.size() > 0);
	}

	public List<String> getData() {
		if (!this.enabled)
			return null;
		List<String> data = new ArrayList<String>(this.queue);
		this.queue.clear();
		return data;
	}

	@Override
	public void invoke(long window, int codepoint) {
		if (this.enabled) {
			String charr;

			charr = new String(Character.toChars(codepoint));

			if (this.lastChar.equals(charr) && ((System.currentTimeMillis() - this.lastPress) < 50))
				return; // 0.05 seconds

			this.lastChar = charr;
			this.lastPress = System.currentTimeMillis();
			this.queue.add(charr);
		}
	}

}
