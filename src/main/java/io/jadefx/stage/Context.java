package io.jadefx.stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import io.jadefx.style.Stylesheet;

public class Context {

	private final Window window;

	private long nvgContext;

	private boolean modernOpenGL;
	private boolean isCore = true;
	
	private boolean flush;

	private List<Stylesheet> currentSheets = new ArrayList<>();
	
	private DefaultFonts fonts;

	private boolean loaded = false;
	
	public Context(Window window) {
		this(window, -1);
	}
	
	public Context(Window window, long nvgContext) {
		this.window = window;
		this.nvgContext = nvgContext;
		try {
			this.fonts = new DefaultFonts(nvgContext);
			NanoVG.nvgFontFace(nvgContext, fonts.ROBOTO.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.loaded = true;
	}

	public void init() {
		if ( nvgContext > -1 )
			return;
		
		this.modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
				|| (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);

		if (this.isModernOpenGL()) {
			int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
			nvgContext = NanoVGGL3.nvgCreate(flags);
		} else {
			int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
			nvgContext = NanoVGGL2.nvgCreate(flags);
		}

		isCore = true;
	}

	public void dispose() {
		if (this.isModernOpenGL()) {
			NanoVGGL3.nvgDelete(nvgContext);
		} else {
			NanoVGGL2.nvgDelete(nvgContext);
		}
	}

	/**
	 * Returns the internal NanoVG pointer.
	 * 
	 * @return
	 */
	public long getNVG() {
		return nvgContext;
	}

	/**
	 * Force resets the OpenGL Viewport to fit the window.
	 */
	public void refresh() {
		GL11.glViewport(0, 0, (int) (window.getWidth() * window.getPixelRatio()), (int) (window.getHeight() * window.getPixelRatio()));
	}
	
	/**
	 * Force the context into a flush state. When flushed, the next frame update will be forced to re-render the scene.
	 */
	public void flush() {
		flush = true;
	}
	
	/**
	 * Returns whether the context is in flushed state. See {@link #flush()}.
	 */
	public boolean isFlushed() {
		return flush;
	}
	
	/**
	 * Returns whether the context is loaded and ready to use.
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Hidden method to set the flush state of the context.
	 */
	protected void setFlushed(boolean flush) {
		this.flush = flush;
	}

	/**
	 * Returns whether the internal renderer is using modern opengl (OpenGL 3.2+)
	 * 
	 * @return
	 */
	public boolean isModernOpenGL() {
		return this.modernOpenGL;
	}

	/**
	 * Returns whether the window was created with a core OpenGL profile or not.
	 * 
	 * @return
	 */
	public boolean isCoreOpenGL() {
		return this.isCore;
	}

	/**
	 * Get the window associated with this context
	 */
	public Window getWindow() {
		return this.window;
	}

	/**
	 * Returns the current list of stylesheets used for rendering the node.
	 * 
	 * @return
	 */
	public List<Stylesheet> getCurrentStyling() {
		return currentSheets;
	}
	
	private static InputStream inputStream(String path) throws IOException {
		InputStream stream;
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			stream = new FileInputStream(file);
		} else {
			stream = Context.class.getClassLoader().getResourceAsStream(path);
		}
		return stream;
	}
	
	private static byte[] toByteArray(InputStream stream, int bufferSize) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[bufferSize];

		try {
			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
		ByteBuffer data = null;
		InputStream stream = inputStream(resource);
		if (stream == null) {
			throw new FileNotFoundException(resource);
		}
		byte[] bytes = toByteArray(stream, stream.available());
		data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes);
		data.flip();
		return data;
	}
}

class DefaultFonts {
	public final FontData ROBOTO;
	
	private Map<String, FontData> loadedFonts = new HashMap<>();
	
	protected static Map<String, ByteBuffer> fontData = new HashMap<>();
	
	public DefaultFonts(long vg) throws IOException {
		add(ROBOTO = new FontData(vg, "Roboto", "jadefx/font/Roboto-Regular.ttf"));

		fallback(vg, new FontData(vg, "NotoColorEmoji", "jadefx/font/NotoColorEmoji.ttf"));
		fallback(vg, new FontData(vg, "NotoSans", "jadefx/font/NotoSansCJKsc-Medium.otf"));
		fallback(vg, new FontData(vg, "Entypo", "jadefx/font/entypo.ttf"));
	}

	private void add(FontData fontData) {
		loadedFonts.put(fontData.getName(), fontData);
	}
	
	private void fallback(long vg, FontData fallback) {
		for (Entry<String, FontData> entry : loadedFonts.entrySet()) {
			NanoVG.nvgAddFallbackFontId(vg, entry.getValue().getHandle(), fallback.getHandle());
		}
	}
}

class FontData {
	private String name;
	private int handle;
	
	public FontData(long vg, String name, String resourcePath) throws IOException {
		this.name = name;
		
		ByteBuffer data = DefaultFonts.fontData.get(name);
		if ( data == null ) {
			data = Context.ioResourceToByteBuffer(resourcePath);
			DefaultFonts.fontData.put(name, data);
		}
		
		this.handle = NanoVG.nvgCreateFontMem(vg, name, data, 0);
	}
	
	public int getHandle() {
		return this.handle;
	}
	
	public String getName() {
		return this.name;
	}
}
