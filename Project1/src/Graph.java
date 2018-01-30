import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
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
            Rectangle depotRectangle = new Rectangle(10,10);
            depotRectangle.setTranslateX(mapXToGraph(depot.getX()));
            depotRectangle.setTranslateY(mapYToGraph(depot.getY()));
            depotRectangle.setStyle("-fx-background-color: #04d403");
            depotPane.getChildren().add(depotRectangle);
        }
    }

    void addCustomers(Customer[] customers) {
        for (Customer customer : customers) {
            Circle customerCircle = new Circle(5);
            customerCircle.setTranslateX(mapXToGraph(customer.getX()));
            customerCircle.setTranslateY(mapYToGraph(customer.getY()));
            customerCircle.setStyle("-fx-background-color: #1eb0ff");
        }
    }

    private double mapXToGraph(double x) {
        return x * factorX;
    }

    private double mapYToGraph(double y) {
        return height - (y * factorY);
    }

}
