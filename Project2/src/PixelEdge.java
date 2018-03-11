import java.util.Comparator;

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

class rgbDistanceComparator implements Comparator<PixelEdge>
{
    @Override
    public int compare(PixelEdge x, PixelEdge y)
    {
        if (x.distance < y.distance)
        {
            return 1;
        }
        if (x.distance > y.distance)
        {
            return -1;
        }
        return 0;
    }
}