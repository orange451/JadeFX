package io.jadefx.style;

import io.jadefx.collections.ObservableList;

public interface StyleBackground {
	public Background getBackground();
	public void setBackground(Background color);
	
	public ObservableList<Background> getBackgrounds();
}
