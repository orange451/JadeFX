package mobile.jadefx;

import org.mini.gui.GApplication;
import org.mini.gui.GForm;

public class GJadeFXApplication extends GApplication {
	
	private GForm form;
	
	private MobileApplication application;
	
	public GJadeFXApplication(MobileApplication application) {
		this.application = application;
	}

	@Override
	public GForm getForm() {
		if ( application == null )
			return null;
		
		if ( form != null )
			return form;
		
		form = application.createForm();
		return form;
	}
}
