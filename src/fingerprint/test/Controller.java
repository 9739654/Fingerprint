package fingerprint.test;

import fingerprint.linefinder.LineFinder;
import fingerprint.linefinder.LineParams;
import fingerprint.linefinder.LineResult;
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
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {
	@FXML
	BorderPane root;
	@FXML
	MenuItem mniFileOpen, mniFileExit;
	@FXML
	Button btnFilter1, btnApplyFilter, btnAcceptFilter, btnSaveFingerprint;
	@FXML
	ImageView imgLeft, imgRight;
	@FXML
	Slider binarizeParam;
	@FXML
	ComboBox<Filter> filterChooser;
	@FXML
	Button btnCheckLines;
    @FXML
    TextField inpFingerprintName;

	LazyLoad<FileChooser> fileChooserSupplier = new LazyLoad().withSupplier(() -> new FileChooser());
	//Image originalImage;
	Filters filters = Filters.getFilters();
	FileChooser.ExtensionFilter extensionFilter;
	LineFinder lineFinder;
	LineParams lineParams;
    List<FingerprintData> fingerprints = new ArrayList<>();

    {
		lineFinder = new LineFinder();
		lineParams = new LineParams();
		lineParams.horizontalIndexes = new int[] {33, 50, 67};
		lineParams.verticalIndexes = lineParams.horizontalIndexes;
		lineParams.unit = LineParams.Unit.PERCENTAGE;
        readData();
	}

    void readData() {
        //odczyt z pliku
        BufferedReader reader;
        try {
            String currentLine;
            reader = new BufferedReader(new FileReader("data.txt"));
            while ((currentLine = reader.readLine()) != null) {
                char firstLetter = currentLine.charAt(0);
                if(firstLetter == '#') {
                    FingerprintData newFingerprint = new FingerprintData();
                    fingerprints.add(newFingerprint);
                    String name = currentLine.substring(1, currentLine.length());
                    newFingerprint.setName(name);
                    int i = 0;
                    String data;

                    while(i++ < 10 && (data = reader.readLine()) != null) {
                        char symbol = data.charAt(0);
                        char cIndex = data.charAt(1);
                        int index = Character.getNumericValue(cIndex);
                        String svalue = data.substring(3, data.length());
                        int value = Integer.parseInt(svalue);

                        if(symbol == 'V') {
                            newFingerprint.setVerticalData(index, value);
                        }
                        else if(symbol == 'H') {
                            newFingerprint.setHorizontalData(index, value);
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //wypisanie danych
        for (FingerprintData fingerprint : fingerprints) {
            System.out.println(fingerprint.getName());
            for (int i = 0; i < fingerprint.getHorizontalData().length; i++) {
                System.out.println(fingerprint.getHorizontalData(i));
            }
            for (int i = 0; i < fingerprint.getVerticalData().length; i++) {
                System.out.println(fingerprint.getVerticalData(i));
            }
        }
    }


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
	void handleCheckLines() {
		System.out.println("Image size: " + (int)imgLeft.getImage().getWidth() + "x" + (int)imgLeft.getImage().getHeight());
		LineResult result = lineFinder
				.image(imgLeft.getImage())
				.params(lineParams)
				.find()
				.getResult();

		System.out.println(result);
	}

    @FXML
    void handleSaveFingerprint() throws FileNotFoundException, UnsupportedEncodingException {
        //Najpierw należy wyszukać odcisk

        //zapisanie danych do pliku
	    int[] verticalData = Filters
			    .findByClass(SearchFingerprint.class)
			    .get()
			    .verticalData;
	    int[] horizontalData = Filters
			    .findByClass(SearchFingerprint.class)
			    .get()
			    .verticalData;

        String name = inpFingerprintName.getText();
        System.out.println(name);

        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("data.txt", true)));
            //PrintWriter writer = new PrintWriter("data.txt", "UTF-8");
            writer.println("#" + name);
            for (int i = 0; i < verticalData.length; i++) {
                writer.println("V" + i + " " + verticalData[i]);
                System.out.println("V" + i + " " + verticalData[i]);
            }

            for (int i = 0; i < horizontalData.length; i++) {
                writer.println("H" + i + " " + horizontalData[i]);
                System.out.println("H" + i + " " + horizontalData[i]);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	@FXML
	void handleFileOpen(ActionEvent event) {
		getFile().ifPresent(choosen -> tryOpenFile(choosen));
	}

	@FXML
	void handleOpenTestImage() {
		File source = new File("./filter-test.jpg");
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
		btnCheckLines.setDisable(disable);
        btnSaveFingerprint.setDisable(disable);
        inpFingerprintName.setDisable(disable);
    }

	@FXML
	void handleFileExit(ActionEvent event) {
		Platform.exit();
	}

}
