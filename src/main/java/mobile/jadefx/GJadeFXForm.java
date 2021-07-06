package mobile.jadefx;

import org.mini.gui.GForm;

import io.jadefx.scene.Scene;
import io.jadefx.scene.Window;
import io.jadefx.scene.layout.Pane;

public class GJadeFXForm extends GForm {
	
	private Window window;
	
	public GJadeFXForm() {
		// Create window
		this.window = new Window(this.display, this.callback.getNvContext());
		
		// Give it focus
		window.getWindowFocusCallback().invoke(window.getID(), true);

		// Resize when parent changes
		this.setSizeChangeListener((width, height) -> updateSize() );
		updateSize();

		Pane root = new Pane();
		root.setBackgroundLegacy(null);
		window.setScene(new Scene(root));
	}
	
	private void updateSize() {
		GForm.flush();
		
		window.getWindowSizeCallback().invoke(window.getID(), this.callback.getDeviceWidth(), this.callback.getDeviceHeight());
		window.getFramebufferSizeCallback().invoke(window.getID(), this.callback.getFrameBufferWidth(), this.callback.getFrameBufferHeight());
		
		if ( window.getScene() != null )
			window.getScene().dirty();
		
		if ( window.getContext() != null )
			display(window.getContext().getNVG());
		
		System.out.println("Upated screen size " + window.getWidth() + " / " + window.getHeight());
	}
	
	@Override
	public void display(long vg) {
		if ( window.getContext().isFlushed() )
			GForm.flush();
		
		super.display(vg);
	}
	
	@Override
	public boolean paint(long vg) {
		window.render();
		return true;
	}

	protected Window getWindow() {
		return this.window;
	}
}
