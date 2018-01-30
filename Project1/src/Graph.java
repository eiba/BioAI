import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Graph extends Pane {

    private final Pane depotPane, customerPane, roadPane;
    private final int height;
    private final double factorX, factorY;


    Graph(int width, int height, int minX, int minY, int maxX, int maxY) {
        super();
        depotPane = new Pane();
        customerPane = new Pane();
        roadPane= new Pane();
        super.setMinSize(width, height);
        super.setMaxSize(width, height);
        super.getChildren().addAll(depotPane, customerPane, roadPane);

        this.height = height;
        this.factorX = width / (Math.abs(minX) + Math.abs(maxX));
        this.factorY = height / (Math.abs(minY) + Math.abs(maxY));
    }

    void addDepots(Depot[] depots) {
        for (Depot depot : depots) {
            Rectangle depotRect = new Rectangle(10,10);
            depotRect.setTranslateX(mapXToGraph(depot.getX()));
            depotRect.setTranslateY(mapYToGraph(depot.getY()));
            depotRect.setStyle("-fx-background-color: blue");
            depotPane.getChildren().add(depotRect);
        }
    }

    private double mapXToGraph(double x) {
        return x * factorX;
    }

    private double mapYToGraph(double y) {
        return height - (y * factorY);
    }

}
