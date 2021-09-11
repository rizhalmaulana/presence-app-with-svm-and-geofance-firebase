package ac.id.ubpkarawang.sigeoo.Preprocessor.Contours;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Preprocessor.Command;
import ac.id.ubpkarawang.sigeoo.Preprocessor.PreProcessor;

public class DifferenceOfGaussian implements Command {
    private double sigma1;
    private double sigma2;
    private Size size1;
    private Size size2;

    public DifferenceOfGaussian(double[] sigmas) {
        this.sigma1 = sigmas[0];
        this.sigma2 = sigmas[1];
        this.size1 = new Size(2 * Math.ceil(2*sigma1) + 1, 2 * Math.ceil(2*sigma1) + 1);
        this.size2 = new Size(2 * Math.ceil(2*sigma2) + 1, 2 * Math.ceil(2*sigma2) + 1);
    }

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            Mat gauss1 = new Mat();
            Mat gauss2 = new Mat();
            Imgproc.GaussianBlur(img, gauss1, size1, sigma1);
            Imgproc.GaussianBlur(img, gauss2, size2, sigma2);
            Core.absdiff(gauss1, gauss2, img);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
