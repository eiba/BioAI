import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

class Segment {

    final Pixel root;
    final HashMap<Pixel, ArrayList<PixelEdge>> pixelEdgeMap;
    final ArrayList<Pixel> pixels;
    final ArrayList<PixelEdge> edges;
    Color centroid;

    Segment(Pixel root) {
        this.root = root;
        pixelEdgeMap = new HashMap<>();
        pixelEdgeMap.put(root, new ArrayList<>());
        pixels = new ArrayList<>();
        pixels.add(root);
        edges = new ArrayList<>();
    }

    boolean contains(Pixel pixel) {
        return pixelEdgeMap.containsKey(pixel);
    }

    public void add(PixelEdge pixelEdge) {
        ArrayList<PixelEdge> currentPixelEdges = pixelEdgeMap.get(pixelEdge.currentPixel);
        currentPixelEdges.add(pixelEdge);
        pixelEdgeMap.put(pixelEdge.neighbourPixel, new ArrayList<>());
        pixels.add(pixelEdge.currentPixel);
        edges.add(pixelEdge);
    }

    //Iterates over the pixels hashmap and returns the pixels as an array
//    public Pixel[] getPixels(){
//
//        Pixel[] pixels = new Pixel[this.pixels.keySet().size()];
//        int i = 0;
//        for(Pixel pixel: this.pixels.keySet()){
//            pixels[i] = pixel;
//            i++;
//        }
//        return pixels;
//    }
}
