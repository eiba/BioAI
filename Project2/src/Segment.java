import java.util.HashMap;

class Segment {

    final PixelSegment root;
    final HashMap<Pixel, PixelSegment> pixels;

    Segment(Pixel root) {
        this.root = new PixelSegment(root);
        pixels = new HashMap<>();
    }

    boolean contains(Pixel pixel) {
        return pixels.containsKey(pixel);
    }

    public void add(Pixel parent, Pixel child) {

        PixelSegment pixelSegment = pixels.get(parent);

        if (pixelSegment == null) {
            pixelSegment = new PixelSegment(parent);
            pixels.put(parent, pixelSegment);
        }
        pixelSegment.add(child);
    }
}
