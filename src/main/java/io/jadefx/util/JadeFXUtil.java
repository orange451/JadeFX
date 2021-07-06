package io.jadefx.util;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import io.jadefx.geometry.Insets;
import io.jadefx.gl.util.BoxShadowRenderer;
import io.jadefx.paint.Color;
import io.jadefx.scene.Context;
import io.jadefx.style.Background;
import io.jadefx.style.BoxShadow;

public class JadeFXUtil {

	public static void fillRoundRect(Context context, double x, double y, double width, double height, float c1, float c2, float c3, float c4, Color color) {
		long vg = context.getNVG();
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRoundedRectVarying(vg, (float)x, (float)y, (float)width, (float)height, c1, c2, c3, c4);
		if ( color != null )
			NanoVG.nvgFillColor(vg, color.getNVG());
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(vg);
	}
	
	public static void fillRect(Context context, double x, double y, double width, double height, Color color) {
		long vg = context.getNVG();
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, (float)x, (float)y, (float)width, (float)height);
		if ( color != null )
			NanoVG.nvgFillColor(vg, color.getNVG());
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(vg);
	}

	public static void fillRoundRect(Context context, double x, double y, double width, double height, float[] cornerRadii, Color color) {
		fillRoundRect(context, x, y, width, height, cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3], color);
	}

	public static void boxShadow(Context context, BoxShadow boxShadow, double x, double y, double width, double height, float[] cornerRadii, float borderWidth) {
		if ( context == null )
			return;
		
		float xx = (float)x - boxShadow.getSpread() + boxShadow.getXOffset();
		float yy = (float)y - boxShadow.getSpread() + boxShadow.getYOffset();
		float ww = (float)width + boxShadow.getSpread()*2;
		float hh = (float)height + boxShadow.getSpread()*2;
		
		if ( boxShadow.isInset() ) {
			xx += boxShadow.getSpread() * 2;
			yy += boxShadow.getSpread() * 2;
			ww -= boxShadow.getSpread() * 4;
			hh -= boxShadow.getSpread() * 4;
		}
		
		// Compute the average corner radius
		float averageCorner = 0;
		if ( cornerRadii != null ) {
			for (int i = 0; i < cornerRadii.length; i++)
				averageCorner += cornerRadii[i];
			averageCorner /= (float)cornerRadii.length;
		}
		
		// Compute feather (f) and radius (r)
		float f = boxShadow.getBlurRadius();
		float r = averageCorner + boxShadow.getSpread() + (borderWidth*0.5f);
		if ( boxShadow.isInset() )
			r = averageCorner - boxShadow.getSpread();
		
		if ( context.isCoreOpenGL() ) {
			// Flip the y :shrug:
			yy = context.getWindow().getHeight() - yy - hh;
			
			// Save NANOVG
			NanoVG.nvgSave(context.getNVG());
			NanoVG.nvgEndFrame(context.getNVG());

			BoxShadowRenderer.boxShadow(context, xx, yy, ww, hh, f, r, boxShadow.getFromColor(), cornerRadii, boxShadow.isInset());
			
			// Restore NANOVG
			NanoVG.nvgRestore(context.getNVG());
			context.refresh();
		} else {
			if ( boxShadow.isInset() ) {
				try(MemoryStack stack = MemoryStack.stackPush()) {
					NVGPaint paint = NanoVG.nvgBoxGradient(context.getNVG(), xx, yy, ww, hh, r, f, boxShadow.getToColor().getNVG(), boxShadow.getFromColor().getNVG(), NVGPaint.mallocStack(stack));
					NanoVG.nvgBeginPath(context.getNVG());
					NanoVG.nvgRoundedRectVarying(context.getNVG(), (float)x, (float)y, (float)width, (float)height, cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3]);
					NanoVG.nvgFillPaint(context.getNVG(), paint);
					NanoVG.nvgFill(context.getNVG());
					NanoVG.nvgClosePath(context.getNVG());
				}
			} else {
				try(MemoryStack stack = MemoryStack.stackPush()) {
					NVGPaint paint = NanoVG.nvgBoxGradient(context.getNVG(), xx, yy, ww, hh, r, f, boxShadow.getFromColor().getNVG(), boxShadow.getToColor().getNVG(), NVGPaint.mallocStack(stack));
					NanoVG.nvgBeginPath(context.getNVG());
					NanoVG.nnvgRect(context.getNVG(), xx - boxShadow.getBlurRadius(), yy - boxShadow.getBlurRadius(), ww + boxShadow.getBlurRadius()*2, hh + boxShadow.getBlurRadius()*2);
					NanoVG.nvgFillPaint(context.getNVG(), paint);
					NanoVG.nvgFill(context.getNVG());
					NanoVG.nvgClosePath(context.getNVG());
				}
			}
		}
	}

	public static void drawBorder(Context context, double x, double y, double width, double height, Insets border, Background background, Color borderColor, float[] borderRadii) {
		if ( context == null )
			return;
		
		float xx = (float) x;
		float yy = (float) y;
		float ww = (float) width;
		float hh = (float) height;
		long nvg = context.getNVG();
		if ( nvg <= 0 )
			return;

		float boundsTopLeft = (float) (border.getTop()+border.getLeft())/2f;
		float boundsTopRight = (float) (border.getTop()+border.getRight())/2f;
		float boundsBottomRight = (float) (border.getBottom()+border.getRight())/2f;
		float boundsBottomLeft = (float) (border.getBottom()+border.getLeft())/2f;
		if ( borderRadii[0] <= 0 )
			boundsTopLeft = 0;
		if ( borderRadii[1] <= 0 )
			boundsTopRight = 0;
		if ( borderRadii[2] <= 0 )
			boundsBottomRight = 0;
		if ( borderRadii[3] <= 0 )
			boundsBottomLeft = 0;
		
		// Force scissor
		//Bounds bounds = context.getClipBounds();
		//NanoVG.nvgScissor(nvg, (int)x, (int)y, (int)width, (int)height);
		
		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, borderColor.getNVG());

		float b1 = Math.max((borderRadii[0]) + boundsTopLeft, 0);
		float b2 = Math.max((borderRadii[1]) + boundsTopRight, 0);
		float b3 = Math.max((borderRadii[2]) + boundsBottomRight, 0);
		float b4 = Math.max((borderRadii[3]) + boundsBottomLeft, 0);
		NanoVG.nvgRoundedRectVarying(nvg, xx, yy, ww, hh, b1, b2, b3, b4);
		
		if ( background == null ) {
			xx += border.getLeft();
			yy += border.getTop();
			ww -= border.getWidth();
			hh -= border.getHeight();
			
			NanoVG.nvgPathWinding(nvg, NanoVG.NVG_CW);
			NanoVG.nvgRoundedRectVarying(nvg, xx, yy, ww, hh, borderRadii[0], borderRadii[1], borderRadii[2], borderRadii[3]);
			NanoVG.nvgPathWinding(nvg, NanoVG.NVG_CCW);
		}
		
		NanoVG.nvgFill(nvg);
		NanoVG.nnvgClosePath(nvg);

		// Reset scissor
		//NanoVG.nvgScissor(nvg, (float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
	}
}
