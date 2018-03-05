import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    private final ScrollPane imagePane = new ScrollPane();
    private final HBox imageBox = new HBox(4);
    private final ImageView imageViewImage = new ImageView();
    private final ImageView imageViewGreenLine = new ImageView();
    private final ImageView imageViewBlackWhite = new ImageView();
    private final ImageView imageViewSegments = new ImageView();

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
        imageBox.getChildren().addAll(imageViewGreenLine, imageViewBlackWhite, imageViewSegments, imageViewImage);
        imagePane.setContent(imageBox);
        setCenter(imagePane);
    }

    void drawImage(Solution solution, int width, int height) {

        //Original Image
        final WritableImage imageOriginal = new WritableImage(width, height);
        final PixelWriter pixelWriterOriginal = imageOriginal.getPixelWriter();

        for (Segment segment : solution.segments) {
            for (Pixel pixel : segment.pixels) {
                pixelWriterOriginal.setArgb(pixel.column, pixel.row, pixel.argb);
            }
        }

        imageViewImage.setImage(imageOriginal);

        //Green Line Image
        final WritableImage imageGreen = new WritableImage(width, height);
        final PixelWriter pixelWriterGreen = imageGreen.getPixelWriter();

        for (Segment segment : solution.segments) {
            for (Pixel pixel : segment.pixels) {
                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
                    pixelWriterGreen.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
                }
                else if (!segment.containsAllNeighbours(pixel)) {
                    pixelWriterGreen.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
                }
                else {
                    pixelWriterGreen.setArgb(pixel.column, pixel.row, pixel.argb);
                }
            }
        }

        imageViewGreenLine.setImage(imageGreen);

        //Black White Image
        final WritableImage imageBlack = new WritableImage(width, height);
        final PixelWriter pixelWriterBlack = imageBlack.getPixelWriter();

        for (Segment segment : solution.segments) {
            for (Pixel pixel : segment.pixels) {
                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.BLACK);
                }
                else if (!segment.containsAllNeighbours(pixel)) {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.BLACK);
                }
                else {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.WHITE);
                }
            }
        }

        imageViewBlackWhite.setImage(imageBlack);

        //Segment Image
        final WritableImage image = new WritableImage(width, height);
        final PixelWriter pixelWriter = image.getPixelWriter();

        for (Segment segment : solution.segments) {
            for (Pixel pixel : segment.pixels) {
                pixelWriter.setArgb(pixel.column, pixel.row, segment.getArgb());
            }
        }

        imageViewSegments.setImage(image);
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
