package io.jadefx.stage;

import io.jadefx.JadeFX;
import io.jadefx.util.JadeFXUtil;

public class Stage extends Window {

	public Stage() {
		this(JadeFXUtil.createWindowGLFW(300, 300, "Window"), JadeFXUtil.makeNanoVGContext(true));
		JadeFX.create(this.getHandle(), this.getContext().getNVG());
	}
	
	public Stage(long handle, long nvgContext) {
		super(handle, nvgContext);
	}
}
