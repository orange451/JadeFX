package io.jadefx.scene.control;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;

import io.jadefx.collections.ObservableList;
import io.jadefx.paint.Color;
import io.jadefx.scene.Context;
import io.jadefx.scene.text.Font;
import io.jadefx.style.Background;
import io.jadefx.style.BackgroundSolid;
import io.jadefx.style.Shadow;
import io.jadefx.style.StyleBackground;
import io.jadefx.style.StyleOperationDefinitions;
import io.jadefx.style.Stylesheet;

public abstract class Labeled extends Control implements StyleBackground {
	private String text;
	
	private ByteBuffer textInternal;
	
	private float[] textBounds;
	
	private float[] textBoundsActual;
	
	private boolean wrapText;
	
	private String elipsis;
	
	private Color textColor = Color.BLACK;
	
	private Font font;
	
	private ObservableList<Shadow> textShadow = new ObservableList<>();

	private ObservableList<Background> backgrounds = new ObservableList<>();
	
	public Labeled() {
		this(null);
	}
	
	public Labeled(String text) {
		this.setText(text);
		this.setElipsisString("\u2026");
		this.setFont(new Font("Roboto", 18));
		
        backgrounds.setAddCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        backgrounds.setRemoveCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        textShadow.setAddCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
        textShadow.setRemoveCallback((e)->this.setFlag(FLAG_CSS_DIRTY));
	}
	
	public void setElipsisString(String string) {
		this.elipsis = string;
		this.dirty();
	}

	public void setText(String text) {
		this.text = text;
		this.textInternal = null;
		this.textBounds = null; // reset this because we need to generate new one next render.
		this.dirty();
	}

	public String getText() {
		return this.text;
	}

	/**
	 * Sets the color for the text of this label.
	 */
	public void setTextFill(Color color) {
		this.textColor = color;
	}

	/**
	 * Returns the color of the text of this label.
	 */
	public Color getTextFill() {
		return this.textColor;
	}
	

	public void setFont(Font font) {
		if ( this.font != null && this.font.equals(font) )
			return;
		
		this.font = font;
		this.textBounds = null; // reset this because we need to generate new one next render.
		this.dirty();
	}

	public Font getFont() {
		return this.font;
	}
	
	@Override
	protected void stylePush() {
		super.stylePush();
		Stylesheet.findAndApplyStyle(this.getScene().getContext().getCurrentStyling(), this, this, StyleOperationDefinitions.FONT_SIZE, StyleOperationDefinitions.COLOR);
	}
	
	protected void stylePop() {
		super.stylePop();
		Stylesheet.findAndApplyStyle(this.getScene().getContext().getCurrentStyling(), this, this, StyleOperationDefinitions.FONT_SIZE, StyleOperationDefinitions.COLOR);
	}
	
	@Override
	protected void size() {
		// Get initial text bounds
		if ( textBounds == null )
			textBounds = computeTextBounds2(this.text);

		if ( textBoundsActual == null )
			textBoundsActual = new float[textBounds.length];
		
		textBoundsActual[0] = textBounds[0];
		textBoundsActual[1] = textBounds[1];
		textBoundsActual[2] = textBounds[2];
		textBoundsActual[3] = textBounds[3];
		
		// Normal sizing routine
		super.size();
		
		// Add elipsis if text is too long!
		float textWidth = textBoundsActual[2] - textBoundsActual[1];
		if ( textWidth > this.getWidth() ) {
			bindFont(this.getScene().getContext().getNVG());
			
			ByteBuffer newText = null;
			for (int i = 0; i < this.text.length(); i++) {
				String tempString = this.text.substring(0, i) + elipsis;
				ByteBuffer tempBytes = toUtf8(tempString);
				float tempWid = NanoVG.nvgTextBounds(this.getScene().getContext().getNVG(), 0, 0, tempBytes, new float[4]);
				if ( tempWid <= this.getWidth() ) {
					newText = tempBytes;
					textWidth = tempWid;
				} else {
					break;
				}
			}
			
			this.textInternal = newText;
			textBoundsActual[2] = textBoundsActual[1] + textWidth;
		} else {
			boolean isUpdated = this.textInternal != null && this.text != null && textInternal.capacity() < this.text.length();
			if ( isUpdated || this.textInternal == null )
				this.textInternal = toUtf8(text);
		}
	}
	
	private void bindFont(long vg) {
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
		NanoVG.nvgFontSize(vg, (int) this.getFont().getSize());
		NanoVG.nvgFontFace(vg, this.getFont().getFamily());
	}
	
