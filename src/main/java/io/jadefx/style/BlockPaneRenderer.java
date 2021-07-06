package io.jadefx.style;

import io.jadefx.collections.ObservableList;
import io.jadefx.geometry.Insets;
import io.jadefx.scene.Context;
import io.jadefx.util.JadeFXUtil;

public interface BlockPaneRenderer extends StyleBorder,StyleBackground,StyleBoxShadow {
	
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	
	public static void render(Context context, BlockPaneRenderer node) {
		
		// Draw drop shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( shadow.isInset() )
				continue;
			JadeFXUtil.boxShadow(context, shadow, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getBorderRadii(), node.getBorderWidth());
		}
		
		// Draw border
		Insets border = node.getBorder();
		if ( node.getBorderStyle() != BorderStyle.NONE && (border.getWidth() > 0 || border.getHeight() > 0) && node.getBorderColor() != null ) {
			JadeFXUtil.drawBorder(context, node.getX(), node.getY(), node.getWidth(), node.getHeight(), border, node.getBackground(), node.getBorderColor(), node.getBorderRadii() );
		}
		
		// Draw background(s)
		ObservableList<Background> backgrounds = node.getBackgrounds();
		for (int i = 0; i < backgrounds.size(); i++) {
			backgrounds.get(i).render(context, node.getX()+border.getLeft(), node.getY()+border.getTop(), node.getWidth()-border.getWidth(), node.getHeight()-border.getHeight(), node.getBorderRadii());
		}
		
		// Draw inset shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( !shadow.isInset() )
				continue;
			JadeFXUtil.boxShadow(context, shadow, node.getX()+border.getLeft(), node.getY()+border.getTop(), node.getWidth()-border.getWidth(), node.getHeight()-border.getHeight(), node.getBorderRadii(), node.getBorderWidth());
		}
	}
}
