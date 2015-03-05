package fingerprint;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
	@FXML
	BorderPane root;
	@FXML
	MenuItem mniFileOpen, mniFileExit;
	@FXML
	Button btnFilter1, btnFilter2;
	@FXML
	ImageView imgLeft, imgRight;
	@FXML
	Slider filter1Param;

	LazyLoad<FileChooser> fileChooserSupplier = new LazyLoad().withSupplier(() -> new FileChooser());
	Image originalImage;
	ImageFilters filters;

	@FXML
	void initialize() {
		filters = new ImageFilters();
	}

	@FXML
	void handleFilter1(ActionEvent event) {
		Image result = filters
				.withImage(originalImage)
				.withThreshold((int) filter1Param.getValue())
				.binarize()
				.getResult();
		imgRight.setImage(result);
	}

	@FXML
	void handleFilter2(ActionEvent event) {
		Image result = filters
				.withImage(originalImage)
				.horizontalEdges()
				.getResult();
		imgRight.setImage(result);
	}

	@FXML
	void handleFileOpen(ActionEvent event) {
		File choosen = fileChooserSupplier
				.get()
				.showOpenDialog(root.getScene().getWindow());
		originalImage = new Image(choosen.toURI().toString());
		imgLeft.setImage(originalImage);
	}

	@FXML
	void handleFileExit(ActionEvent event) {
		Platform.exit();
	}
}
