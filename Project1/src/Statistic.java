import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class Statistic extends HBox{

    private final Text distanceText = new Text();

    Statistic() {
        super(10);
        super.setAlignment(Pos.CENTER);

        HBox distanceBox = new HBox();
        distanceBox.getChildren().addAll(new Text("Average fitness score: "), distanceText);

        super.getChildren().addAll(distanceBox);
    }

    void setDistance(double distance) {
        distanceText.setText(String.valueOf(distance));
    }


}
