import java.util.ArrayList;

class PixelSegment {

    final Pixel parent;
    final ArrayList<Pixel> children;

    PixelSegment(Pixel parent) {
        this.parent = parent;
        children = new ArrayList<>();
    }

    void add(Pixel child) {
        children.add(child);
    }

}
