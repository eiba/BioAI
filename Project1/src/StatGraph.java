import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class StatGraph extends Pane {

    private final double width, height;
    private final int iterations;

    private int currentIteration = 0;
    private double previousX, previousY;

    StatGraph(double width, double height, int iterations) {
        super();
        super.setMinSize(width, height);
        super.setMaxSize(width, height);

        this.width = width;
        this.height = height;
        this.iterations = iterations;

        final Line approvedLine = new Line();
        approvedLine.setStroke(Color.YELLOW);
        approvedLine.setStartX(0);
        approvedLine.setStartY(height * 0.95);
        approvedLine.setEndX(width);
        approvedLine.setEndY(height * 0.95);

        final Line optimalLine = new Line();
        optimalLine.setStroke(Color.GREEN);
        optimalLine.setStartX(0);
        optimalLine.setStartY(height);
        optimalLine.setEndX(width);
        optimalLine.setEndY(height);


        Text approvedText = new Text("95%");
        approvedText.setY(approvedLine.getEndY());
        approvedText.setX(-25);

        Text optimalText = new Text("100%");
        optimalText.setY(optimalLine.getEndY());
        optimalText.setX(-32);

        super.getChildren().addAll(approvedLine, optimalLine, approvedText, optimalText);
    }

    void addIteration(double fitness) {
        final Line line = new Line();
        line.setStroke(Color.BLUE);

        final double endX = ((double) currentIteration ++ / iterations) * width;
        final double endY = fitness * height;
        line.setEndX(endX);
        line.setEndY(endY);

        if (previousY == 0) {
            previousY = endY;
        }
        line.setStartX(previousX);
        line.setStartY(previousY);

        previousX = endX;
        previousY = endY;
        Platform.runLater(() -> super.getChildren().add(line));
    }
}
