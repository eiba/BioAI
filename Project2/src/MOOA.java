import java.awt.*;
import java.io.IOException;

public class MOOA {

    private ImageParser img;

    //Multi Objective Optimization Algorithm
    public MOOA(String filename, int individualCount){

        //Step 1: parse the image
        try {
            img = new ImageParser(filename);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ImageSegmentation segmentation = new ImageSegmentation(img);

        //Step 2: Create Initial segments with Prim's algorithm
        //@TODO: Implement Prim's algorithm to make individualCount number of minumum spanning trees. Each tree is used to create one initial segment.
        int [][] initialGenotypes = segmentation.createInitialSegments(individualCount);
    }
}
