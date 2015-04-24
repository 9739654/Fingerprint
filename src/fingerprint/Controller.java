package fingerprint;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

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
	//Image originalImage;
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
				.withImage(imgLeft.getImage())
				.filter()
				.setImage(imgRight);
	}

	@FXML
	void handleAcceptFilter() {
		imgLeft.setImage(imgRight.getImage());
        imgRight.setImage(null);
	}

	@FXML
	void handleFileOpen(ActionEvent event) {
		getFile().ifPresent(choosen -> tryOpenFile(choosen));
	}

	@FXML
	void handleOpenTestImage() {
		File source = new File("./filter-test.png");
		tryOpenFile(source);
	}

	private void tryOpenFile(File source) {
		try {
			source = source.getCanonicalFile();
			openFile(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openFile(File source) {
		String uri = source.toURI().toString();
		System.out.println("opening: " + uri);
		imgLeft.setImage(new Image(uri));
		enableButtons(true);
	}

    @FXML
    void handleFileSave(ActionEvent event) {
        getFile().ifPresent(file -> {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(imgLeft.getImage(), null), "png", file);
            } catch (Exception s) {
                s.printStackTrace();
            }
        });
    }

    private Optional<File> getFile() {
        FileChooser fileChooser = fileChooserSupplier.get();
        File choosen = fileChooser.showOpenDialog(root.getScene().getWindow());
        return Optional.ofNullable(choosen);
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
