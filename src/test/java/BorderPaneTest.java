import io.jadefx.application.Application;
import io.jadefx.scene.Scene;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.BorderPane;
import io.jadefx.scene.layout.Pane;

public class BorderPaneTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Scene scene) {
		BorderPane layout = new BorderPane();
		scene.setStylesheet(""
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
		layout.getClassList().add("box");

		layout.setTop(new Label("Top"));
		layout.setLeft(new Label("Left"));
		layout.setRight(new Label("Right"));
		layout.setBottom(new Label("Bottom"));
		layout.setCenter(new Label("Center"));
		
		((Pane)scene.getRoot()).getChildren().add(layout);
	}
}