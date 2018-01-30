import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Graph extends Pane {

    private final Pane depotPane, customerPane, roadPane;
    private final int minX, minY;
    private final double height, factorX, factorY;

    private final Color[] colors = {Color.GREEN, Color.RED, Color.ORANGE, Color.BROWN, Color.PURPLE, Color.BLACK};

    private Depot[] depots;
    private Customer[] customers;


    Graph(double width, double height, int minX, int minY, int maxX, int maxY) {
        super();
        depotPane = new Pane();
        customerPane = new Pane();
        roadPane= new Pane();
        super.setMinSize(width, height);
        super.setMaxSize(width, height);
        super.getChildren().addAll(depotPane, customerPane, roadPane);

        this.minX = minX;
        this.minY = minY;
        this.height = height;
        this.factorX = width / Math.abs(maxX - minX);
        this.factorY = height / Math.abs(maxY - minY);
    }

    void addDepots(Depot[] depots) {
        this.depots = depots;
        for (Depot depot : depots) {
            Rectangle depotRectangle = new Rectangle(10,10);
            depotRectangle.setTranslateX(mapXToGraph(depot.getX()));
            depotRectangle.setTranslateY(mapYToGraph(depot.getY()));
            depotRectangle.setStyle("-fx-fill: #04d403");
            depotPane.getChildren().add(depotRectangle);
        }
    }

    void addCustomers(Customer[] customers) {
        this.customers = customers;
        for (Customer customer : customers) {
            Circle customerCircle = new Circle(5);
            customerCircle.setTranslateX(mapXToGraph(customer.getX()));
            customerCircle.setTranslateY(mapYToGraph(customer.getY()));
            customerCircle.setStyle("-fx-fill: #1eb0ff");
            customerPane.getChildren().add(customerCircle);

        }
    }

    void addRoutes(SolutionLine[] solutionLines) {

        int counter = 0;

        for (SolutionLine solutionLine : solutionLines) {
            Color color = colors[counter % colors.length];
            counter ++;

            int depotID = solutionLine.getDepot_nr();
            int currentX = depots[depotID - 1].getX();
            int currentY = depots[depotID - 1].getY();

            int[] customerIDs = solutionLine.getSequence();
            for (int customerID : customerIDs) {
                Line lineRoute = new Line();
                lineRoute.setStroke(color);
                lineRoute.setStartX(mapXToGraph(currentX));
                lineRoute.setStartY(mapYToGraph(currentY));
                lineRoute.setEndX(mapXToGraph(customers[customerID - 1].getX()));
                lineRoute.setEndY(mapYToGraph(customers[customerID - 1].getY()));
                currentX = customers[customerID - 1].getX();
                currentY = customers[customerID - 1].getY();
                roadPane.getChildren().add(lineRoute);
            }
            Line lineRoute = new Line();
            lineRoute.setStroke(color);
            lineRoute.setStartX(mapXToGraph(currentX));
            lineRoute.setStartY(mapYToGraph(currentY));
            lineRoute.setEndX(mapXToGraph(depots[depotID - 1].getX()));
            lineRoute.setEndY(mapYToGraph(depots[depotID - 1].getY()));
            roadPane.getChildren().add(lineRoute);
        }
    }

    private double mapXToGraph(double x) {
        return (x - minX) * factorX;
    }

    private double mapYToGraph(double y) {
        return height - ((y - minY) * factorY);
    }

}
