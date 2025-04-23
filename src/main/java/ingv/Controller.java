package ingv;

import ingv.utilities.PrintColors;
import ingv.utilities.Utilities;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.layout.StackPane;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.gluonhq.maps.MapView;
import com.gluonhq.maps.MapPoint;

public class Controller implements Initializable {

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private TextField limitField;

    @FXML
    private Button loadBtn;

    @FXML
    private TableColumn<INGVEvent, LocalDate> dateClm;

    @FXML
    private TableColumn<INGVEvent, String> locationCLm;

    @FXML
    private TableColumn<INGVEvent, Double> magnitudeClm;

    @FXML
    private TableView<INGVEvent> table;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private StackPane stackPane;

    @FXML
    private TextField searchField;

    @FXML
    private VBox mapContainer;

    @FXML
    private Button clearTableButton;

    private LoadReportService loader;
    private ObservableList<INGVEvent> events;
    private FilteredList<INGVEvent> filteredData;
    private CustomMapLayer earthquakeLayer;
    private MapView mapView;
    final double MIN_ZOOM = 3.0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        events = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(events);
        table.setItems(filteredData);

        mapView = new MapView();
        earthquakeLayer = new CustomMapLayer();
        mapView.addLayer(earthquakeLayer);
        mapView.setCenter(new MapPoint(41.9, 12.5)); // Rome
        mapView.setZoom(MIN_ZOOM);


        mapContainer.getChildren().add(mapView);
        stackPane.getChildren().get(1).setVisible(false);

        loader = loaderInit();

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        progressIndicator.setVisible(false);
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());
        limitField.setText("1000");

        dateClm.setCellValueFactory(new PropertyValueFactory<>("time"));
        locationCLm.setCellValueFactory(new PropertyValueFactory<>("eventLocationName"));
        magnitudeClm.setCellValueFactory(new PropertyValueFactory<>("magnitude"));

        /*
         * LISTENERS & BINDINGS
         */


        // force the map to refresh when the mouse is dragged
        mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED,e -> {earthquakeLayer.refreshMap();});

        // check the fairness of limit input
        limitField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Utilities.showDialog(Alert.AlertType.ERROR, "Invalid limit", "Limit must be a positive number (up to 10000)!");
                limitField.setText("1000");
            }
            int value = Integer.parseInt(newValue);
            if (value < 0 || value > 10000) {
                Utilities.showDialog(Alert.AlertType.ERROR, "Invalid limit", "Limit must be a positive number (up to 10000)!");
                limitField.setText("1000");
            }

        });

        // search implementation
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseQuery = newValue.toLowerCase();
                String eventDate = event.getTime().toLocalDate().toString().toLowerCase();
                return event.getEventLocationName().toLowerCase().contains(newValue.toLowerCase()) ||
                        String.valueOf(event.getMagnitude()).contains(lowerCaseQuery) ||
                        eventDate.contains(lowerCaseQuery);
            });
        });

        // Bind the search field to the table to automatically disable it when there are no events
        searchField.disableProperty().bind(Bindings.isEmpty(events));

    }

    // Method to generate the URL based on the input fields
    private URL generateURL() {
        String baseURL = "http://webservices.ingv.it/fdsnws/event/1/";
        String query = "query?starttime=" + startDate.getValue().toString() + "&endtime=" + endDate.getValue().toString() + "&limit=" + limitField.getText() + "&minmag=2&maxmag=10&mindepth=-10&maxdepth=1000&minlat=-90&maxlat=90&minlon=-180&maxlon=180&minversion=100&orderby=time-asc&format=text";
        URL url = null;
        try {
            url = new URI(baseURL + query).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(PrintColors.format("URL: " + url, PrintColors.YELLOW));
        return url;
    }

    // Method to initialize the loader
    private LoadReportService loaderInit() {
        loader = new LoadReportService();

        loader.setOnSucceeded(event -> {
            System.out.println(PrintColors.format("Data successfully loaded", PrintColors.GREEN));
            events.setAll(loader.getValue());
            progressIndicator.setVisible(false);
            loader.reset();
        });

        loader.setOnFailed(s -> {
            System.out.println(PrintColors.format("Loading Failed", PrintColors.RED));
            Utilities.showDialog(Alert.AlertType.ERROR, "Error", "Error loading report file");
            progressIndicator.setVisible(false);
        });

        loader.setOnCancelled(s -> {
            System.out.println(PrintColors.format("Loading cancelled", PrintColors.RED));
            Utilities.showDialog(Alert.AlertType.ERROR, "Error", "Loading cancelled");
            progressIndicator.setVisible(false);
        });
        return loader;
    }

    /*
     * HANDLERS
     */

    @FXML
    private void handleClose(ActionEvent event) {
        Platform.exit();
    }

    // handler for the clear button click. Clears the table and resets the input fields
    @FXML
    public void handleClear() {
        System.out.println(PrintColors.format("Clearing table...", PrintColors.YELLOW));
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());
        limitField.setText("1000");
        searchField.clear();
        events.clear();
    }

    // handler for the load button click
    @FXML
    public void handleLoading() {
        System.out.println(PrintColors.format("Fetching data...", PrintColors.YELLOW));
        loader.setUrl(generateURL());
        progressIndicator.setVisible(true);
        progressIndicator.progressProperty().bind(loader.progressProperty());
        loader.restart();
    }

    // handler for table view export on file
    @FXML
    public void handleAllExport(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save All Events");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt, *.csv"));
        File file = fileChooser.showSaveDialog(null);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(INGVEvent event : events){
                writer.write(event.toString());
                writer.newLine();
            }
        } catch (IOException e){
            e.printStackTrace();
            Utilities.showDialog(Alert.AlertType.ERROR, "Error", "Error saving events");
        }
        Utilities.showDialog(Alert.AlertType.INFORMATION, "Information", "Events correctly saved to: "+ file.getAbsolutePath());
    }

    // handler for the export button click. Exports the selected events to a file
    @FXML
    public void handleExport(){
        ObservableList<INGVEvent> selectedItems = table.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Selected Events");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt, *.csv"));
        File file = fileChooser.showSaveDialog(null);

        System.out.println(PrintColors.format("Exporting data...", PrintColors.GREEN));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (INGVEvent event : selectedItems) {
                writer.write(event.toString());
                writer.newLine();
            }
            System.out.println(PrintColors.format("Data successfully exported", PrintColors.GREEN));
        } catch (IOException e) {
            System.out.println(PrintColors.format("Error exporting data: " + e.getMessage(), PrintColors.RED));
            Utilities.showDialog(Alert.AlertType.ERROR, "Error", "Error exporting data: " + e.getMessage());
            e.printStackTrace();
        }
        Utilities.showDialog(Alert.AlertType.INFORMATION, "Information", "Events correctly saved to: "+ file.getAbsolutePath());
    }

    // handler for the show map button
    @FXML
    public void handleShowMap(){
        stackPane.getChildren().get(0).setVisible(false);
        stackPane.getChildren().get(1).setVisible(true);

        for(INGVEvent t : events){
            System.out.println(t);
            MapPoint mapPoint = new MapPoint(t.getLatitude(), t.getLongitude());
            earthquakeLayer.addMarker(mapPoint, t);
        }
    }

    @FXML
    public void handleShowTable(){
        stackPane.getChildren().get(0).setVisible(true);
        stackPane.getChildren().get(1).setVisible(false);
        earthquakeLayer.clearMarkers();
    }
}
