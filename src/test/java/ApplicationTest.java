import io.jadefx.application.Application;
import io.jadefx.scene.layout.Pane;
import io.jadefx.stage.Stage;

public class ApplicationTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Pane pane = new Pane() {
			@Override
			public void size() {
				super.size();
				
			}
		};
		stage.getScene().setStylesheet(""
				+ ".box {"
				+ "		box-shadow: 4px 8px 32px 0px rgba(0, 0, 0, 0.4),"
				+ "					2px 4px 8px  0px rgba(0, 0, 0, 0.2);"
				+ "		width: 128px;"
				+ "		height: 128px;"
				+ "		background-color: yellow;"
				+ "		border-radius: 8px;"
				+ "		transition: background-color 0.2s;"
				+ "}"
				+ ""
				+ ".box:hover {"
				+ "		background-color: green;"
				+ "}");
		pane.getClassList().add("box");
		
		((Pane)stage.getScene().getRoot()).getChildren().add(pane);
	}
}
