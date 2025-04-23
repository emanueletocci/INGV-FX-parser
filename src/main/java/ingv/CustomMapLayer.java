package ingv;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;

public class CustomMapLayer extends MapLayer {
    private final List<Node> markers = new ArrayList<>();

    public void addMarker(MapPoint point, INGVEvent event) {
        Circle marker = new Circle(8, Color.RED);
        marker.setStroke(Color.WHITE);

        marker.setUserData(event);

        Tooltip tooltip = new Tooltip(
                "Magnitude: " + event.getMagnitude() + "\n" +
                        "Depth: " + event.getDepthkm() + " km\n" +
                        "Date: " + event.getTime()
        );
        Tooltip.install(marker, tooltip);

        marker.setOnMouseEntered(e -> tooltip.show(marker, e.getScreenX() + 10, e.getScreenY() + 10));
        marker.setOnMouseExited(e -> tooltip.hide());

        markers.add(marker);
        getChildren().add(marker);
        markDirty();
    }

    @Override
    protected void layoutLayer() {
        for (Node marker : markers) {
            INGVEvent event = (INGVEvent) marker.getUserData();
            MapPoint point = new MapPoint(event.getLatitude(), event.getLongitude());

            Point2D screenPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            marker.setTranslateX(screenPoint.getX());
            marker.setTranslateY(screenPoint.getY());
        }
    }

    public void clearMarkers() {
        markers.clear();
        getChildren().clear();
        markDirty();
    }

    public void refreshMap(){
        markDirty();
    }
}
