package io.jadefx.style;

import java.util.HashMap;
import java.util.Map;

import io.jadefx.collections.ObservableList;
import io.jadefx.geometry.Insets;
import io.jadefx.stage.Context;
import io.jadefx.util.JadeFXUtil;

public interface BlockPaneRenderer extends StyleBorder,StyleBackground,StyleBoxShadow {
	
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	
	static Map<Context, float[]> boxClips = new HashMap<>();
	
	public static void render(Context context, BlockPaneRenderer node) {
		
		// Compute box clip
		float[] boxClip = boxClips.get(context);
		if ( boxClip == null ) {
			boxClip = new float[4];
			boxClips.put(context, boxClip);
		}
		Insets border = node.getBorder();
		boxClip[0] = (float) (node.getX() + border.getLeft());
		boxClip[1] = (float) (node.getY() + border.getRight());
		boxClip[2] = (float) (node.getWidth() - border.getWidth());
		boxClip[3] = (float) (node.getHeight() - border.getHeight());
		
		// Draw drop shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( shadow.isInset() )
				continue;
			JadeFXUtil.boxShadow(context, shadow, node.getX(), node.getY(), node.getWidth(), node.getHeight(), border, node.getBorderRadii(), node.getBorderWidth(), boxClip);
		}
		
		// Draw border
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
			JadeFXUtil.boxShadow(context, shadow, node.getX()+border.getLeft(), node.getY()+border.getTop(), node.getWidth()-border.getWidth(), node.getHeight()-border.getHeight(), border, node.getBorderRadii(), node.getBorderWidth(), boxClip);
		}
	}
}
