public class PixelEdge implements Comparable<PixelEdge> {

    final Pixel currentPixel, neighbourPixel;
    final double distance;

    PixelEdge(Pixel currentPixel, Pixel neighbourPixel, double distance) {
        this.currentPixel = currentPixel;
        this.neighbourPixel = neighbourPixel;
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
