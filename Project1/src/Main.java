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
        ProcessFile f = new ProcessFile("./TestData/p01");
        //System.out.println(f.depots[0].getCars()[1].getCurrent_duration());
        PopulationGenerator g = new PopulationGenerator(f,100);

        PrintSolution p = new PrintSolution(g.speciemens[0]);
        System.out.println("------------------------------");
        PrintSolution b = new PrintSolution(g.speciemens[1]);

    }

}
