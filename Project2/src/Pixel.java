import java.awt.Color;
import java.util.ArrayList;

class Pixel {

    final int row, column, argb;
    final Color color;
    final PixelEdge[] edges;
    final Pixel[] pixels;
//    final ArrayList<PixelEdge> edgeList;

    /*
    pixelEdgeMap index mapping:
    0 = Pixel object above
    1 = Pixel object right
    2 = Pixel object down
    3 = Pixel object left
     */

    Pixel(int row, int column, int argb) {
        this.row = row;
        this.column = column;
        this.argb = argb;
        this.color = new Color(argb);
        edges = new PixelEdge[4];
        pixels = new Pixel[4];
//        edgeList = new ArrayList<>();
//        neighbours = new Pixel[4];
//        neighbourDistances = new double[4];
    }

    int getEdgeIndex(PixelEdge pixelEdge) {
        for (int i = 0; i < 4; i ++) {
            if (edges[i] == pixelEdge) {
                return i;
            }
        }
        return -1;
    }

//    void createEdgeList() {
//        for (PixelEdge pixelEdge : edges) {
//            if (pixelEdge != null) {
//                edgeList.add(pixelEdge);
//            }
//        }
//    }

    static int mapDirection(int i) {
        if (i < 2) {
            return i + 2;
        }
        return i - 2;
    }

}
