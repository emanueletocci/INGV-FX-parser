package ingv;

import ingv.utilities.PrintColors;
import ingv.utilities.Utilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Scanner;

public class LoadReportService extends Service<ObservableList<INGVEvent>> {
    private URL url;

    public LoadReportService() {}
    public LoadReportService(URL url) {
        this.url = url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    // Task to fetch data from the URL
    @Override
    protected Task<ObservableList<INGVEvent>> createTask() {
        return new Task() {
            @Override
            protected ObservableList<INGVEvent> call() throws Exception {
                ObservableList<INGVEvent> events = FXCollections.observableArrayList();
                try (Scanner reader = new Scanner(url.openStream())) {
                    reader.useDelimiter("\\|");
                    reader.nextLine(); //skip the header
                    while (reader.hasNext()) {
                        events.add(parseLine(reader));
                    }
                } catch (Exception e) {
                    PrintColors.format("Error reading from URL: " + e.getMessage(), PrintColors.RED);
                    Utilities.showDialog(Alert.AlertType.ERROR, "Error", "Error reading from URL: " + e.getMessage());
                    e.printStackTrace();
                }
                return events;
            }
        };
    }

    private INGVEvent parseLine(Scanner reader){
        String eventID = reader.next();
        LocalDateTime time = LocalDateTime.parse(reader.next());
        double latitude = Double.parseDouble(reader.next());
        double longitude = Double.parseDouble(reader.next());
        double depthkm = Double.parseDouble(reader.next());
        String author = reader.next();
        String catalog = reader.next();
        String contributor = reader.next();
        String contributorID = reader.next();
        String magType = reader.next();
        double magnitude = Double.parseDouble(reader.next());
        String magAuthor = reader.next();
        String eventLocationName = reader.next();
        reader.nextLine();
        return new INGVEvent(eventID, time, latitude, longitude, depthkm, author, catalog, contributor, contributorID, magType, magnitude, magAuthor, eventLocationName);
    }
}
