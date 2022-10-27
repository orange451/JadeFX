package test;

import io.jadefx.application.MobileApplication;
import io.jadefx.paint.Color;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.HBox;
import io.jadefx.scene.layout.VBox;
import io.jadefx.stage.Stage;

public class VBoxTest extends MobileApplication {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage, String[] args) {
		VBox vbox = new VBox();
		HBox hbox = new HBox();

		vbox.setPrefSize(16, 16);
		hbox.setPrefSize(16, 16);
		stage.getScene().setRoot(vbox);
		
		
		vbox.setBackgroundLegacy(Color.LIGHT_GRAY);
		hbox.setBackgroundLegacy(Color.DIM_GRAY);
		
		hbox.getChildren().add(new Label("Hello 1"));
		hbox.getChildren().add(new Label("Hello 2"));
		hbox.getChildren().add(new Label("Hello 3"));

		vbox.getChildren().add(new Label("Test 1"));
		vbox.getChildren().add(new Label("Test 2"));
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(new Label("Test 4"));
	}
}