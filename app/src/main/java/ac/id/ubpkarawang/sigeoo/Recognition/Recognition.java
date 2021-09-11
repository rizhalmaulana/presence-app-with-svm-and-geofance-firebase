package ac.id.ubpkarawang.sigeoo.Recognition;

import org.opencv.core.Mat;

public interface Recognition {
    public static final int TRAINING = 0;
    public static final int RECOGNITION = 1;
    public static final int KNN = 0;
    public static final int SVM = 0;

    boolean train();
    String recognize(Mat img, String expectedLabel);
    void saveTestData();
    void saveToFile();
    void loadFromFile();
    void addImage(Mat img, String label, boolean featuresAlreadyExtracted);
    Mat getFeatureVector(Mat img);
}
