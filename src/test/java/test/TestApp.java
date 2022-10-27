package test;

import io.jadefx.application.MobileApplication;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.stage.Stage;

public class TestApp extends MobileApplication {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage, String[] args) {
		Pane card = new StackPane();
		stage.getScene().setRoot(card);
		
		card.setStyle("border-radius: 8px;"
				+ "box-shadow: 4px 8px 32px 0px rgba(0, 0, 0, 0.4),"
				+ "			   2px 4px 8px  0px rgba(0, 0, 0, 0.2);"
				+ "background-color: rgb(220, 220, 220);"
				+ "width: 50%;"
				+ "height: 50%;"
				+ "alignment: center;");
		
		card.getChildren().add(new Label("Hello World"));
	}
}
	