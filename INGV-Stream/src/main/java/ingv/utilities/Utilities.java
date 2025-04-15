package ingv.utilities;
import javafx.scene.control.Alert;

public class Utilities {
    // Method to show a dialog with a specific type, title, and message
    public static void showDialog(Alert.AlertType type, String titolo, String messaggio) {
        Alert alert = new Alert(type);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
