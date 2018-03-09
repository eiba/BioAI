import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private final ProgressBar progressBar = new ProgressBar();

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
        VBox vBox = new VBox();
        progressBar.prefWidthProperty().bind(vBox.widthProperty());
        outputText.setFill(Color.valueOf("#BBBBBB"));
        outputText.setTranslateX(5);
        outputPane.setStyle("-fx-background: #2B2B2B");
        outputPane.setContent(outputText);
        outputPane.setMinHeight(150);
        outputPane.setMaxHeight(150);
        vBox.getChildren().addAll(progressBar, outputPane);
        setBottom(vBox);

        //Image initialization
        imageBox.getChildren().addAll(imageViewGreenLine, imageViewBlackWhite, imageViewSegments, imageViewImage);
//        imageBox.setStyle("-fx-background-color: red");
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

    void debugDrawImage(Solution parent1, Solution parent2, Solution child, int width, int height, int splitPoint) {
        //Green Line Image
        final WritableImage imageGreen = new WritableImage(width, height);
        final PixelWriter pixelWriterGreen = imageGreen.getPixelWriter();

        for (Segment segment : parent1.segments) {
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

        for (int i = splitPoint; i < splitPoint + width; i ++) {
            final int indexRow = i / width;
            final int indexCol = i % width;
            pixelWriterGreen.setColor(indexCol, indexRow, Color.RED);
        }

        imageViewGreenLine.setImage(imageGreen);

        //Green Line Image
        final WritableImage imageBlack = new WritableImage(width, height);
        final PixelWriter pixelWriterBlack = imageBlack.getPixelWriter();

        for (Segment segment : parent2.segments) {
            for (Pixel pixel : segment.pixels) {
                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
                }
                else if (!segment.containsAllNeighbours(pixel)) {
                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
                }
                else {
                    pixelWriterBlack.setArgb(pixel.column, pixel.row, pixel.argb);
                }
            }
        }

        for (int i = splitPoint; i < splitPoint + width; i ++) {
            final int indexRow = i / width;
            final int indexCol = i % width;
            pixelWriterBlack.setColor(indexCol, indexRow, Color.RED);
        }

        imageViewBlackWhite.setImage(imageBlack);

        //Green Line Image
        final WritableImage imageSegment = new WritableImage(width, height);
        final PixelWriter pixelWriterSegment = imageSegment.getPixelWriter();

        for (Segment segment : child.segments) {
            for (Pixel pixel : segment.pixels) {
                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
                    pixelWriterSegment.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
                }
                else if (!segment.containsAllNeighbours(pixel)) {
                    pixelWriterSegment.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
                }
                else {
                    pixelWriterSegment.setArgb(pixel.column, pixel.row, pixel.argb);
                }
            }
        }

        for (int i = splitPoint; i < splitPoint + width; i ++) {
            final int indexRow = i / width;
            final int indexCol = i % width;
            pixelWriterSegment.setColor(indexCol, indexRow, Color.RED);
        }

        imageViewSegments.setImage(imageSegment);
        imageViewImage.setImage(null);
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

    void resetProgress() {
        progressBar.setProgress(0);
    }

    void setProgress(double value) {
        if (value > progressBar.getProgress()) {
            progressBar.setProgress(value);
        }
    }

}
