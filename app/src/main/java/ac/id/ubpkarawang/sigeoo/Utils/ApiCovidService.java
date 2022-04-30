package ac.id.ubpkarawang.sigeoo.Utils;

import ac.id.ubpkarawang.sigeoo.Model.Covid.DataIndonesia;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiCovidService {
    @GET("indonesia")
    Call<DataIndonesia> getKasus();
}
