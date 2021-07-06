package io.jadefx.style;

import io.jadefx.scene.Context;

public abstract class Background {
	public abstract void render(Context context, double boundsX, double boundsY, double boundsW, double boundsH, float[] cornerRadii);
}
