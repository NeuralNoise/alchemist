package view.controls.custom;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class KeyReleasedConsumingTextField extends TextField {
	
	public KeyReleasedConsumingTextField() {
		this.addEventHandler(KeyEvent.KEY_RELEASED, e -> e.consume());
	}

}
