import io.jadefx.application.Application;
import io.jadefx.paint.Color;
import io.jadefx.scene.Scene;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.BorderPane;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.stage.Stage;

public class BorderPaneTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage, String[] args) {
		BorderPane layout = new BorderPane();
		layout.getClassList().add("box");

		layout.setTop(new Label("Top"));
		layout.setLeft(new Label("Left"));
		layout.setRight(new Label("Right"));
		layout.setBottom(new Label("Bottom"));
		layout.setCenter(new Label("Center"));
		
		stage.setScene(new Scene(layout, 320, 240));
		
		stage.getScene().setStylesheet(""
				+ ".box {"
				+ "		box-shadow: 8px 16px 32px 0px rgba(0, 0, 0, 0.3),"
				+ "					2px 3px  6px  0px rgba(0, 0, 0, 0.1);"
				+ "		width: calc(100% - 64px);"
				+ "		height: calc(100% - 64px);"
				+ "		background-color: white;"
				+ "		border-radius: 8px;"
				+ "		border-width: 1px;"
				+ "		border-color: rgb(160, 160, 160);"
				+ "}");
		
		stage.show();
	}
}
