package ac.id.ubpkarawang.sigeoo.Recognition;

import android.content.Context;
import android.content.res.Resources;

import ac.id.ubpkarawang.sigeoo.R;

public class RecognitionFactory {
    public static Recognition getRecognitionAlgorithm(Context context, int method, String algorithm) {
        Resources resources = context.getResources();
        if (algorithm.equals(resources.getString(R.string.eigenfaces))){
            return new Eigenfaces(context, method);
        } else if (algorithm.equals(resources.getString(R.string.imageReshaping))){
            return new SupportVectorMachine(context, method);
        } else if (algorithm.equals(resources.getString(R.string.caffe))) {
            return new Caffe(context, method);
        } else {
            return null;
        }
    }
}
