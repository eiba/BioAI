import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

class Segment {

    final PixelSegment root;
    final HashMap<Pixel, PixelSegment> pixels;
    final ArrayList<Pixel> pixelArray;
    Color centroid;

    Segment(Pixel root) {
        this.root = new PixelSegment(root);
        pixels = new HashMap<>();
        pixelArray = new ArrayList<>();
    }

    boolean contains(Pixel pixel) {
        return pixels.containsKey(pixel);
    }

    public void add(Pixel parent, Pixel child) {

        PixelSegment pixelSegment = pixels.get(parent);

        if (pixelSegment == null) {
            pixelSegment = new PixelSegment(parent);
            pixels.put(parent, pixelSegment);
            pixelArray.add(parent);
        }
        pixelSegment.add(child);
    }

    //Iterates over the pixels hashmap and returns the pixels as an array
    public Pixel[] getPixels(){

        Pixel[] pixels = new Pixel[this.pixels.keySet().size()];
        int i = 0;
        for(Pixel pixel: this.pixels.keySet()){
            pixels[i] = pixel;
            i++;
        }
        return pixels;
    }
}
