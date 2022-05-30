package ac.id.ubpkarawang.sigeoo.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.ProgressBar;

import ac.id.ubpkarawang.sigeoo.R;

public class LoadingDialog {

    Activity activity;
    AlertDialog alertDialog;

    public LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog_loading, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissLoading() {
        alertDialog.dismiss();
    }
}
