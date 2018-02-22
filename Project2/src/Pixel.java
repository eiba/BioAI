import java.awt.*;

class Pixel {

    final int row, column, rgba;
    final Color color;
    final Pixel[] neighbours;
    final double[] neighbourDistances;

    /*
    neighbours and neighbourDistances index mapping:
    0 = Pixel object above
    1 = Pixel object right
    2 = Pixel object down
    3 = Pixel object left
     */

    Pixel(int row, int column, int rgba) {
        this.row = row;
        this.column = column;
        this.rgba = rgba;
        this.color = new Color(rgba);
        neighbours = new Pixel[4];
        neighbourDistances = new double[4];
    }

}
