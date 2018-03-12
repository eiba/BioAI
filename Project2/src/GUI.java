import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GUI extends BorderPane {

    // Output variables
    private final Text outputText = new Text();
    private final ScrollPane outputPane = new ScrollPane();
    private final StringBuilder outputBuilder = new StringBuilder();
    private final DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private final ProgressBar progressBar = new ProgressBar();

    // Image variables
    private final FlowPane imagePane = new FlowPane();
    private final HBox imageBox = new HBox(4);
    private final ImageView imageViewImage = new ImageView();
    private final ImageView imageViewGreenLine = new ImageView();
    private final ImageView imageViewBlackWhite = new ImageView();
    private final ImageView imageViewSegments = new ImageView();

    //Options variables
    Text textFilename = new Text("Select folder");
//    TextField inputFilename = new TextField("353013");
    Text textMin = new Text("Minimum number of segments");
    TextField inputMin= new TextField("1");
    Text textMax = new Text("Maximum number of segments");
    TextField inputMax = new TextField("50");
    Text textPopulation = new Text("Population size");
    TextField inputPopulation = new TextField("50");
    Text textIterations = new Text("Iterations");
    TextField inputIterations = new TextField("100");
    Text textCrossover = new Text("Crossover rate");
    TextField inputCrossover = new TextField("1.0");
    Text textMutation = new Text("Mutation rate");
    TextField inputMutation = new TextField("0.2");
    CheckBox weightedSum = new CheckBox("Use weighted sum");
    Text textEdge = new Text("Edge weight");
    TextField inputEdge = new TextField("0.5");
    Text textDeviation = new Text("Deviation weight");
    TextField inputDeviation = new TextField("0.5");
    Text textTournament = new Text("Tournament size");
    TextField inputTournament = new TextField("3");
    Button start = new Button("Start");
    Button stop = new Button("Stop");

    //Running variables
    private boolean loop;
    private int counter, steps;


    GUI() {
        super();

        // Options initialization
        File dir = new File("./Test Images Project 2");
        File[] listOfFiles = dir.listFiles();
        ArrayList<String> folders = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                folders.add(listOfFiles[i].getName());
            }
        }
        final ChoiceBox<String> taskMenu = new ChoiceBox<>(FXCollections.observableArrayList(folders));
        taskMenu.setValue(folders.get(0));

        VBox optionBox = new VBox(5);
        optionBox.getChildren().addAll(textFilename, taskMenu, textMin, inputMin, textMax, inputMax,
                textPopulation, inputPopulation, textIterations, inputIterations, textCrossover, inputCrossover,
                textMutation, inputMutation, weightedSum, textEdge, inputEdge, textDeviation, inputDeviation, textTournament,
                inputTournament);
        optionBox.setAlignment(Pos.CENTER);
        setLeft(optionBox);

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(start, stop);
        hBox.setAlignment(Pos.CENTER);
        stop.setDisable(true);
        setTop(hBox);


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
//        imageBox.getChildren().addAll();
        imagePane.getChildren().addAll(imageViewGreenLine, imageViewBlackWhite, imageViewSegments, imageViewImage);
        imagePane.setAlignment(Pos.CENTER);
        imagePane.setVgap(5);
        imagePane.setHgap(5);
        imagePane.setPrefWrapLength(900);
        setCenter(imagePane);

        start.setOnAction((e) -> {
            MOEA MOEA = new MOEA(
                    this,
                    "./Test Images Project 2/" + taskMenu.getValue() + "/Test image.jpg",
                    getPopulationSize(),
                    20,
                    getMutationRate(),
                    getCrossoverRate(),
                    getIterations(),
                    getMinSegments(),
                    getMaxSegments(),
                    getEdgeWeight(),
                    getDeviationWeight(),
                    weightedSum.isSelected(),
                    getNuberOfTournaments());

            Thread mooaThread = new Thread(() -> {
                Solution[] solutions = MOEA.iterate();
            });
            loop = true;

            //Cleaning Image directory
            File directory = new File("./Student Images");
            for(File file: directory.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }

            mooaThread.start();
            start.setDisable(true);
            stop.setDisable(false);
        });

        stop.setOnAction((e) -> {
            loop = false;
            stop.setDisable(true);
        });
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

