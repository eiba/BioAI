import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.Locale;

public class Statistic extends HBox{

    private final Text distanceText = new Text();

    Statistic() {
        super(10);
        super.setAlignment(Pos.CENTER);

        HBox distanceBox = new HBox();
        distanceBox.getChildren().addAll(new Text("Fitness score: "), distanceText);

        super.getChildren().addAll(distanceBox);
    }

    void setDistance(double solutionDistance, double optimalDistance) {
        double percent = optimalDistance / solutionDistance * 100;
        distanceText.setText(String.format(Locale.US, "%.2f%%, Solution: %.4f, Optimal: %.4f", percent, solutionDistance, optimalDistance));
    }


}
