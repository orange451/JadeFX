import io.jadefx.application.MobileApplication;
import io.jadefx.scene.Scene;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.HBox;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.stage.Stage;

public class MobileApplicationTest extends MobileApplication {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage, String[] args) {
		// Create buttons
		HBox box = new HBox();
		for (int i = 0; i < 2; i++) {
			Pane button = new StackPane();
			button.getClassList().add("test-button");
			button.getChildren().add(new Label("Button " + (i+1)));
			box.getChildren().add(button);
		}
		
		// Set scene with buttons
		stage.setScene(new Scene(box, 320, 240));
		
		// Apply stylesheet to scene
		stage.getScene().setStylesheet(""
				+ "scene {"
				+ "		background-color: #FFF;"
				+ "		align: center;"
				+ "		font-family: Google Sans;"
				+ "		font-size: 18px;"
				+ "}"
				+ ""
				+ "hbox {"
				+ "		spacing: 16px;"
				+ "		align: center;"
				+ "}"
				+ ""
				+ ".test-button {"
				+ "		box-shadow: 0 2px 3px -2px rgba(0, 0, 0, .4),"
				+ "					0 4px 8px -2 rgba(0, 0, 0, .1),"
				+ "					0 1px 18px -2 rgba(0, 0, 0, .04);"
				+ "		border-radius: 24px;"
				+ "		border-style: none;"
				+ "		color: #3c4043;"
				+ "		padding: 16px 32px;"
				+ "		alignment: center;"
				+ "		background-color: #fff;"
				+ "		border-width: 2px;"
				+ "		transition: background-color 0.1s, box-shadow 0.1s, border-color 0.1s;"
				+ "}"
				+ ""
				+ ".test-button:hover {"
				+ "		background-color: #F6F9FE;"
				+ "		color: #174ea6;"
				+ "}"
				+ ""
				+ ".test-button:focus {"
				+ "		border-style: none;"
				+ "}"
				+ ""
				+ ".test-button:active {"
				+ "		border-style: solid;"
				+ "		border-color: #4285f4;"
				+ "		box-shadow: 0px 4px 6px -2px rgba(60, 64, 67, 0.2),"
				+ "					0px 8px 12px 2px rgba(60, 64, 67, 0.1);"
				+ "}");
		
		// Turn stage on
		stage.show();
	}
}
