import io.jadefx.application.Application;
import io.jadefx.scene.Scene;
import io.jadefx.scene.layout.Pane;

public class ApplicationTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Scene scene) {
		Pane pane = new Pane();
		scene.setStylesheet(""
				+ ".box {"
				+ "		box-shadow: 4px 8px 32px 0px rgba(0, 0, 0, 0.4),"
				+ "					2px 4px 8px  0px rgba(0, 0, 0, 0.2);"
				+ "		width: 128px;"
				+ "		height: 128px;"
				+ "		background-color: yellow;"
				+ "		border-radius: 8px;"
				+ "}");
		pane.getClassList().add("box");
		
		((Pane)scene.getRoot()).getChildren().add(pane);
	}
}
