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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

public class Controller {
	private static final int errorMargin = 1000;

	class Pair<K, V> {
		K key;
		V value;

		public Pair(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	@FXML
	BorderPane root;
	@FXML
	MenuItem mniFileOpen, mniFileExit;
	@FXML
	Button btnApplyFilter, btnAcceptFilter, btnSaveFingerprint, btnCheckLines, btnFind;
	@FXML
	ImageView imgLeft, imgRight;
	@FXML
	ComboBox<Filter> filterChooser;
	@FXML
	TextField inpFingerprintName;
	@FXML
	TextArea resultsArea;
	@FXML
	Label resultsLabel;

	LazyLoad<FileChooser> fileChooserSupplier = new LazyLoad().withSupplier(() -> new FileChooser());
	Filters filters = Filters.getFilters();
	FileChooser.ExtensionFilter extensionFilter;
	LineFinder lineFinder;
	LineParams lineParams;
	List<FingerprintData> fingerprints = new ArrayList<>();

	Map<Integer, Integer> horizontalLines = null;
	Map<Integer, Integer> verticalLines = null;
	File currentImage;

	BiFunction<Double, Double, Double> avarageFunction = (i1, i2) -> Math.abs(i1 - i2);

	BiFunction<Double, Double, Double> squareFunction = (i1, i2) -> Math.pow(i1 - i2, 2.0);

	BiFunction<Double, Double, Double> compareFunction =  avarageFunction;

	{
		lineFinder = new LineFinder();
		lineParams = new LineParams();
		lineParams.horizontalIndexes = new int[]{ 10, 20, 30, 40, 50, 60, 70, 80, 90};
		lineParams.verticalIndexes = lineParams.horizontalIndexes;
		lineParams.unit = LineParams.Unit.PERCENTAGE;
		reloadFingerprintData();
	}

	void reloadFingerprintData() {
		//odczyt z pliku
		fingerprints.clear();

		BufferedReader reader;
		try {
			String currentLine;
			File f = new File("data.txt");
			if (!f.exists()) {
				f.createNewFile();
			}
			reader = new BufferedReader(new FileReader(f));
			FingerprintData newFingerprint = null;
			while ((currentLine = reader.readLine()) != null) {
				char firstLetter = currentLine.charAt(0);
				switch (firstLetter) {
					case '#':
						newFingerprint = new FingerprintData();
						fingerprints.add(newFingerprint);
						String name = currentLine.substring(1, currentLine.indexOf('?'));
						String filename = currentLine.substring(currentLine.indexOf('?') + 1, currentLine.length());
						File file = new File(filename);
						newFingerprint.setFile(file);
						newFingerprint.setName(name);
						break;
					default:
						char symbol = currentLine.charAt(0);
						char cIndex = currentLine.charAt(1);
						int index = Character.getNumericValue(cIndex);
						String svalue = currentLine.substring(3, currentLine.length());
						int value = Integer.parseInt(svalue);

						if (symbol == 'V') {
							newFingerprint.setVerticalData(index, value);
						} else if (symbol == 'H') {
							newFingerprint.setHorizontalData(index, value);
						} else {
							System.err.println("Unsupported symbol");
						}
						break;
				}
//				if (firstLetter == '#') {
//					FingerprintData newFingerprint = new FingerprintData();
//					fingerprints.add(newFingerprint);
//					String name = currentLine.substring(1, currentLine.length());
//					newFingerprint.setName(name);
//					int i = 0;
//					String data;
//
//					while (i++ < 6 && (data = reader.readLine()) != null) {
//						char symbol = data.charAt(0);
//						char cIndex = data.charAt(1);
//						int index = Character.getNumericValue(cIndex);
//						String svalue = data.substring(3, data.length());
//						int value = Integer.parseInt(svalue);
//
//						if (symbol == 'V') {
//							newFingerprint.setVerticalData(index, value);
//						} else if (symbol == 'H') {
//							newFingerprint.setHorizontalData(index, value);
//						}
//					}
//				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//wypisanie danych
//		for (FingerprintData fingerprint : fingerprints) {
//			System.out.println("#" + fingerprint.getName());
//			for (int i = 0; i < fingerprint.getHorizontalData().length; i++) {
//				System.out.println("H" + fingerprint.getHorizontalData(i));
//			}
//			for (int i = 0; i < fingerprint.getVerticalData().length; i++) {
//				System.out.println("V" + fingerprint.getVerticalData(i));
//			}
//		}
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
	void handleApplyFilter() {
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

	/**
	 * Calculates fingerprint data
	 */
	@FXML
	void handleCheckLines() {
		Filters.findByClass(SearchFingerprint.class)
				.ifPresent(filter -> filter
						.withImage(imgLeft.getImage())
						.filter()
						.setImage(imgRight));
		handleAcceptFilter();

		System.out.println("Image size: " + (int) imgLeft.getImage().getWidth() + "x" + (int) imgLeft.getImage().getHeight());
		LineResult result = lineFinder
				.image(imgLeft.getImage())
				.params(lineParams)
				.find()
				.getResult();

		horizontalLines = result.horizontalLines;
		verticalLines = result.verticalLines;

		System.out.println(result);
		resultsArea.setText(result.toString());
	}

	/**
	 * Saves current fingerprint data to file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	@FXML
	void handleSaveFingerprint() throws FileNotFoundException, UnsupportedEncodingException {
		String name = inpFingerprintName.getText();
		System.out.println(name);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Zapisywanie odcisku");
		alert.setHeaderText(null);
		if (saveFingerprint(name)) {
			alert.setContentText("Zapisano odcisk w bazie danych!");
		} else {
			alert.setContentText("Zapisywanie odcisku się nie powiodło");
		}
		alert.showAndWait();
		reloadFingerprintData();
	}

	private boolean saveFingerprint(String name) {
		handleCheckLines();
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("data.txt", true)));
			writer.println("#" + name + "?" + currentImage.getAbsolutePath());

			int i = 0;
			for (Map.Entry<Integer, Integer> entry : horizontalLines.entrySet()) {
				writer.println("H" + i + " " + entry.getValue());
				System.out.println("H" + i + " " + entry.getValue());
				i++;
			}
			i = 0;
			for (Map.Entry<Integer, Integer> entry : verticalLines.entrySet()) {
				writer.println("V" + i + " " + entry.getValue());
				System.out.println("V" + i + " " + entry.getValue());
				i++;
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@FXML
	void handleFindFigerprint(ActionEvent actionEvent) throws FileNotFoundException {
		handleCheckLines();

		List<FingerprintData> compareData = new ArrayList<>();

		System.out.println("Wyszukiwanie odcisku w bazie danych");
		int minErrorIndex = -1;
		Comparator<Pair<Integer, Double>> comparator = (p1, p2) -> p1.value.compareTo(p2.value);
		Queue<Pair<Integer, Double>> result = new PriorityQueue<>(comparator);
		double minimalFoundAverage = errorMargin;

		//Liczenie błędu
		System.out.println("BŁAD POMIAROWY:");

		int currentFingerprint = 0;
		for (FingerprintData fingerprint : fingerprints) {
			String name = fingerprint.getName();
			int value;
			FingerprintData tmp = new FingerprintData();
			tmp.setName(name);
			System.out.println("#" + name);
			int i = 0;
			for (Map.Entry<Integer, Integer> entry : horizontalLines.entrySet()) {
				value = Math.abs(fingerprint.getHorizontalData(i) - entry.getValue());
				tmp.setHorizontalData(i, value);
				System.out.print("H" + value + "   ");
				i++;
			}
			tmp.calcutateHorizontalAverage();
			System.out.print("odległość H: " + tmp.getHorizontalAverage() + "\n");

			i = 0;
			for (Map.Entry<Integer, Integer> entry : verticalLines.entrySet()) {
				value = Math.abs(fingerprint.getVerticalData(i) - entry.getValue());
				tmp.setVerticalData(i, value);
				System.out.print("V" + value + "   ");
				i++;
			}
			tmp.calcutateVerticalAverage();
			System.out.print("odległość V: " + tmp.getVerticalAverage());

			// calculate total difference
			double horizontalValue = 0;
			for (int q : tmp.getHorizontalData()) {
				horizontalValue += q * q;
			}
			double verticalValue = 0;
			for (int p : tmp.getVerticalData()) {
				verticalValue += p * p;
			}
			tmp.setAverage(horizontalValue * verticalValue);

			double average = tmp.getAverage();
			System.out.println("\nodległość: " + average);

			compareData.add(tmp);
			result.add(new Pair(currentFingerprint, average));

			if (average < minimalFoundAverage) {
				minimalFoundAverage = average;
				minErrorIndex = currentFingerprint;
			}

			currentFingerprint++;
		}

		String message;
		if (minErrorIndex >= 0) {
			String foundName = fingerprints.get(minErrorIndex).getName();
			if (minimalFoundAverage == 0.0) {
				message = "Odcisk w 100% należy do " + foundName;
			} else {
				message = "Odcisk najbardziej pasuje do " + foundName + "\nBłąd pomiarowy wynosi: " + minimalFoundAverage;
			}
			for (int i=0; i<10; i++) {
				Pair<Integer, ?> r = result.poll();
				System.out.println(i + 1 + ": " + fingerprints.get(r.key).getName() + " " + r.value);
//				if (minimalFoundAverage == 0 && i==1 || i==0) {
//					imgRight.setImage(new Image(new FileInputStream(fingerprints.get(r.key).getFile())));
//				}
			}

		} else {
			message = "Brak odcisku w bazie";
		}
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Wyszukiwanie odcisku w bazie danych");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
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
		currentImage = source;
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
		btnApplyFilter.setDisable(disable);
		filterChooser.setDisable(disable);
		btnAcceptFilter.setDisable(disable);
		btnCheckLines.setDisable(disable);
		btnSaveFingerprint.setDisable(disable);
		inpFingerprintName.setDisable(disable);
		resultsArea.setDisable(disable);
		resultsLabel.setDisable(disable);
		btnFind.setDisable(disable);
	}

	@FXML
	void handleFileExit(ActionEvent event) {
		Platform.exit();
	}

	/**
	 * Rebuild data for all images in directory
	 */
	@FXML
	void handleRebuild() throws IOException {
		System.out.println("handle rebuild");
		File f = new File("data.txt");
		f.delete();
		f.createNewFile();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(new File("/home/mati/Pobrane"));
		File result = chooser.showDialog(root.getScene().getWindow());
		if (result == null) {
			return;
		}
		System.out.println(result);
		try {
			Files.walk(result.toPath())
					.filter(path -> path.toString().toLowerCase().endsWith(".jpg") && Files.isRegularFile(path))
					.forEach(this::rebuildImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rebuild one image
	 * @param path
	 */
	private void rebuildImage(Path path) {
		String name = path.getFileName().toString();
		System.out.println("Rebuilding " + name);
		tryOpenFile(path.toFile());
		saveFingerprint(name);
		reloadFingerprintData();
	}
}
