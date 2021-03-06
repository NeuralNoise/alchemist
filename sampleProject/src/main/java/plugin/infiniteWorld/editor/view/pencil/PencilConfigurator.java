package plugin.infiniteWorld.editor.view.pencil;

import com.brainless.alchemist.view.util.ViewLoader;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import plugin.infiniteWorld.editor.presentation.PencilConfiguratorPresenter;
import plugin.infiniteWorld.editor.presentation.PencilConfiguratorPresenter.Mode;
import plugin.infiniteWorld.editor.presentation.PencilConfiguratorPresenter.Shape;

public class PencilConfigurator extends VBox {
	
	private final PencilConfiguratorPresenter presenter;
	
	@FXML
	private ToggleButton circleButton, squareButton, diamondButton, airbrushButton, roughButton, noiseButton;
	
	@FXML
	private Slider sizeSlider, strengthSlider;
	
	@FXML
	private TextField sizeField, strengthField;
	
	public PencilConfigurator(PencilConfiguratorPresenter presenter) {
		this.presenter = presenter;
		ViewLoader.loadFXMLForControl(this);
	}
	
	@FXML
	private void initialize(){
		circleButton.selectedProperty().bindBidirectional(presenter.getShapeProperty().getToggle(Shape.CIRCLE));
		squareButton.selectedProperty().bindBidirectional(presenter.getShapeProperty().getToggle(Shape.SQUARE));
		diamondButton.selectedProperty().bindBidirectional(presenter.getShapeProperty().getToggle(Shape.DIAMOND));
		
		airbrushButton.selectedProperty().bindBidirectional(presenter.getModeProperty().getToggle(Mode.AIRBRUSH));
		roughButton.selectedProperty().bindBidirectional(presenter.getModeProperty().getToggle(Mode.ROUGH));
		noiseButton.selectedProperty().bindBidirectional(presenter.getModeProperty().getToggle(Mode.NOISE));
		
		sizeSlider.setMax(PencilConfiguratorPresenter.MAX_SIZE);
		sizeSlider.valueProperty().bindBidirectional(presenter.getSizeProperty());
		sizeField.textProperty().bindBidirectional(presenter.getSizeProperty(), new NumberStringConverter());
		
		strengthSlider.valueProperty().bindBidirectional(presenter.getStrengthProperty());
		strengthField.textProperty().bindBidirectional(presenter.getStrengthProperty(), new NumberStringConverter());
	}

}
