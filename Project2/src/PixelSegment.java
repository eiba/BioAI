import java.util.ArrayList;

public class PixelSegment {

    final Pixel parent;
    final ArrayList<Pixel> children;

    PixelSegment(Pixel parent) {
        this.parent = parent;
        children = new ArrayList<>();
    }

    void addChild(Pixel child) {

    }

}
