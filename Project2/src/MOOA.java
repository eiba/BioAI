import java.awt.*;
import java.io.IOException;

public class MOOA {

    private ImageParser imageParser;

    //Multi objective optimization algorithm goes here
    public MOOA(String filename){

        //parse the image
        try{
        imageParser = new ImageParser(filename);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        System.out.println(eucledeanRGB(new Color(imageParser.pixels[0][0]),new Color(imageParser.pixels[250][250])));
    }

    //eucledean distance in RGB color space
    public double eucledeanRGB(Color Color1, Color Color2){

        int differenceRed = Color1.getRed() - Color2.getRed();
        int differenceGreen = Color1.getGreen() - Color2.getGreen();
        int differenceBlue = Color1.getBlue() - Color2.getBlue();

        return Math.sqrt(Math.pow(differenceRed, 2) + Math.pow(differenceGreen, 2) + Math.pow(differenceBlue, 2));

    }


}
