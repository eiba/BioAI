import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Locale;

public class Statistic extends HBox{

    private final Text distanceText = new Text();
    private final Text updateText = new Text();

    Statistic() {
        super(10);
        super.setAlignment(Pos.CENTER);

        HBox distanceBox = new HBox();
        distanceBox.getChildren().add(distanceText);

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(updateText, distanceBox);
        super.getChildren().addAll(vBox);
    }

    void setDistance(double solutionDistance, double optimalDistance,int iterationsUsed) {
        double percent = optimalDistance / solutionDistance * 100;
        distanceText.setText(String.format(Locale.US, "Fitness score: %.2f%%, Solution: %.4f, Optimal: %.4f, Iterations: %d", percent, solutionDistance, optimalDistance,iterationsUsed));
    }

    void setUpdate(String update) {
        Platform.runLater(() -> {
            updateText.setText(update);
        });
    }

}