//    void debugDrawImage(Solution parent1, Solution parent2, Solution child, int width, int height, int splitPoint) {
//        //Green Line Image
//        final WritableImage imageGreen = new WritableImage(width, height);
//        final PixelWriter pixelWriterGreen = imageGreen.getPixelWriter();
//
//        for (Segment segment : parent1.segments) {
//            for (Pixel pixel : segment.pixels) {
//                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
//                    pixelWriterGreen.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
//                }
//                else if (!segment.containsAllNeighbours(pixel)) {
//                    pixelWriterGreen.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
//                }
//                else {
//                    pixelWriterGreen.setArgb(pixel.column, pixel.row, pixel.argb);
//                }
//            }
//        }
//
//        for (int i = splitPoint; i < splitPoint + width; i ++) {
//            final int indexRow = i / width;
//            final int indexCol = i % width;
//            pixelWriterGreen.setColor(indexCol, indexRow, Color.RED);
//        }
//
//        imageViewGreenLine.setImage(imageGreen);
//
//        //Green Line Image
//        final WritableImage imageBlack = new WritableImage(width, height);
//        final PixelWriter pixelWriterBlack = imageBlack.getPixelWriter();
//
//        for (Segment segment : parent2.segments) {
//            for (Pixel pixel : segment.pixels) {
//                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
//                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
//                }
//                else if (!segment.containsAllNeighbours(pixel)) {
//                    pixelWriterBlack.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
//                }
//                else {
//                    pixelWriterBlack.setArgb(pixel.column, pixel.row, pixel.argb);
//                }
//            }
//        }
//
//        for (int i = splitPoint; i < splitPoint + width; i ++) {
//            final int indexRow = i / width;
//            final int indexCol = i % width;
//            pixelWriterBlack.setColor(indexCol, indexRow, Color.RED);
//        }
//
//        imageViewBlackWhite.setImage(imageBlack);
//
//        //Green Line Image
//        final WritableImage imageSegment = new WritableImage(width, height);
//        final PixelWriter pixelWriterSegment = imageSegment.getPixelWriter();
//
//        for (Segment segment : child.segments) {
//            for (Pixel pixel : segment.pixels) {
//                if (pixel.column == 0 || pixel.column == width-1 || pixel.row == 0 || pixel.row == height-1) {
//                    pixelWriterSegment.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
//                }
//                else if (!segment.containsAllNeighbours(pixel)) {
//                    pixelWriterSegment.setColor(pixel.column, pixel.row, Color.LIMEGREEN);
//                }
//                else {
//                    pixelWriterSegment.setArgb(pixel.column, pixel.row, pixel.argb);
//                }
//            }
//        }
//
//        for (int i = splitPoint; i < splitPoint + width; i ++) {
//            final int indexRow = i / width;
//            final int indexCol = i % width;
//            pixelWriterSegment.setColor(indexCol, indexRow, Color.RED);
//        }
//
//        imageViewSegments.setImage(imageSegment);
//        imageViewImage.setImage(null);
//    }

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

    void resetProgress(int steps) {
        counter = 0;
        this.steps = steps;
        progressBar.setProgress(0);
    }

    synchronized void addProgress() {
        counter ++;
        progressBar.setProgress((double) counter / steps);
    }

    synchronized boolean getLoop() {
        return loop;
    }

    void moeaStopped() {
        start.setDisable(false);
        stop.setDisable(true);
    }

    private int getPopulationSize() {
        return Integer.valueOf(inputPopulation.getText());
    }
    private double getMutationRate() {
        return Double.valueOf(inputMutation.getText());
    }
    private double getCrossoverRate() {
        return Double.valueOf(inputCrossover.getText());
    }
    private int getIterations() {
        return Integer.valueOf(inputIterations.getText());
    }
    private int getMinSegments() {
        return Integer.valueOf(inputMin.getText());
    }
    private int getMaxSegments() {
        return Integer.valueOf(inputMax.getText());
    }
    private double getEdgeWeight() {
        return Double.valueOf(inputEdge.getText());
    }
    private double getDeviationWeight() {
        return Double.valueOf(inputDeviation.getText());
    }
    private int getNuberOfTournaments() {
        return Integer.valueOf(inputTournament.getText());
    }

}
