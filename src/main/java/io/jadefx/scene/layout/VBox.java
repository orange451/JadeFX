package io.jadefx.scene.layout;

import io.jadefx.geometry.HPos;
import io.jadefx.geometry.Pos;
import io.jadefx.scene.Node;

public class VBox extends DirectionalBox {

	protected void layoutChildren() {
		if ( !hasFlag(FLAG_LAYOUT_DIRTY) )
			return;
		
		double yStart = 0;
		for (int i = 0; i < this.children.size(); i++) {
			Node node = this.children.get(i);
			Pos useAlignment = node.usingAlignment();

			double xMult = 0;
			if ( useAlignment.getHpos() == HPos.CENTER)
				xMult = 0.5f;
			if ( useAlignment.getHpos() == HPos.RIGHT)
				xMult = 1;
			
			double xx = (this.getWidth()-node.getWidth())*xMult;
			node.setLocalPosition(xx, yStart);
			
			yStart += node.getHeight();
			yStart += spacing;
		}
	}
	
	@Override
	protected double getMaxElementHeight() {
		return this.getCombinedElementHeight() + (Math.max(0, children.size()-1)*spacing);
	}
	
	@Override
	public String getElementType() {
		return "vbox";
	}
}
