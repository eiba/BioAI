import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

class Segment {

    final Pixel root;
    final HashMap<Pixel, ArrayList<PixelEdge>> pixelEdgeMap;
    final ArrayList<Pixel> pixels;
    final ArrayList<PixelEdge> edges;

    // Segment color variables
    int alphaTotal, redTotal, greenTotal, blueTotal, pixelCount;

    Segment(Pixel root) {
        this.root = root;
        pixelEdgeMap = new HashMap<>();
        pixelEdgeMap.put(root, new ArrayList<>());
        pixels = new ArrayList<>();
        pixels.add(root);
        edges = new ArrayList<>();

        // Updating segment color
        alphaTotal = root.color.getAlpha();
        redTotal = root.color.getRed();
        greenTotal = root.color.getGreen();
        blueTotal = root.color.getBlue();
        pixelCount = 1;
    }

    boolean contains(Pixel pixel) {
        return pixelEdgeMap.containsKey(pixel);
    }

    public void add(PixelEdge pixelEdge) {
        ArrayList<PixelEdge> currentPixelEdges = pixelEdgeMap.get(pixelEdge.currentPixel);
        currentPixelEdges.add(pixelEdge);
        pixelEdgeMap.put(pixelEdge.neighbourPixel, new ArrayList<>());
        pixels.add(pixelEdge.neighbourPixel);
        edges.add(pixelEdge);

        // Updating segment color
        alphaTotal += pixelEdge.neighbourPixel.color.getAlpha();
        redTotal += pixelEdge.neighbourPixel.color.getRed();
        greenTotal += pixelEdge.neighbourPixel.color.getGreen();
        blueTotal += pixelEdge.neighbourPixel.color.getBlue();
        pixelCount ++;
    }

    Segment copyFrom(Pixel root) {
        final Segment copy = new Segment(root);
        final ArrayList<PixelEdge> descendants = new ArrayList<>(pixelEdgeMap.get(root));

        while (!descendants.isEmpty()) {
            final PixelEdge pixelEdge = descendants.remove(0);
            copy.add(pixelEdge);
            descendants.addAll(pixelEdgeMap.get(pixelEdge.neighbourPixel));
        }

        return copy;
    }

    int getSize(Pixel pixel) {
        int size = 1;

        final ArrayList<PixelEdge> descendants = new ArrayList<>(pixelEdgeMap.get(pixel));

        while (!descendants.isEmpty()) {
            final PixelEdge pixelEdge = descendants.remove(0);
            descendants.addAll(pixelEdgeMap.get(pixelEdge.neighbourPixel));
            size ++;
        }

        return size;
    }

    int getArgb() {
        return new Color(redTotal / pixelCount, greenTotal / pixelCount, blueTotal / pixelCount, alphaTotal / pixelCount).getRGB();
    }

    Color getColor() {
        return new Color(redTotal / pixelCount, greenTotal / pixelCount, blueTotal / pixelCount, alphaTotal / pixelCount);
    }

//    void calculateCentroid() {
//
//
//        final ArrayList<PixelEdge> descendants = new ArrayList<>(pixelEdgeMap.get(root));
//        int alpha = root.color.getAlpha();
//        int red = root.color.getRed();
//        int green = root.color.getGreen();
//        int blue = root.color.getBlue();
//        int count = 1;
//
//        while (!descendants.isEmpty()) {
//            final PixelEdge pixelEdge = descendants.remove(0);
//            alpha = pixelEdge.neighbourPixel.color.getAlpha();
//            red = pixelEdge.neighbourPixel.color.getRed();
//            green = pixelEdge.neighbourPixel.color.getGreen();
//            blue = pixelEdge.neighbourPixel.color.getBlue();
//            count ++;
//            descendants.addAll(pixelEdgeMap.get(pixelEdge.neighbourPixel));
//        }
//
//        centroidColor = new Color(red / count, green / count, blue / count, alpha / count);
//    }
}
