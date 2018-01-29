import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;

public class Main{

    public static void main(String[] args) {
        ProcessFile f = new ProcessFile("./TestData/p08");
        //System.out.println(f.depots[1].getMaximum_duration());
        PopulationGenerator g = new PopulationGenerator(f,1);
        PrintSolution p = new PrintSolution();
        p.Print(g.speciemens[0]);
        System.out.println("------------------------------");
        p.Print(g.speciemens[1]);


    }

}
