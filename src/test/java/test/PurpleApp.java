package test;

import io.jadefx.application.MobileApplication;
import io.jadefx.geometry.ScreenOrientation;
import io.jadefx.scene.control.Label;
import io.jadefx.scene.layout.BorderPane;
import io.jadefx.scene.layout.Pane;
import io.jadefx.scene.layout.StackPane;
import io.jadefx.scene.layout.VBox;
import io.jadefx.stage.Stage;

public class PurpleApp extends MobileApplication {
	
	private static final String LBL_SIGN_UP = "Get started";
	
	private static final String LBL_SIGN_IN = "Sign in";
	
	@Override
	public void start(Stage stage, String[] args) {
		MobileApplication.showStatusBar();
		MobileApplication.setOrientation(ScreenOrientation.PORTRAIT);
		MobileApplication.setMultitouchEnabled(true);
		
		stage.getScene().setStylesheet(""
				+ "scene {"
				+ "		/*background-image: linear-gradient(180deg, #40C5FA, #7272EF);*/"
				+ "		background-image: linear-gradient(180deg, yellow, white);"
				+ "		font-size: 22px;"
				+ "		color: white;"
				+ "		alignment: top center;"
				+ "		font-family: Google Sans;"
				+ "		font-size: 18px;"
				+ "}"
				+ ""
				+ ".main-layout {"
				+ "		background-color: transparent;"
				+ "		width: calc(100% - 48px);"
				+ "		height: calc(100% - 48px);"
				+ "}"
				+ ""
				+ ".button-layout {"
				+ "		width: 100%;"
				+ "		spacing: 16px;"
				+ "		alignment: center;"
				+ "}"
				+ ""
				+ ".innerFontColor {"
				+ "		color: #707070;"
				+ "}"
				+ ""
				+ ".padding {"
				+ "		padding: 16px 38px;"
				+ "}"
				+ ""
				+ ".test-button {"
				+ "		box-shadow: 0 2px 3px -4px rgba(0, 0, 0, .4),"
				+ "					0 4px 8px 0 rgba(0, 0, 0, .1),"
				+ "					0 1px 18px 0 rgba(0, 0, 0, .04);"
				+ "		border-radius: 24px;"
				+ "		border-style: none;"
				+ "		color: #3c4043;"
				+ "		padding: 16px 48px;"
				+ "		alignment: center;"
				+ "		background-color: #fff;"
				+ "		border-width: 4px;"
				+ "		width: 100%;"
				+ "		transition: background-color 0.1s, box-shadow 0.1s, border-color 0.1s, border-width 0.1s;"
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
				+ "		border-color: #FFFFFF50;"
				+ "		box-shadow: 0px 4px 6px 0px rgba(60, 64, 67, 0.2),"
				+ "					0px 8px 12px 3px rgba(60, 64, 67, 0.1);"
				+ "}"
				+ ""
				+ ".test-button:select {"
				+ "		border-style: solid;"
				+ "		border-color: #FFFFFF50;"
				+ "}"
				+ ""
				+ ".test-button2 {"
				+ "		width: 100%;"
				+ "		border-radius: 24px;"
				+ "		border-style: none;"
				+ "		padding: 16px 48px;"
				+ "		border-width: 4px;"
				+ "		alignment: center;"
				+ "		background-color: rgba(255, 255, 255, 0.1);"
				+ "		transition: background-color 0.1s;"
				+ "}"
				+ ""
				+ ".test-button2:active {"
				+ "		background-color: rgba(255, 255, 255, 0.33);"
				+ "}");
		
		BorderPane layout = new BorderPane();
		stage.getScene().setRoot(layout);
		
		layout.getClassList().add("main-layout");
		
		// Button layout
		VBox buttons = new VBox();
		buttons.getClassList().add("button-layout");
		layout.setBottom(buttons);
		
		// Sign Up
		{
			Label label = new Label(LBL_SIGN_UP);
			label.getClassList().add("innerFontColor");
			
			Pane button = new StackPane();
			button.getClassList().add("test-button");
			button.getClassList().add("padding");
			button.setElementId("MyButton");
			button.getChildren().add(label);
			buttons.getChildren().add(button);
			
			button.setOnMouseClicked((event)->{
				System.out.println("Clicked test button!");
			});
		}
		
		// Sign In
		{
			Label label = new Label(LBL_SIGN_IN);
			
			Pane button = new StackPane();
			button.getClassList().add("test-button2");
			button.getClassList().add("padding");
			button.setElementId("MyButton");
			button.getChildren().add(label);
			buttons.getChildren().add(button);
			
			button.setOnMouseClicked((event)->{
				System.out.println("Clicked sign in!");
			});
		}

		layout.setCenter(new Label("Hello World"));
	}

	public static void main(String[] args) {
		launch(args);
	}
}