package view.worldEdition;

import javafx.scene.control.Tab;
import presentation.worldEditor.presenter.Tool;

public class TrinketTab extends Tab implements ToolEditor {

	public TrinketTab() {
		setText("Trinkets");
		setClosable(false);
	}
	
	@Override
	public Tool getTool() {
		return null;
	}

}
