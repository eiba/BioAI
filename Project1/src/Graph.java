import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph extends Pane {

    private final Pane depotPane, customerPane, routePane;
    private final int minX, minY;
    private final double height, factorX, factorY;

    private final Color[] colors = {Color.GREEN, Color.RED, Color.ORANGE, Color.BLUE, Color.PURPLE};

    private Depot[] depots;
    private Customer[] customers;
    private final HashMap<Customer, Circle> customerCircleHashMap;


    Graph(double width, double height, int minX, int minY, int maxX, int maxY) {
        super();
        depotPane = new Pane();
        customerPane = new Pane();
        routePane = new Pane();
        super.setMinSize(width, height);
        super.setMaxSize(width, height);
        super.getChildren().addAll(routePane, depotPane, customerPane);

        this.minX = minX;
        this.minY = minY;
        this.height = height;
        this.factorX = width / Math.abs(maxX - minX);
        this.factorY = height / Math.abs(maxY - minY);

        customerCircleHashMap = new HashMap<>();
    }

    void setDepots(Depot[] depots) {
        this.depots = depots;
        depotPane.getChildren().clear();

        for (Depot depot : depots) {
            Rectangle depotRectangle = new Rectangle(10,10);
            depotRectangle.setLayoutX(-5);
            depotRectangle.setLayoutY(-5);
            depotRectangle.setTranslateX(mapXToGraph(depot.getX()));
            depotRectangle.setTranslateY(mapYToGraph(depot.getY()));
            depotRectangle.setStyle("-fx-fill: #0ac5d4");
            depotPane.getChildren().add(depotRectangle);
        }
    }

    void setCustomers(Customer[] customers) {
        this.customers = customers;
        customerPane.getChildren().clear();

        for (Customer customer : customers) {
            Circle customerCircle = new Circle(5);
            customerCircle.setTranslateX(mapXToGraph(customer.getX()));
            customerCircle.setTranslateY(mapYToGraph(customer.getY()));
            customerCircleHashMap.put(customer, customerCircle);
            customerPane.getChildren().add(customerCircle);

        }
    }

    void setRoutes(ProposedSolution solution) {
        routePane.getChildren().clear();

        int counter = 0;

        for (Depot depot : solution.depots) {
            for (Car car : depot.getCars()) {
                Color color = colors[counter % colors.length];
                counter ++;

                int currentX = depot.getX();
                int currentY = depot.getY();

                ArrayList<Customer> customers = car.getCustomerSequence();
                for (Customer customer : customers) {
                    Circle customerCircle = customerCircleHashMap.get(customer);
                    customerCircle.setFill(color);

                    Line lineRoute = new Line();
                    lineRoute.setStroke(color);
                    lineRoute.setStartX(mapXToGraph(currentX));
                    lineRoute.setStartY(mapYToGraph(currentY));
                    lineRoute.setEndX(mapXToGraph(customer.getX()));
                    lineRoute.setEndY(mapYToGraph(customer.getY()));
                    currentX = customer.getX();
                    currentY = customer.getY();
                    routePane.getChildren().add(lineRoute);
                }
                Line lineRoute = new Line();
                lineRoute.setStroke(color);
                lineRoute.setStartX(mapXToGraph(currentX));
                lineRoute.setStartY(mapYToGraph(currentY));
                lineRoute.setEndX(mapXToGraph(depot.getX()));
                lineRoute.setEndY(mapYToGraph(depot.getY()));
                routePane.getChildren().add(lineRoute);
            }
        }
    }

    private double mapXToGraph(double x) {
        return (x - minX) * factorX;
    }

    private double mapYToGraph(double y) {
        return height - ((y - minY) * factorY);
    }

}
