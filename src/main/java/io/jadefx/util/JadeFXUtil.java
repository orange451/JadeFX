package io.jadefx.util;

import org.lwjgl.glfm.GLFM;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import io.jadefx.geometry.Insets;
import io.jadefx.gl.util.BoxShadowRenderer;
import io.jadefx.gl.util.LinearGradientRenderer;
import io.jadefx.paint.Color;
import io.jadefx.stage.Context;
import io.jadefx.style.Background;
import io.jadefx.style.BoxShadow;
import io.jadefx.style.ColorStop;

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
	
	public static void linearGradient(Context context, float angle, ColorStop[] stops, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;
		
		if ( context.isCoreOpenGL() ) {
			float xx = (float)x;
			float yy = (float)y;
			float ww = (float)width;
			float hh = (float)height;
			
			// Flip the y :shrug:
			yy = context.getWindow().getHeight() - yy - hh;
			
			// Save NANOVG
			NanoVG.nvgSave(context.getNVG());
			NanoVG.nvgEndFrame(context.getNVG());

			LinearGradientRenderer.render(context, angle, stops, xx, yy, ww, hh, cornerRadii);
			
			// Restore NANOVG
			NanoVG.nvgRestore(context.getNVG());
			context.refresh();
		} else {
			for (int i = 0; i < stops.length-1; i++) {
				// Compute position
				float centerx = (float)x + (float)width*0.5f;
				float centery = (float)y + (float)height*0.5f;
				float xx = centerx - (float)((Math.cos(Math.toRadians(angle)) * width) * 0.5 + 0.5);
				float yy = centery - (float)((Math.sin(Math.toRadians(angle)) * height) * 0.5 + 0.5);
				float dirX = (float) Math.cos(Math.toRadians(angle));
				float dirY = (float) Math.sin(Math.toRadians(angle));
				float step = stops[i].getRatio();
				float nextstep = stops[i+1].getRatio();
				float startX = (float)xx + (dirX * step * (float)width);
				float startY = (float)yy + (dirY * step * (float)height);
				float endX = (float)xx + (dirX * nextstep * (float)width);
				float endY = (float)yy + (dirY * nextstep * (float)height);
				
				// Compute colors
				Color c = stops[i].getColor();
				if ( i > 0 )
					c = Color.TRANSPARENT;
				Color e = stops[i+1].getColor();
				
				try(MemoryStack stack = MemoryStack.stackPush()) {
					// Create gradient paint
					NVGPaint grad = NanoVG.nvgLinearGradient(context.getNVG(), startX, startY, endX, endY, c.getNVG(), e.getNVG(), NVGPaint.mallocStack(stack));

					// Draw gradient
					NanoVG.nvgBeginPath(context.getNVG());
					NanoVG.nvgRoundedRectVarying(context.getNVG(), (int)x, (int)y, (int)width, (int)height, (float)cornerRadii[0], (float)cornerRadii[1], (float)cornerRadii[2], (float)cornerRadii[3]);
					NanoVG.nvgFillPaint(context.getNVG(), grad);
					NanoVG.nvgFill(context.getNVG());
					NanoVG.nvgClosePath(context.getNVG());
				}
			}
		}
	}

	public static void boxShadow(Context context, BoxShadow boxShadow, double x, double y, double width, double height, float[] cornerRadii, float borderWidth, float[] boxClip) {
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
			if ( boxClip != null )
				boxClip[1] = context.getWindow().getHeight() - boxClip[1] - boxClip[3];
			
			// Save NANOVG
			NanoVG.nvgSave(context.getNVG());
			NanoVG.nvgEndFrame(context.getNVG());

			BoxShadowRenderer.boxShadow(context, xx, yy, ww, hh, f, r, boxShadow.getFromColor(), cornerRadii, boxShadow.isInset(), boxClip);
			
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
	
	/**
	 * Returns a new nanovg context. If requestModernOpenGL is true then we will return a OpenGL 3.2-backed context if it is available on the system.
	 */
	public static long makeNanoVGContext(boolean requestModernOpenGL) {
		long vg;
		boolean modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
				|| (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);
		if (modernOpenGL && requestModernOpenGL) {
			int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
			vg = NanoVGGL3.nvgCreate(flags);
		} else {
			int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
			vg = NanoVGGL2.nvgCreate(flags);
		}
		
		return vg;
	}

	/**
	 * Create a GLFW window backed by OpenGL Core profile. GLVersion 3.3
	 */
	public static long createWindowGLFW(int width, int height, String title) {
		return createWindowGLFW(width, height, title, 3, 3);
	}

	/**
	 * Create a GLFW window backed by OpenGL Core profile. GLVersion is user supplied.
	 */
	public static long createWindowGLFW(int width, int height, String title, int major, int minor) {
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		return GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
	}

	/**
	 * Setup the default display properties for a GLFM window.
	 */
	public static void setupDefaultDisplayGLFM(long handle) {
		GLFM.glfmSetDisplayConfig(handle,
        		GLFM.GLFMRenderingAPIOpenGLES3,
        		GLFM.GLFMColorFormatRGBA8888,
        		GLFM.GLFMDepthFormat16,
        		GLFM.GLFMStencilFormat8,
        		GLFM.GLFMMultisampleNone);
	}
}
