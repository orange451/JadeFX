package io.jadefx.scene.layout;

import io.jadefx.geometry.Pos;
import io.jadefx.geometry.VPos;
import io.jadefx.scene.Node;

public class HBox extends DirectionalBox {

	protected void layoutChildren() {
		double xStart = 0;
		for (int i = 0; i < this.children.size(); i++) {
			Node node = this.children.get(i);
			Pos useAlignment = node.usingAlignment();

			double yMult = 0;
			if ( useAlignment.getVpos() == VPos.CENTER)
				yMult = 0.5f;
			if ( useAlignment.getVpos() == VPos.BOTTOM)
				yMult = 1f;
			
			double yy = (this.getHeight()-node.getHeight())*yMult;
			node.setLocalPosition(xStart, yy);
			
			xStart += node.getWidth();
			xStart += spacing;
			
			System.out.println("HBOX UPDATING CHILD " + node.name() + " / " + node.getX() + " / " + node.getY());
		}
	}
	
	@Override
	protected double getMaxElementWidth() {
		return this.getCombinedElementWidth() + (Math.max(0, children.size()-1)*spacing);
	}
	
	@Override
	public String getElementType() {
		return "hbox";
	}
}
