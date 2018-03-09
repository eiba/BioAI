public class PixelEdge implements Comparable<PixelEdge> {

    final Pixel pixelA, pixelB;
    final double distance;

    PixelEdge(Pixel pixelA, Pixel pixelB, double distance) {
        this.pixelA = pixelA;
        this.pixelB = pixelB;
        this.distance = distance;
    }

    @Override
    public int compareTo(PixelEdge o) {
        if (this.distance < o.distance) {
            return -1;
        }
        else if (this.distance > o.distance) {
            return 1;
        }
        return 0;
    }
}
