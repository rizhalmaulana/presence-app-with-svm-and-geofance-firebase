package ac.id.ubpkarawang.sigeoo.Preprocessor.StandardPreprocessing;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Helpers.Eyes;
import ac.id.ubpkarawang.sigeoo.Preprocessor.Command;
import ac.id.ubpkarawang.sigeoo.Preprocessor.PreProcessor;

public class EyeAlignment implements Command {
    private static final double DESIRED_RIGHT_EYE_X = 0.24;
    private static final double DESIRED_RIGHT_EYE_Y = 0.30;
    private static final double DESIRED_LEFT_EYE_X = (1.0 - DESIRED_RIGHT_EYE_X);

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        Eyes[] eyes = preProcessor.setEyes();
        if (eyes == null || eyes[0] == null){
            return null;
        }
        for (int i=0; i<images.size(); i++){
            Mat img = images.get(i);
            Eyes eye = eyes[i];
            double desiredLen = (DESIRED_LEFT_EYE_X - DESIRED_RIGHT_EYE_X) * img.cols();
            double scale = 0.9 * desiredLen / eye.getDist();
            MatOfFloat leftCenter = eye.getLeftCenter();
            MatOfFloat rightCenter = eye.getRightCenter();
            double centerX = ((leftCenter.get(0,0)[0] + rightCenter.get(0,0)[0]) / 2);
            double centerY = ((leftCenter.get(1,0)[0] + rightCenter.get(1,0)[0]) / 2);
            Mat rotMat = Imgproc.getRotationMatrix2D(new Point(centerX,centerY), eye.getAngle(), scale);
            rotMat.put(2, 0, img.cols() * 0.5 - centerX);
            rotMat.put(2, 1, img.rows() * DESIRED_RIGHT_EYE_Y - centerY);
            Imgproc.warpAffine(img, img, rotMat, new Size(img.cols(),img.rows()));
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
