package ac.id.ubpkarawang.sigeoo.Preprocessor;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

import ac.id.ubpkarawang.sigeoo.Helpers.Eyes;
import ac.id.ubpkarawang.sigeoo.Helpers.FaceDetection;
import ac.id.ubpkarawang.sigeoo.Helpers.PreferencesHelper;
import ac.id.ubpkarawang.sigeoo.Preprocessor.BrightnessCorrection.GammaCorrection;
import ac.id.ubpkarawang.sigeoo.Preprocessor.Contours.DifferenceOfGaussian;
import ac.id.ubpkarawang.sigeoo.Preprocessor.Contours.LocalBinaryPattern;
import ac.id.ubpkarawang.sigeoo.Preprocessor.Contours.Masking;
import ac.id.ubpkarawang.sigeoo.Preprocessor.ContrastAdjustment.HistogrammEqualization;
import ac.id.ubpkarawang.sigeoo.Preprocessor.StandardPostprocessing.Resize;
import ac.id.ubpkarawang.sigeoo.Preprocessor.StandardPreprocessing.Crop;
import ac.id.ubpkarawang.sigeoo.Preprocessor.StandardPreprocessing.EyeAlignment;
import ac.id.ubpkarawang.sigeoo.Preprocessor.StandardPreprocessing.GrayScale;
import ac.id.ubpkarawang.sigeoo.R;


public class PreProcessorFactory {
    private Context context;
    private PreferencesHelper preferencesHelper;
    private Resources resources;
    public enum PreprocessingMode {DETECTION, RECOGNITION};
    private PreProcessor preProcessorRecognition;
    private PreProcessor preProcessorDetection;
    private List<Mat> images;
    public CommandFactory commandFactory;
    private FaceDetection faceDetection;
    private boolean eyeDetectionEnabled;

    public PreProcessorFactory(Context context) {
        this.context = context;
        this.faceDetection = new FaceDetection(context);
        preferencesHelper = new PreferencesHelper(context);
        resources = context.getResources();
        eyeDetectionEnabled = preferencesHelper.getEyeDetectionEnabled();
        commandFactory = new CommandFactory();
        commandFactory.addCommand(resources.getString(R.string.grayscale), new GrayScale());
        commandFactory.addCommand(resources.getString(R.string.eyeAlignment), new EyeAlignment());
        commandFactory.addCommand(resources.getString(R.string.crop), new Crop());
        commandFactory.addCommand(resources.getString(R.string.gammaCorrection), new GammaCorrection(preferencesHelper.getGamma()));
        commandFactory.addCommand(resources.getString(R.string.doG), new DifferenceOfGaussian(preferencesHelper.getSigmas()));
        commandFactory.addCommand(resources.getString(R.string.masking), new Masking());
        commandFactory.addCommand(resources.getString(R.string.histogrammEqualization), new HistogrammEqualization());
        commandFactory.addCommand(resources.getString(R.string.resize), new Resize());
        commandFactory.addCommand(resources.getString(R.string.lbp), new LocalBinaryPattern());
    }

    public List<Mat> getCroppedImage(Mat img){
        preProcessorDetection = new PreProcessor(faceDetection, getCopiedImageList(img), context);
        List<String> preprocessingsDetection = getPreprocessings(PreferencesHelper.Usage.DETECTION);
        images = new ArrayList<Mat>();
        images.add(img);
        preProcessorRecognition = new PreProcessor(faceDetection, images, context);

        try {
            preprocess(preProcessorDetection, preprocessingsDetection);
            preProcessorRecognition.setFaces(PreprocessingMode.RECOGNITION);
            preProcessorRecognition = commandFactory.executeCommand(resources.getString(R.string.crop), preProcessorRecognition);
            if (eyeDetectionEnabled) {
                Eyes[] eyes = preProcessorRecognition.setEyes();
                if (eyes == null || eyes[0] == null){
                    return null;
                }
            }
            preProcessorRecognition.setImages(Resize.preprocessImage(preProcessorRecognition.getImages(), preferencesHelper.getFaceSize()));
        } catch (NullPointerException e){
            Log.d("getCroppedImage", "No face detected");
            return null;
        }
        return preProcessorRecognition.getImages();
    }

    public List<Mat> getProcessedImage(Mat img, PreprocessingMode preprocessingMode) throws NullPointerException {

        preProcessorDetection = new PreProcessor(faceDetection, getCopiedImageList(img), context);

        images = new ArrayList<Mat>();
        images.add(img);
        preProcessorRecognition = new PreProcessor(faceDetection, images, context);

        try {
            preprocess(preProcessorDetection, getPreprocessings(PreferencesHelper.Usage.DETECTION));

            preProcessorDetection.setFaces(preprocessingMode);
            preProcessorRecognition.setFaces(preProcessorDetection.getFaces());
            preProcessorRecognition.setAngle(preProcessorDetection.getAngle());
            preProcessorRecognition = commandFactory.executeCommand(resources.getString(R.string.crop), preProcessorRecognition);

            if (eyeDetectionEnabled) {
                Eyes[] eyes = preProcessorRecognition.setEyes();
                if (eyes == null || eyes[0] == null){
                    return null;
                }
            }

            if (preprocessingMode == PreprocessingMode.RECOGNITION){
                preprocess(preProcessorRecognition, getPreprocessings(PreferencesHelper.Usage.RECOGNITION));
            }

        } catch (NullPointerException e){
            Log.d("getProcessedImage", "No face detected");
            return null;
        }
        if (preprocessingMode == PreprocessingMode.DETECTION){
            return preProcessorDetection.getImages();
        } else {
            return preProcessorRecognition.getImages();
        }
    }

    private List<String> getPreprocessings(PreferencesHelper.Usage usage){
        ArrayList<String> preprocessings = new ArrayList<String>();
        preprocessings.addAll(preferencesHelper.getStandardPreprocessing(usage));
        preprocessings.addAll(preferencesHelper.getBrightnessPreprocessing(usage));
        preprocessings.addAll(preferencesHelper.getContoursPreprocessing(usage));
        preprocessings.addAll(preferencesHelper.getContrastPreprocessing(usage));
        preprocessings.addAll(preferencesHelper.getStandardPostprocessing(usage));
        return preprocessings;
    }

    private void preprocess(PreProcessor preProcessor, List<String> preprocessings){
        for (String name : preprocessings){
            preProcessor = commandFactory.executeCommand(name, preProcessor);
        }
    }

    public Rect[] getFacesForRecognition() {
        if(preProcessorRecognition != null){
            return preProcessorRecognition.getFaces();
        } else {
            return null;
        }
    }

    private List<Mat> getCopiedImageList(Mat img){
        List<Mat> images = new ArrayList<Mat>();
        Mat imgCopy = new Mat();
        img.copyTo(imgCopy);
        images.add(imgCopy);
        return images;
    }

    public int getAngleForRecognition(){
        return preProcessorRecognition.getAngle();
    }

    public void setCascadeClassifierForFaceDetector(String cascadeAssetName){
        faceDetection.setCascadeClassifierForFaceDetector(context, cascadeAssetName);
    }
}