	protected float[] computeTextBounds2(String usingText) {
		float[] bounds = new float[4];
		if ( this.getScene() == null )
			return bounds;
		
		bindFont(this.getScene().getContext().getNVG());
		
		try {
			textInternal = toUtf8(usingText);
		} catch (Exception e) {
			//
		}
		
		if ( wrapText ) {
			float breakRowWidth = (float) Math.min(this.getAvailableSize().x, this.getMaxWidth());
			NanoVG.nvgTextBoxBounds(this.getScene().getContext().getNVG(), 0, 0, breakRowWidth, textInternal, bounds);
		} else {
			NanoVG.nvgTextBounds(this.getScene().getContext().getNVG(), 0, 0, textInternal, bounds);
		}
		
		return bounds;
	}

	private ByteBuffer toUtf8(String s) {
		if (s == null) {
			return null;
		}
		int pos = s.lastIndexOf('\000');
		if (pos >= 0 && pos == s.length() - 1) {
			//
		} else {
			s += '\000';
		}
		
		byte[] barr = null;
		try {
			barr = s.getBytes("utf-8");
		} catch (UnsupportedEncodingException ex) {
			//
		}
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(barr.length);
		for (int i = 0; i< barr.length; i++)
			buffer.put(barr[i]);
		buffer.flip();
		return buffer;
	}
	
	@Override
	protected double getMaxElementWidth() {
		if ( textBounds == null )
			return 0;
		return textBounds[2]-textBounds[0];
	}
	
	@Override
	protected double getMaxElementHeight() {
		if ( textBounds == null )
			return 0;
		return textBounds[3]-textBounds[1];
	}

	/*@Override
	protected double computePrefWidth() {
		float textSize = getTextBounds()[2]-getTextBounds()[0];
		textSize += this.padding.getWidth();
		
		double computedSize = super.computePrefWidth();
		
		if ( this.getPrefWidth() == 0 )
			return Math.max(textSize, computedSize);
		else
			return computedSize;
	}
	
	@Override
	protected double computePrefHeight() {
		float textSize = getTextBounds()[3]-getTextBounds()[1];
		textSize += this.padding.getHeight();
		
		double computedSize = super.computePrefHeight();
		
		if ( this.getPrefHeight() == 0 )
			return Math.max(textSize, computedSize);
		else
			return computedSize;
	}*/
	
	protected float[] getTextBounds() {
		if ( this.textBoundsActual == null )
			return new float[4];
		
		return this.textBoundsActual;
	}
	
	/**
	 * Return the list of text shadows.
	 * @return
	 */
	public ObservableList<Shadow> getTextShadowList() {
		return this.textShadow;
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */	
	public void setBackground(Background color) {
		for (int i = 0; i < backgrounds.size(); i++) {
			if ( backgrounds.get(i) instanceof BackgroundSolid ) {
				backgrounds.remove(i--);
			}
		}
		
		this.setFlag(FLAG_CSS_DIRTY);
		
		if ( color != null )
			getBackgrounds().add(0, color);
	}
	
	/**
	 * Get the current background color of this node.
	 * @return
	 */
	public Background getBackground() {
		if ( this.backgrounds.size() == 0 )
			return null;
		
		return this.backgrounds.get(0);
	}
	
	/**
	 * Get list of backgrounds used for drawing.
	 */
	public ObservableList<Background> getBackgrounds() {
		return this.backgrounds;
	}

	@Override
	public void render(Context context) {
		if ( textInternal == null ) {
			return;
		}
		
		long vg = context.getNVG();
		
		// get Absolute position
		float absX = (float)(getX() + this.getPadding().getLeft());
		float absY = (float)(getY() + this.getPadding().getTop());
		
		// Draw background(s)
		ObservableList<Background> backgrounds = getBackgrounds();
		for (int i = 0; i < backgrounds.size(); i++) {
			backgrounds.get(i).render(context, getX(), getY(), getWidth()-border.getWidth(), getHeight()-border.getHeight(), new float[4]);
		}
		
		// Setup font
		bindFont(vg);
		
		for (int i = 0; i < textShadow.size(); i++) {
			Shadow shadow = textShadow.get(i);
			NanoVG.nvgFontBlur(vg, shadow.getBlurRadius());
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgFillColor(vg, shadow.getFromColor().getNVG());
			NanoVG.nvgText(vg, absX+shadow.getXOffset(), absY+shadow.getYOffset(), textInternal);
			NanoVG.nvgClosePath(vg);
		}

		// Draw
		NanoVG.nvgFontBlur(vg, 0);
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgFillColor(vg, this.getTextFill().getNVG());
		NanoVG.nvgText(vg, absX, absY, textInternal);
		NanoVG.nvgClosePath(vg);
	}
}
