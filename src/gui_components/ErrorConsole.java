package gui_components;

import gui.FadingTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * @author Eric Song
 *
 */
public class ErrorConsole {

	private Label errorMessage;
	private SequentialTransition blinkThenFade;
	
	public ErrorConsole(double errorFontSize) {
		errorMessage = new Label();
		errorMessage.setStyle("-fx-text-fill: red;");
		errorMessage.setFont(Font.font("Verdana", errorFontSize));
		FadingTransition transition = new FadingTransition();
		blinkThenFade = transition.setupNode(errorMessage);

	}

	/**
	 * @param myError
	 * displays myError on the user interface as a blinking text
	 */
	public void displayErrorMessage(String myError) {
		blinkThenFade.stop();
		errorMessage.setText(myError);
		blinkThenFade.play();
	}

	public Label getErrorMessage(){
		return errorMessage;
	}
}
