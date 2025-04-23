package ingv;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class CustomMapLayer extends MapLayer {
    private final List<Pair<MapPoint, Node>> points = new ArrayList<>();

    // Aggiungi un marker in una posizione geografica
    public void addMarker(MapPoint point) {
        Circle marker = new Circle(5, Color.RED);
        points.add(new Pair<>(point, marker));
        getChildren().add(marker);
        markDirty();
    }

    @Override
    protected void layoutLayer() {
        for (Pair<MapPoint, Node> candidate : points) {
            MapPoint point = candidate.getKey();
            Node icon = candidate.getValue();
            Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            icon.setVisible(true);
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());
        }
    }

    public void clearMarkers() {
        points.clear();
        getChildren().clear();
        markDirty();
    }
}
