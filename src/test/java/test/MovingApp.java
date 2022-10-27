package test;

import io.jadefx.application.MobileApplication;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.stage.Stage;

public class MovingApp extends MobileApplication {
	
	@Override
	public void start(Stage stage, String[] args) {
		Pane card = new StackPane() {
			@Override
			public void position() {
				double x = Math.sin(System.currentTimeMillis()/1000d) + 1;
				this.setPrefWidth(x * 64 + 64);
				
				super.position();
			}
		};
		
		stage.getScene().setRoot(card);
		
		card.setStyle("border-radius: 8px;"
				+ "box-shadow: 4px 8px 32px 0px rgba(0, 0, 0, 0.4),"
				+ "			   2px 4px 8px  0px rgba(0, 0, 0, 0.2);"
				+ "background-color: rgb(220, 220, 220);"
				+ "height: 150px;");
		
		card.getChildren().add(new Label("Hello World"));
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
	