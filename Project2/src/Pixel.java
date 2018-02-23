import java.awt.Color;
import java.util.ArrayList;

class Pixel {

    final int row, column, rgba;
    final Color color;
    final PixelEdge[] edges;
    final ArrayList<PixelEdge> edgeList;

    /*
    edges index mapping:
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
        edges = new PixelEdge[4];
        edgeList = new ArrayList<>();
//        neighbours = new Pixel[4];
//        neighbourDistances = new double[4];
    }

    void createEdgeList() {
        for (PixelEdge pixelEdge : edges) {
            if (pixelEdge != null) {
                edgeList.add(pixelEdge);
            }
        }
    }

}
