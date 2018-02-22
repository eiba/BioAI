import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        MOOA mooa = new MOOA(
                "./TestImages/1/Test image.jpg",
                50,
                20,
                0.2,
                0.7,
                100,
                1,
                50,
                0.5,
                0.5);

        Solution[] solutions = mooa.iterate();
    }
}
