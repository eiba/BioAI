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

    void setDistance(double distance) {
        distanceText.setText(String.format("%.4f", distance, Locale.US));
    }


}
