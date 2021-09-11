package ac.id.ubpkarawang.sigeoo.Helpers;

import org.opencv.core.MatOfFloat;

public class Eyes {
    double dist;
    MatOfFloat rightCenter;
    MatOfFloat leftCenter;
    double angle;

    public Eyes(double dist, MatOfFloat rightCenter, MatOfFloat leftCenter, double angle) {
        this.dist = dist;
        this.rightCenter = rightCenter;
        this.leftCenter = leftCenter;
        this.angle = angle;
    }

    public double getDist() {
        return dist;
    }

    public MatOfFloat getRightCenter() {
        return rightCenter;
    }

    public MatOfFloat getLeftCenter() {
        return leftCenter;
    }

    public double getAngle() {
        return angle;
    }
}
