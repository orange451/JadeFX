import io.jadefx.application.Application;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.HBox;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.stage.Stage;

public class PrettyShading extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		HBox box = new HBox();
		
		for (int i = 0; i < 3; i++) {
			Pane button = new StackPane();
			button.getClassList().add("box");
			button.getChildren().add(new Label("Button " + (i+1)));
			box.getChildren().add(button);
		}
		
		stage.getScene().setStylesheet(""
				+ "scene {"
				+ "		background-color: #FFF;"
				+ "}"
				+ ""
				+ "hbox {"
				+ "		spacing: 8px;"
				+ "}"
				+ ""
				+ ".box {"
				+ "		box-shadow: 0 2px 3px -2px rgba(0, 0, 0, .4),"
				+ "					0 4px 8px 0 rgba(0, 0, 0, .1),"
				+ "					0 1px 18px 0 rgba(0, 0, 0, .04);"
				+ "		border-radius: 24px;"
				+ "		border-style: none;"
				+ "		color: #3c4043;"
				+ "		padding: 16px 32px;"
				+ "		alignment: center;"
				+ "		background-color: #fff;"
				+ "		font-family: Google Sans;"
				+ "		font-size: 18px;"
				+ "		border-width: 2px;"
				+ "		transition: background-color 0.1s, box-shadow 0.1s, border-color 0.1s;"
				+ "}"
				+ ""
				+ ".box:hover {"
				+ "		background-color: #F6F9FE;"
				+ "		color: #174ea6;"
				+ "}"
				+ ""
				+ ".box:focus {"
				+ "		border-style: none;"
				+ "}"
				+ ""
				+ ".box:active {"
				+ "		border-style: solid;"
				+ "		border-color: #4285f4;"
				+ "		box-shadow: 0px 4px 6px 0px rgba(60, 64, 67, 0.2),"
				+ "					0px 8px 12px 3px rgba(60, 64, 67, 0.1);"
				+ "}");
		((Pane)stage.getScene().getRoot()).getChildren().add(box);
	}
}
