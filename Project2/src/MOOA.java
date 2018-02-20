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

    }
}
