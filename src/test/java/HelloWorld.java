import io.jadefx.application.Application;
import io.jadefx.scene.Scene;
import io.jadefx.scene.control.Label;
import io.jadefx.stage.Stage;

public class HelloWorld extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage, String[] args) {
		stage.setScene(new Scene(new Label("Hello Wold"), 320, 240));
		stage.show();
	}
}
