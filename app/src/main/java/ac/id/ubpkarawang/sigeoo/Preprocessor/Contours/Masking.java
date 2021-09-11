package ac.id.ubpkarawang.sigeoo.Preprocessor.Contours;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Preprocessor.Command;
import ac.id.ubpkarawang.sigeoo.Preprocessor.PreProcessor;

public class Masking implements Command {
    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            preProcessor.normalize0255(img);

            double otsu_thresh_val = Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_OTSU);
            Imgproc.Canny(img, img, otsu_thresh_val * 0.5, otsu_thresh_val);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
