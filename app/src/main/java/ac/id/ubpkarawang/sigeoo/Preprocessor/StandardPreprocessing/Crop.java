package ac.id.ubpkarawang.sigeoo.Preprocessor.StandardPreprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Preprocessor.Command;
import ac.id.ubpkarawang.sigeoo.Preprocessor.PreProcessor;

public class Crop implements Command {

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        Mat img = preProcessor.getImages().get(0);
        List<Mat> processed = new ArrayList<Mat>();
        if (preProcessor.getFaces().length == 0){
            return null;
        } else {
            for (Rect rect : preProcessor.getFaces()){
                Mat subImg = img.submat(rect);
                processed.add(subImg);
            }
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }

    public Mat preprocessImage(Mat img, Rect rect){
        return img.submat(rect);
    }
}
