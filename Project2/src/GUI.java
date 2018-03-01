import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUI extends BorderPane {

    // Output variables
    private final Text outputText = new Text();
    private final ScrollPane outputPane = new ScrollPane();
    private final StringBuilder outputBuilder = new StringBuilder();
    private final DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    // Image variables
    private final ImageView imageView = new ImageView();

    GUI() {
        super();

        // Output initialization
        outputText.setFill(Color.valueOf("#BBBBBB"));
        outputText.setTranslateX(5);
        outputPane.setStyle("-fx-background: #2B2B2B");
        outputPane.setContent(outputText);
        outputPane.setMinHeight(150);
        outputPane.setMaxHeight(150);
        setBottom(outputPane);

        //Image initialization
        setCenter(imageView);
    }

    void drawImage(Solution solution, int width, int height) {
        final WritableImage image = new WritableImage(width, height);
        final PixelWriter pixelWriter = image.getPixelWriter();

        for (Segment segment : solution.segments) {
            
        }

        imageView.setImage(image);
    }

    void out(String message) {
        Date date = new Date();
        outputBuilder.append(formatter.format(date));
        outputBuilder.append(": ");
        outputBuilder.append(message);
        Platform.runLater(() -> {
            outputText.setText(outputBuilder.toString());
            outputPane.setVvalue( 1.0d );
        });
        outputBuilder.append('\n');
    }

}
