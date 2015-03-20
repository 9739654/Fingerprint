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
	Button btnFilter1, btnApplyFilter, btnAcceptFilter;
	@FXML
	ImageView imgLeft, imgRight;
	@FXML
	Slider binarizeParam;
	@FXML
	ComboBox<Filter> filterChooser;

	LazyLoad<FileChooser> fileChooserSupplier = new LazyLoad().withSupplier(() -> new FileChooser());
	Image originalImage;
	Filters filters = Filters.getFilters();
	FileChooser.ExtensionFilter extensionFilter;

	@FXML
	void initialize() {
		enableButtons(false);
		for (Filter fn : filters) {
			filterChooser.getItems().add(fn);
		}
		filterChooser.setValue(filters.get(0));
		extensionFilter = new FileChooser.ExtensionFilter("Obrazki", ".jpg", ".jpeg", ".bmp", ".png");

	}

	@FXML
	void handleBinarize(ActionEvent event) {
	}

	@FXML
	void handleApplyFilter(ActionEvent event) {
		filterChooser
				.getValue()
				.withImage(originalImage)
				.filter()
				.setImage(imgRight);
	}

	@FXML
	void handleAcceptFilter() {
		originalImage = imgRight.getImage();
		imgLeft.setImage(originalImage);
	}

	@FXML
	void handleFileOpen(ActionEvent event) {
		FileChooser fileChooser = fileChooserSupplier.get();
		fileChooser.setSelectedExtensionFilter(extensionFilter);
		File choosen = fileChooser.showOpenDialog(root.getScene().getWindow());
		if (choosen != null) {
			originalImage = new Image(choosen.toURI().toString());
			imgLeft.setImage(originalImage);
            enableButtons(true);
		}
	}

	@FXML
	private void enableButtons(boolean enable) {
		boolean disable = !enable;
		btnFilter1.setDisable(disable);
		btnApplyFilter.setDisable(disable);
		filterChooser.setDisable(disable);
		binarizeParam.setDisable(disable);
		btnAcceptFilter.setDisable(disable);
	}

	@FXML
	void handleFileExit(ActionEvent event) {
		Platform.exit();
	}
}
