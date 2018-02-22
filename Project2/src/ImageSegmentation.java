import java.awt.*;

public class ImageSegmentation {
    ImageParser img;

    public ImageSegmentation(ImageParser img){
        this.img = img;
    }

    public int[][] createInitialSegments(int individualCount){
        //@TODO: use prim's algorithm to generate initial segments

        return null;
    }

    //eucledean distance in RGB color space
    public double eucledeanRGB(Color Color1, Color Color2){

        int differenceRed = Color1.getRed() - Color2.getRed();
        int differenceGreen = Color1.getGreen() - Color2.getGreen();
        int differenceBlue = Color1.getBlue() - Color2.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));

    }
}
