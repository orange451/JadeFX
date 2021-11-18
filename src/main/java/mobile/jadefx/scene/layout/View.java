package mobile.jadefx.scene.layout;

import io.jadefx.paint.Color;
import io.jadefx.scene.Node;
import io.jadefx.scene.layout.BorderPane;

public class View extends BorderPane {
	
	public View() {
		this.setBackgroundLegacy(Color.WHITE);
	}
	
	public View(Node content) {
		this();
		this.setCenter(content);
	}
	
	@Override
	public String getElementType() {
		return "view";
	}
}
