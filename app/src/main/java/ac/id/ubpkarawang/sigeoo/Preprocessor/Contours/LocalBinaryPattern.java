package ac.id.ubpkarawang.sigeoo.Preprocessor.Contours;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Helpers.PreferencesHelper;
import ac.id.ubpkarawang.sigeoo.Preprocessor.Command;
import ac.id.ubpkarawang.sigeoo.Preprocessor.PreProcessor;

public class LocalBinaryPattern implements Command {
    @Override
    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for(Mat img : images){
            // Resize for Performance enhancement
            PreferencesHelper preferencesHelper = new PreferencesHelper(preProcessor.getContext());
            Size size = new Size(preferencesHelper.getN(), preferencesHelper.getN());
            Imgproc.resize(img, img, size);
            Mat lbp = new Mat(img.rows()-2, img.cols()-2, img.type());
            for (int i=1; i<img.rows()-1; i++){
                for (int j=1; j<img.cols()-1; j++){
                    BitSet out = new BitSet(8);
                    double cen = img.get(i, j)[0];
                    if(img.get(i-1, j-1)[0] > cen) out.set(0);
                    if(img.get(i-1, j)[0] > cen) out.set(1);
                    if(img.get(i-1, j+1)[0] > cen) out.set(2);
                    if(img.get(i, j+1)[0] > cen) out.set(3);
                    if(img.get(i+1,j+1)[0] > cen) out.set(4);
                    if(img.get(i+1,j)[0] > cen) out.set(5);
                    if(img.get(i+1,j-1)[0] > cen) out.set(6);
                    if(img.get(i,j-1)[0] > cen) out.set(7);
                    int value = 0;
                    for(int k=0; k<out.length(); k++){
                        int index = out.nextSetBit(k);
                        value += Math.pow(2,out.length() - 1 - index);
                        k = index;
                    }
                    lbp.put(i-1, j-1, value);
                }
            }
            processed.add(lbp);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
