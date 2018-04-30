import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.Locale;

class StatGraph extends Pane {

    private final double width, height;
    private final int iterations;

    private int currentIteration = 0;
    private double previousX, previousY;

    private Text bestSolutionText = new Text();

    StatGraph(double width, double height, int iterations) {
        super();
        super.setMouseTransparent(true);
        super.setTranslateY(-height);
        super.setMinSize(width, height);
        super.setMaxSize(width, height);
        height *= 2;

        this.width = width;
        this.height = height;
        this.iterations = iterations;

        final Line halfLine = new Line();
        halfLine.setStroke(Color.GRAY);
        halfLine.setStartX(0);
        halfLine.setStartY(height * 0.5);
        halfLine.setEndX(width);
        halfLine.setEndY(height * 0.5);

        final Line approvedLine = new Line();
        approvedLine.setStroke(Color.YELLOW);
        approvedLine.setStartX(0);
        approvedLine.setStartY(height * 0.90);
        approvedLine.setEndX(width);
        approvedLine.setEndY(height * 0.90);

        final Line optimalLine = new Line();
        optimalLine.setStroke(Color.GREEN);
        optimalLine.setStartX(0);
        optimalLine.setStartY(height);
        optimalLine.setEndX(width);
        optimalLine.setEndY(height);


        Text halfText = new Text("50%");
        halfText.setY(halfLine.getEndY());
        halfText.setX(-25);

        Text approvedText = new Text("90%");
        approvedText.setY(approvedLine.getEndY());
        approvedText.setX(-25);

        Text optimalText = new Text("100%");
        optimalText.setY(optimalLine.getEndY());
        optimalText.setX(-32);

        bestSolutionText.setY(height * 0.5 + 20);

        super.getChildren().addAll(halfLine, approvedLine, optimalLine, halfText,approvedText, optimalText, bestSolutionText);
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

    void setBestSolution(double fitness, double percent) {
        Platform.runLater(() -> bestSolutionText.setText(String.format(Locale.US, "Best makespan: %.2f\nPercent: %.2f%%", fitness, percent)));
    }
}
