package ac.id.ubpkarawang.sigeoo.Utils;

import java.util.List;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.MyLatLng;

public interface LoadLocationListener {
    void onLoadLocationSuccess(List<MyLatLng> latLngs);
    void onLoadLocationFailed(String message);
}
