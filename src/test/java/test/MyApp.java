package test;

import org.lwjgl.opengl.GL11;
import org.mini.gui.GCallBack;

import io.jadefx.application.MobileApplication;
import io.jadefx.geometry.Pos;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.scene.layout.VBox;
import io.jadefx.scene.text.Font;
import io.jadefx.stage.Stage;
import io.jadefx.util.OperatingSystem;

public class MyApp extends MobileApplication {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage, String[] args) {
		VBox thing = new VBox();
		thing.setSpacing(8);
		thing.setBackgroundLegacy(null);
		stage.getScene().setRoot(thing);

		for (int i = 0; i < 3; i++) {
			StackPane card = new VBox();
			card.setStyle("border-radius: 8px;" + "box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.2);" + "width: 150px;"
					+ "height: 150px;" + "background-color: white;");

			/*
			 * card.setStyle("border-radius: 8px;" +
			 * "box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.2)," +
			 * "			0px 6px 20px 0px rgba(0, 0, 0, 0.19);" + "width: 150px;" +
			 * "height: 150px;" + "background-color: white;");
			 */

			card.setAlignment(Pos.TOP_LEFT);
			thing.getChildren().add(card);

			{
				StackPane banner = new StackPane();
				banner.setAlignment(Pos.CENTER);
				if (i == 1) {
					banner.setStyle("width: 100%;" + "height: 70%;" + "background-color: #4CAF50;"
							+ "border-radius: 8px 8px 0px 0px;" + "box-shadow: 0px 0px 10px 0px #000000 inset;");
				} else {
					banner.setStyle("width: 100%;" + "height: 70%;" + "background-color: #4CAF50;"
							+ "border-radius: 8px 8px 0px 0px;");
				}
				card.getChildren().add(banner);

				// Label l1 = new Label("" + (i+1));
				Label l1 = new Label(i + " 你Hello");
				l1.setFont(new Font(l1.getFont().getFamily(), 32));
				l1.setStyle("color:white;");
				banner.getChildren().add(l1);
			}

			{
				StackPane footer = new StackPane();
				footer.setAlignment(Pos.CENTER);
				footer.setStyle("width: 100%;" + "height: 30%;" + "background-color:transparent;");
				card.getChildren().add(footer);

				Label l1 = new Label("This is the footer");
				footer.getChildren().add(l1);
			}
		}

		// GL Info
		String glVendor = new String(GL11.glGetString(GL11.GL_VENDOR));
		String glRenderer = new String(GL11.glGetString(GL11.GL_RENDERER));
		String glVersion = new String(GL11.glGetString(GL11.GL_VERSION));
		String opSystem = OperatingSystem.detect().toString();
		System.out.println("Vendor : " + glVendor);
		System.out.println("Renderer : " + glRenderer);
		System.out.println("Version : " + glVersion);
		System.out.println("Operating System : " + opSystem);
	}
}
