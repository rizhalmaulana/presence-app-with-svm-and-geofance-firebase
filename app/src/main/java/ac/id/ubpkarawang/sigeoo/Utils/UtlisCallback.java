package ac.id.ubpkarawang.sigeoo.Utils;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.BeritaResponse;

public interface UtlisCallback {
    void onSuccess(BeritaResponse beritaResponse);
    void onFailed(String error);
}
